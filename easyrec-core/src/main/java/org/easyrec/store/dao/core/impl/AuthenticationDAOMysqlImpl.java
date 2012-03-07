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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.store.dao.core.AuthenticationDAO;
import org.easyrec.utils.spring.cache.annotation.LongCacheable;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;

/**
 * <DESCRIPTION>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 16:46:14 +0200 (Fr, 12 Aug 2011) $<br/>
 * $Revision: 113 $</p>
 *
 * @author Stephan Zavrel
 */
@DAO
public class AuthenticationDAOMysqlImpl extends AbstractTableCreatingDAOImpl implements AuthenticationDAO {
    // constants
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/core/Authentication.sql";

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    // constructor
    public AuthenticationDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService) {
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

    @LongCacheable
    public List<String> getDomainURLsForTenant(Integer tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId must not be 'null'!");
        }
        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_DOMAIN_URL_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_TENANT_ID_COLUMN_NAME);
        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        args = new Object[]{tenantId};
        argTypes = new int[]{Types.INTEGER};

        return getJdbcTemplate().queryForList(sqlString.toString(), args, argTypes, String.class);

    }

    @LongCacheable
    public List<Integer> getTenantsForDomainURL(String domain) {
        if (domain == null) {
            throw new IllegalArgumentException("domain must not be 'null'!");
        }
        if (domain.length() == 0) {
            throw new IllegalArgumentException("domain must not be an empty String!");
        }

        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_TENANT_ID_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_DOMAIN_URL_COLUMN_NAME);
        sqlString.append("=?");

        Object[] args;
        int[] argTypes;

        args = new Object[]{domain};
        argTypes = new int[]{Types.VARCHAR};

        return (List<Integer>) getJdbcTemplate().queryForList(sqlString.toString(), args, argTypes, Integer.class);

    }

    public int insertDomainURLForTenant(Integer tenantId, String domain) {


        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId must not be 'null'!");
        }
        if (domain == null) {
            throw new IllegalArgumentException("domain must not be 'null'!");
        }
        if (domain.length() == 0) {
            throw new IllegalArgumentException("domain must not be an empty String!");
        }

        StringBuilder sqlString = new StringBuilder("INSERT INTO ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" SET ");
        sqlString.append(DEFAULT_TENANT_ID_COLUMN_NAME);
        sqlString.append(" =?, ");
        sqlString.append(DEFAULT_DOMAIN_URL_COLUMN_NAME);
        sqlString.append("=?");


        Object[] args = {tenantId, domain};

        int[] argTypes = {Types.INTEGER, Types.VARCHAR};

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(sqlString.toString(), argTypes);

        int rowsAffected = getJdbcTemplate().update(factory.newPreparedStatementCreator(args));
        return rowsAffected;
    }


}
