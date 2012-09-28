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
import com.google.common.base.Predicates;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import org.easyrec.store.dao.core.types.ItemTypeDAO;
import org.easyrec.utils.spring.cache.annotation.InvalidatesCache;
import org.easyrec.utils.spring.cache.annotation.LongCacheable;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class provides a Mysql implementation of the {@link org.easyrec.store.dao.core.types.ItemTypeDAO} interface.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2012-02-23 16:01:50 +0100 (Thu, 23 Feb 2012) $<br/>
 * $Revision: 140 $</p>
 *
 * @author Roman Cerny
 */

@DAO
public class ItemTypeDAOMysqlImpl extends AbstractTableCreatingDAOImpl implements ItemTypeDAO {
    // constants
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/core/types/ItemType.sql";

    // constructor 
    public ItemTypeDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);

        // output connection information
        if (logger.isInfoEnabled()) {
            try {
                logger.info(DaoUtils.getDatabaseURLAndUserName(dataSource));
            } catch (Exception e) {
                logger.error("failed to get database url & username", e);
            }
        }
    }

    // abstract template method of 'TableCreatingDAOImpl' implementation
    @Override
    public String getDefaultTableName() {
        return DEFAULT_TABLE_NAME;
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SQL_SCRIPT_NAME;
    }

    public String getTypeById(Integer tenantId, final Integer id) {
        return getTypeById(tenantId, id, null);
    }

    // interface 'ItemTypeDAO' implementation
    @LongCacheable
    public String getTypeById(Integer tenantId, final Integer id, Boolean visible) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");

        if (id == null)
            return null;

        ResultSetExtractor<String> rse = new ResultSetExtractor<String>() {
            public String extractData(ResultSet rs) {
                try {
                    if (rs.next()) {
                        return DaoUtils.getStringIfPresent(rs, DEFAULT_NAME_COLUMN_NAME);
                    }
                } catch (SQLException e) {
                    logger.error("error occured", e);
                    throw new RuntimeException(e);
                }
                throw new IllegalArgumentException("unknown id: '" + id + "' for item type");
            }
        };

        Object[] args = {tenantId, id};
        int[] argTypes = {Types.INTEGER, Types.INTEGER};

        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append(DEFAULT_NAME_COLUMN_NAME);
        sqlQuery.append(" FROM ");
        sqlQuery.append(DEFAULT_TABLE_NAME);
        sqlQuery.append(" WHERE ");
        sqlQuery.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlQuery.append("=? AND ");
        sqlQuery.append(DEFAULT_ID_COLUMN_NAME);
        sqlQuery.append("=?");

        if (visible != null) {
            sqlQuery.append(" AND ").append(DEFAULT_VISIBLE_COLUMN_NAME).append("=?");

            args = ObjectArrays.concat(args, visible);
            argTypes = Ints.concat(argTypes, new int[]{Types.BIT});
        }

        return getJdbcTemplate().query(sqlQuery.toString(), args, argTypes, rse);
    }

    public Integer getIdOfType(Integer tenantId, final String itemType) {
        return getIdOfType(tenantId, itemType, null);
    }

    public boolean isVisible(Integer tenantId, Integer id) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");
        Preconditions.checkNotNull(id, "missing constraints: missing 'id'");

        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append(DEFAULT_VISIBLE_COLUMN_NAME).append(" FROM ").append(DEFAULT_TABLE_NAME);
        sqlQuery.append(" WHERE ").append(DEFAULT_TENANT_COLUMN_NAME).append("=? AND ");
        sqlQuery.append(DEFAULT_ID_COLUMN_NAME).append("=?");

        Object[] args = {tenantId, id};
        int[] argt = {Types.INTEGER, Types.INTEGER};

        return getJdbcTemplate().queryForObject(sqlQuery.toString(), args, argt, Boolean.class);
    }

    public boolean isVisible(Integer tenantId, String itemType) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");
        Preconditions.checkNotNull(itemType, "missing constraints: missing 'itemType'");

        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append(DEFAULT_VISIBLE_COLUMN_NAME).append(" FROM ").append(DEFAULT_TABLE_NAME);
        sqlQuery.append(" WHERE ").append(DEFAULT_TENANT_COLUMN_NAME).append("=? AND ");
        sqlQuery.append(DEFAULT_NAME_COLUMN_NAME).append("=?");

        Object[] args = {tenantId, itemType};
        int[] argt = {Types.INTEGER, Types.VARCHAR};

        return getJdbcTemplate().queryForObject(sqlQuery.toString(), args, argt, Boolean.class);
    }

    @LongCacheable
    public Integer getIdOfType(Integer tenantId, final String itemType, Boolean visible) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");

        if (itemType == null)
            return null;

        ResultSetExtractor<Integer> rse = new ResultSetExtractor<Integer>() {
            public Integer extractData(ResultSet rs) {
                try {
                    if (rs.next()) {
                        return DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME);
                    }
                } catch (SQLException e) {
                    logger.error("error occured", e);
                    throw new RuntimeException(e);
                }
                throw new IllegalArgumentException("unknown item type: '" + itemType + "'");
            }
        };

        Object[] args = {tenantId, itemType};
        int[] argTypes = {Types.INTEGER, Types.VARCHAR};

        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append(DEFAULT_ID_COLUMN_NAME);
        sqlQuery.append(" FROM ");
        sqlQuery.append(DEFAULT_TABLE_NAME);
        sqlQuery.append(" WHERE ");
        sqlQuery.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlQuery.append("=? AND ");
        sqlQuery.append(DEFAULT_NAME_COLUMN_NAME);
        sqlQuery.append(" LIKE ?");

        if (visible != null) {
            sqlQuery.append(" AND ").append(DEFAULT_VISIBLE_COLUMN_NAME).append("=?");

            args = ObjectArrays.concat(args, visible);
            argTypes = Ints.concat(argTypes, new int[]{Types.BIT});
        }

        return getJdbcTemplate().query(sqlQuery.toString(), args, argTypes, rse);
    }

    public int insertOrUpdate(Integer tenantId, String itemType) {
        return insertOrUpdate(tenantId, itemType, (Boolean) null);
    }

    @InvalidatesCache
    public int insertOrUpdate(Integer tenantId, String itemType, Boolean visible) {
        Integer newId = existsType(tenantId, itemType);
        if (newId == null) {
            newId = getMaxItemTypeId(tenantId) + 1;
        }
        insertOrUpdate(tenantId, itemType, newId, visible);
        return newId;
    }

    public int insertOrUpdate(Integer tenantId, String itemType, Integer id) {
        return insertOrUpdate(tenantId, itemType, id, null);
    }

    @InvalidatesCache
    public int insertOrUpdate(Integer tenantId, String itemType, Integer id, Boolean visible) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");
        Preconditions.checkNotNull(itemType, "missing constraints: missing 'itemType'");
        Preconditions.checkNotNull(id, "missing constraints: missing 'id'");

        StringBuilder query;
        Object[] args;
        int[] argTypes;

        if (existsType(tenantId, itemType) == null) {
            query = new StringBuilder("INSERT INTO ");
            query.append(DEFAULT_TABLE_NAME);
            query.append(" SET ");
            query.append(DEFAULT_TENANT_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_NAME_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_ID_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_VISIBLE_COLUMN_NAME);
            query.append("=?");

            if (visible == null)
                visible = true;

            args = new Object[]{tenantId, itemType, id, visible};
            argTypes = new int[]{Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.BIT};
        } else {
            query = new StringBuilder("UPDATE ");
            query.append(DEFAULT_TABLE_NAME);
            query.append(" SET ");
            query.append(DEFAULT_TENANT_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_NAME_COLUMN_NAME);
            query.append("=?, ");
            query.append(DEFAULT_ID_COLUMN_NAME);
            query.append("=?");

            if (visible != null) {
                query.append(", ");
                query.append(DEFAULT_VISIBLE_COLUMN_NAME);
                query.append("=?");
                args = new Object[]{tenantId, itemType, id, visible, tenantId, itemType};
                argTypes =
                        new int[]{Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.BIT, Types.INTEGER, Types.VARCHAR};
            } else {
                args = new Object[]{tenantId, itemType, id, tenantId, itemType};
                argTypes = new int[]{Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.VARCHAR};
            }

            query.append(" WHERE ");
            query.append(DEFAULT_TENANT_COLUMN_NAME);
            query.append("=? AND ");
            query.append(DEFAULT_NAME_COLUMN_NAME);
            query.append("=?");
        }

        return getJdbcTemplate().update(query.toString(), args, argTypes);
    }

    public HashMap<String, Integer> getMapping(Integer tenantId) {
        return getMapping(tenantId, null);
    }

    @LongCacheable
    public HashMap<String, Integer> getMapping(Integer tenantId, Boolean visible) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");

        ResultSetExtractor<HashMap<String, Integer>> rse = new ResultSetExtractor<HashMap<String, Integer>>() {
            public HashMap<String, Integer> extractData(ResultSet rs) {
                HashMap<String, Integer> mapping = new HashMap<String, Integer>();
                try {
                    while (rs.next()) {
                        mapping.put(DaoUtils.getStringIfPresent(rs, DEFAULT_NAME_COLUMN_NAME),
                                DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME));
                    }
                    return mapping;
                } catch (SQLException e) {
                    logger.error("error occured", e);
                    throw new RuntimeException(e);
                }
            }
        };

        Object[] args = {tenantId};
        int[] argTypes = {Types.INTEGER};

        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append(DEFAULT_NAME_COLUMN_NAME);
        sqlQuery.append(", ");
        sqlQuery.append(DEFAULT_ID_COLUMN_NAME);
        sqlQuery.append(" FROM ");
        sqlQuery.append(DEFAULT_TABLE_NAME);
        sqlQuery.append(" WHERE ");
        sqlQuery.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlQuery.append("=?");

        if (visible != null) {
            sqlQuery.append(" AND ").append(DEFAULT_VISIBLE_COLUMN_NAME).append("=?");

            args = ObjectArrays.concat(args, visible);
            argTypes = Ints.concat(argTypes, new int[]{Types.BIT});
        }

        return getJdbcTemplate().query(sqlQuery.toString(), args, argTypes, rse);
    }

    public Set<String> getTypes(Integer tenantId) {
        return getTypes(tenantId, null);
    }

    @LongCacheable
    public Set<String> getTypes(Integer tenantId, Boolean visible) {
        Preconditions.checkNotNull(tenantId, "missing constraints: missing 'tenantId'");

        ResultSetExtractor<Set<String>> rse = new ResultSetExtractor<Set<String>>() {
            public Set<String> extractData(ResultSet rs) {
                Set<String> types = new TreeSet<String>();
                try {
                    while (rs.next()) {
                        types.add(DaoUtils.getStringIfPresent(rs, DEFAULT_NAME_COLUMN_NAME));
                    }
                    return types;
                } catch (SQLException e) {
                    logger.error("error occured", e);
                    throw new RuntimeException(e);
                }
            }
        };

        Object[] args = {tenantId};
        int[] argTypes = {Types.INTEGER};

        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append(DEFAULT_NAME_COLUMN_NAME);
        sqlQuery.append(" FROM ");
        sqlQuery.append(DEFAULT_TABLE_NAME);
        sqlQuery.append(" WHERE ");
        sqlQuery.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlQuery.append("=?");

        if (visible != null) {
            sqlQuery.append(" AND ").append(DEFAULT_VISIBLE_COLUMN_NAME).append("=?");

            args = ObjectArrays.concat(args, visible);
            argTypes = Ints.concat(argTypes, new int[]{Types.BIT});
        }

        sqlQuery.append(" ORDER BY ");
        sqlQuery.append(DEFAULT_NAME_COLUMN_NAME);

        Set<String> result =
                Sets.newTreeSet(getJdbcTemplate().queryForList(sqlQuery.toString(), args, argTypes, String.class));

        return Sets.newTreeSet(Sets.filter(result, Predicates.notNull()));
    }

    private int getMaxItemTypeId(Integer tenantId) {
        // validate
        if (tenantId == null) {
            throw new IllegalArgumentException("missing constraints: missing 'tenantId'");
        }

        ResultSetExtractor<Integer> rse = new ResultSetExtractor<Integer>() {
            public Integer extractData(ResultSet rs) {
                try {
                    if (rs.next()) {
                        return DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME);
                    }
                } catch (SQLException e) {
                    logger.error("error occured", e);
                    throw new RuntimeException(e);
                }
                throw new IllegalArgumentException("unknown tenant");
            }
        };

        StringBuilder sqlQuery = new StringBuilder("SELECT MAX(");
        sqlQuery.append(DEFAULT_ID_COLUMN_NAME);
        sqlQuery.append(") AS ");
        sqlQuery.append(DEFAULT_ID_COLUMN_NAME);
        sqlQuery.append(" FROM ");
        sqlQuery.append(DEFAULT_TABLE_NAME);
        sqlQuery.append(" WHERE ");
        sqlQuery.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlQuery.append("=?");

        Object[] args = {tenantId};
        int[] argTypes = {Types.INTEGER};

        Integer result = getJdbcTemplate().query(sqlQuery.toString(), args, argTypes, rse);

        return result == null ? 1 : result;
    }

    ///////////////////////////////////////////////////////////////////
    // protected methods
    protected Integer existsType(Integer tenantId, String itemType) {
        Integer id;
        try {
            id = getIdOfType(tenantId, itemType);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return id;
    }
}
