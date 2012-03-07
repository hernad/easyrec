package org.easyrec.store.dao.web.impl;


import org.easyrec.store.dao.web.BackTrackingDAO;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.Types;


/**
 * The Class implements the BacktrackingDAO
 * <p/>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2007
 * <p/>
 * <b>last modified:</b><br/> $Author: phlavac $<br/> $Date: 2008-07-17
 * 20:00:46 +0200 (Do, 17 Jul 2008) $<br/> $Revision: 15249 $
 * </p>
 *
 * @author phlavac
 */

public class BackTrackingDAOMysqlImpl extends JdbcDaoSupport implements BackTrackingDAO {

    private static final String SQL_ADD_ITEM;
    private static final String SQL_COUNT_ITEM;

    private static final int[] ARGTYPES_ADD_ITEM;
    private static final PreparedStatementCreatorFactory PS_ADD_ITEM;

    static {

        SQL_ADD_ITEM = new StringBuilder().append(" INSERT INTO backtracking ")
                .append("    (userId, tenantId, itemFromId, itemToId, assocType, timestamp) VALUES ")
                .append("    (?,?,?,?,?,now()) ").toString();

        SQL_COUNT_ITEM = new StringBuilder().append(" SELECT count(1) FROM backtracking WHERE")
                .append("  tenantId = ? AND itemFromId = ? and itemToId = ? and assocType = ? ").toString();


        ARGTYPES_ADD_ITEM = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};


        PS_ADD_ITEM = new PreparedStatementCreatorFactory(SQL_ADD_ITEM, ARGTYPES_ADD_ITEM);
        PS_ADD_ITEM.setReturnGeneratedKeys(true);
    }

    public BackTrackingDAOMysqlImpl() {}

    public BackTrackingDAOMysqlImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    /*
    * (non-Javadoc)
    *
    * @see at.researchstudio.sat.recommender.remote.store.dao.ItemDAO#saveItem(java.lang.String,
    *      java.lang.String, int, java.lang.String)
    */
    public void track(Integer userId, Integer tenantId, Integer itemFromId, Integer itemToId, Integer assocType) {

        Object[] args = {userId, tenantId, itemFromId, itemToId, assocType};
        try {
            getJdbcTemplate().update(PS_ADD_ITEM.newPreparedStatementCreator(args));
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see at.researchstudio.sat.recommender.remote.store.dao.ItemDAO#saveItem(java.lang.String,
     *      java.lang.String, int, java.lang.String)
     */
    public Integer getItemCount(Integer tenantId, Integer itemFromId, Integer itemToId, Integer assocType) {


        try {
            return getJdbcTemplate()
                    .queryForInt(SQL_COUNT_ITEM, new Object[]{tenantId, itemFromId, itemToId, assocType},
                            new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER});
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e);
            }
        }
        return null;
    }


    /**
     * Clear tenant specific backtracking data.
     *
     * @param tenantId
     */
    public void clear(Integer tenantId) {
        try {
            getJdbcTemplate().update("DELETE FROM backtracking WHERE tenantId = ?", new Object[]{tenantId});
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e);
            }
        }
    }

}