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
package org.easyrec.store.dao.core.types.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;
import org.easyrec.store.dao.core.types.ProfiledItemTypeDAO;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;

/**
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 16:46:14 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 113 $</p>
 *
 * @author szavrel
 */
@DAO
public class ProfiledItemTypeDAOMysqlImpl extends ItemTypeDAOMysqlImpl implements ProfiledItemTypeDAO {


    public ProfiledItemTypeDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService) {
        super(dataSource, sqlScriptService);
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

    public String getProfileMatcher(Integer tenantId, String itemType) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");
        Preconditions.checkNotNull(itemType, "missing constraints: missing 'itemType'");

        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_PROFILE_MATCHER_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_NAME_COLUMN_NAME);
        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        args = new Object[]{tenantId, itemType};
        argTypes = new int[]{Types.INTEGER, Types.VARCHAR};

        return getJdbcTemplate().queryForObject(sqlString.toString(), args, argTypes, String.class);
    }

    public String getProfileMatcher(Integer tenantId, Integer id) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");
        Preconditions.checkNotNull(id, "missing constraints: missing 'id'");

        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_PROFILE_MATCHER_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_ID_COLUMN_NAME);
        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        args = new Object[]{tenantId, id};
        argTypes = new int[]{Types.INTEGER, Types.INTEGER};

        return getJdbcTemplate().queryForObject(sqlString.toString(), args, argTypes, String.class);
    }

    public String getProfileSchema(Integer tenantId, String itemType) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");
        Preconditions.checkNotNull(itemType, "missing constraints: missing 'itemType'");

        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_PROFILE_SCHEMA_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_NAME_COLUMN_NAME);
        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        args = new Object[]{tenantId, itemType};
        argTypes = new int[]{Types.INTEGER, Types.VARCHAR};

        return getJdbcTemplate().queryForObject(sqlString.toString(), args, argTypes, String.class);

    }

    public String getProfileSchema(Integer tenantId, Integer id) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");
        Preconditions.checkNotNull(id, "missing constraints: missing 'id'");

        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_PROFILE_SCHEMA_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_ID_COLUMN_NAME);
        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        args = new Object[]{tenantId, id};
        argTypes = new int[]{Types.INTEGER, Types.INTEGER};

        return getJdbcTemplate().queryForObject(sqlString.toString(), args, argTypes, String.class);
    }

    public int insertOrUpdate(Integer tenantId, String itemType, Integer id, String profileSchema,
                              String profileMatcher) {
        return insertOrUpdate(tenantId, itemType, id, profileSchema, profileMatcher, null);
    }

    public int insertOrUpdate(Integer tenantId, String itemType, Integer id, String profileSchema,
                              String profileMatcher, Boolean visible) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");
        Preconditions.checkNotNull(itemType, "missing constraints: missing 'itemType'");
        Preconditions.checkNotNull(id, "missing constraints: missing 'id'");

        StringBuilder query = new StringBuilder();

        Object[] args;
        int[] argTypes;

        if (existsType(tenantId, itemType) == null) {
            if (visible == null)
                visible = true;

            args = new Object[]{tenantId, itemType, id, profileSchema, profileMatcher, visible};
            argTypes = new int[]{Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.BIT};

            query.append("INSERT INTO ");
            query.append(DEFAULT_TABLE_NAME);
            query.append(" SET ");
            query.append(DEFAULT_TENANT_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_NAME_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_ID_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_PROFILE_SCHEMA_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_PROFILE_MATCHER_COLUMN_NAME);
            query.append("=?,");
            query.append(DEFAULT_VISIBLE_COLUMN_NAME);
            query.append("=?");
        } else {
            args = new Object[]{tenantId, itemType, id, profileSchema, profileMatcher, tenantId, itemType};
            argTypes = new int[]{Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR,
                    Types.INTEGER, Types.VARCHAR};

            query.append("UPDATE ");
            query.append(DEFAULT_TABLE_NAME);
            query.append(" SET ");
            query.append(DEFAULT_TENANT_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_NAME_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_ID_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_PROFILE_SCHEMA_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_PROFILE_MATCHER_COLUMN_NAME);
            query.append("=?");

            if (visible != null) {
                query.append(", ");
                query.append(DEFAULT_VISIBLE_COLUMN_NAME);
                query.append("=?");
                args = new Object[]{tenantId, itemType, id, profileSchema, profileMatcher, visible, tenantId, itemType};
                argTypes =
                        new int[]{Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.BIT,
                                Types.INTEGER, Types.VARCHAR};
            } else {
                args = new Object[]{tenantId, itemType, id, profileSchema, profileMatcher, tenantId, itemType};
                argTypes = new int[]{Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR,
                        Types.INTEGER, Types.VARCHAR};
            }

            query.append(" WHERE ");
            query.append(DEFAULT_TENANT_COLUMN_NAME);
            query.append("=? AND ");
            query.append(DEFAULT_NAME_COLUMN_NAME);
            query.append("=?");
        }

        return getJdbcTemplate().update(query.toString(), args, argTypes);
    }

    public List<Integer> getTenantIds() {
        StringBuilder sqlString = new StringBuilder("SELECT DISTINCT ");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);

        return getJdbcTemplate().queryForList(sqlString.toString(), Integer.class);

    }

    public List<Integer> getItemTypeIds(Integer tenantId) {
        return getItemTypeIds(tenantId, null);
    }

    public List<Integer> getItemTypeIds(Integer tenantId, Boolean visible) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");

        Object[] args = new Object[]{tenantId};
        int[] argTypes = new int[]{Types.INTEGER};

        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_ID_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append(" =?");

        if (visible != null) {
            sqlString.append(" AND ").append(DEFAULT_VISIBLE_COLUMN_NAME).append("=?");

            args = ObjectArrays.concat(args, visible);
            argTypes = Ints.concat(argTypes, new int[]{Types.BIT});
        }

        return getJdbcTemplate().queryForList(sqlString.toString(), args, argTypes, Integer.class);
    }


}
