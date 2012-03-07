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

package org.easyrec.plugin.itemitem.store.dao.impl;

import org.easyrec.plugin.itemitem.model.UserAssoc;
import org.easyrec.plugin.itemitem.store.dao.UserAssocDAO;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDroppingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Date;

/**
 * MySQL implementation of @see{org.easyrec.plugin.itemitem.store.dao.UserAssocDAO} <p/> <p> <b>Company:&nbsp;</b> SAT,
 * Research Studios Austria </p> <p/> <p> <b>Copyright:&nbsp;</b> (c) 2007 </p> <p/> <p> <b>last modified:</b><br/>
 * $Author$<br/> $Date$<br/> $Revision$ </p>
 *
 * @author Patrick Marschik
 */
public class UserAssocDAOMysqlImpl extends AbstractTableCreatingDroppingDAOImpl implements UserAssocDAO {
    // ------------------------------ FIELDS ------------------------------

    public static final String QUERY_INSERT;
    public static final int[] ARGT_INSERT;
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/plugins/itemitem/ItemItemUserAssoc.sql";

    // -------------------------- STATIC METHODS --------------------------

    static {
        final StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(TABLE_NAME).append(" SET ");
        query.append(COLUMN_TENANT).append("=?, ");
        query.append(COLUMN_USERFROM).append("=?, ");
        query.append(COLUMN_ASSOCVALUE).append("=?, ");
        query.append(COLUMN_ITEMTO).append("=?, ");
        query.append(COLUMN_ITEMTOTYPE).append("=?, ");
        query.append(COLUMN_SOURCETYPE).append("=?, ");
        query.append(COLUMN_CHANGEDATE).append("=?");
        query.append(" ON DUPLICATE KEY UPDATE ");
        query.append(COLUMN_ASSOCVALUE).append("=VALUES(").append(COLUMN_ASSOCVALUE).append("), ");
        query.append(COLUMN_CHANGEDATE).append("=VALUES(").append(COLUMN_CHANGEDATE).append(")");

        QUERY_INSERT = query.toString();
        ARGT_INSERT = new int[]{Types.INTEGER, Types.INTEGER, Types.DOUBLE, Types.INTEGER, Types.INTEGER, Types.INTEGER,
                Types.TIMESTAMP};
    }

    // --------------------------- CONSTRUCTORS ---------------------------

    public UserAssocDAOMysqlImpl(final DataSource dataSource, final SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
    }

    // ------------------------ INTERFACE METHODS ------------------------


    // --------------------- Interface TableCreatingDAO ---------------------

    @Override
    public String getDefaultTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SQL_SCRIPT_NAME;
    }

    // --------------------- Interface UserAssocDAO ---------------------

    public int insertOrUpdateUserAssoc(final UserAssoc userAssoc) {
        final Object[] args = new Object[]{userAssoc.getTenantId(), userAssoc.getUserFrom(), userAssoc.getAssocValue(),
                userAssoc.getItemTo().getItem(), userAssoc.getItemTo().getType(), userAssoc.getSourceTypeId(),
                new Date()};

        return getJdbcTemplate().update(QUERY_INSERT, args, ARGT_INSERT);
    }
}
