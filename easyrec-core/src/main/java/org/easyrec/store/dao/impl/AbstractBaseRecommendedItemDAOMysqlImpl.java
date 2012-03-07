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

import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.store.dao.BaseRecommendationDAO;
import org.easyrec.store.dao.BaseRecommendedItemDAO;
import org.easyrec.store.dao.core.RecommendationDAO;
import org.easyrec.utils.spring.store.dao.DaoUtils.ArgsAndTypesHolder;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;

import java.sql.Types;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides a Mysql implementation of the {@link org.easyrec.store.dao.BaseRecommendedItemDAO} interface.
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

public abstract class AbstractBaseRecommendedItemDAOMysqlImpl<RI, T> extends AbstractTableCreatingDAOImpl
        implements BaseRecommendedItemDAO<RI, T> {
    // constants
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/RecommandedItem.sql";

    // constructor
    protected AbstractBaseRecommendedItemDAOMysqlImpl(SqlScriptService sqlScriptService) {
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

    // abstract generic method definition of 'BaseRecommendedItemDAO<A>' interface
    public abstract int insertRecommendedItem(RI recommendedItem);

    public abstract RI loadRecommendedItem(Integer recommendedItemId);

    public abstract Iterator<RI> getRecommendedItemIterator(int bulkSize);

    public abstract Iterator<RI> getRecommendedItemIterator(int bulkSize, TimeConstraintVO timeConstraints);

    public abstract List<RI> getRecommendedItems(TimeConstraintVO timeConstraints);

    public abstract List<RI> getRecommendedItemsOfRecommendation(Integer recommendationId);

    public abstract List<RI> getRecommendedItemsOfRecommendation(Integer recommendationId, T tenant);

    //////////////////////////////////////////////////////////////////////////////
    // protected methods
    protected String getRecommendedItemIteratorQueryString() {
        String recAlias = "rec";
        String recItemAlias = "recItem";

        // join with recommendation (to retrieve tenantId)
        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(recItemAlias);
        sqlString.append(".*, ");
        sqlString.append(recAlias);
        sqlString.append(".");
        sqlString.append(RecommendationDAO.DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" AS ");
        sqlString.append(recItemAlias);
        sqlString.append(", ");
        sqlString.append(RecommendationDAO.DEFAULT_TABLE_NAME);
        sqlString.append(" AS ");
        sqlString.append(recAlias);
        sqlString.append(" WHERE ");
        sqlString.append(recItemAlias);
        sqlString.append(".");
        sqlString.append(DEFAULT_RECOMMENDATION_COLUMN_NAME);
        sqlString.append("=");
        sqlString.append(recAlias);
        sqlString.append(".");
        sqlString.append(RecommendationDAO.DEFAULT_ID_COLUMN_NAME);
        return sqlString.toString();
    }

    protected String getRecommendedItemIteratorQueryString(TimeConstraintVO timeConstraints,
                                                           ArgsAndTypesHolder holder) {
        String recAlias = "rec";
        String recItemAlias = "recItem";

        // join with recommendation (to retrieve tenantId)
        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(recItemAlias);
        sqlString.append(".*, ");
        sqlString.append(recAlias);
        sqlString.append(".");
        sqlString.append(RecommendationDAO.DEFAULT_RECOMMENDATION_TIME_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(recAlias);
        sqlString.append(".");
        sqlString.append(RecommendationDAO.DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" AS ");
        sqlString.append(recItemAlias);
        sqlString.append(", ");
        sqlString.append(RecommendationDAO.DEFAULT_TABLE_NAME);
        sqlString.append(" AS ");
        sqlString.append(recAlias);
        sqlString.append(" WHERE ");
        sqlString.append(recItemAlias);
        sqlString.append(".");
        sqlString.append(DEFAULT_RECOMMENDATION_COLUMN_NAME);
        sqlString.append("=");
        sqlString.append(recAlias);
        sqlString.append(".");
        sqlString.append(RecommendationDAO.DEFAULT_ID_COLUMN_NAME);

        if (timeConstraints.getDateFrom() != null) {
            sqlString.append(" AND ");
            sqlString.append(BaseRecommendationDAO.DEFAULT_RECOMMENDATION_TIME_COLUMN_NAME);
            sqlString.append(" >= ?");
            holder.getArgs()[0] = timeConstraints.getDateFrom();
            if (timeConstraints.getDateTo() != null) {
                sqlString.append(" AND ");
                sqlString.append(BaseRecommendationDAO.DEFAULT_RECOMMENDATION_TIME_COLUMN_NAME);
                sqlString.append(" <= ?");
                holder.setArgs(ObjectArrays.concat(holder.getArgs(), timeConstraints.getDateTo()));
                holder.setArgTypes(Ints.concat(holder.getArgTypes(), new int[] { Types.TIMESTAMP }));
            }
        } else {
            sqlString.append(" AND ");
            sqlString.append(BaseRecommendationDAO.DEFAULT_RECOMMENDATION_TIME_COLUMN_NAME);
            sqlString.append(" <= ?");
            holder.getArgs()[0] = timeConstraints.getDateTo();
        }

        return sqlString.toString();
    }
}
