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
package org.easyrec.utils.spring.store.dao.impl;

import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.TableCreatingDAO;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.List;

/**
 * This class provides a Mysql implementation of the {@link org.easyrec.utils.spring.store.dao.TableCreatingDAO}
 * interface.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Roman Cerny
 */

@DAO
public abstract class AbstractTableCreatingDAOImpl extends JdbcDaoSupport implements TableCreatingDAO {
    private SqlScriptService sqlScriptService;

    // constructors    
    private AbstractTableCreatingDAOImpl() {
        // this constructor is never called
        throw new IllegalStateException("this constructor is private and should never be called");
    }

    protected AbstractTableCreatingDAOImpl(SqlScriptService sqlScriptService) {
        setSqlScriptService(sqlScriptService);
    }

    // abstract template methods
    public abstract String getDefaultTableName();

    public abstract String getTableCreatingSQLScriptName();

    // interface 'TableCreatingDAO' implementation       
    public void createTable() {
        if (sqlScriptService == null) {
            throw new IllegalStateException("property 'sqlScriptService' is required, but not set");
        }

        if (existsTable()) {
            logger.info("table " + getDefaultTableName() + " already exists, skipped creation of table");
            return;
        }

        List<String> sqlCommands = sqlScriptService.parseSqlScript(getTableCreatingSQLScriptName());
        if (sqlCommands.size() < 1) {
            logger.error("could not create table " + getDefaultTableName() + ", no sql command found in '" +
                    getTableCreatingSQLScriptName() + "'");
            throw new IllegalArgumentException(
                    "could not create table " + getDefaultTableName() + ", no sql command found in '" +
                            getTableCreatingSQLScriptName() + "'");
        }
        if (sqlCommands.size() > 1) {
            logger.warn("Attention: more than one sql statement found in '" + getTableCreatingSQLScriptName() +
                    "', only the first command is executed, following commands are ignored");
        }
        getJdbcTemplate().execute(sqlCommands.get(0));

        if (logger.isInfoEnabled()) {
            logger.info("succesfully created table '" + getDefaultTableName() + "'");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("used ddl statement: '" + sqlCommands.get(0) + "'");
        }
    }

    public boolean existsTable() {
        return DaoUtils.existsTable(getDataSource(), getDefaultTableName());
    }

    // getter/setter
    public SqlScriptService getSqlScriptService() {
        return sqlScriptService;
    }

    public void setSqlScriptService(SqlScriptService sqlScriptService) {
        this.sqlScriptService = sqlScriptService;
    }
}
