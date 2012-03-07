/**Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

import org.easyrec.store.dao.BaseItemAssocDAO;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;

import java.util.Iterator;
import java.util.List;

/**
 * This class provides a Mysql implementation of the {@link org.easyrec.store.dao.BaseItemAssocDAO} interface.
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
 * @author Roman Cerny
 */

@DAO
public abstract class AbstractBaseItemAssocDAOMysqlImpl<IA, AI, IT, AT, I, C> extends AbstractTableCreatingDAOImpl
        implements BaseItemAssocDAO<IA, AI, IT, AT, I, C> {
    // constants
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/core/ItemAssoc.sql";

    // constructor
    protected AbstractBaseItemAssocDAOMysqlImpl(SqlScriptService sqlScriptService) {
        super(sqlScriptService);
    }

    // abstract template method implementation of 'TableCreatingDAOImpl'
    @Override
    public String getDefaultTableName() {
        return DEFAULT_TABLE_NAME;
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SQL_SCRIPT_NAME;
    }

    // interface 'BaseItemAssocDAO' implementation
    public int removeAllItemAssocs() {
        if (logger.isDebugEnabled()) {
            logger.debug("removing ALL 'itemAssoc' entries");
        }
        StringBuilder sqlString = new StringBuilder("DELETE FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        int rowsAffected = getJdbcTemplate().update(sqlString.toString());
        if (logger.isInfoEnabled()) {
            logger.info("number of deleted 'itemAssoc' entries: " + rowsAffected);
        }
        return rowsAffected;
    }

    // abstract generic method definition of 'BaseItemAssocDAO<A>' interface
    public abstract int insertItemAssoc(IA itemAssoc);

    public abstract int updateItemAssocUsingPrimaryKey(IA itemAssoc);

    public abstract int updateItemAssocUsingUniqueKey(IA itemAssoc);

    public abstract IA loadItemAssocByUniqueKey(IA itemAssoc);

    public abstract IA loadItemAssocByPrimaryKey(Integer itemAssocId);

    public abstract Iterator<IA> getItemAssocIterator(int bulkSize);

    public abstract int removeItemAssocsQBE(IA itemAssoc);

    public abstract List<AI> getItemsFrom(IT itemFromType, AT assocType, I itemTo, C constraints);

    public abstract List<AI> getItemsTo(I itemFrom, AT assocType, IT itemToTypeId, C constraints);

    public abstract List<IA> getItemAssocs(I itemFrom, AT assocTypeId, I itemTo, C constraints);

    public abstract List<IA> getItemAssocsQBE(I itemFrom, AT assocTypeId, I itemTo, C constraints);

    //////////////////////////////////////////////////////////////////////////////
    // protected methods
    protected String getItemAssocIteratorQueryString() {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(DEFAULT_TABLE_NAME);
        return query.toString();
    }
}
