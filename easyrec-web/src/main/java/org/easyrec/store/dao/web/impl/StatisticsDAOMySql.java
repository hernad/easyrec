/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.easyrec.store.dao.web.impl;

import org.easyrec.model.web.RemoteTenant;
import org.easyrec.model.web.statistics.*;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.store.dao.web.StatisticsDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dkovacs, dmann
 */
public class StatisticsDAOMySql extends JdbcDaoSupport implements StatisticsDAO {

    private AssocStatisticRowMapper assocStatisticRowMapper = new AssocStatisticRowMapper();
    private TenantStatisticRowMapper tenantStatisticRowMapper = new TenantStatisticRowMapper();
    private UserStatisticRowMapper userStatisticRowMapper = new UserStatisticRowMapper();
    private ConversionStatisticRowMapper conversionStatisticRowMapper = new ConversionStatisticRowMapper();
    private RuleMinerStatisticRowMapper ruleMinerStatisticRowMapper = new RuleMinerStatisticRowMapper();


    private TypeMappingService typeMappingService;


    public StatisticsDAOMySql(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void setTypeMappingService(TypeMappingService typeMappingService) {
        this.typeMappingService = typeMappingService;
    }


    /**
     * This function returns a map of ActionBundles. An ActionBundle stores
     * the number of actions for a given unit.
     * e.g. {"VIEW", {{1,5},{2,8},{6,30},{11,10}}},
     * {"BUY",  {{2,5},{6,18},{8,13},{15,17}}},
     * This can be interpreted as follows:
     * On day 1 there were 5 view-actions.
     * On day 2 there were 8 view- and 5 buy-actions.
     * ....
     * If the actionType Parameter is not null, only Action Bundles of the
     * given type are returned.
     * <p/>
     * Actionsbundles are retrieved from the action and backtracking table.
     * actiontypes
     * VIEW    1
     * RATE    3
     * BUY     4
     * backtrackingtype
     * CLICK ON CHART 998
     * CLICK ON RECOMMENDATION 100[X] X...see actiontypes
     * e.g. 1001 CLICK ON RECOMMENDATION OTHER USERS ALSO VIEWED
     */
    public HashMap<Integer, HashMap<Integer, Integer>> getActionBundleMap(int tenant, long from, long to,
                                                                          Integer actionType, Integer assocType) {

        HashMap<Integer, HashMap<Integer, Integer>> actionBundleMap = new HashMap<Integer, HashMap<Integer, Integer>>();

        SqlRowSet rs;
        StringBuilder sql = new StringBuilder();
        Object[] args;
        int[] argTypes;

        if (assocType == null) {

            if (actionType == null) {
                // query actionbundles for all actions type is slower than to query
                // action bundles for each action type.
                List<Integer> actionTypes = new LinkedList<Integer>(
                        typeMappingService.getActionTypeMapping(tenant).values());

                List<Object> lArgs = new ArrayList<Object>();
                List<Integer> lArgTypes = new ArrayList<Integer>();

                for (int i = 0; i < actionTypes.size(); i++) {
                    Integer actionTypeId = actionTypes.get(i);

                    sql.append(" SELECT ").append("    actionTypeId,     ").append("    DAY(actiontime) as unit, ")
                            .append("    COUNT(1) as cnt").append(" FROM ").append("    action  ").append(" WHERE ")
                            .append("    tenantId = ?   AND ").append("    actionTime >= ? AND ")
                            .append("    actionTime <= ? AND ").append("    actionTypeId = ").append(actionTypeId)
                            .append(" GROUP BY ").append("   actiontypeid, ").append("   DAY(actiontime) ");

                    if (i < actionTypes.size() - 1) {
                        sql.append("   UNION ");
                    }
                    lArgs.add(tenant);
                    lArgs.add(new Date(from));
                    lArgs.add(new Date(to));

                    lArgTypes.add(Types.INTEGER);
                    lArgTypes.add(Types.TIMESTAMP);
                    lArgTypes.add(Types.TIMESTAMP);

                }

                int[] intArray = new int[lArgTypes.size()];
                for (int i = 0; i < lArgTypes.size(); i++) {
                    intArray[i] = lArgTypes.get(i);
                }

                argTypes = intArray;
                args = lArgs.toArray();


            } else {
                sql = new StringBuilder().append(" SELECT ").append("    actionTypeId,     ")
                        .append("    DAY(actiontime) as unit, ").append("    COUNT(1) as cnt").append(" FROM ")
                        .append("    action  ").append(" WHERE ").append("    tenantId = ? AND   ")
                        .append("    actionTime >= ? AND ").append("    actionTime <= ?     ");

                if (actionType != null) {
                    sql.append(" AND actionTypeId=").append(actionType);
                }

                sql.append(" GROUP BY ").append("   actiontypeid, ").append("   DAY(actiontime) ");

                args = new Object[]{tenant, new Date(from), new Date(to)};
                argTypes = new int[]{Types.INTEGER, Types.DATE, Types.DATE};
            }

            rs = getJdbcTemplate().queryForRowSet(sql.toString(), args, argTypes);

            while (rs.next()) {
                if (actionBundleMap.get(rs.getInt("actionTypeId")) == null) {

                    actionBundleMap.put(rs.getInt("actionTypeId"), new HashMap<Integer, Integer>());
                }
                actionBundleMap.get(rs.getInt("actionTypeId")).put(rs.getInt("unit"), rs.getInt("cnt"));
            }
        }


        // backtracking table
        // - all clicks on recommendations like:
        //   "other users also viewed/bought/rated" are mapped assocType = 1001
        // - clicks on charts are mapped to assocType = 998
        // - clicks on "recommendation for user" are mapped to assocType = 999

        if (actionType == null) {

            String sAssocType = " 1=1 ";
            if (assocType != null) {
                if (assocType > 1000) {
                    sAssocType = " assocType < 100";
                } else {
                    sAssocType = " assocType = " + assocType;
                }
            }

            sql = new StringBuilder().append("SELECT ")
                    .append("	IF(assocType<100, 1001, assocType) as actionTypeId, ")
                    .append("	DAY(timestamp) as unit,  ").append("	COUNT(1) as cnt ").append("FROM  ")
                    .append("	backtracking   ").append("WHERE  ").append("	tenantId = ? AND    ")
                    .append("	timestamp > ? AND  ").append("	timestamp < ? AND ").append(sAssocType)
                    .append(" GROUP BY  ").append("	assocType,  ").append("	DAY(timestamp) ");

            rs = getJdbcTemplate().queryForRowSet(sql.toString(), new Object[]{tenant, new Date(from), new Date(to)},
                    new int[]{Types.INTEGER, Types.DATE, Types.DATE});

            while (rs.next()) {
                if (actionBundleMap.get(rs.getInt("actionTypeId")) == null) {

                    actionBundleMap.put(rs.getInt("actionTypeId"), new HashMap<Integer, Integer>());
                }
                actionBundleMap.get(rs.getInt("actionTypeId")).put(rs.getInt("unit"), rs.getInt("cnt"));
            }
        }

        return actionBundleMap;
    }


    /**
     * Get statistics about a given action and assocType (e.g. bought and bought_together)
     */
    public AssocStatistic getAssocStatistics(int tenantId, int actionTypeId, int assocTypeId) {

        String sql = new StringBuilder().append(" SELECT * FROM ")
                .append("    (SELECT Count(1) as actions FROM action WHERE actionTypeId = ? AND tenantid = ?) v JOIN ")
                .append("    (SELECT Count(1) as rules FROM itemassoc WHERE assocTypeId = ? AND tenantid = ?) r JOIN ")
                .append(" (SELECT ").append(" 	ROUND(AVG(c),0) AS averageNumberOfRulesPerItem,  ")
                .append("       COUNT(c) AS itemsWithRules, ")
                .append("       ROUND(STD(c),0)   AS stdNumberOfRulesPerItem FROM (SELECT itemFromId, count(1) AS c FROM itemassoc WHERE assocTypeId = ? AND tenantid = ? GROUP BY itemFromId) b) b")
                .toString();

        AssocStatistic a = getJdbcTemplate()
                .queryForObject(sql, new Object[]{actionTypeId, tenantId, assocTypeId, tenantId, assocTypeId, tenantId},
                        new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER,
                                Types.INTEGER}, assocStatisticRowMapper);
        return a;
    }

    /**
     * Compute tenant statistics for
     * recommendation_coverage: Number of total actions of items that are in the itemassoc table.
     * 10M entries/tenant: average computation time 4,5 minutes
     */
    public TenantStatistic getTenantStatistics(RemoteTenant remoteTenant) {

        Integer tenantId = remoteTenant.getId();
        // will be replaced with the actual l1 size as soon l1 is configureable
        // in the administration menu.
        Integer l1 = 5000;

        String sql = new StringBuilder().append(" SELECT  ").append("     a.actions, ").append("     b.backtracks, ")
                .append("     i.items, ").append("     u.users, ")
                .append("     ROUND(a.actions/u.users,2) AS average_actions_per_user, ")
                .append("     ROUND((e.number_of_total_actions_of_items_in_itemassoc/a.actions)*100,2) as recommendation_coverage ")
                .append(" FROM ").append(" (SELECT count(1) as actions FROM action WHERE tenantid=?) a JOIN  ")
                .append(" (SELECT count(1) as backtracks FROM backtracking WHERE tenantid=?) b JOIN  ")
                .append(" (SELECT count(1) as items FROM (SELECT distinct itemid FROM action where tenantid = ?) a) i JOIN  ")
                .append(" (SELECT count(1) as users FROM (SELECT distinct userid FROM action where tenantid = ?) a) u JOIN ")
                .append(" (SELECT SUM(c) AS number_of_total_actions_of_items_in_itemassoc FROM ( ")
                .append("   SELECT c FROM  ").append("       (SELECT itemid, itemtypeid, tenantid, COUNT(1) as c ")
                .append("        FROM action  ").append("        WHERE  ").append("           tenantid = ?   ")
                .append("        GROUP BY  ").append("           itemid ").append("        LIMIT ").append(l1)
                .append("        ) a INNER JOIN itemassoc i ON (  ").append("           i.itemfromid = a.itemid AND   ")
                .append("           a.itemtypeid = i.itemFromTypeId AND  ")
                .append("           a.tenantid   = i.tenantid)  ").append("       GROUP BY   ")
                .append("           a.itemid, a.itemtypeid, a.tenantid  ").append("       ) a) e  ").toString();

        TenantStatistic t = getJdbcTemplate()
                .queryForObject(sql, new Object[]{tenantId, tenantId, tenantId, tenantId, tenantId},
                        new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER},
                        tenantStatisticRowMapper);

        return t;
    }

    /**
     * Get Users Statistics for a given Tenant
     * !!! 10M actions --> about 1 hour to execute!!!
     */
    public UserStatistic getUserStatistics(int tenantId) {
        String sql = new StringBuilder().append(" SELECT ").append("     u1.users_with_1_action, ")
                .append("     u2.users_with_2_actions, ").append("     u5_10.users_with_3_10_actions, ")
                .append("     u10_100.users_with_11_100_actions, ").append("     u100.users_with_101_and_more_actions ")
                .append(" FROM ")
                .append(" (select count(1) as users_with_1_action from (select count(1) as actions, userid from action where tenantid = ? group by userid having count(1) = 1 ) u) u1 join ")
                .append(" (select count(1) as users_with_2_actions from (select count(1) as actions, userid from action where tenantid = ? group by userid having count(1) = 2 ) u) u2 join ")
                .append(" (select count(1) as users_with_3_10_actions from (select count(1) as actions, userid from action where tenantid = ? group by userid having count(1) > 2 and count(1) <= 10) u) u5_10 join ")
                .append(" (select count(1) as users_with_11_100_actions from (select count(1) as actions, userid from action where tenantid = ? group by userid having count(1) > 10 and count(1) <= 100) u) u10_100 join ")
                .append(" (select count(1) as users_with_101_and_more_actions from (select count(1) as actions, userid from action where tenantid = ? group by userid having count(1) > 100 ) u) u100 ")
                .toString();

        UserStatistic u = getJdbcTemplate()
                .queryForObject(sql, new Object[]{tenantId, tenantId, tenantId, tenantId, tenantId},
                        new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER},
                        userStatisticRowMapper);

        return u;

    }

    /**
     * Get Users Statistics for a given Tenant for the last X day.
     * The more day the longer the query need to execute.
     */
    public UserStatistic getUserStatistics(int tenantId, int days) {

        Date refDate = new Date(System.currentTimeMillis() - (days * 86400000l)); //convert days to millis
        String sql = new StringBuilder().append(" SELECT ").append("         u1.users_with_1_action, ")
                .append("         u2.users_with_2_actions, ").append("         u5_10.users_with_3_10_actions, ")
                .append("         u10_100.users_with_11_100_actions, ")
                .append("         u100.users_with_101_and_more_actions ").append(" FROM ")
                .append(" 	(select count(1) as users_with_1_action from (select userid from action where tenantid = ? and actiontypeid= 1 and actiontime > ? group by userid having count(1) = 1) u) u1 join ")
                .append("         (select count(1) as users_with_2_actions from (select userid from action where tenantid = ? and actiontypeid= 1 and actiontime > ? group by userid having count(1) = 2) u) u2 join ")
                .append("         (select count(1) as users_with_3_10_actions from (select userid from action where tenantid = ? and actiontypeid= 1 and actiontime > ? group by userid having count(1) > 2 and count(1) <= 10) u) u5_10 join ")
                .append("         (select count(1) as users_with_11_100_actions from (select userid from action where tenantid = ? and actiontypeid= 1 and actiontime > ? group by userid having count(1) > 10 and count(1) <= 100) u) u10_100 join         ")
                .append("         (select count(1) as users_with_101_and_more_actions from (select userid from action where tenantid = ? and actiontypeid= 1 and actiontime > ? group by userid having count(1) > 100 and count(1) <= 100) u) u100 ")
                .toString();

        UserStatistic u = getJdbcTemplate().queryForObject(sql,
                new Object[]{tenantId, refDate, tenantId, refDate, tenantId, refDate, tenantId, refDate, tenantId,
                        refDate},
                new int[]{Types.INTEGER, Types.DATE, Types.INTEGER, Types.DATE, Types.INTEGER, Types.DATE,
                        Types.INTEGER, Types.DATE, Types.INTEGER, Types.DATE}, userStatisticRowMapper);
        return u;

    }


    /**
     * Show the distribution of items with rules group by assocValue greater then
     * the given Parameters
     */
    public RuleMinerStatistic getRuleMinerStatistics(Integer tenantId, Integer minAssocValue1, Integer minAssocValue2,
                                                     Integer minAssocValue3, Integer minAssocValue4) {

        String sql = new StringBuilder().append(" SELECT * ").append(" FROM ")
                .append(" (SELECT COUNT(g.itemfromid) as itemswithRules FROM (SELECT distinct itemfromid FROM itemassoc WHERE tenantid = ?) g) g1 JOIN  ")
                .append(" (SELECT COUNT(g.itemfromid) as itemsWithMinAssocValue1 FROM (SELECT distinct itemfromid FROM itemassoc WHERE tenantid = ? AND assocValue >= ")
                .append(minAssocValue1).append("  ) g) g2 JOIN ")
                .append(" (SELECT COUNT(g.itemfromid) as itemsWithMinAssocValue2 FROM (SELECT distinct itemfromid FROM itemassoc WHERE tenantid = ? AND assocValue >= ")
                .append(minAssocValue2).append("  ) g) g3 JOIN ")
                .append(" (SELECT COUNT(g.itemfromid) as itemsWithMinAssocValue3 FROM (SELECT distinct itemfromid FROM itemassoc WHERE tenantid = ? AND assocValue >= ")
                .append(minAssocValue3).append("  ) g) g4 JOIN ")
                .append(" (SELECT COUNT(g.itemfromid) as itemsWithMinAssocValue4 FROM (SELECT distinct itemfromid FROM itemassoc WHERE tenantid = ? AND assocValue >= ")
                .append(minAssocValue4).append("  ) g) g5 ").toString();

        RuleMinerStatistic r = getJdbcTemplate()
                .queryForObject(sql, new Object[]{tenantId, tenantId, tenantId, tenantId, tenantId},
                        new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER},
                        ruleMinerStatisticRowMapper);

        return r;
    }

    /**
     * Return Conversion statistics = The number of item that where bought because
     * they were clicked in a recommendation before.
     */
    public ConversionStatistic getConversionStatistics(Integer tenantId, Integer buyActionTypeId) {

        if (tenantId != null && buyActionTypeId != null) {

            String sql = new StringBuilder().append(" SELECT COUNT(1) AS recommendationToBuyCount FROM ( ")
                    .append("    SELECT ")
                    .append("        itemId, userid ")
                    .append(" FROM ")
                    .append("    action WHERE itemTypeId = 1 AND actionTypeId = ? AND tenantid = ?) a ")
                    .append(" INNER JOIN ")
                    .append("   (SELECT itemToId, userid FROM backtracking WHERE tenantid= ?) b ")
                    .append(" ON (a.itemId = b.itemToId AND a.userid= b.userid) ").toString();

            ConversionStatistic c = getJdbcTemplate()
                    .queryForObject(sql, new Object[]{buyActionTypeId, tenantId, tenantId},
                            new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER

                            }, conversionStatisticRowMapper);
            return c;
        } else return new ConversionStatistic(0);
    }

    /**
     * Returns the number of actions for the current month
     */
    public Integer getMonthlyActions(Integer tenantId) {
        if (tenantId != null) {

            return getJdbcTemplate().queryForInt(
                    " SELECT COUNT(1) FROM action WHERE MONTH(actionTime) = MONTH(now()) AND YEAR(actionTime) = YEAR(now()) AND tenantid = ? ",
                    new Object[]{tenantId}, new int[]{Types.INTEGER});
        } else return 0;
    }


    private static class AssocStatisticRowMapper implements RowMapper<AssocStatistic> {
        public AssocStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new AssocStatistic(rs.getInt("actions"), rs.getInt("rules"), rs.getInt("itemsWithRules"),
                    rs.getInt("averageNumberOfRulesPerItem"), rs.getInt("stdNumberOfRulesPerItem"));
        }
    }

    private static class TenantStatisticRowMapper implements RowMapper<TenantStatistic> {
        public TenantStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new TenantStatistic(rs.getInt("actions"), rs.getInt("backtracks"), rs.getInt("items"),
                    rs.getInt("users"), rs.getFloat("average_actions_per_user"),
                    rs.getFloat("recommendation_coverage"));
        }
    }

    private static class UserStatisticRowMapper implements RowMapper<UserStatistic> {
        public UserStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new UserStatistic(rs.getInt("users_with_1_action"), rs.getInt("users_with_2_actions"),
                    rs.getInt("users_with_3_10_actions"), rs.getInt("users_with_11_100_actions"),
                    rs.getInt("users_with_101_and_more_actions"));
        }
    }

    private static class RuleMinerStatisticRowMapper implements RowMapper<RuleMinerStatistic> {
        public RuleMinerStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new RuleMinerStatistic(rs.getInt("itemswithRules"), rs.getInt("itemsWithMinAssocValue1"),
                    rs.getInt("itemsWithMinAssocValue2"), rs.getInt("itemsWithMinAssocValue3"),
                    rs.getInt("itemsWithMinAssocValue4"));
        }
    }

    private static class ConversionStatisticRowMapper implements RowMapper<ConversionStatistic> {
        public ConversionStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new ConversionStatistic(rs.getInt("recommendationToBuyCount"));
        }
    }
}