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
import org.easyrec.utils.spring.store.dao.DaoUtils.ArgsAndTypesHolder;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;

import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class provides a Mysql implementation of the {@link org.easyrec.store.dao.BaseRecommendationDAO} interface.
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

public abstract class AbstractBaseRecommendationDAOMysqlImpl<R> extends AbstractTableCreatingDAOImpl
        implements BaseRecommendationDAO<R> {
    // constants
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/core/Recommendation.sql";

    // constructor
    protected AbstractBaseRecommendationDAOMysqlImpl(SqlScriptService sqlScriptService) {
        super(sqlScriptService);
    }

    // members
    private HashMap<Integer, Integer> tenantCache;

    // abstract template method implementation of 'AbstractTableCreatingDAOImpl' 
    @Override
    public String getDefaultTableName() {
        return DEFAULT_TABLE_NAME;
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SQL_SCRIPT_NAME;
    }

    // non-generic method implementation of 'BaseRecommendedItemDAO<A>' interface
    public Integer getTenantIdOfRecommendationById(Integer recommendationId, boolean useCache) {
        if (useCache) {
            // lazy initialization
            if (tenantCache == null) {
                tenantCache = new HashMap<Integer, Integer>();
            }
            if (tenantCache.containsKey(recommendationId)) {
                return tenantCache.get(recommendationId);
            } else {
                Integer tenantId = retrieveTenantIdOfRecommendationByIdFromDatabase(recommendationId);
                tenantCache.put(recommendationId, tenantId);
                return tenantId;
            }
        } else {
            return retrieveTenantIdOfRecommendationByIdFromDatabase(recommendationId);
        }
    }

    // abstract generic method definition of 'BaseRecommendationDAO<A>' interface
    public abstract int insertRecommendation(R recommendation);

    public abstract R loadRecommendation(Integer recommendationId);

    public abstract Iterator<R> getRecommendationIterator(int bulkSize);

    public abstract Iterator<R> getRecommendationIterator(int bulkSize, TimeConstraintVO timeConstraints);

    //////////////////////////////////////////////////////////////////////////////
    // protected methods
    protected String getRecommendationQueryString() {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_ID_COLUMN_NAME);
        query.append("=?");
        return query.toString();
    }


    protected String getRecommendationIteratorQueryString() {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(DEFAULT_TABLE_NAME);
        return query.toString();
    }

    protected String getRecommendationIteratorQueryString(TimeConstraintVO timeConstraints, ArgsAndTypesHolder holder) {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(DEFAULT_TABLE_NAME);

        if (timeConstraints.getDateFrom() != null) {
            query.append(" WHERE ");
            query.append(DEFAULT_RECOMMENDATION_TIME_COLUMN_NAME);
            query.append(" >= ?");
            holder.getArgs()[0] = timeConstraints.getDateFrom();
            if (timeConstraints.getDateTo() != null) {
                query.append(" AND ");
                query.append(DEFAULT_RECOMMENDATION_TIME_COLUMN_NAME);
                query.append(" <= ?");
                holder.setArgs(ObjectArrays.concat(holder.getArgs(), timeConstraints.getDateTo()));
                holder.setArgTypes(Ints.concat(holder.getArgTypes(), new int[] { Types.TIMESTAMP }));
            }
        } else {
            query.append(" WHERE ");
            query.append(DEFAULT_RECOMMENDATION_TIME_COLUMN_NAME);
            query.append(" <= ?");
            holder.getArgs()[0] = timeConstraints.getDateTo();
        }

        return query.toString();
    }

    //////////////////////////////////////////////////////////////////////////////
    // private methods    
    private Integer retrieveTenantIdOfRecommendationByIdFromDatabase(Integer recommendationId) {
        StringBuilder query = new StringBuilder("SELECT ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(" FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_ID_COLUMN_NAME);
        query.append("=?");

        Object[] args = {recommendationId};
        int[] argTypes = {Types.INTEGER};

        return getJdbcTemplate().queryForInt(query.toString(), args, argTypes);
    }
}
