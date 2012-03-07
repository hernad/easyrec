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
package org.easyrec.mahout.store.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.easyrec.mahout.store.MahoutDataModelMappingDAO;
import org.easyrec.mahout.store.iterator.LongResultSetIteratorMysql;
import org.easyrec.store.dao.BaseActionDAO;
import org.easyrec.utils.spring.cache.annotation.ShortCacheable;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.Date;

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
 * @author David Mann
 */
@DAO
public class MahoutDataModelMappingDAOMysqlImpl extends JdbcDaoSupport implements MahoutDataModelMappingDAO {


    FastIDSetExtractor fastIDSetExtractor = new FastIDSetExtractor();
    GenericPreferenceRowMapper genericPreferenceRowMapper = new GenericPreferenceRowMapper();
    GenericBooleanPreferenceRowMapper genericBooleanPreferenceRowMapper = new GenericBooleanPreferenceRowMapper();

    private final String getUserIDsQuery = MessageFormat.format(
            "SELECT DISTINCT {0}  FROM {1} WHERE  {2} = ? AND {3} <= ? AND {4} = ?",
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String getPreferencesFromUserQuery = MessageFormat.format(
            "SELECT DISTINCT {0}, {1} ,{2}  FROM {3} WHERE {4} = ? AND {5} <= ? AND {6} = ? AND {7} = ?",
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME,
            BaseActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String getItemIDsFromUserQuery = MessageFormat.format(
            "SELECT DISTINCT {0} FROM {1} WHERE {2} = ? AND {3} <= ? and {4} = ? AND {5} = ?",
            BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String getItemIDsQuery = MessageFormat.format(
            "SELECT DISTINCT {0} FROM {1} WHERE {2} = ? AND {3} <= ? AND {4} = ?",
            BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String getPreferencesForItemQuery = MessageFormat.format(
            "SELECT {0}, {1} ,{2} FROM {3} WHERE {4} = ? AND {5} <= ? AND {6}=?  AND {7} = ? ORDER BY {6}",
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME,
            BaseActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String getPreferenceQuery = MessageFormat.format(
            "SELECT {0} FROM {1} WHERE {2} = ? AND {5} <= ? AND {3}=? AND {4}=? AND {6} = ? ORDER BY {5} DESC LIMIT 1",
            BaseActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String getBooleanPreferenceQuery = MessageFormat.format(
            "SELECT COUNT(*) FROM {0} WHERE {1} = ? AND {4} <= ? AND {2}=? AND {3}=? AND {5} = ? ORDER BY {4} DESC LIMIT 1",
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String getPreferenceTimeQuery = MessageFormat.format(
            "SELECT {0} FROM {1} WHERE {2} = ? AND {0} <= ? AND {3}=? AND {4}=? AND {5} = ? ORDER BY {0} DESC LIMIT 1",
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String getNumUsersQuery = MessageFormat.format(
            "SELECT COUNT(DISTINCT {0}) FROM {1} WHERE {2} = ? AND {3} <= ? AND {4} = ?",
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String getNumItemsQuery = MessageFormat.format(
            "SELECT COUNT(DISTINCT {0}) FROM {1} WHERE {2} = ? AND {3} <= ? AND {4} = ?",
            BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String getNumUsersWithPreferenceForQuery = MessageFormat.format(
            "SELECT COUNT(DISTINCT {0},{1}) FROM {2} WHERE {3} = ? AND {4} <= ? AND {1} = ? AND {5} = ?",
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String getNumUsersWithPreferenceForTwoQuery = MessageFormat.format(
            "SELECT COUNT(*) FROM (SELECT {0} FROM {2} WHERE {3} = ? AND {4} <= ? AND ({1} = ? OR {1} = ?) AND {5} = ? group by {0} having count(distinct {1}) = 2) as mycount",
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String hasPreferenceValuesQuery = MessageFormat.format(
            "SELECT (COUNT(DISTINCT {0},{1}) > 0) FROM {2} WHERE {3} = ? AND {4} <= ? AND {5} = ?",
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ITEM_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    private final String userExistsQuery = MessageFormat.format(
            "Select count(*) from {0} where {1}=? and {2}=? AND {3} <= ?  AND {4} = ?",
            BaseActionDAO.DEFAULT_TABLE_NAME,
            BaseActionDAO.DEFAULT_USER_COLUMN_NAME,
            BaseActionDAO.DEFAULT_TENANT_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME,
            BaseActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME
    );

    public MahoutDataModelMappingDAOMysqlImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @ShortCacheable
    @Override
    public LongPrimitiveIterator getUserIDs(int tenantId, Date cutoffDate, int actionTypeId) {
        Object[] args = new Object[]{tenantId, cutoffDate, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER};
        return new LongResultSetIteratorMysql(getDataSource(), getUserIDsQuery, args, argTypes);
    }


    @Override
    public PreferenceArray getPreferencesFromUser(int tenantId, Date cutoffDate, long userID, int actionTypeId) throws TasteException {
        Object[] args = new Object[]{tenantId, cutoffDate, userID, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER};
        try {
            return new GenericUserPreferenceArray(getJdbcTemplate().query(getPreferencesFromUserQuery, args, argTypes, genericPreferenceRowMapper));
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchUserException(userID);
        }
    }

    @Override
    public PreferenceArray getBooleanPreferencesFromUser(int tenantId, Date cutoffDate, long userID, int actionTypeId) throws TasteException {
        Object[] args = new Object[]{tenantId, cutoffDate, userID, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER};
        try {
            return new GenericUserPreferenceArray(getJdbcTemplate().query(getPreferencesFromUserQuery, args, argTypes, genericBooleanPreferenceRowMapper));
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchUserException(userID);
        }
    }

    @Override
    public FastIDSet getItemIDsFromUser(int tenantId, Date cutoffDate, long userID, int actionTypeId) throws TasteException {
        Object[] args = new Object[]{tenantId, cutoffDate, userID, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER};

        try {
            return getJdbcTemplate().query(getItemIDsFromUserQuery, args, argTypes, fastIDSetExtractor);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchUserException(userID);
        }
    }

    @ShortCacheable
    @Override
    public LongPrimitiveIterator getItemIDs(int tenantId, Date cutoffDate, int actionTypeId) {
        Object[] args = new Object[]{tenantId, cutoffDate, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER};

        return new LongResultSetIteratorMysql(getDataSource(), getItemIDsQuery, args, argTypes);
    }

    @Override
    public PreferenceArray getPreferencesForItem(int tenantId, Date cutoffDate, long itemID, int actionTypeId) throws TasteException {
        Object[] args = new Object[]{tenantId, cutoffDate, itemID, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER};

        try {
            return new GenericItemPreferenceArray(getJdbcTemplate().query(getPreferencesForItemQuery, args, argTypes, genericPreferenceRowMapper));
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchItemException(itemID);
        }
    }

    @Override
    public PreferenceArray getBooleanPreferencesForItem(int tenantId, Date cutoffDate, long itemID, int actionTypeId) throws TasteException {
        Object[] args = new Object[]{tenantId, cutoffDate, itemID, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER};

        try {
            return new GenericItemPreferenceArray(getJdbcTemplate().query(getPreferencesForItemQuery, args, argTypes, genericBooleanPreferenceRowMapper));
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchItemException(itemID);
        }
    }

    @Override
    public Float getPreferenceValue(int tenantId, Date cutoffDate, long userID, long itemID, int actionTypeId) throws TasteException {
        Object[] args = new Object[]{tenantId, cutoffDate, userID, itemID, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        try {
            return (Float) getJdbcTemplate().queryForObject(getPreferenceQuery, args, argTypes, Float.class);
        } catch (EmptyResultDataAccessException e) {
            //as mahout/taste doesn't catch the NoSuchUserException, we don't throw it to save time
            return null;
        }
    }

    @Override
    public Float getBooleanPreferenceValue(int tenantId, Date cutoffDate, long userID, long itemID, int actionTypeId) throws TasteException {
        Object[] args = new Object[]{tenantId, cutoffDate, userID, itemID, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        int numberOfActions = getJdbcTemplate().queryForInt(getBooleanPreferenceQuery, args, argTypes);
        if (numberOfActions == 0) {
            //as mahout/taste doesn't catch the NoSuchUserException, we don't throw it to save time
            return null;
        } else {
            return 1f;
        }
    }

    @Override
    public Long getPreferenceTime(int tenantId, Date cutoffDate, long userID, long itemID, int actionTypeId) throws TasteException {
        Object[] args = new Object[]{tenantId, cutoffDate, userID, itemID, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.INTEGER};
        try {
            return getJdbcTemplate().queryForObject(getPreferenceTimeQuery, args, argTypes, Date.class).getTime();
        } catch (EmptyResultDataAccessException e) {
            //as mahout/taste doesn't catch the NoSuchUserException, we don't throw it to save time
            return null;
        }
    }

    @ShortCacheable
    @Override
    public int getNumItems(int tenantId, Date cutoffDate, int actionTypeId) {
        Object[] args = new Object[]{tenantId, cutoffDate, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER};

        return getJdbcTemplate().queryForInt(getNumItemsQuery, args, argTypes);
    }

    @ShortCacheable
    @Override
    public int getNumUsers(int tenantId, Date cutoffDate, int actionTypeId) {
        Object[] args = new Object[]{tenantId, cutoffDate, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER};

        return getJdbcTemplate().queryForInt(getNumUsersQuery, args, argTypes);
    }

    @Override
    public int getNumUsersWithPreferenceFor(int tenantId, Date cutoffDate, long itemID, int actionTypeId) {
        Object[] args = new Object[]{tenantId, cutoffDate, itemID, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER};

        return getJdbcTemplate().queryForInt(getNumUsersWithPreferenceForQuery, args, argTypes);
    }

    @Override
    public int getNumUsersWithPreferenceFor(int tenantId, Date cutoffDate, long itemID1, long itemID2, int actionTypeId) {
        Object[] args = new Object[]{tenantId, cutoffDate, itemID1, itemID2, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        return getJdbcTemplate().queryForInt(getNumUsersWithPreferenceForTwoQuery, args, argTypes);
    }

    @Override
    public boolean hasPreferenceValues(int tenantId, Date cutoffDate, int actionTypeId) {
        Object[] args = new Object[]{tenantId, cutoffDate, actionTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.INTEGER};

        return (getJdbcTemplate().queryForInt(hasPreferenceValuesQuery, args, argTypes) == 1);
    }

    @Override
    public float getMaxPreference(int tenantId, Date cutoffDate, int actionTypeId) {
        return 10f;
        // TODO : SELECT MIN(ratingValue) FROM ACTION
        // WHERE tenantId = 45 AND actionTime <=
        // "2011-06-18 21:12:21" AND ratingValue != "";
    }

    @Override
    public float getMinPreference(int tenantId, Date cutoffDate, int actionTypeId) {
        return 0f;
    }

    private boolean userExists(int tenantId, Date cutoffDate, long userID, int itemTypeId) {
        Object[] args = new Object[]{userID, tenantId, cutoffDate, itemTypeId};
        int[] argTypes = new int[]{Types.INTEGER, Types.INTEGER, Types.TIMESTAMP, Types.INTEGER};

        return getJdbcTemplate().queryForInt(userExistsQuery, args, argTypes) != 0;
    }


    /*
       Here you will find the RowMapper & DataSet Extractors used to handle the data relieved from the sql requests.
     */


    /**
     * This DataSet Extractor is used to Map the mysql result to the FastIDSet Java object.
     */
    private static class FastIDSetExtractor implements ResultSetExtractor<FastIDSet> {

        // logging
        private final Log logger = LogFactory.getLog(this.getClass());

        @Override
        public FastIDSet extractData(ResultSet rs) {
            FastIDSet result = new FastIDSet();

            try {

                while (rs.next()) {
                    result.add(rs.getLong(1));
                }
            } catch (SQLException e) {
                logger.error("An error occured during FastIDSet ResultSet extraction", e);
                throw new RuntimeException(e);
            }

            return result;
        }

    }

    /**
     * This RowMapper is used to Map the mysql result Row's to the GenericPreference Java object.
     */
    private class GenericPreferenceRowMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new GenericPreference(rs.getLong(1), rs.getLong(2), rs.getFloat(3));
        }
    }

    /**
     * This RowMapper is used to Map the mysql result Row's to the GenericPreference Java object.
     * its a special implementation to handle boolean recommender which only have BUY VIEW Actions.
     */
    private class GenericBooleanPreferenceRowMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new GenericPreference(rs.getLong(1), rs.getLong(2), 1.0f);
        }
    }

}
