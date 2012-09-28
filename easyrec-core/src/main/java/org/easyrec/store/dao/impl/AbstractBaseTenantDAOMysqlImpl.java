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
package org.easyrec.store.dao.impl;

import org.easyrec.model.core.TenantVO;
import org.easyrec.store.dao.BaseTenantDAO;
import org.easyrec.utils.spring.cache.annotation.LongCacheable;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * This class provides a Mysql implementation of the {@link org.easyrec.store.dao.BaseTenantDAO} interface.
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
public abstract class AbstractBaseTenantDAOMysqlImpl extends AbstractTableCreatingDAOImpl implements BaseTenantDAO {
    // constants
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/Tenant.sql";

    // members
    private TenantVORowMapper tenantVORowMapper = new TenantVORowMapper();

    // constructor
    protected AbstractBaseTenantDAOMysqlImpl(SqlScriptService sqlScriptService) {
        super(sqlScriptService);
    }

    // abstract template method implementation of 'AbstractTableCreatingDAOImpl' 
    @Override
    public String getDefaultTableName() {
        return DEFAULT_TABLE_NAME;
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SQL_SCRIPT_NAME;
    }


    // interface 'BaseTenantDAO' implementation
    @LongCacheable
    public TenantVO getTenantById(Integer tenantId) {
        // validate input parameters
        if (tenantId == null) {
            throw new IllegalArgumentException("missing 'tenantId'");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("loading 'tenant' with id '" + tenantId + "'");
        }

        StringBuilder sqlString = new StringBuilder("SELECT * FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_ID_COLUMN_NAME);
        sqlString.append("=?");
        Object[] args = {tenantId};
        int[] argTypes = {Types.INTEGER};

        return getJdbcTemplate().queryForObject(sqlString.toString(), args, argTypes, tenantVORowMapper);
    }

    @LongCacheable
    public TenantVO getTenantByStringId(String stringId) {
        // validate input parameters
        if (stringId == null) {
            throw new IllegalArgumentException("missing 'stringId'");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("loading 'tenant' with stringId '" + stringId + "'");
        }

        StringBuilder sqlString = new StringBuilder("SELECT * FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_STRING_ID_COLUMN_NAME);
        sqlString.append(" LIKE ?");
        Object[] args = {stringId};
        int[] argTypes = {Types.VARCHAR};

        return getJdbcTemplate().queryForObject(sqlString.toString(), args, argTypes, tenantVORowMapper);
    }

    public List<TenantVO> getAllTenants() {

        if (logger.isDebugEnabled()) {
            logger.debug("loading list of all tenants'");
        }

        StringBuilder sqlString = new StringBuilder("SELECT * FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);

        return getJdbcTemplate().query(sqlString.toString(), tenantVORowMapper);
    }

    public int insertTenant(TenantVO tenant) {

        if (logger.isDebugEnabled()) {
            logger.debug("inserting tenant '" + tenant.getStringId() + "'");
        }


        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" SET ");
        query.append(DEFAULT_ID_COLUMN_NAME);
        query.append(" =?, ");
        query.append(DEFAULT_STRING_ID_COLUMN_NAME);
        query.append(" =?, ");
        query.append(DEFAULT_DESCRIPTION_COLUMN_NAME);
        query.append(" =?, ");
        query.append(DEFAULT_RATING_RANGE_MIN_COLUMN_NAME);
        query.append(" =?, ");
        query.append(DEFAULT_RATING_RANGE_MAX_COLUMN_NAME);
        query.append(" =?, ");
        query.append(DEFAULT_RATING_RANGE_NEUTRAL_COLUMN_NAME);
        query.append(" =?");

        tenant.setId(getNewTenantId());

        Object[] args = {tenant.getId(), tenant.getStringId(), tenant.getDescription(), tenant.getRatingRangeMin(),
                tenant.getRatingRangeMax(), tenant.getRatingRangeNeutral()};
        int[] argTypes = {Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.DOUBLE};

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query.toString(), argTypes);

        getJdbcTemplate().update(factory.newPreparedStatementCreator(args));

        return tenant.getId();
    }

    public int setTenantActive(TenantVO tenant, boolean active) {

        if (logger.isDebugEnabled()) {
            logger.debug("setting tenant '" + tenant.getStringId() + "' to " + active);
        }

        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" SET ");
        query.append(DEFAULT_ACTIVE_COLUMN_NAME).append("=?");
        query.append(" WHERE ");
        query.append(DEFAULT_ID_COLUMN_NAME).append("=?");

        Object[] args = {active, tenant.getId()};
        int[] argTypes = {Types.BOOLEAN, Types.INTEGER};

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query.toString(), argTypes);

        return getJdbcTemplate().update(factory.newPreparedStatementCreator(args));

    }

    public String getTenantConfig(Integer tenantId) {

        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId must not be 'null'!");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("loading 'tenantConfig' for tenant '" + tenantId + "'");
        }

        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_CONFIG_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_ID_COLUMN_NAME);
        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        args = new Object[]{tenantId};
        argTypes = new int[]{Types.INTEGER};

        return getJdbcTemplate().queryForObject(sqlString.toString(), args, argTypes, String.class);
    }

    public int storeTenantConfig(Integer tenantId, String tenantConfig) {

        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId must not be 'null'!");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("storing tenantConfig for tenant '" + tenantId + "'");
        }

        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" SET ");
        query.append(DEFAULT_CONFIG_COLUMN_NAME).append("=?");
        query.append(" WHERE ");
        query.append(DEFAULT_ID_COLUMN_NAME).append("=?");

        Object[] args = {tenantConfig, tenantId};
        int[] argTypes = {Types.VARCHAR, Types.INTEGER};

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query.toString(), argTypes);

        return getJdbcTemplate().update(factory.newPreparedStatementCreator(args));
    }

    public String getTenantStatistic(Integer tenantId) {

        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId must not be 'null'!");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("loading 'tenantStatistic' for tenant '" + tenantId + "'");
        }

        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_STATISTIC_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_ID_COLUMN_NAME);
        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        args = new Object[]{tenantId};
        argTypes = new int[]{Types.INTEGER};

        return getJdbcTemplate().queryForObject(sqlString.toString(), args, argTypes, String.class);
    }

    public int storeTenantStatistic(Integer tenantId, String tenantStatistic) {

        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId must not be 'null'!");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("storing tenantStatistic for tenant '" + tenantId + "'");
        }

        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" SET ");
        query.append(DEFAULT_STATISTIC_COLUMN_NAME).append("=?");
        query.append(" WHERE ");
        query.append(DEFAULT_ID_COLUMN_NAME).append("=?");


        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query.toString(),
                new int[]{Types.VARCHAR, Types.INTEGER});

        return getJdbcTemplate().update(factory.newPreparedStatementCreator(new Object[]{tenantStatistic, tenantId}));
    }


    //////////////////////////////////////////////////////////////////////////////
    // private methods
    private int getNewTenantId() {

        StringBuilder query = new StringBuilder("Select MAX(").append(DEFAULT_ID_COLUMN_NAME).append(") FROM ")
                .append(DEFAULT_TABLE_NAME);

        return getJdbcTemplate().queryForInt(query.toString()) + 1;
    }


    //////////////////////////////////////////////////////////////////////////////
    // private inner classes
    private class TenantVORowMapper implements RowMapper<TenantVO> {
        public TenantVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            TenantVO tenant = new TenantVO(DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_STRING_ID_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_DESCRIPTION_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_RATING_RANGE_MIN_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_RATING_RANGE_MAX_COLUMN_NAME),
                    DaoUtils.getDouble(rs, DEFAULT_RATING_RANGE_NEUTRAL_COLUMN_NAME),
                    DaoUtils.getBoolean(rs, DEFAULT_ACTIVE_COLUMN_NAME));
            return tenant;
        }
    }


}