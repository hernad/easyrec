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
package org.easyrec.plugin.arm.store.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.store.dao.core.impl.ItemAssocDAOMysqlImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Date;
import org.easyrec.plugin.arm.store.dao.RuleminingItemAssocDAO;

/**
 * @author Stephan Zavrel
 */
public class RuleminingItemAssocDAOMysqlImpl extends ItemAssocDAOMysqlImpl implements RuleminingItemAssocDAO {

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    public RuleminingItemAssocDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService) {
        super(dataSource, sqlScriptService);
    }

    public int insertOrUpdateItemAssoc(ItemAssocVO<Integer,Integer> itemAssoc) {
        // validate input parameters
        if (itemAssoc == null) {
            throw new IllegalArgumentException("missing 'itemAssoc'");
        }

        // validate unique key
        validateUniqueKey(itemAssoc);
        validateAssocValue(itemAssoc);
        validateViewType(itemAssoc);

        if (logger.isDebugEnabled()) {
            logger.debug("inserting 'itemAssoc': " + itemAssoc);
        }

        // @HINT: maybe use UniqueIdService later (instead of auto_imcrement)
        StringBuilder sqlString = new StringBuilder("INSERT INTO ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" (");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_FROM_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ASSOC_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ASSOC_VALUE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_TO_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_SOURCE_INFO_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_VIEW_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        if (itemAssoc.isActive() != null) {
            sqlString.append(DEFAULT_ACTIVE_COLUMN_NAME);
            sqlString.append(", ");
        }
        sqlString.append(DEFAULT_CHANGE_DATE_COLUMN_NAME);
        if (itemAssoc.getChangeDate() == null) {
            itemAssoc.setChangeDate(new Date(System.currentTimeMillis()));
        }
        if (itemAssoc.isActive() != null) {
            sqlString.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        } else {
            sqlString.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }

        sqlString.append(" ON DUPLICATE KEY UPDATE ");
        sqlString.append(DEFAULT_ASSOC_VALUE_COLUMN_NAME);
        sqlString.append("=?, ");
        sqlString.append(DEFAULT_VIEW_TYPE_COLUMN_NAME);
        sqlString.append("=?, ");
        sqlString.append(DEFAULT_SOURCE_INFO_COLUMN_NAME);
        sqlString.append("=?, ");
        if (itemAssoc.isActive() != null) {
            sqlString.append(DEFAULT_ACTIVE_COLUMN_NAME);
            sqlString.append("=?, ");
        }
        sqlString.append(DEFAULT_CHANGE_DATE_COLUMN_NAME);
        sqlString.append("=?");

        Object[] args;
        int[] argTypes;
        if (itemAssoc.isActive() != null) {
            args = new Object[]{itemAssoc.getTenant(), itemAssoc.getItemFrom().getItem(),
                    itemAssoc.getItemFrom().getType(), itemAssoc.getAssocType(), itemAssoc.getAssocValue(),
                    itemAssoc.getItemTo().getItem(), itemAssoc.getItemTo().getType(), itemAssoc.getSourceType(),
                    itemAssoc.getSourceInfo(), itemAssoc.getViewType(), itemAssoc.isActive(), itemAssoc.getChangeDate(),
                    itemAssoc.getAssocValue(), itemAssoc.getViewType(), itemAssoc.getSourceInfo(), itemAssoc.isActive(),
                    itemAssoc.getChangeDate()};
            argTypes = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.DOUBLE,
                    Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.BOOLEAN,
                    Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.BOOLEAN, Types.TIMESTAMP,};
        } else {
            args = new Object[]{itemAssoc.getTenant(), itemAssoc.getItemFrom().getItem(),
                    itemAssoc.getItemFrom().getType(), itemAssoc.getAssocType(), itemAssoc.getAssocValue(),
                    itemAssoc.getItemTo().getItem(), itemAssoc.getItemTo().getType(), itemAssoc.getSourceType(),
                    itemAssoc.getSourceInfo(), itemAssoc.getViewType(), itemAssoc.getChangeDate(),
                    itemAssoc.getAssocValue(), itemAssoc.getViewType(), itemAssoc.getSourceInfo(),
                    itemAssoc.getChangeDate()};
            argTypes = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.DOUBLE,
                    Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.TIMESTAMP,
                    Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.TIMESTAMP,};
        }
        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(sqlString.toString(), argTypes);

        int rowsAffected = getJdbcTemplate().update(factory.newPreparedStatementCreator(args));

        return rowsAffected;
    }

    ///////////////////////////////////////////////////////////////////////////
    // private methods
    private void validateUniqueKey(ItemAssocVO<Integer,Integer> itemAssoc) {
        if (itemAssoc.getItemFrom() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'itemFrom'");
        }
        if (itemAssoc.getItemTo() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'itemTo'");
        }
        if (itemAssoc.getItemFrom().getItem() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'itemFromId'");
        }
        if (itemAssoc.getItemFrom().getType() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'itemFromTypeId'");
        }
        if (itemAssoc.getItemTo().getItem() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'itemToId'");
        }
        if (itemAssoc.getItemTo().getType() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'itemToTypeId'");
        }
        if (itemAssoc.getAssocType() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'assocTypeId'");
        }
        if (itemAssoc.getSourceType() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'sourceTypeId'");
        }
        if (itemAssoc.getTenant() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'tenantId'");
        }
    }

    private void validateAssocValue(ItemAssocVO<Integer,Integer> itemAssoc) {
        if (itemAssoc.getAssocValue() == null) {
            throw new IllegalArgumentException("missing constraints, missing 'assocValue', must not be NULL");
        }
    }

    private void validateViewType(ItemAssocVO<Integer,Integer> itemAssoc) {
        if (itemAssoc.getViewType() == null) {
            throw new IllegalArgumentException("missing constraints, missing 'viewTypeId', must not be NULL");
        }
    }

}
