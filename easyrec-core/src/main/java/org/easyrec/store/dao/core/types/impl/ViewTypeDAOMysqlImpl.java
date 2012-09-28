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

import org.easyrec.store.dao.core.types.ViewTypeDAO;
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
 * This class provides a Mysql implementation of the {@link org.easyrec.store.dao.core.types.ViewTypeDAO} interface.
 * <p/>
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
 * @author Roman Cerny
 */

@DAO
public class ViewTypeDAOMysqlImpl extends AbstractTableCreatingDAOImpl implements ViewTypeDAO {
    // constants
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/core/types/ViewType.sql";

    // constructor 
    public ViewTypeDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService) {
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

    // abstract template method of 'TableCreatingDAOImpl' implementation
    @Override
    public String getDefaultTableName() {
        return DEFAULT_TABLE_NAME;
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SQL_SCRIPT_NAME;
    }

    // interface 'ViewTypeDAO' implementation
    @LongCacheable
    public String getTypeById(Integer tenantId, final Integer id) {
        // validate
        if (tenantId == null) {
            throw new IllegalArgumentException("missing constraints: missing 'tenantId'");
        }
        if (id == null) {
            return null;
        }
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
                throw new IllegalArgumentException("unknown id: '" + id + "' for view type");
            }
        };
        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append(DEFAULT_NAME_COLUMN_NAME);
        sqlQuery.append(" FROM ");
        sqlQuery.append(DEFAULT_TABLE_NAME);
        sqlQuery.append(" WHERE ");
        sqlQuery.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlQuery.append("=? AND ");
        sqlQuery.append(DEFAULT_ID_COLUMN_NAME);
        sqlQuery.append("=?");

        Object[] args = {tenantId, id};
        int[] argTypes = {Types.INTEGER, Types.INTEGER};

        return getJdbcTemplate().query(sqlQuery.toString(), args, argTypes, rse);
    }

    @LongCacheable
    public Integer getIdOfType(Integer tenantId, final String viewType) {
        // validate
        if (tenantId == null) {
            throw new IllegalArgumentException("missing constraints: missing 'tenantId'");
        }
        if (viewType == null) {
            return null;
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
                throw new IllegalArgumentException("unknown view type: '" + viewType + "'");
            }
        };
        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append(DEFAULT_ID_COLUMN_NAME);
        sqlQuery.append(" FROM ");
        sqlQuery.append(DEFAULT_TABLE_NAME);
        sqlQuery.append(" WHERE ");
        sqlQuery.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlQuery.append("=? AND ");
        sqlQuery.append(DEFAULT_NAME_COLUMN_NAME);
        sqlQuery.append(" LIKE ?");

        Object[] args = {tenantId, viewType};
        int[] argTypes = {Types.INTEGER, Types.VARCHAR};
        return getJdbcTemplate().query(sqlQuery.toString(), args, argTypes, rse);
    }

    public int insertOrUpdate(Integer tenantId, String viewType, Integer id) {        // validate
        if (tenantId == null) {
            throw new IllegalArgumentException("missing constraints: missing 'tenantId'");
        }
        if (viewType == null) {
            throw new IllegalArgumentException("missing constraints: missing 'viewType'");
        }
        if (id == null) {
            throw new IllegalArgumentException("missing constraints: missing 'id'");
        }
        StringBuilder query = new StringBuilder();
        boolean update;
        if (!existsType(tenantId, viewType)) {
            query.append("INSERT INTO ");
            update = false;
        } else {
            query.append("UPDATE ");
            update = true;
        }
        query.append(DEFAULT_TABLE_NAME);
        query.append(" SET ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_NAME_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_ID_COLUMN_NAME);
        query.append("=?");

        Object[] args;
        int[] argTypes;

        if (update) {
            query.append(" WHERE ");
            query.append(DEFAULT_TENANT_COLUMN_NAME);
            query.append("=? AND ");
            query.append(DEFAULT_NAME_COLUMN_NAME);
            query.append("=?");
            args = new Object[]{tenantId, viewType, id, tenantId, viewType};
            argTypes = new int[]{Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.VARCHAR};
        } else {
            args = new Object[]{tenantId, viewType, id};
            argTypes = new int[]{Types.INTEGER, Types.VARCHAR, Types.INTEGER};
        }
        return getJdbcTemplate().update(query.toString(), args, argTypes);
    }

    @LongCacheable
    public HashMap<String, Integer> getMapping(Integer tenantId) {
        // validate
        if (tenantId == null) {
            throw new IllegalArgumentException("missing constraints: missing 'tenantId'");
        }

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

        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append(DEFAULT_NAME_COLUMN_NAME);
        sqlQuery.append(", ");
        sqlQuery.append(DEFAULT_ID_COLUMN_NAME);
        sqlQuery.append(" FROM ");
        sqlQuery.append(DEFAULT_TABLE_NAME);
        sqlQuery.append(" WHERE ");
        sqlQuery.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlQuery.append("=?");

        Object[] args = {tenantId};
        int[] argTypes = {Types.INTEGER};
        return getJdbcTemplate().query(sqlQuery.toString(), args, argTypes, rse);
    }

    @LongCacheable
    public Set<String> getTypes(Integer tenantId) {
        // validate
        if (tenantId == null) {
            throw new IllegalArgumentException("missing constraints: missing 'tenantId'");
        }
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

        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append(DEFAULT_NAME_COLUMN_NAME);
        sqlQuery.append(" FROM ");
        sqlQuery.append(DEFAULT_TABLE_NAME);
        sqlQuery.append(" WHERE ");
        sqlQuery.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlQuery.append("=?");
        sqlQuery.append(" ORDER BY ");
        sqlQuery.append(DEFAULT_NAME_COLUMN_NAME);

        Object[] args = {tenantId};
        int[] argTypes = {Types.INTEGER};
        return getJdbcTemplate().query(sqlQuery.toString(), args, argTypes, rse);
    }

    ///////////////////////////////////////////////////////////////////
    // private methods
    private boolean existsType(Integer tenantId, String viewType) {
        Integer id;
        try {
            id = getIdOfType(tenantId, viewType);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return (id != null);
    }
}
