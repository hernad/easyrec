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

import org.easyrec.model.core.ItemVO;
import org.easyrec.plugin.pearson.model.UserAssoc;
import org.easyrec.plugin.pearson.store.dao.LatestActionDAO;
import org.easyrec.plugin.pearson.store.dao.UserAssocDAO;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

/**
 * MySQL implementation of @see{org.easyrec.plugin.pearson.store.dao.UserAssocDAO}
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
public class UserAssocDAOMysqlImpl extends AbstractTableCreatingDAOImpl implements UserAssocDAO {

    private static class ItemRowMapper implements RowMapper {

        private final Integer tenantId;
        private final Integer itemTypeId;

        public ItemRowMapper(Integer tenantId, Integer itemTypeId) {
            this.tenantId = tenantId;
            this.itemTypeId = itemTypeId;
        }

        public Object mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Integer itemId = DaoUtils.getInteger(rs, DEFAULT_ITEM_TO_COLUMN_NAME);
            final ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(tenantId, itemId,
                    itemTypeId);
            return item;
        }
    }

    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/content/PearsonUserAssoc.sql";

    /**
     * @param sqlScriptService
     */
    public UserAssocDAOMysqlImpl(final DataSource dataSource, final SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
    }

    public int deleteAlreadyVotedAssocs(final Integer tenantId, final Integer sourceTypeId) {
        final StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE\n");
        query.append("");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(" = ? AND\n");
        query.append("");
        query.append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
        query.append(" = ? AND\n");
        query.append("");
        query.append(DEFAULT_ITEM_TO_COLUMN_NAME);
        query.append(" IN (\n");
        query.append("    SELECT ");
        query.append(LatestActionDAO.DEFAULT_ITEM_COLUMN_NAME);
        query.append("\n");
        query.append("    FROM ");
        query.append(LatestActionDAO.DEFAULT_TABLE_NAME);
        query.append(" AS a\n");
        query.append("    WHERE \n");
        query.append("        ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(".");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(" = a.");
        query.append(LatestActionDAO.DEFAULT_TENANT_COLUMN_NAME);
        query.append(" AND\n");
        query.append("        ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(".");
        query.append(DEFAULT_USER_FROM_COLUMN_NAME);
        query.append(" = a.");
        query.append(LatestActionDAO.DEFAULT_USER_COLUMN_NAME);
        query.append(" AND\n");
        query.append("        ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(".");
        query.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
        query.append(" = a.");
        query.append(LatestActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append("\n");
        query.append("    )");

        final Object[] args = new Object[]{tenantId, sourceTypeId};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER};

        return getJdbcTemplate().update(query.toString(), args, argt);
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

    @SuppressWarnings("unchecked")
    public List<ItemVO<Integer, Integer>> getItemsAssociatedToUser(final Integer tenantId,
                                                                            final Integer userId,
                                                                            final Integer itemTypeId,
                                                                            final Integer sourceTypeId) {
        final StringBuilder query = new StringBuilder("SELECT DISTINCT\n   ");
        query.append(DEFAULT_ITEM_TO_COLUMN_NAME);
        query.append("\nFROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append("\nWHERE\n  ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(" = ? AND\n  ");
        query.append(DEFAULT_USER_FROM_COLUMN_NAME);
        query.append(" = ? AND\n  ");
        query.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
        query.append(" = ? AND\n  ");
        query.append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
        query.append(" = ?");

        final Object[] args = new Object[]{tenantId, userId, itemTypeId, sourceTypeId};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        final RowMapper mapper = new ItemRowMapper(tenantId, itemTypeId);

        final List<ItemVO<Integer, Integer>> items = getJdbcTemplate()
                .query(query.toString(), args, argt, mapper);

        return items;
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

    /*
     * (non-Javadoc)
     *
     * @see org.easyrec.plugin.pearson.store.dao.UserAssocDAO#insertOrUpdateUserAssoc(org .easyrec.slopeone_obsolete.model.UserAssoc)
     */
    public int insertOrUpdateUserAssoc(final UserAssoc userAssoc) {
        final StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" SET ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_USER_FROM_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_ASSOC_VALUE_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_ITEM_TO_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_CHANGE_DATE_COLUMN_NAME);
        query.append("=?");
        query.append(" ON DUPLICATE KEY UPDATE ");
        query.append(DEFAULT_ASSOC_VALUE_COLUMN_NAME);
        query.append("=VALUES(");
        query.append(DEFAULT_ASSOC_VALUE_COLUMN_NAME);
        query.append("), ");
        query.append(DEFAULT_CHANGE_DATE_COLUMN_NAME);
        query.append("=VALUES(");
        query.append(DEFAULT_CHANGE_DATE_COLUMN_NAME);
        query.append(")");

        final Object[] args = new Object[]{userAssoc.getTenantId(), userAssoc.getUserFrom(), userAssoc.getAssocValue(),
                userAssoc.getItemTo().getItem(), userAssoc.getItemTo().getType(), userAssoc.getSourceTypeId(),
                new Date()};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER, Types.DOUBLE, Types.INTEGER, Types.INTEGER,
                Types.INTEGER, Types.TIMESTAMP};

        return getJdbcTemplate().update(query.toString(), args, argt);
    }
}
