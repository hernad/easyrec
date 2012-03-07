/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.model.web.Item;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.model.web.statistics.ItemDetails;
import org.easyrec.store.dao.web.ItemDAO;
import org.easyrec.utils.Web;
import org.easyrec.utils.io.MySQL;
import org.easyrec.utils.io.Text;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.util.HtmlUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;


/**
 * The Class implements the ItemDAO.
 * <p/>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2007
 * <p/>
 * <b>last modified:</b><br/> $Author: szavrel $<br/> $Date: 2008-07-17
 * 20:00:46 +0200 (Do, 17 Jul 2008) $<br/> $Revision: 18665 $
 * </p>
 *
 * @author <AUTHOR>
 */

public class ItemDAOMysqlImpl extends BasicDAOMysqlImpl implements ItemDAO {
    private static final String SQL_GET_ITEM;
    private static final String SQL_GET_ITEM_BY_ID;
    private static final String SQL_ADD_ITEM;
    private static final String SQL_INSERT_OR_UPDATE_ITEM;
    private static final String SQL_ACTIVATE_ITEM;
    private static final String SQL_DEACTIVATE_ITEM;
    private static final String SQL_REMOVE_ITEM;
    private static final String SQL_REMOVE_ITEMS;
    private static final String SQL_HOT_ITEMS;
    private static final String SQL_SEARCH_ITEMS_START;
    private static final String SQL_SEARCH_ITEMS_COUNT_START;
    private static final StringBuilder SQL_GET_ITEMS;
    private static final String SQL_GET_ITEMTYPE_OF_ITEM;
    private static final int[] ARGTYPES_ITEM_KEY;
    private static final int[] ARGTYPES_ITEM_ID;
    private static final int[] ARGTYPES_ADD_ITEM;
    private static final int[] ARGTYPES_INSERT_OR_UPDATE_ITEM;

    private static final PreparedStatementCreatorFactory PS_ADD_ITEM;
    private static final PreparedStatementCreatorFactory PS_GET_ITEM;
    private static final PreparedStatementCreatorFactory PS_GET_ITEM_BY_ID;
    @SuppressWarnings({"UnusedDeclaration"})
    private static final PreparedStatementCreatorFactory PS_INSERT_OR_UPDATE_ITEM;

    private ItemRowMapper itemRowMapper = new ItemRowMapper();
    private ItemDetailsRowMapper itemDetailsRowMapper = new ItemDetailsRowMapper();

    private Cache cache;
    
    private HashMap<String, Item> itemCache = new HashMap<String, Item>();


    static {

        SQL_ADD_ITEM = new StringBuilder().append(" INSERT INTO ").append(DEFAULT_TABLE_NAME)
                .append("    (TENANTID, ITEMID, ITEMTYPE, DESCRIPTION, URL, IMAGEURL) VALUES ")
                .append("    (?,?,?,?,?,?) ").toString();
        //.append(" ON DUPLICATE KEY UPDATE ")
        //.append("    DESCRIPTION = ?, ")
        //.append("    URL = ? ");

        SQL_INSERT_OR_UPDATE_ITEM = new StringBuilder().append(" INSERT INTO ").append(DEFAULT_TABLE_NAME)
                .append("    (TENANTID, ITEMID, ITEMTYPE, DESCRIPTION, URL, IMAGEURL) VALUES ")
                .append("    (?,?,?,?,?,?) ").append(" ON DUPLICATE KEY UPDATE ").append("    DESCRIPTION = ?, ")
                .append("    URL = ?, ").append("    IMAGEURL = ? ").toString();

        SQL_GET_ITEM = new StringBuilder().append(" SELECT ")
                .append("    ID, TENANTID, ITEMID, ITEMTYPE, DESCRIPTION, URL, IMAGEURL, ACTIVE, CREATIONDATE ")
                .append(" FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ")
                .append("    TENANTID = ? AND ITEMID = ? AND ITEMTYPE = ?").toString();

        SQL_GET_ITEM_BY_ID = new StringBuilder().append(" SELECT ")
                .append("    ID, TENANTID, ITEMID, ITEMTYPE, DESCRIPTION, URL, IMAGEURL, ACTIVE, CREATIONDATE ")
                .append(" FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ").append("    ID = ?").toString();


        SQL_HOT_ITEMS = new StringBuilder().append("SELECT ").append("   i.*, count(1) as value ").append(" FROM ")
                .append("   backtracking b INNER JOIN idmapping m ON (b.itemFromId = m.intId) ")
                .append(" INNER JOIN item i ON (i.itemid = m.stringId AND i.tenantId = b.tenantId) ")
                .append(" WHERE b.tenantId = ? ").append(" GROUP BY b.itemFromId, b.tenantId  ")
                .append(" ORDER BY value DESC ").toString();


        SQL_ACTIVATE_ITEM = new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME).append(" SET active = 1 ")
                .append(" WHERE TENANTID = ? AND ").append("       ITEMID   = ? AND ").append("       ITEMTYPE = ? ")
                .toString();

        SQL_DEACTIVATE_ITEM = new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME)
                .append(" SET active = 0 ").append(" WHERE TENANTID = ? AND ").append("       ITEMID   = ? AND ")
                .append("       ITEMTYPE = ? ").toString();

        SQL_GET_ITEMS = new StringBuilder().append(" SELECT ")
                .append("    ID, TENANTID, ITEMID, ITEMTYPE, DESCRIPTION, URL, IMAGEURL, ACTIVE, CREATIONDATE ")
                .append(" FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ").append("    TENANTID = ? ")
                .append("    AND ITEMTYPE = ? ");
        //.append(" ORDER BY ")
        //.append("    ITEMID ")

        SQL_REMOVE_ITEM = new StringBuilder().append(" DELETE FROM ").append(DEFAULT_TABLE_NAME)
                .append(" WHERE TENANTID = ? AND ").append("       ITEMID   = ? AND ").append("       ITEMTYPE = ? ")
                .toString();

        SQL_REMOVE_ITEMS = new StringBuilder().append(" DELETE FROM ").append(DEFAULT_TABLE_NAME)
                .append(" WHERE TENANTID = ? ").toString();

        SQL_SEARCH_ITEMS_START =
                "SELECT id, tenantId, itemId, itemType, description, url, imageUrl, active, creationDate FROM " +
                        DEFAULT_TABLE_NAME + " WHERE tenantId = ?";

        SQL_SEARCH_ITEMS_COUNT_START =
                "SELECT count(1) FROM " + DEFAULT_TABLE_NAME + " WHERE tenantId = ?";


        SQL_GET_ITEMTYPE_OF_ITEM = "SELECT id FROM itemtype WHERE tenantId=? AND name=(SELECT distinct itemtype from item where tenantId=? and itemid=(select stringId from idmapping where intId = ?))";


        ARGTYPES_ITEM_KEY = new int[]{Types.INTEGER, Types.VARCHAR, Types.VARCHAR};
        ARGTYPES_ITEM_ID = new int[]{Types.INTEGER};
        ARGTYPES_ADD_ITEM = new int[]{Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR};
        ARGTYPES_INSERT_OR_UPDATE_ITEM = new int[]{Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};

        PS_ADD_ITEM = new PreparedStatementCreatorFactory(SQL_ADD_ITEM, ARGTYPES_ADD_ITEM);

        PS_INSERT_OR_UPDATE_ITEM = new PreparedStatementCreatorFactory(SQL_INSERT_OR_UPDATE_ITEM,
                ARGTYPES_INSERT_OR_UPDATE_ITEM);


        PS_ADD_ITEM.setReturnGeneratedKeys(true);

        PS_GET_ITEM = new PreparedStatementCreatorFactory(SQL_GET_ITEM, ARGTYPES_ITEM_KEY);

        PS_GET_ITEM_BY_ID = new PreparedStatementCreatorFactory(SQL_GET_ITEM_BY_ID, ARGTYPES_ITEM_ID);
    }

    public ItemDAOMysqlImpl(DataSource dataSource) {
        super(dataSource);
        this.setTableId(DEFAULT_TABLE_KEY);
        this.setTableName(DEFAULT_TABLE_NAME);

    }

    private static String makeCacheKey(Integer tenantId, String itemType, String itemId) {
        return new StringBuilder().append(tenantId).append(DELIMITER).append(itemId).append(DELIMITER)
                .append(itemType).toString();
    }

    @Override
    public Item add(Integer tenantId, String itemId, String itemType, String itemDescription, String url,
                    String imageurl) {
        try {
            Object[] args = {tenantId, itemId, itemType, HtmlUtils.htmlEscape(itemDescription), Web.makeUrlSecure(url),
                    Web.makeUrlSecure(imageurl)};

            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(PS_ADD_ITEM.newPreparedStatementCreator(args), keyHolder);

            return new Item(keyHolder.getKey().toString(), tenantId, itemId, itemType,
                    HtmlUtils.htmlEscape(itemDescription), Web.makeUrlSecure(url), Web.makeUrlSecure(imageurl),
                    null, true, new Date().toString());

        } catch (Exception e) {
            logger.error("An error occured adding an item!", e);
            return null;
        }

    }

    @Override
    public Item insertOrUpdate(Integer tenantId, String itemId, String itemType, String itemDescription, String url,
                               String imageurl) {
        Object[] args = {tenantId, itemId, itemType, itemDescription, url, imageurl, itemDescription, url, imageurl};

        try {
            getJdbcTemplate().update(SQL_INSERT_OR_UPDATE_ITEM, args, ARGTYPES_INSERT_OR_UPDATE_ITEM);
            
            cache.remove(makeCacheKey(tenantId, itemType, itemId));
//            itemCache.remove(makeCacheKey(tenantId, itemType, itemId));
            return null;
            /*return new Item(keyHolder.getKey().toString(),
           tenantId,
           itemId,
           itemType,
           itemDescription,
           url,
           imageurl,
           null,
           true,
           new Date().toString());*/
        } catch (Exception e) {
            if (logger.isDebugEnabled()) logger.debug("failed to update item or failed to remove it from the cache", e);
            return null;
        }

    }

    @Override
    public Item get(Integer id) {
        try {
            return getJdbcTemplate().query(
                    PS_GET_ITEM_BY_ID.newPreparedStatementCreator(new Object[]{id}), itemRowMapper).get(0);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) logger.debug("failed to get item with id " + id, e);
            return null;
        }
    }

    //@ShortCacheable
    @Override
    public Item get(RemoteTenant remoteTenant, String itemId, String itemType) {
        String cacheId = makeCacheKey(remoteTenant.getId(), itemType, itemId);

        Element e = cache.get(cacheId);

//        Item i = itemCache.get(cacheId);

        if (e != null) {
            return (Item) e.getValue();
        } else {
            Object[] args = {remoteTenant.getId(), itemId, itemType};

            try {
                Item i = getJdbcTemplate().query(PS_GET_ITEM.newPreparedStatementCreator(args), itemRowMapper).get(0);
                i.setUrl(Text.matchMax(remoteTenant.getUrl(), i.getUrl()));
                i.setImageUrl(Text.matchMax(remoteTenant.getUrl(), i.getImageUrl()));
                cache.put(new Element(cacheId, i));
//                itemCache.put(cacheId, i);

                return i;
            } catch (Exception ex) {
                if (logger.isDebugEnabled())
                    logger.debug("failed to get item or failed to insert it into the cache", ex);
                return null;
            }
        }
    }

    @Override
    public boolean exists(RemoteTenant remoteTenant, String itemId, String itemType) {
        return get(remoteTenant, itemId, itemType) != null;
    }

    @Override
    public void activate(Integer tenantId, String itemId, String itemType) {
        Object[] args = {tenantId, itemId, itemType};

        try {
            getJdbcTemplate().update(SQL_ACTIVATE_ITEM, args, ARGTYPES_ITEM_KEY);
            cache.remove(makeCacheKey(tenantId, itemType, itemId));
//            itemCache.remove(makeCacheKey(tenantId, itemType, itemId));

        } catch (Exception e) {
            if (logger.isDebugEnabled())
                logger.debug("failed to activate item or failed to remove it from the cache", e);
        }
    }

    @Override
    public void deactivate(Integer tenantId, String itemId, String itemType) {
        Object[] args = {tenantId, itemId, itemType};

        try {
            getJdbcTemplate().update(SQL_DEACTIVATE_ITEM, args, ARGTYPES_ITEM_KEY);
            cache.remove(makeCacheKey(tenantId, itemType, itemId));
//            itemCache.remove(makeCacheKey(tenantId, itemType, itemId));

        } catch (Exception e) {
            if (logger.isDebugEnabled())
                logger.debug("failed to deactivate item or failed to remove it from the cache", e);
        }
    }

    @Override
    public List<Item> getItems(RemoteTenant remoteTenant, String description, int start, int end) {
        List<Item> items;

        Object[] args = {remoteTenant.getId(), Item.DEFAULT_STRING_ITEM_TYPE};
        int[] argTypes = {Types.INTEGER, Types.VARCHAR};

        StringBuilder sql = new StringBuilder(SQL_GET_ITEMS);
        sql = MySQL.addLikeClause(sql, "DESCRIPTION", description);
        sql = MySQL.addLimitClause(sql, start, end);

        try {
            items = getJdbcTemplate().query(sql.toString(), args, argTypes, itemRowMapper);

            for (final Object item1 : items) {
                Item item = (Item) item1;
                item.setUrl(Text.matchMax(remoteTenant.getUrl(), item.getUrl()));
                item.setImageUrl(Text.matchMax(remoteTenant.getUrl(), item.getImageUrl()));
            }

            return items;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void remove(Integer tenantId, String itemId, String itemType) {
        Object[] args = {tenantId, itemId, itemType};

        try {
            getJdbcTemplate().update(SQL_REMOVE_ITEM, args, ARGTYPES_ITEM_KEY);
            cache.remove(makeCacheKey(tenantId, itemType, itemId));
//            itemCache.remove(makeCacheKey(tenantId, itemType, itemId));
        } catch (Exception e) {
            if (logger.isDebugEnabled()) logger.debug("failed to remove item from db or cache", e);
        }
    }

    @Override
    public void removeItems(Integer tenantId) {
        Object[] args = {tenantId};
        int[] argTypes = {Types.INTEGER};

        try {
            getJdbcTemplate().update(SQL_REMOVE_ITEMS, args, argTypes);
            cache.removeAll();
//            itemCache.clear();

        } catch (Exception e) {
            if (logger.isDebugEnabled()) logger.debug("failed to remove items from db or cache", e);
        }
    }

    @Override
    public ItemDetails getItemDetails(Integer tenantId, String itemId, String itemType) {
        String sql = new StringBuilder().append(" SELECT * FROM (").append("   SELECT")
                .append("       i.stringId as itemId, ").append("       a.itemTypeId, ").append("       a.tenantId,")
                .append("       MIN(a.actionTime) as minActionTime, ")
                .append("       MAX(a.actionTime) as maxActionTime,").append("       COUNT(distinct a.id) as actions,")
                .append("       COUNT(distinct a.userId) as users    ").append("   FROM ")
                .append("      action a INNER JOIN     ").append("      idmapping i ON (")
                .append("          i.intId = a.itemId AND").append("          i.stringId = ? ").append("      )     ")
                .append("   GROUP BY ").append("      a.itemId, a.itemTypeId, a.tenantId")
                .append(" ) a INNER JOIN itemtype t ON (").append("        t.id   = a.itemTypeId AND ")
                .append("        t.name = ? AND ").append("        t.tenantid = ? ").append(" ) ").append(" WHERE")
                .append("    a.tenantid = ? ").toString();

        try {
            return getJdbcTemplate().queryForObject(sql, new Object[]{itemId, itemType, tenantId, tenantId},
                    new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER}, itemDetailsRowMapper);
        } catch (EmptyResultDataAccessException e) {
            if (logger.isDebugEnabled()) logger.debug("no item with specified id found.");
            return null;
        } catch (Exception e) {
            logger.error("failed to get item details", e);
            return null;
        }
    }

    @Override
    public List<Item> getItemsWithRules(Integer tenantId, String description, int start, int end) {
        StringBuilder sql = new StringBuilder().append(" SELECT ").append(" 	i.* ").append(" FROM  ")
                .append(" 	item i INNER JOIN ( ")
                .append(" 	SELECT stringid FROM (SELECT distinct itemFromId FROM itemassoc WHERE tenantid = ?");

        if (Strings.isNullOrEmpty(description))
            sql = MySQL.addLimitClause(sql, start, end);

        sql.append(") a INNER JOIN idmapping m ON (m.intId = a.itemFromId)) a ")
                .append("   	ON (i.itemid = stringid AND tenantid=?) ").append("  WHERE 1=1");
        sql = MySQL.addLikeClause(sql, "i.DESCRIPTION", description);

        if (!Strings.isNullOrEmpty(description))
            sql = MySQL.addLimitClause(sql, start, end);

        try {
            return getJdbcTemplate()
                    .query(sql.toString(), new Object[]{tenantId, tenantId}, new int[]{Types.INTEGER, Types.INTEGER},
                            itemRowMapper);
        } catch (Exception e) {
            logger.error("failed to get items with rules", e);
            return null;
        }
    }

    @Override
    public int getNumberOfItemsWithRules(Integer tenantId, String description) {
        if (Strings.isNullOrEmpty(description)) {
            try {
                return getJdbcTemplate()
                        .queryForInt("select count(distinct itemfromid) from itemassoc where tenantid = ? ",
                                new Object[]{tenantId}, new int[]{Types.INTEGER});
            } catch (Exception e) {
                logger.debug(e);
                return -1;
            }
        } else return -1;

        /*  use this statement if searchquery with descirption should return
         * the exact number of results.   TODO: dm  !?!??! remove unused code!
        StringBuilder sql = new StringBuilder()
        .append("SELECT  ")
        .append("	Count(1) ")
        .append("FROM  ")
        .append("	(SELECT ")
        .append("        itemId,")
        .append("        itemtype,")
        .append("        tenantid")
        .append("    FROM")
        .append("    item WHERE 1=1 ");

        sql = MySQL.addLikeClause(sql, "DESCRIPTION", description);

        sql.append("    ")
        .append("    ) i INNER JOIN ( ")
        .append("	SELECT  ")
        .append("	  i.stringId AS itemId, ")
        .append("      tf.name as itemType, ")
        .append("      ia.tenantId ")
        .append("    FROM  ")
        .append("      itemassoc ia INNER JOIN ")
        .append("      itemtype tf ON (tf.id = ia.itemFromTypeId AND tf.name='ITEM') INNER JOIN ")
        .append("      idmapping i ON (i.intId = ia.itemFromId) ")
        .append("    WHERE  ")
        .append("      ia.tenantId = ? ")
        .append("    GROUP BY ")
        .append("      i.stringId, ")
        .append("      tf.name, ")
        .append("      ia.tenantId        ")
        .append("  ) a ON ( ")
        .append("               i.itemId = a.itemId AND  ")
        .append("               i.itemType = a.itemType AND ")
        .append("               i.tenantid = a.tenantId) ");
        try {
            return getJdbcTemplate().queryForInt(
                    sql.toString(),
                    new Object[] {tenantId },
                    new int[]    {Types.INTEGER });
        } catch (Exception e) {
           logger.debug(e);
           return -1;
        }*/
    }

    @Override
    public List<Item> getHotItems(RemoteTenant remoteTenant, Integer start, Integer end) {
        List<Item> items;

        Object[] args = {remoteTenant.getId()};
        int[] argTypes = {Types.INTEGER};

        try {
            items = getJdbcTemplate()
                    .query(new StringBuilder(SQL_HOT_ITEMS).append(" LIMIT ").append(start).append(", ").append(end)
                            .toString(), args, argTypes, itemRowMapper);
            for (final Object item1 : items) {
                Item item = (Item) item1;
                item.setUrl(Text.matchMax(remoteTenant.getUrl(), item.getUrl()));
                item.setImageUrl(Text.matchMax(remoteTenant.getUrl(), item.getImageUrl()));
            }
            return items;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Integer count(Integer tenantId, String description) {
        StringBuilder sql = new StringBuilder().
                append(" SELECT Count(1) FROM ").
                append(DEFAULT_TABLE_NAME).
                append(" WHERE tenantId= ? ");

        Object[] args = {tenantId};
        int[] argTypes = {Types.INTEGER};

        sql = MySQL.addLikeClause(sql, "DESCRIPTION", description);

        try {
            return getJdbcTemplate().queryForInt(sql.toString(), args, argTypes);
        } catch (Exception e) {
            logger.debug("failed to get count", e);
            return 0;
        }
    }

    private String replaceWildcard(String searchStringWithWildcards) {
        String result = searchStringWithWildcards;

        // if no wildcard was used search for containment
        if (!result.contains("*") && !result.contains("?"))
            result = "*" + result + "*";

        // escape sql wildcards
        result = result.replace("%", "\\%");
        result = result.replace("_", "\\_");

        // then replace wildcards with sql wildcards
        result = result.replace('*', '%');
        result = result.replace('?', '_');

        return result;
    }

    @Override
    public List<Item> searchItems(int tenantId, String itemId, Iterable<String> itemTypes, String description,
                                  String url, String imageUrl, Boolean active, TimeConstraintVO creationDateConstraint,
                                  Boolean hasRules, String rulesOfType, SortColumn sortColumn, boolean sortDescending,
                                  Integer offset, Integer itemCount) {
        boolean queryForRules = Objects.firstNonNull(hasRules, false);
        StringBuilder query = new StringBuilder();
        List<Object> args = Lists.newArrayList();
        List<Integer> argt = Lists.newArrayList();
        String sortOrder = sortDescending ? "DESC" : "ASC";
        String reverseSortOrder = sortDescending ? "ASC" : "DESC";

        if (!queryForRules) {
            query.append(SQL_SEARCH_ITEMS_START);
            args.add(tenantId);
            argt.add(Types.INTEGER);

            generateSearchQuery(itemId, itemTypes, description, url, imageUrl, active, creationDateConstraint,
                    query, args, argt);
        } else {
            generateSearchQueryWithRules(query, args, argt, tenantId, itemId, itemTypes, description, url, imageUrl,
                    active, creationDateConstraint, rulesOfType, true);
        }

        switch (sortColumn) {
            case ITEM_ID:
                query.append("\nORDER BY LENGTH(itemId) ")
                        .append(sortOrder)
                        .append(", itemId ")
                        .append(sortOrder);

                break;
            case ITEM_TYPE:
                query.append("\nORDER BY itemType ")
                        .append(sortOrder);

                break;
            case DESCRIPTION:
                query.append("\nORDER BY description ")
                        .append(sortOrder);

                break;
            case NONE:
            default:
                break;
        }

        if (offset != null && itemCount != null) {
            query.append(" LIMIT ?,?");

            args.add(offset);
            argt.add(Types.INTEGER);

            args.add(itemCount);
            argt.add(Types.INTEGER);
        }

        return getJdbcTemplate().query(query.toString(), args.toArray(), Ints.toArray(argt), itemRowMapper);
    }

    @Override
    public int searchItemsTotalCount(int tenantId, String itemId, Iterable<String> itemTypes, String description,
                                     String url, String imageUrl, Boolean active,
                                     TimeConstraintVO creationDateConstraint, Boolean hasRules, String rulesOfType) {
        boolean queryForRules = Objects.firstNonNull(hasRules, false);
        StringBuilder query = new StringBuilder();
        List<Object> args = Lists.newArrayList();
        List<Integer> argt = Lists.newArrayList();

        if (!queryForRules) {
            query.append(SQL_SEARCH_ITEMS_COUNT_START);
            args.add(tenantId);
            argt.add(Types.INTEGER);

            generateSearchQuery(itemId, itemTypes, description, url, imageUrl, active, creationDateConstraint,
                    query, args, argt);
        } else {
            query.append("SELECT COUNT(*)\n");
            query.append("FROM (\n");
            generateSearchQueryWithRules(query, args, argt, tenantId, itemId, itemTypes, description, url, imageUrl,
                    active, creationDateConstraint, rulesOfType, false);
            query.append(") TEMP");

        }

        return getJdbcTemplate().queryForInt(query.toString(), args.toArray(), Ints.toArray(argt));
    }

    @Override
    public int getItemTypeIdOfItem(Integer tenantId, Integer itemId) {
        Object[] args = new Object[]{tenantId, tenantId, itemId};
        int[] argTypes = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER};

        return getJdbcTemplate().queryForInt(SQL_GET_ITEMTYPE_OF_ITEM, args, argTypes);
    }

    private void generateSearchQueryWithRules(StringBuilder query, List<Object> args, List<Integer> argt,
                                              Integer tenantId, String itemId, Iterable<String> itemTypes,
                                              String description, String url, String imageUrl, Boolean active,
                                              TimeConstraintVO creationDateConstraint, String rulesOfType,
                                              boolean selectAll) {
        query.append("SELECT i.id\n");

        if (selectAll) {
            query.append("  , i.tenantId\n");
            query.append("  , i.itemid\n");
            query.append("  , i.itemtype\n");
            query.append("  , i.description\n");
            query.append("  , i.url\n");
            query.append("  , i.imageurl\n");
            query.append("  , i.active\n");
            query.append("  , i.creationdate\n");
        }

        query.append("FROM itemassoc a\n");
        query.append("  INNER JOIN itemtype t\n");
        query.append("    ON ( t.tenantid = a.tenantid\n");
        query.append("         AND t.id = a.itemfromtypeid )\n");
        query.append("  INNER JOIN idmapping m\n");
        query.append("    ON ( a.itemfromid = m.intid )\n");
        query.append("  INNER JOIN item i\n");
        query.append("    ON ( a.tenantid = i.tenantid\n");
        query.append("         AND m.stringid = i.itemid\n");
        query.append("         AND t.name = i.itemtype )\n");
        query.append("  INNER JOIN assoctype\n");
        query.append("    ON ( assoctype.tenantid = a.tenantid\n");
        query.append("         AND assoctype.id= a.assoctypeid )\n");
        query.append("WHERE\n");
        query.append("  a.tenantid = ?\n");
        args.add(tenantId);
        argt.add(Types.INTEGER);

        if (!Strings.isNullOrEmpty(rulesOfType)) {
            query.append("  AND assoctype.name LIKE ?");
            args.add(replaceWildcard(rulesOfType));
            argt.add(Types.VARCHAR);
        }

        generateSearchQuery(itemId, itemTypes, description, url, imageUrl, active, creationDateConstraint, query,
                args, argt, "i.");

        query.append("GROUP BY i.id");
    }

    private void generateSearchQuery(String itemId, Iterable<String> itemTypes, String description,
                                     String url, String imageUrl, Boolean active,
                                     TimeConstraintVO creationDateConstraint, StringBuilder query, List<Object> args,
                                     List<Integer> argt) {
        generateSearchQuery(itemId, itemTypes, description, url, imageUrl, active, creationDateConstraint, query,
                args, argt, "");
    }

    private void generateSearchQuery(String itemId, Iterable<String> itemTypes, String description,
                                     String url, String imageUrl, Boolean active,
                                     TimeConstraintVO creationDateConstraint, StringBuilder query, List<Object> args,
                                     List<Integer> argt, String fieldPrefix) {
        if (itemId != null) {
            query.append(" AND ").append(fieldPrefix).append("itemId LIKE ?");

            args.add(replaceWildcard(itemId));
            argt.add(Types.VARCHAR);
        }

        if (itemTypes != null && itemTypes.iterator().hasNext()) {
            query.append(" AND (");

            for (String itemType : itemTypes) {
                query.append(fieldPrefix).append("itemType = ? OR ");
                args.add(itemType);
                argt.add(Types.VARCHAR);
            }

            query.append("FALSE)");
        }

        if (description != null) {
            query.append(" AND ").append(fieldPrefix).append("description LIKE ?");

            args.add(replaceWildcard(description));
            argt.add(Types.VARCHAR);
        }

        if (url != null) {
            query.append(" AND ").append(fieldPrefix).append("url LIKE ?");

            args.add(replaceWildcard(url));
            argt.add(Types.VARCHAR);
        }

        if (imageUrl != null) {
            query.append(" AND ").append(fieldPrefix).append("imageUrl LIKE ?");

            args.add(replaceWildcard(imageUrl));
            argt.add(Types.VARCHAR);
        }

        if (active != null) {
            query.append(" AND ").append(fieldPrefix).append("active = ?");

            args.add(active);
            argt.add(Types.TINYINT);
        }

        if (creationDateConstraint != null) {
            if (creationDateConstraint.getDateFrom() != null && creationDateConstraint.getDateTo() != null) {
                query.append(" AND ").append(fieldPrefix).append("creationDate BETWEEN ? AND ?");

                args.add(creationDateConstraint.getDateFrom());
                argt.add(Types.TIMESTAMP);
                args.add(creationDateConstraint.getDateTo());
                argt.add(Types.TIMESTAMP);
            } else if (creationDateConstraint.getDateFrom() != null) {
                query.append(" AND ").append(fieldPrefix).append("creationDate >= ?");

                args.add(creationDateConstraint.getDateFrom());
                argt.add(Types.TIMESTAMP);
            } else if (creationDateConstraint.getDateTo() != null) {
                query.append(" AND ").append(fieldPrefix).append("creationDate <= ?");

                args.add(creationDateConstraint.getDateTo());
                argt.add(Types.TIMESTAMP);
            } else
                throw new IllegalArgumentException("creationDateConstraint must have either dateFrom or dateTo set.");
        }
    }

    @Override
    public Integer count(Integer tenantId) {
        return count(tenantId, null);
    }

    @Override
    public void emptyCache() {
        cache.removeAll();
//        itemCache.clear();
    }

    private static class ItemRowMapper implements RowMapper<Item> {
        @Override
        public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Item(DaoUtils.getStringIfPresent(rs, DEFAULT_ID_COLUMN_NAME),
                    DaoUtils.getIntegerIfPresent(rs, DEFAULT_TENANTID_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_ITEMID_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_ITEMTYPE_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_DESCRIPTION_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_URL_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_IMAGEURL_COLUMN_NAME),
                    DaoUtils.getDoubleIfPresent(rs, DEFAULT_VALUE_COLUMN_NAME),
                    DaoUtils.getBoolean(rs, DEFAULT_ACTIVE_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_CREATION_DATE_COLUMN_NAME));
        }
    }

    private static class ItemDetailsRowMapper implements RowMapper<ItemDetails> {
        @Override
        public ItemDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ItemDetails(DaoUtils.getStringIfPresent(rs, DEFAULT_ITEMID_COLUMN_NAME),
                    DaoUtils.getIntegerIfPresent(rs, DEFAULT_ITEMTYPE_COLUMN_NAME),
                    DaoUtils.getIntegerIfPresent(rs, DEFAULT_TENANTID_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, "minActionTime"), DaoUtils.getStringIfPresent(rs, "maxActionTime"),
                    DaoUtils.getIntegerIfPresent(rs, "actions"), DaoUtils.getIntegerIfPresent(rs, "users"));
        }
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }
    
    
}