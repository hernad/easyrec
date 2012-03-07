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

/**
 *
 */
package org.easyrec.plugin.pearson.store.dao.impl;

import org.easyrec.plugin.pearson.model.User;
import org.easyrec.plugin.pearson.store.dao.UserDAO;
import org.easyrec.store.dao.core.ActionDAO;
import org.easyrec.utils.spring.cache.annotation.LongCacheable;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * <DESCRIPTION>
 * <p/>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2007
 * </p>
 * <p/>
 * <p>
 * <b>last modified:</b><br/>
 * $Author$<br/>
 * $Date$<br/>
 * $Revision$
 * </p>
 *
 * @author Patrick Marschik
 */
@DAO
public class UserDAOMysqlImpl extends AbstractTableCreatingDAOImpl implements UserDAO {

    private static final class UserRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Integer id = DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME);
            final Integer tenant = DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME);
            final Integer user = DaoUtils.getInteger(rs, DEFAULT_USER_COLUMN_NAME);
            final String name = DaoUtils.getStringIfPresent(rs, DEFAULT_NAME_COLUMN_NAME);

            return new User(id, name, tenant, user);
        }
    }

    private static RowMapper defaultUserRowMapper = new UserRowMapper();
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/content/User.sql";

    /**
     * @param sqlScriptService
     */
    public UserDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
    }

    @SuppressWarnings("unchecked")
    public void createUsersFromActionsForTenant(Integer tenantId) {
        final StringBuilder userQuery = new StringBuilder("SELECT DISTINCT ");
        userQuery.append(ActionDAO.DEFAULT_USER_COLUMN_NAME);
        userQuery.append(" FROM ");
        userQuery.append(ActionDAO.DEFAULT_TABLE_NAME);
        userQuery.append(" WHERE ");
        userQuery.append(ActionDAO.DEFAULT_TENANT_COLUMN_NAME);
        userQuery.append(" = ?");

        final List<Integer> userIds = getJdbcTemplate()
                .queryForList(userQuery.toString(), new Object[]{tenantId}, new int[]{Types.INTEGER}, Integer.class);

        final StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(DEFAULT_TABLE_NAME);
        query.append("(");
        query.append(DEFAULT_NAME_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_USER_COLUMN_NAME);
        query.append(") VALUES (?, ?, ?)");

        final int[] argt = new int[]{Types.VARCHAR, Types.INTEGER, Types.INTEGER};

        for (final Integer userId : userIds) {
            final Object[] args = new Object[]{"unknown" + userId, tenantId, userId};

            try {
                getJdbcTemplate().update(query.toString(), args, argt);
            } catch (final DataIntegrityViolationException ex0) {
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl# getDefaultTableName()
     */
    @Override
    public String getDefaultTableName() {
        return DEFAULT_TABLE_NAME;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl# getTableCreatingSQLScriptName()
     */
    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SQL_SCRIPT_NAME;
    }

    @SuppressWarnings("unchecked")
    @LongCacheable
    public List<Integer> getUserIdsForTenant(Integer tenantId) {
        final StringBuilder query = new StringBuilder("SELECT ");
        query.append(DEFAULT_USER_COLUMN_NAME);
        query.append(" FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append("=?");

        final Object[] args = new Object[]{tenantId};
        final int[] argt = new int[]{Types.INTEGER};

        return getJdbcTemplate().queryForList(query.toString(), args, argt, Integer.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.easyrec.plugin.pearson.store.dao.UserDAO#getUsersForTenant(java.lang.Integer )
     */
    @SuppressWarnings("unchecked")
    @LongCacheable
    public List<User> getUsersForTenant(Integer tenantId) {
        final StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append("=?");

        final Object[] args = new Object[]{tenantId};
        final int[] argt = new int[]{Types.INTEGER};

        return getJdbcTemplate().query(query.toString(), args, argt, defaultUserRowMapper);
    }
}
