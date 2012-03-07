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
package org.easyrec.store.dao.impl;

import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.store.dao.BaseActionDAO;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.DaoUtils.ArgsAndTypesHolder;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * This class provides a Mysql implementation of the {@link org.easyrec.store.dao.BaseActionDAO} interface.
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
public abstract class AbstractBaseActionDAOMysqlImpl<A, RI, AT, IT, I, RAT, T, U> extends AbstractTableCreatingDAOImpl
        implements BaseActionDAO<A, RI, AT, IT, I, RAT, T, U> {
    // constants
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/Action.sql";

    // members
    protected ResultSetExtractor<Date> dateResultSetExtractor = new DateResultSetExtractor();

    // constructor
    protected AbstractBaseActionDAOMysqlImpl(SqlScriptService sqlScriptService) {
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

    // interface 'BaseActionDAO<A>' implementation
    public Date getNewestActionDate() {
        StringBuilder query = new StringBuilder("SELECT ");
        query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
        query.append(" FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" ORDER BY ");
        query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
        query.append(" DESC");

        return getJdbcTemplate().query(query.toString(), dateResultSetExtractor);
    }

    // abstract generic method definition of 'BaseActionDAO<A, RI, AT, IT, I, RAT, T, U>' interface
    public abstract Date getNewestActionDate(T tenant, U user, String sessionId);

    public abstract int insertAction(A action, boolean useDateFromVO);

    public abstract int removeActionsByTenant(T tenant);

    public abstract Iterator<A> getActionIterator(int bulkSize);

    public abstract Iterator<A> getActionIterator(int bulkSize, TimeConstraintVO timeConstraints);

    public abstract List<A> getActionsFromUser(T tenant, U user, String sessionId);

    public abstract List<RI> getRankedItemsByActionType(T tenant, AT actionType, IT itemType, Integer numberOfResults,
                                                        TimeConstraintVO timeConstraints, Boolean sortDesc);

    public abstract List<I> getItemsByUserActionAndType(T tenant, U user, String sessionId, AT consideredActionType,
                                                        IT consideredItemType, Integer numberOfLastActionsConsidered);

    public abstract List<I> getItemsByUserActionAndType(T tenant, U user, String sessionId, AT consideredActionType,
                                                        IT consideredItemType, Double ratingThreshold, Integer numberOfLastActionsConsidered);

    public abstract List<RAT> getDirectItemRatings(T tenant, U user, String sessionId, IT itemType,
                                                   Integer numberOfResults, TimeConstraintVO timeRange,
                                                   Boolean sortDescending, Boolean goodRatingsOnly,
                                                   Integer tenantSpecificIdForRatingAction);
    // public abstract List<RAT> getAggregatedItemRatings(T tenant, U user, String sessionId, IT itemType, Integer numberOfResults, TimeConstraintVO timeRange, Boolean sortDescending, Boolean goodRatingsOnly);


    //////////////////////////////////////////////////////////////////////////////
    // protected methods
    protected String getActionIteratorQueryString() {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(DEFAULT_TABLE_NAME);
        return query.toString();
    }

    protected String getActionIteratorQueryString(TimeConstraintVO timeConstraints, ArgsAndTypesHolder holder) {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(DEFAULT_TABLE_NAME);

        if (timeConstraints.getDateFrom() != null) {
            query.append(" WHERE ");
            query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
            query.append(" >= ?");
            holder.getArgs()[0] = timeConstraints.getDateFrom();
            if (timeConstraints.getDateTo() != null) {
                query.append(" AND ");
                query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
                query.append(" <= ?");
                holder.setArgs( ObjectArrays.concat(holder.getArgs(), timeConstraints.getDateTo()));
                holder.setArgTypes(Ints.concat(holder.getArgTypes(), new int[] { Types.TIMESTAMP }));
            }
        } else {
            query.append(" WHERE ");
            query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
            query.append(" <= ?");
            holder.getArgs()[0] = timeConstraints.getDateTo();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("sending query: " + query);
        }
        return query.toString();
    }

    private class DateResultSetExtractor implements ResultSetExtractor<Date> {
        public Date extractData(ResultSet rs) {
            try {
                if (rs.next()) {
                    return DaoUtils.getDate(rs, DEFAULT_ACTION_TIME_COLUMN_NAME);
                }
            } catch (SQLException e) {
                logger.error("error occured", e);
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    ;
}
