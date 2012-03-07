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

package org.easyrec.plugin.pearson.store.dao.impl;

import org.easyrec.plugin.pearson.model.User;
import org.easyrec.plugin.pearson.model.Weight;
import org.easyrec.plugin.pearson.store.dao.LatestActionDAO;
import org.easyrec.plugin.pearson.store.dao.WeightDAO;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class WeightDAOMysqlImpl extends AbstractTableCreatingDAOImpl implements WeightDAO {
    private final static class WeightMapper implements RowMapper {
        public Object mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Integer id = DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME);
            final Integer tenantId = DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME);
            final Integer user1Id = DaoUtils.getInteger(rs, DEFAULT_USER1_COLUMN_NAME);
            final Integer user2Id = DaoUtils.getInteger(rs, DEFAULT_USER2_COLUMN_NAME);
            final Double weight = DaoUtils.getDouble(rs, DEFAULT_WEIGHT_COLUMN_NAME);

            final User user1 = new User(null, null, tenantId, user1Id);
            final User user2 = new User(null, null, tenantId, user2Id);

            final Weight weightObj = new Weight(id, user1, user2, weight);

            return weightObj;
        }
    }

    private static final RowMapper defaultWeightMapper = new WeightMapper();
    private static final String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/content/Weight.sql";

    protected WeightDAOMysqlImpl(final DataSource dataSource, final SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
    }

    @Override
    public String getDefaultTableName() {
        return DEFAULT_TABLE_NAME;
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SQL_SCRIPT_NAME;
    }

    @SuppressWarnings("unchecked")
    public List<Weight> getWeightsForUser1(final Integer tenantId, final Integer user1Id) {
        final StringBuilder query = new StringBuilder("SELECT ");
        query.append(DEFAULT_ID_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_USER1_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_USER2_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_WEIGHT_COLUMN_NAME);
        query.append(" FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append("=? AND ");
        query.append(DEFAULT_USER1_COLUMN_NAME);
        query.append("=?");

        final Object[] args = new Object[]{tenantId, user1Id};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER};

        final List<Weight> weights = getJdbcTemplate().query(query.toString(), args, argt, defaultWeightMapper);

        return weights;
    }

    @SuppressWarnings("unchecked")
    public List<Weight> getWeightsForUser1AndItem(final Integer tenantId, final Integer user1Id, final Integer itemId,
                                                  final Integer itemTypeId) {
        final StringBuilder query = new StringBuilder("SELECT ");
        query.append(DEFAULT_ID_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_USER1_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_USER2_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_WEIGHT_COLUMN_NAME);
        query.append("\nFROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append("\nWHERE\n");
        query.append("   ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(" = ? AND\n");
        query.append("   ");
        query.append(DEFAULT_USER1_COLUMN_NAME);
        query.append(" = ? AND\n");
        query.append("   ");
        query.append(DEFAULT_USER2_COLUMN_NAME);
        query.append(" IN (SELECT DISTINCT ");
        query.append(LatestActionDAO.DEFAULT_USER_COLUMN_NAME);
        query.append(" FROM ");
        query.append(LatestActionDAO.DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(LatestActionDAO.DEFAULT_TENANT_COLUMN_NAME);
        query.append(" = ? AND ");
        query.append(LatestActionDAO.DEFAULT_ITEM_COLUMN_NAME);
        query.append(" = ? AND ");
        query.append(LatestActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append(" = ?)");

        final Object[] args = new Object[]{tenantId, user1Id, tenantId, itemId, itemTypeId};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        final List<Weight> result = getJdbcTemplate().query(query.toString(), args, argt, defaultWeightMapper);

        return result;
    }

    public void insertOrUpdateWeightSymmetric(final Weight weight) {
        final StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(DEFAULT_TABLE_NAME);
        query.append("(");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_USER1_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_USER2_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_WEIGHT_COLUMN_NAME);
        query.append(") VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE ");
        query.append(DEFAULT_WEIGHT_COLUMN_NAME);
        query.append(" = ?");

        final Object[] args1 = new Object[]{weight.getUser1().getTenantId(), weight.getUser1().getUser(),
                weight.getUser2().getUser(), weight.getWeight(), weight.getWeight()};
        final Object[] args2 = new Object[]{weight.getUser1().getTenantId(), weight.getUser2().getUser(),
                weight.getUser1().getUser(), weight.getWeight(), weight.getWeight()};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.DOUBLE, Types.DOUBLE};

        getJdbcTemplate().update(query.toString(), args1, argt);
        getJdbcTemplate().update(query.toString(), args2, argt);
    }

}
