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
package org.easyrec.store.dao.web.impl;

import org.easyrec.store.dao.web.AssocDAO;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Types;

/**
 * The Class implements the ItemDAO.
 * <p/>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2007
 * <p/>
 * <b>last modified:</b><br/> $Author: phlavac $<br/> $Date: 2008-07-17
 * 20:00:46 +0200 (Do, 17 Jul 2008) $<br/> $Revision: 16095 $
 * </p>
 *
 * @author <AUTHOR>
 */

public class AssocDAOMysqlImpl extends BasicDAOMysqlImpl implements AssocDAO {

    private static final String SQL_ADD_RULE;
    private static final String SQL_ACTIVATE_RULE;
    private static final String SQL_DEACTIVATE_RULE;

    private static final int[] ARGTYPES_ADD_RULE;
    private static final int[] ARGTYPES_SETSTATUS_RULE;


    private static final PreparedStatementCreatorFactory PS_ADD_RULE;


    static {

        SQL_ADD_RULE = new StringBuilder().append(" INSERT INTO itemassoc").append("    (TENANTID, ")
                .append("     ITEMFROMID, ITEMFROMTYPEID, ITEMTOID, ITEMTOTYPEID, ")
                .append("     ASSOCTYPEID, ASSOCVALUE, ").append("     sourceTypeId, sourceInfo, ")
                .append("     viewTypeId, active, ").append("     changeDate ").append("     ) VALUES ")
                .append("    (?,?,?,?,?,?,?,?,?,?,?,?) ").toString();
        //.append(" ON DUPLICATE KEY UPDATE ")
        //.append("    DESCRIPTION = ?, ")
        //.append("    URL = ? ");


        SQL_ACTIVATE_RULE = new StringBuilder().append(" UPDATE itemassoc ").append(" SET active = 1 ")
                .append(" WHERE TENANTID         = ? AND ").append("       ITEMFROMID       = ? AND ")
                .append("       ITEMFROMTYPEID   = ? AND ").append("       ITEMTOID         = ? AND ")
                .append("       ITEMTOTYPEID     = ? AND ").append("       ITEMID   = ? AND ")
                .append("       ITEMTYPE = ? ").toString();

        SQL_DEACTIVATE_RULE = new StringBuilder().append(" UPDATE itemassoc ").append(" SET active = 0 ")
                .append(" WHERE TENANTID = ? AND ").append("       ITEMFROMID   = ? AND ")
                .append("       ITEMFROMTYPEID = ? ").toString();

        ARGTYPES_ADD_RULE = new int[]{Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR};
        ARGTYPES_SETSTATUS_RULE = new int[]{Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR};

        PS_ADD_RULE = new PreparedStatementCreatorFactory(SQL_ADD_RULE, ARGTYPES_ADD_RULE);
        PS_ADD_RULE.setReturnGeneratedKeys(true);


    }

    public AssocDAOMysqlImpl(DataSource dataSource) {
        super(dataSource);
        this.setTableId("id");
        this.setTableName("itemassoc");
    }

    /*
    * (non-Javadoc)
    *
    * @see at.researchstudio.sat.recommender.remote.store.dao.ItemDAO#saveItem(java.lang.String,
    *      java.lang.String, int, java.lang.String)
    */
    public void add(Integer tenantId, Integer itemFromId, Integer itemFromType, Integer itemToId, Integer itemToTypeId,
                    Integer assocType, Float assocValue, String url, String imageurl) {

        Object[] args = {tenantId, itemFromId, itemFromType, itemToId, itemToTypeId, imageurl};

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            int rowsAffected = getJdbcTemplate().update(PS_ADD_RULE.newPreparedStatementCreator(args), keyHolder);


        } catch (Exception e) {
            logger.debug(e);
        }
    }


    /*
    * (non-Javadoc)
    * @see at.researchstudio.sat.recommender.remote.store.dao.ItemDAO#activate(java.lang.String, java.lang.String, java.lang.String)
    */
    public void activate(Integer tenantId, Integer itemFromId, Integer itemToId) {
        Object[] args = {tenantId, itemFromId, itemToId};
        try {
            getJdbcTemplate().update(SQL_ACTIVATE_RULE, args, ARGTYPES_SETSTATUS_RULE);

        } catch (Exception e) {
            logger.debug(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see at.researchstudio.sat.recommender.remote.store.dao.ItemDAO#deactivate(java.lang.String, java.lang.String, java.lang.String)
     */
    public void deactivate(Integer tenantId, Integer itemFromId, Integer itemToId) {
        Object[] args = {tenantId, itemFromId, itemToId};
        try {
            getJdbcTemplate().update(SQL_DEACTIVATE_RULE, args, ARGTYPES_SETSTATUS_RULE);

        } catch (Exception e) {
            logger.debug(e);
        }
    }

}