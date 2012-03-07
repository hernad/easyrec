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
package org.easyrec.plugin.arm.store.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ItemVO;
import org.easyrec.store.dao.BaseActionDAO;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import javax.xml.bind.annotation.XmlElement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.easyrec.plugin.arm.TupleCounter;
import org.easyrec.plugin.arm.model.ARMConfigurationInt;
import org.easyrec.plugin.arm.model.ARMStatistics;
import org.easyrec.plugin.arm.model.TupleVO;
import org.easyrec.plugin.arm.store.dao.RuleminingActionDAO;

/**
 * This class provides methods to access data in a datamining/rulemining database.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2006</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-11 18:35:47 +0100 (Fr, 11 Feb 2011) $<br/>
 * $Revision: 17681 $</p>
 *
 * @author Stephan Zavrel
 */
@DAO
public class RuleminingActionDAOMysqlImpl extends JdbcDaoSupport implements RuleminingActionDAO {

    private static class ActionResultSetExtractor
            implements ResultSetExtractor<TObjectIntHashMap<ItemVO<Integer, Integer>>> {

        private int minSupp;
        // logging
        private final Log logger = LogFactory.getLog(this.getClass());

        public TObjectIntHashMap<ItemVO<Integer, Integer>> extractData(ResultSet rs) {
            TObjectIntHashMap<ItemVO<Integer, Integer>> map = new TObjectIntHashMap<ItemVO<Integer, Integer>>();
            int itemId, itemTypeId, tenantId, cnt = 0;

            try {
                while (rs.next()) {
                    itemId = rs.getInt(BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME);
                    itemTypeId = rs.getInt(BaseActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME);
                    tenantId = rs.getInt(BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME);
                    cnt = rs.getInt("cnt");
                    map.put(new ItemVO<Integer, Integer>(tenantId, itemId, itemTypeId), cnt);
                }
                // optimization: replaces former adjustSupport method
                minSupp = cnt;
            } catch (SQLException e) {
                logger.error("An error occured during ResultSet extraction", e);
                throw new RuntimeException(e);
            }
            return map;
        }

        public Integer getMinSupp() {
            return this.minSupp;
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // constructor
    public RuleminingActionDAOMysqlImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public Integer getNumberOfBaskets(Integer tenantId, Integer actionType, Double ratingNeutral, List<Integer> itemTypes) {
        List<Object> args = Lists.newArrayList();
        List<Integer> argt = Lists.newArrayList();


        StringBuilder query = new StringBuilder("SELECT count(DISTINCT userId) as cnt FROM ");
        query.append(BaseActionDAO.DEFAULT_TABLE_NAME);
        query.append(" WHERE ").append(BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME).append("=")
                .append(tenantId);
        query.append(" AND ").append(BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append("=")
                .append(actionType);

        if (ratingNeutral != null) {
            query.append(" AND ").append(BaseActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME).append(">")
                    .append(ratingNeutral);
        }

        if (!itemTypes.isEmpty()) {
            query.append(" AND ").append(BaseActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" IN (");

            for (int i = 0; i < itemTypes.size(); i++) {
                query.append("?");
                args.add(itemTypes.get(i));
                argt.add(Types.INTEGER);

                if (i < itemTypes.size() - 1) {
                    query.append(",");
                } else {
                    query.append(")");
                }
            }
        }
        return getJdbcTemplate().queryForObject(query.toString(), args.toArray(), Ints.toArray(argt),
                Integer.class);
    }

    /**
     * Number of baskets excluding single item baskets
     *
     * @param analysis anlysis
     * @return int
     */
    public Integer getNumberOfBasketsESIB(Integer tenantId, Integer actionType, Double ratingNeutral, List<Integer> itemTypes) {
        List<Object> args = Lists.newArrayList();
        List<Integer> argt = Lists.newArrayList();

        StringBuilder query = new StringBuilder(
                "SELECT count(b.userId) FROM (SELECT userId, count(userId) as cnt FROM ");
        query.append(BaseActionDAO.DEFAULT_TABLE_NAME);
        query.append(" WHERE ").append(BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME).append("=")
                .append(tenantId);
        query.append(" AND ").append(BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append("=")
                .append(actionType);

        if (ratingNeutral != null) {
            query.append(" AND ").append(BaseActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME).append(">")
                    .append(ratingNeutral);
        }

        if (!itemTypes.isEmpty()) {
            query.append(" AND ").append(BaseActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" IN (");

            for (int i = 0; i < itemTypes.size(); i++) {
                query.append("?");
                args.add(itemTypes.get(i));
                argt.add(Types.INTEGER);
                if (i < itemTypes.size() - 1) {
                    query.append(",");
                } else {
                    query.append(")");
                }
            }
        }
        query.append(" GROUP BY userId HAVING cnt>1) b");

        return getJdbcTemplate().queryForObject(query.toString(), args.toArray(), Ints.toArray(argt), Integer.class);
    }

    public int getNumberOfProducts(Integer tenantId, Integer actionType, Double ratingNeutral, List<Integer> itemTypes) {
        List<Object> args = Lists.newArrayList();
        List<Integer> argt = Lists.newArrayList();

        StringBuilder query = new StringBuilder("SELECT count(DISTINCT itemId, itemTypeId) as cnt FROM ");
        query.append(BaseActionDAO.DEFAULT_TABLE_NAME);
        query.append(" WHERE ").append(BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME).append("=")
                .append(tenantId);
        query.append(" AND ").append(BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append("=")
                .append(actionType);

        if (ratingNeutral != null) {
            query.append(" AND ").append(BaseActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME).append(">")
                    .append(ratingNeutral);
        }

        if (!itemTypes.isEmpty()) {
            query.append(" AND ").append(BaseActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" IN (");

            for (int i = 0; i < itemTypes.size(); i++) {
                query.append("?");
                args.add(itemTypes.get(i));
                argt.add(Types.INTEGER);

                if (i < itemTypes.size() - 1) {
                    query.append(",");
                } else {
                    query.append(")");
                }
            }
        }
        return getJdbcTemplate().queryForObject(query.toString(), args.toArray(), Ints.toArray(argt), Integer.class);
    }


    public TObjectIntHashMap<ItemVO<Integer, Integer>> defineL1(ARMConfigurationInt configuration) {
        ActionResultSetExtractor rse = new ActionResultSetExtractor();

        List<Object> args = Lists.newArrayList();
        List<Integer> argt = Lists.newArrayList();

        StringBuilder query = new StringBuilder("SELECT ");
        query.append(BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME).append(", ");
        query.append(BaseActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(", ");
        query.append(BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME).append(", ");
        query.append("count(*) as cnt FROM ");
        query.append(BaseActionDAO.DEFAULT_TABLE_NAME);
        query.append(" WHERE ").append(BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME).append("=")
                .append(configuration.getTenantId());
        query.append(" AND ").append(BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append("=")
                .append(configuration.getActionType());

        if (configuration.getRatingNeutral() != null) {
            query.append(" AND ").append(BaseActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME).append(">")
                    .append(configuration.getRatingNeutral());
        }
        
        if (!configuration.getItemTypes().isEmpty()) {
        query.append(" AND ").append(BaseActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" IN (");

            for (int i = 0; i < configuration.getItemTypes().size(); i++) {
                query.append("?");
                args.add(configuration.getItemTypes().get(i));
                argt.add(Types.INTEGER);
                if (i < configuration.getItemTypes().size() - 1) {
                    query.append(",");
                } else {
                    query.append(")");
                }
            }
        }
        query.append(" GROUP BY ").append(BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME).append(",").append(BaseActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME)
                .append(" HAVING cnt >= ? ORDER BY cnt DESC");

        args.add(configuration.getSupport());
        argt.add(Types.INTEGER);

        query.append(" LIMIT ?");

        args.add(configuration.getMaxSizeL1());
        argt.add(Types.INTEGER);
        
        TObjectIntHashMap<ItemVO<Integer, Integer>> ret = getJdbcTemplate()
                .query(query.toString(),  args.toArray(), Ints.toArray(argt), rse);

        if (ret.size() == configuration.getMaxSizeL1()) configuration.setSupport(rse.getMinSupp());

        return ret;
    }

    public List<TupleVO> defineL2(TObjectIntHashMap<ItemVO<Integer, Integer>> L1,
                                  TupleCounter tupleCounter,
                                  ARMConfigurationInt configuration,
                                  ARMStatistics stats) {

        List<TupleVO> ret = null;

        List<Object> args = Lists.newArrayList();
        List<Integer> argt = Lists.newArrayList();

        RowMapper<ItemVO<Integer, Integer>> itemVOMapper = new RowMapper<ItemVO<Integer, Integer>>() {
            public ItemVO<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new ItemVO<Integer, Integer>(rs.getInt(BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME),
                        rs.getInt(BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME),
                        rs.getInt(BaseActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME));
            }
        };

        try {
            // get all Baskets with at least 2 items
            StringBuilder query = new StringBuilder();
            query.append("SELECT ").append(BaseActionDAO.DEFAULT_USER_COLUMN_NAME);
            query.append(" FROM ").append(BaseActionDAO.DEFAULT_TABLE_NAME);
            query.append(" WHERE ").append(BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME).append("=")
                    .append(configuration.getTenantId());
            query.append(" AND ").append(BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append("=")
                    .append(configuration.getActionType());

            if (configuration.getRatingNeutral() != null) {
                query.append(" AND ").append(BaseActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME).append(">")
                        .append(configuration.getRatingNeutral());
            }

            if (!configuration.getItemTypes().isEmpty()) {
                query.append(" AND ").append(BaseActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" IN (");

                for (int i = 0; i < configuration.getItemTypes().size(); i++) {
                    query.append("?");
                    args.add(configuration.getItemTypes().get(i));
                    argt.add(Types.INTEGER);
                    if (i < configuration.getItemTypes().size() - 1) {
                        query.append(",");
                    } else {
                        query.append(")");
                    }
                }
            }
            query.append(" GROUP BY ").append(BaseActionDAO.DEFAULT_USER_COLUMN_NAME);
            query.append(" HAVING count(*)>=2");

            List<Integer> baskets = getJdbcTemplate().queryForList(query.toString(), args.toArray(),
                    Ints.toArray(argt), Integer.class);

            for (Integer basket : baskets) {
                StringBuilder query2 = new StringBuilder();
                query2.append("SELECT DISTINCT ");
                query2.append(BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME).append(", ");
                query2.append(BaseActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(", ");
                query2.append(BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME);
                query2.append(" FROM ").append(BaseActionDAO.DEFAULT_TABLE_NAME);
                query2.append(" WHERE ").append(BaseActionDAO.DEFAULT_USER_COLUMN_NAME).append("=").append(basket);
                query2.append(" AND ").append(BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME).append("=")
                        .append(configuration.getTenantId());
                query2.append(" AND ").append(BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append("=")
                        .append(configuration.getActionType());

                if (configuration.getRatingNeutral() != null) {
                    query2.append(" AND ").append(BaseActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME).append(">")
                            .append(configuration.getRatingNeutral());
                }

                if (!configuration.getItemTypes().isEmpty()) {
                    query2.append(" AND ").append(BaseActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" IN (");

                    for (int i = 0; i < configuration.getItemTypes().size(); i++) {
                        query2.append(configuration.getItemTypes().get(i));
                        if (i < configuration.getItemTypes().size() - 1) {
                            query2.append(",");
                        } else {
                            query2.append(")");
                        }
                    }
                }

                List<ItemVO<Integer, Integer>> items = getJdbcTemplate()
                        .query(query2.toString(), itemVOMapper);

                ArrayList<ItemVO<Integer, Integer>> v = new ArrayList<ItemVO<Integer, Integer>>();

                for (ItemVO<Integer, Integer> itemVO : items) {
                    if (!L1.containsKey(itemVO)) {
                        continue;
                    }
                    v.add(itemVO);
                }

                if (v.size() <= 1) {
                    continue;
                }
                for (int i = 0; i < v.size() - 1; i++) {
                    for (int j = i + 1; j < v.size(); j++) {
                        tupleCounter.count(v.get(i), v.get(j));
                    }
                }
            }

            stats.setSizeCountMap(tupleCounter.size());
            ret = tupleCounter.getTuples(configuration.getSupport());

        } catch (Exception e) {
            logger.error(e);
        }

        return ret;
    }


    public int getCount(String tableName, String keyA, String keyB) {
        StringBuilder query = new StringBuilder("SELECT count(*) as cnt FROM ");
        query.append(tableName);
        query.append(" a, ");
        query.append(tableName);
        query.append(" b WHERE a.bId=b.bId AND a.prodId=? AND b.prodId=?");

        final Object[] args = {keyA, keyB};
        final int[] argTypes = {Types.VARCHAR, Types.VARCHAR};

        return getJdbcTemplate().queryForInt(query.toString(), args, argTypes);
    }

    public int getNumberOfActions(Integer tenantId, Integer actionType) {
        StringBuilder query = new StringBuilder("SELECT count(1) as cnt FROM ");
        query.append(BaseActionDAO.DEFAULT_TABLE_NAME);
        query.append(" WHERE ").append(BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME).append("=? ")
                .append(" AND ").append(BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append("=?");

        final Object[] args = {tenantId, actionType};
        final int[] argTypes = {Types.INTEGER, Types.INTEGER};

        return getJdbcTemplate().queryForInt(query.toString(), args, argTypes);
    }
}
