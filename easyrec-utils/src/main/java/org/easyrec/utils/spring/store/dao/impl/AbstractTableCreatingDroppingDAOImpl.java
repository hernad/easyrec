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
import org.easyrec.utils.spring.store.dao.TableCreatingDroppingDAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;

/**
 * This class provides a Mysql implementation of the {@link org.easyrec.utils.spring.store.dao.TableCreatingDroppingDAO}
 * interface. <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p>
 * <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public abstract class AbstractTableCreatingDroppingDAOImpl extends AbstractTableCreatingDAOImpl
        implements TableCreatingDroppingDAO {
    // --------------------------- CONSTRUCTORS ---------------------------

    protected AbstractTableCreatingDroppingDAOImpl(SqlScriptService sqlScriptService) {
        super(sqlScriptService);
    }

    // ------------------------ INTERFACE METHODS ------------------------


    // --------------------- Interface TableCreatingDroppingDAO ---------------------

    public void dropTable() {
        if (!DaoUtils.existsTable(getDataSource(), getDefaultTableName())) return;

        StringBuilder query = new StringBuilder("DROP TABLE ");
        query.append(getDefaultTableName());

        getJdbcTemplate().update(query.toString());
    }
}
