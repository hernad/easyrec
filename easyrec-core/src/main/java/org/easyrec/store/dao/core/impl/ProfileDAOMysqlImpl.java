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
package org.easyrec.store.dao.core.impl;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ItemVO;
import org.easyrec.store.dao.core.ProfileDAO;
import org.easyrec.store.dao.impl.AbstractBaseProfileDAOMysqlImpl;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author szavrel
 */
@DAO
public class ProfileDAOMysqlImpl extends AbstractBaseProfileDAOMysqlImpl<Integer, Integer, Integer>
        implements ProfileDAO {

    // constants
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/core/Profile.sql";
    private final String GET_PROFILE_BY_ID_QUERY = new StringBuilder("SELECT ").append(DEFAULT_PROFILE_DATA_COLUMN_NAME)
            .append(" FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ").append(DEFAULT_PROFILE_ID_COLUMN_NAME)
            .append("=?").toString();
    private final String UPDATE_PROFILE_BY_ID_QUERY = new StringBuilder("UPDATE ").append(DEFAULT_TABLE_NAME)
            .append(" SET ").append(DEFAULT_PROFILE_DATA_COLUMN_NAME).append(" =? WHERE ")
            .append(DEFAULT_PROFILE_ID_COLUMN_NAME).append(" =?").toString();
    private final String GET_PROFILE_QUERY = new StringBuilder("SELECT ").append(DEFAULT_PROFILE_DATA_COLUMN_NAME)
            .append(" FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ").append(DEFAULT_TENANT_ID_COLUMN_NAME)
            .append("=? AND ").append(DEFAULT_ITEM_ID_COLUMN_NAME).append("=? AND ")
            .append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME).append("=?").toString();
    private final String GET_ACTIVE_PROFILE_QUERY = new StringBuilder("SELECT ").append(DEFAULT_PROFILE_DATA_COLUMN_NAME)
            .append(" FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ").append(DEFAULT_TENANT_ID_COLUMN_NAME)
            .append("=? AND ").append(DEFAULT_ITEM_ID_COLUMN_NAME).append("=? AND ")
            .append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME).append("=? AND ").append(DEFAULT_ACTIVE_COLUMN_NAME).append("=?").toString();
    private final String STORE_PROFILE_QUERY = new StringBuilder("INSERT INTO ").append(DEFAULT_TABLE_NAME)
            .append(" SET ").append(DEFAULT_TENANT_ID_COLUMN_NAME).append(" =?, ").append(DEFAULT_ITEM_ID_COLUMN_NAME)
            .append(" =?, ").append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME).append(" =?, ")
            .append(DEFAULT_PROFILE_DATA_COLUMN_NAME).append(" =? ON DUPLICATE KEY UPDATE ")
            .append(DEFAULT_PROFILE_DATA_COLUMN_NAME).append(" =?").toString();
    private final String GET_DIM_VALUE_QUERY = new StringBuilder("SELECT  ExtractValue(")
            .append(DEFAULT_PROFILE_DATA_COLUMN_NAME).append(",?) FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ")
            .append(DEFAULT_TENANT_ID_COLUMN_NAME).append("=? AND ").append(DEFAULT_ITEM_ID_COLUMN_NAME)
            .append("=? AND ").append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME).append("=?").toString();
    private final String GET_DIM_VALUE_BY_ID_QUERY = new StringBuilder("SELECT  ExtractValue(")
            .append(DEFAULT_PROFILE_DATA_COLUMN_NAME).append(",?) FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ")
            .append(DEFAULT_PROFILE_ID_COLUMN_NAME).append("=?").toString();
    private final String SQL_ACTIVATE_PROFILE = new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME)
            .append(" SET ").append(DEFAULT_ACTIVE_COLUMN_NAME).append("=1 ").append(" WHERE ")
            .append(DEFAULT_TENANT_ID_COLUMN_NAME).append("=? AND ").append(DEFAULT_ITEM_ID_COLUMN_NAME)
            .append("=? AND ").append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME).append("=?").toString();
    private final String SQL_DEACTIVATE_PROFILE = new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME)
            .append(" SET ").append(DEFAULT_ACTIVE_COLUMN_NAME).append("=0 ").append(" WHERE ")
            .append(DEFAULT_TENANT_ID_COLUMN_NAME).append("=? AND ").append(DEFAULT_ITEM_ID_COLUMN_NAME)
            .append("=? AND ").append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME).append("=?").toString();
    private final String SQL_ACTIVATE_PROFILE_BY_ID = new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME)
            .append(" SET ").append(DEFAULT_ACTIVE_COLUMN_NAME).append("=1 ").append(" WHERE ")
            .append(DEFAULT_PROFILE_ID_COLUMN_NAME).append("=?").toString();
    private final String SQL_DEACTIVATE_PROFILE_BY_ID = new StringBuilder().append(" UPDATE ")
            .append(DEFAULT_TABLE_NAME).append(" SET ").append(DEFAULT_ACTIVE_COLUMN_NAME).append("=0 ")
            .append(" WHERE ").append(DEFAULT_PROFILE_ID_COLUMN_NAME).append("=?").toString();

    private final int[] ARGTYPES_PROFILE_KEY = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER};
    private final int[] ARGTYPES_PROFILE_ID = new int[]{Types.INTEGER};

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    private ItemVORowMapper itemRowMapper = new ItemVORowMapper();

    // constructor
    public ProfileDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
        // output connection information
        if (logger.isInfoEnabled()) {
            try {
                logger.info(DaoUtils.getDatabaseURLAndUserName(dataSource));
            } catch (Exception e) {
                logger.error(e);
            }
        }

    }

    @Override
    public String getDefaultTableName() {
        return DEFAULT_TABLE_NAME;
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SQL_SCRIPT_NAME;
    }

    public String getProfileById(Integer profileId) {

        if (profileId == null) {
            throw new IllegalArgumentException("profileId must not be 'null'!");
        }
        //
        //        StringBuilder sqlString = new StringBuilder("SELECT ");
        //        sqlString.append(DEFAULT_PROFILE_DATA_COLUMN_NAME);
        //        sqlString.append(" FROM ");
        //        sqlString.append(DEFAULT_TABLE_NAME);
        //        sqlString.append(" WHERE ");
        //        sqlString.append(DEFAULT_PROFILE_ID_COLUMN_NAME);
        //        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        args = new Object[]{profileId};
        argTypes = new int[]{Types.INTEGER};

        return getJdbcTemplate().queryForObject(GET_PROFILE_BY_ID_QUERY, args, argTypes, String.class);
    }

    public int updateProfileById(Integer profileId, String profileXML) {

        if (profileId == null) {
            throw new IllegalArgumentException("profileId must not be 'null'");
        }
        //        if (profileXML == null) {
        //            throw new IllegalArgumentException("domain must not be an empty String!");
        //        }
        //
        //        StringBuilder sqlString = new StringBuilder("UPDATE ");
        //        sqlString.append(DEFAULT_TABLE_NAME);
        //        sqlString.append(" SET ");
        //        sqlString.append(DEFAULT_PROFILE_DATA_COLUMN_NAME);
        //        sqlString.append(" =? WHERE ");
        //        sqlString.append(DEFAULT_PROFILE_ID_COLUMN_NAME);
        //        sqlString.append(" =?");


        Object[] args = {profileXML, profileId};

        int[] argTypes = {Types.BLOB, Types.INTEGER};

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(UPDATE_PROFILE_BY_ID_QUERY,
                argTypes);

        int rowsAffected = getJdbcTemplate().update(factory.newPreparedStatementCreator(args));
        return rowsAffected;
    }

    public String getProfile(Integer tenantId, Integer itemId, Integer itemTypeId, Boolean active) {
        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId must not be 'null'!");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("itemId must not be 'null'!");
        }
        if (itemTypeId == null) {
            throw new IllegalArgumentException("itemTypeId must not be 'null'");
        }

        //        StringBuilder sqlString = new StringBuilder("SELECT ");
        //        sqlString.append(DEFAULT_PROFILE_DATA_COLUMN_NAME);
        //        sqlString.append(" FROM ");
        //        sqlString.append(DEFAULT_TABLE_NAME);
        //        sqlString.append(" WHERE ");
        //        sqlString.append(DEFAULT_TENANT_ID_COLUMN_NAME);
        //        sqlString.append("=? AND ");
        //        sqlString.append(DEFAULT_ITEM_ID_COLUMN_NAME);
        //        sqlString.append("=? AND ");
        //        sqlString.append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME);
        //        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        if (active == null) {
            args = new Object[]{tenantId, itemId, itemTypeId};
            argTypes = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER};

            return getJdbcTemplate().queryForObject(GET_PROFILE_QUERY, args, argTypes, String.class);
        } else {
            args = new Object[]{tenantId, itemId, itemTypeId, active};
            argTypes = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.BOOLEAN};

            return getJdbcTemplate().queryForObject(GET_ACTIVE_PROFILE_QUERY, args, argTypes, String.class);
        }
    }
    
    public String getProfile(Integer tenantId, Integer itemId, Integer itemTypeId) {

        return getProfile(tenantId, itemId, itemTypeId, null);
    }

    public int storeProfile(Integer tenantId, Integer itemId, Integer itemTypeId, String profileXML) {


        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId must not be 'null'!");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("itemId must not be 'null'!");
        }
        if (itemTypeId == null) {
            throw new IllegalArgumentException("itemTypeId must not be 'null'");
        }
        //        if (profileXML == null) {
        //            throw new IllegalArgumentException("domain must not be an empty String!");
        //        }
        //
        //        StringBuilder sqlString = new StringBuilder("INSERT INTO ");
        //        sqlString.append(DEFAULT_TABLE_NAME);
        //        sqlString.append(" SET ");
        //        sqlString.append(DEFAULT_TENANT_ID_COLUMN_NAME);
        //        sqlString.append(" =?, ");
        //        sqlString.append(DEFAULT_ITEM_ID_COLUMN_NAME);
        //        sqlString.append(" =?, ");
        //        sqlString.append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME);
        //        sqlString.append(" =?, ");
        //        sqlString.append(DEFAULT_PROFILE_DATA_COLUMN_NAME);
        //        sqlString.append(" =? ON DUPLICATE KEY UPDATE ");
        //        sqlString.append(DEFAULT_PROFILE_DATA_COLUMN_NAME);
        //        sqlString.append(" =?");


        Object[] args = {tenantId, itemId, itemTypeId, profileXML, profileXML};

        int[] argTypes = {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.BLOB, Types.BLOB};

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(STORE_PROFILE_QUERY, argTypes);

        int rowsAffected = getJdbcTemplate().update(factory.newPreparedStatementCreator(args));
        return rowsAffected;
    }

    public void activateProfile(Integer tenant, Integer item, Integer itemType) {

        Object[] args = {tenant, item, itemType};

        try {
            getJdbcTemplate().update(SQL_ACTIVATE_PROFILE, args, ARGTYPES_PROFILE_KEY);
        } catch (Exception e) {
            logger.debug(e);
        }
    }

    public void activateProfile(Integer profileId) {

        Object[] args = {profileId};

        try {
            getJdbcTemplate().update(SQL_ACTIVATE_PROFILE_BY_ID, args, ARGTYPES_PROFILE_ID);
        } catch (Exception e) {
            logger.debug(e);
        }
    }

    public void deactivateProfile(Integer tenant, Integer item, Integer itemType) {

        Object[] args = {tenant, item, itemType};

        try {
            getJdbcTemplate().update(SQL_DEACTIVATE_PROFILE, args, ARGTYPES_PROFILE_KEY);
        } catch (Exception e) {
            logger.debug(e);
        }
    }

    public void deactivateProfile(Integer profileId) {
        Object[] args = {profileId};

        try {
            getJdbcTemplate().update(SQL_DEACTIVATE_PROFILE_BY_ID, args, ARGTYPES_PROFILE_ID);
        } catch (Exception e) {
            logger.debug(e);
        }
    }

    public Set<String> getMultiDimensionValue(Integer tenantId, Integer itemId, Integer itemTypeId,
                                              String dimensionXPath) {

        Set<String> ret = new HashSet<String>();

        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId must not be 'null'!");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("itemId must not be 'null'!");
        }
        if (itemTypeId == null) {
            throw new IllegalArgumentException("itemTypeId must not be 'null'");
        }

        //        StringBuilder sqlString = new StringBuilder("SELECT  ExtractValue(");
        //        sqlString.append(DEFAULT_PROFILE_DATA_COLUMN_NAME);
        //        sqlString.append(",?) FROM ");
        //        sqlString.append(DEFAULT_TABLE_NAME);
        //        sqlString.append(" WHERE ");
        //        sqlString.append(DEFAULT_TENANT_ID_COLUMN_NAME);
        //        sqlString.append("=? AND ");
        //        sqlString.append(DEFAULT_ITEM_ID_COLUMN_NAME);
        //        sqlString.append("=? AND ");
        //        sqlString.append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME);
        //        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        args = new Object[]{dimensionXPath, tenantId, itemId, itemTypeId};
        argTypes = new int[]{Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        String result = getJdbcTemplate().queryForObject(GET_DIM_VALUE_QUERY, args, argTypes, String.class);
        StringTokenizer st = new StringTokenizer(result, " ");

        while (st.hasMoreTokens()) {
            ret.add(st.nextToken());
        }

        return ret;
    }

    @Override
    public Set<String> getMultiDimensionValue(Integer profileId, String dimensionXPath) {
        Set<String> ret = new HashSet<String>();

        if (profileId == null) {
            throw new IllegalArgumentException("profileId must not be 'null'!");
        }

        //        StringBuilder sqlString = new StringBuilder("SELECT  ExtractValue(");
        //        sqlString.append(DEFAULT_PROFILE_DATA_COLUMN_NAME);
        //        sqlString.append(",?) FROM ");
        //        sqlString.append(DEFAULT_TABLE_NAME);
        //        sqlString.append(" WHERE ");
        //        sqlString.append(DEFAULT_PROFILE_ID_COLUMN_NAME);
        //        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        args = new Object[]{dimensionXPath, profileId};
        argTypes = new int[]{Types.VARCHAR, Types.INTEGER};

        String result = getJdbcTemplate().queryForObject(GET_DIM_VALUE_BY_ID_QUERY, args, argTypes, String.class);
        StringTokenizer st = new StringTokenizer(result, " ");

        while (st.hasMoreTokens()) {
            ret.add(st.nextToken());
        }

        return ret;
    }


    public String getSimpleDimensionValue(Integer tenantId, Integer itemId, Integer itemTypeId, String dimensionXPath) {

        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId must not be 'null'!");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("itemId must not be 'null'!");
        }
        if (itemTypeId == null) {
            throw new IllegalArgumentException("itemTypeId must not be 'null'");
        }

        //        StringBuilder sqlString = new StringBuilder("SELECT  ExtractValue(");
        //        sqlString.append(DEFAULT_PROFILE_DATA_COLUMN_NAME);
        //        sqlString.append(",?) FROM ");
        //        sqlString.append(DEFAULT_TABLE_NAME);
        //        sqlString.append(" WHERE ");
        //        sqlString.append(DEFAULT_TENANT_ID_COLUMN_NAME);
        //        sqlString.append("=? AND ");
        //        sqlString.append(DEFAULT_ITEM_ID_COLUMN_NAME);
        //        sqlString.append("=? AND ");
        //        sqlString.append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME);
        //        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        args = new Object[]{dimensionXPath, tenantId, itemId, itemTypeId};
        argTypes = new int[]{Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        return getJdbcTemplate().queryForObject(GET_DIM_VALUE_QUERY, args, argTypes, String.class);
    }

    @Override
    public String getSimpleDimensionValue(Integer profileId, String dimensionXPath) {

        if (profileId == null) {
            throw new IllegalArgumentException("profileId must not be 'null'");
        }

        Object[] args;
        int[] argTypes;

        args = new Object[]{dimensionXPath, profileId};
        argTypes = new int[]{Types.VARCHAR, Types.INTEGER};

        return getJdbcTemplate().queryForObject(GET_DIM_VALUE_BY_ID_QUERY, args, argTypes, String.class);
    }

    public List<ItemVO<Integer, Integer>> getItemsByDimensionValue(Integer tenantId, Integer itemType,
                                                                            String dimensionXPath, String value) {
        List<Object> args = Lists.newArrayList();
        List<Integer> argt = Lists.newArrayList();

        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_TENANT_ID_COLUMN_NAME).append(",");
        sqlString.append(DEFAULT_ITEM_ID_COLUMN_NAME).append(",");
        sqlString.append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");

        if (tenantId != null) {
            sqlString.append(DEFAULT_TENANT_ID_COLUMN_NAME).append("=? AND ");
            args.add(tenantId);
            argt.add(Types.INTEGER);
        }

        if (itemType != null) {
            sqlString.append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME).append("=? AND ");
            args.add(itemType);
            argt.add(Types.INTEGER);
        }

        sqlString.append("ExtractValue(").append(DEFAULT_PROFILE_DATA_COLUMN_NAME);
        sqlString.append(",?)=?");

        args.add(dimensionXPath);
        argt.add(Types.VARCHAR);
        args.add(value);
        argt.add(Types.VARCHAR);

        return getJdbcTemplate().query(sqlString.toString(), args.toArray(), Ints.toArray(argt), itemRowMapper);
    }

    public List<ItemVO<Integer, Integer>> getItemsByItemType(Integer tenantId, Integer itemType, int count) {

        if (itemType == null) {
            throw new IllegalArgumentException("itemType must not be 'null'");
        }

        List<Object> args = Lists.newArrayList((Object)itemType);
        List<Integer> argt = Lists.newArrayList(Types.INTEGER);

        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_TENANT_ID_COLUMN_NAME).append(",");
        sqlString.append(DEFAULT_ITEM_ID_COLUMN_NAME).append(",");
        sqlString.append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_ITEM_TYPE_ID_COLUMN_NAME);
        sqlString.append("=?");
        if (tenantId != null) {
            sqlString.append(" AND ").append(DEFAULT_TENANT_ID_COLUMN_NAME).append("=?");
            args.add(tenantId);
            argt.add(Types.INTEGER);
        }
        if (count != 0) {
            sqlString.append(" LIMIT ?");
            args.add(count);
            argt.add(Types.INTEGER);
        }

        return getJdbcTemplate().query(sqlString.toString(), args.toArray(), Ints.toArray(argt), itemRowMapper);
    }

    //////////////////////////////////////////////////////////////////////////////
    // private inner classes
    private class ItemVORowMapper implements RowMapper<ItemVO<Integer, Integer>> {
        public ItemVO<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
            ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(
                    DaoUtils.getInteger(rs, DEFAULT_TENANT_ID_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_ITEM_ID_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_ITEM_TYPE_ID_COLUMN_NAME));
            return item;
        }
    }


    //   select
    //  IF(ExtractValue(profileData,'count(/movie/burli)')=0,
    //  null,
    //  ExtractValue(profileData,'/movie/burli')) as val
    //    from profile where itemId=3 and itemTypeId=2;


}
