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

import com.google.common.base.Strings;
import org.easyrec.model.web.Operator;
import org.easyrec.store.dao.web.OperatorDAO;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * The Class is the Implementation of the Operator DAO
 * <p/>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2007
 * <p/>
 * <b>last modified:</b><br/> $Author: pmarschik $<br/> $Date: 2008-07-17
 * 20:00:46 +0200 (Do, 17 Jul 2008) $<br/> $Revision: 18099 $
 * </p>
 *
 * @author <AUTHOR>
 */


public class OperatorDAOMysqlImpl extends BasicDAOMysqlImpl implements OperatorDAO {
    private HashMap<String, Operator> operatorCache = new HashMap<String, Operator>();
    private HashMap<String, Operator> tokenCache = new HashMap<String, Operator>();
    private HashMap<String, Integer> operatorTenantCache = new HashMap<String, Integer>();

    private OperatorRowMapper operatorRowMapper = new OperatorRowMapper();

    @SuppressWarnings({"UnusedDeclaration"})
    private static final String SQL_GET_TENANT_OPERATOR;
    private static final String SQL_GET_OPERATOR_FROM_TOKEN;
    private static final String SQL_GET_OPERATOR;
    private static final String SQL_GET_TENANTID_FROM_OPERATOR;
    private static final String SQL_REMOVE_OPERATOR;
    private static final String SQL_ADD_OPERATOR;
    private static final String SQL_UPDATE_OPERATOR;
    private static final String SQL_UPDATE_PASSWORD;
    private static final String SQL_HAS_TENANTS;
    private static final String SQL_ACTIVATE_OPERATOR;
    private static final String SQL_DEACTIVATE_OPERATOR;
    private static final String SQL_SIGN_IN_OPERATOR;
    private static final String SQL_GET_OPERATORS;
    private static final String SQL_UPDATE_LOGIN;


    static {

        SQL_GET_TENANT_OPERATOR = new StringBuilder().append(" SELECT ").append(" o.").append(DEFAULT_TABLE_KEY)
                .append(" , o.PASSWORD, o.FIRSTNAME, o.LASTNAME, ")
                .append("    o.EMAIL, o.PHONE, o.COMPANY, o.ADDRESS, ")
                .append("    o.APIKEY, o.IP, o.ACTIVE, o.CREATIONDATE, o.ACCESSLEVEL,")
                .append("    o.LOGINCOUNT, o.LASTLOGIN ").append(" FROM ").append(DEFAULT_TABLE_NAME)
                .append(" o INNER JOIN tenant t ON (o.operatorid = t.operatorid) ").append(" WHERE ")
                .append("    INSTR(t.stringid, ?) > 0 AND ").append("    o.APIKEY = ? ").toString();

        SQL_GET_OPERATOR = new StringBuilder().append(" SELECT ").append(DEFAULT_TABLE_KEY)
                .append(" , PASSWORD, FIRSTNAME, LASTNAME, ")
                .append("    EMAIL, PHONE, COMPANY, ADDRESS, APIKEY, IP, ACTIVE, ")
                .append("    CREATIONDATE, ACCESSLEVEL, ").append("    LOGINCOUNT, LASTLOGIN ").append(" FROM ")
                .append(DEFAULT_TABLE_NAME).append(" WHERE ").append(DEFAULT_TABLE_KEY).append(" = ? ").toString();

        SQL_GET_OPERATOR_FROM_TOKEN = new StringBuilder().append(" SELECT ").append(DEFAULT_TABLE_KEY)
                .append(" , PASSWORD, FIRSTNAME, LASTNAME, ")
                .append("    EMAIL, PHONE, COMPANY, ADDRESS, APIKEY, IP, ACTIVE, ")
                .append("    CREATIONDATE, ACCESSLEVEL, ").append("    LOGINCOUNT, LASTLOGIN ").append(" FROM ")
                .append(DEFAULT_TABLE_NAME).append(" WHERE ").append(" token = ? ").toString();

        SQL_GET_TENANTID_FROM_OPERATOR = new StringBuilder().append(" select t.id FROM operator o INNER JOIN ")
                .append("  tenant t ON (t.operatorid = o.operatorid)").append(" WHERE")
                .append(" stringId=? AND apikey =? ").toString();


        SQL_ADD_OPERATOR = new StringBuilder().append(" INSERT INTO ").append(DEFAULT_TABLE_NAME).append("    (")
                .append(DEFAULT_TABLE_KEY).append(", PASSWORD, FIRSTNAME, LASTNAME, ")
                .append("     EMAIL, PHONE, COMPANY, ADDRESS, APIKEY, IP, CREATIONDATE) VALUES ")
                .append("    (?,PASSWORD(?),?,?,?,?,?,?,?,?,?) ").toString();

        SQL_UPDATE_OPERATOR = new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME).append(" SET ")
                .append("     FIRSTNAME = ?, ").append("     LASTNAME = ?, ").append("     EMAIL = ?, ")
                .append("     PHONE = ?, ").append("     COMPANY = ?, ").append("     ADDRESS = ?, ")
                .append("     APIKEY = ?, ").append("     IP = ? ").append(" WHERE ").append("    OPERATORID = ? ")
                .toString();

        SQL_UPDATE_LOGIN = new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME).append(" SET ")
                .append("     LASTLOGIN = NOW(), ").append("     LOGINCOUNT = LOGINCOUNT + 1 ").append(" WHERE ")
                .append("    OPERATORID = ? ").toString();

        SQL_UPDATE_PASSWORD = new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME).append(" SET ")
                .append("     PASSWORD = PASSWORD(?) ").append(" WHERE ").append("    OPERATORID = ? ").toString();

        SQL_REMOVE_OPERATOR = new StringBuilder().append("DELETE FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ")
                .append(DEFAULT_TABLE_KEY).append("= ?").toString();

        SQL_HAS_TENANTS = new StringBuilder().append(" SELECT ").append("    Count(1) ").append(" FROM ")
                .append(DEFAULT_TABLE_NAME).append(" o INNER JOIN tenant t").append(" ON (o.operatorid = t.operatorid)")
                .append(" WHERE ").append("t.").append(DEFAULT_TABLE_KEY).append("  = ? ").toString();

        SQL_ACTIVATE_OPERATOR = new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME)
                .append("    SET ACTIVE = 1 ").append(" WHERE ").append(DEFAULT_TABLE_KEY).append(" = ? ").toString();

        SQL_DEACTIVATE_OPERATOR = new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME)
                .append("    SET ACTIVE = 0 ").append(" WHERE ").append(DEFAULT_TABLE_KEY).append(" = ? ").toString();

        SQL_SIGN_IN_OPERATOR = new StringBuilder().append(" SELECT ").append("    COUNT(1) ").append(" FROM ")
                .append(DEFAULT_TABLE_NAME).append(" WHERE ").append(DEFAULT_TABLE_KEY).append("  = ? AND ")
                .append("  PASSWORD = PASSWORD(?) ").toString();

        SQL_GET_OPERATORS = new StringBuilder().append(" SELECT ").append(DEFAULT_TABLE_KEY)
                .append(" , PASSWORD, FIRSTNAME, LASTNAME, ")
                .append("    EMAIL, PHONE, COMPANY, ADDRESS, APIKEY, IP, ACTIVE, ")
                .append("    CREATIONDATE, ACCESSLEVEL, ").append("    LOGINCOUNT, LASTLOGIN ").append(" FROM ")
                .append(DEFAULT_TABLE_NAME).append(" ORDER BY CREATIONDATE DESC LIMIT ?,? ").toString();
    }

    public OperatorDAOMysqlImpl(DataSource dataSource) {
        super(dataSource);
        this.setTableStringId(DEFAULT_TABLE_KEY);
        this.setTableName(DEFAULT_TABLE_NAME);
    }

    /*
     * (non-Javadoc)
     * 
     * @see at.researchstudio.sat.recommender.remote.store.dao.ItemDAO#addTenant(java.lang.String,
     *      java.lang.String)
     */
    public void add(String id, String password, String firstName, String lastName, String email, String phone,
                    String company, String address, String apiKey, String ip) {

        Object[] args = {id, password, firstName, lastName, email, phone, company, address, apiKey, ip, new Date()};

        int[] argTypes = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP};

        try {
            getJdbcTemplate().update(SQL_ADD_OPERATOR, args, argTypes);

        } catch (Exception e) {
            logger.debug(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see at.researchstudio.sat.recommender.remote.store.dao.OperatorDAO#update(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean update(String operatorId, String firstName, String lastName, String email, String phone,
                          String company, String address, String apiKey, String ip) {
        if (exists(operatorId)) {

            Object[] args = {firstName, lastName, email, phone, company, address, apiKey, ip, operatorId};

            int[] argTypes = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                    Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};

            try {
                getJdbcTemplate().update(SQL_UPDATE_OPERATOR, args, argTypes);
                operatorTenantCache.clear();
                operatorCache.remove(operatorId);
            } catch (Exception e) {
                logger.debug("error updating operator", e);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /*
    * (non-Javadoc)
    * @see at.researchstudio.sat.recommender.remote.store.dao.OperatorDAO#updatePassword(java.lang.String, java.lang.String)
    */
    public void updatePassword(String operatorId, String password) {
        if (exists(operatorId)) {

            Object[] args = {password, operatorId};
            int[] argTypes = {Types.VARCHAR, Types.VARCHAR};

            try {
                getJdbcTemplate().update(SQL_UPDATE_PASSWORD, args, argTypes);
                operatorCache.remove(operatorId);
                operatorTenantCache.clear();
            } catch (Exception e) {
                logger.debug(e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see at.researchstudio.sat.recommender.remote.store.dao.OperatorDAO#getOperator(java.lang.String)
     */
    public Operator get(String id) {
        Operator o = operatorCache.get(id);
        if (o != null) {
            return o;
        } else {
            try {
                o = getJdbcTemplate().queryForObject(SQL_GET_OPERATOR, new Object[]{id}, new int[]{Types.VARCHAR},
                        operatorRowMapper);
                if (o.isActive()) {
                    operatorCache.put(o.getOperatorId(), o);
                }
                return o;
            } catch (Exception e) {
                return null;
            }
        }
    }


    /*
    * (non-Javadoc)
    * @see at.researchstudio.sat.recommender.remote.store.dao.RemoteTenantDAO#access(java.lang.String, java.lang.String)
    */
    public Integer getTenantId(String apiKey, String tenantId) {
        String s = new StringBuilder().
                append(apiKey).
                append(DELIMITER).
                append(tenantId).toString();

        Integer i = operatorTenantCache.get(s);

        if (i != null) {
            return i;
        } else {

            Object[] args = {tenantId, apiKey};
            int[] argTypes = {Types.VARCHAR, Types.VARCHAR};

            try {
                i = getJdbcTemplate().queryForInt(SQL_GET_TENANTID_FROM_OPERATOR, args, argTypes);

                operatorTenantCache.put(s, i);
                return i;

            } catch (Exception e) {
                logger.debug(e);
                return null;
            }
        }
    }


    /*
    * (non-Javadoc)
    *
    * @see at.researchstudio.sat.recommender.remote.store.dao.OperatorDAO#removeOperator(java.lang.String)
    */
    public void remove(String id) {

        Object[] args = {id};
        int[] argTypes = {Types.VARCHAR};

        try {
            getJdbcTemplate().update(SQL_REMOVE_OPERATOR, args, argTypes);
            operatorCache.remove(id);
            operatorTenantCache.clear();

        } catch (Exception e) {
            logger.debug(e);
        }
    }


    /*
    * (non-Javadoc)
    * @see at.researchstudio.sat.recommender.remote.store.dao.OperatorDAO#hasTenants(java.lang.String)
    */
    public boolean hasTenants(String operatorId) {
        Object[] args = {operatorId};
        int[] argTypes = {Types.VARCHAR};

        try {
            return getJdbcTemplate().queryForInt(SQL_HAS_TENANTS, args, argTypes) > 0;
        } catch (Exception e) {
            logger.debug(e);
            return false;
        }
    }

    /*
    * (non-Javadoc)
    *
    * @see at.researchstudio.sat.recommender.remote.store.dao.OperatorDAO#setApiKey(java.lang.String,
    *      java.lang.String)
    */
    public boolean activate(String id, String apiKey) {
        Operator o = get(id);

        if (o != null && apiKey.length() > 0) {
            if (apiKey.equals(o.getApiKey())) {

                Object[] args = {id};
                int[] argTypes = {Types.VARCHAR};

                try {
                    getJdbcTemplate().update(SQL_ACTIVATE_OPERATOR, args, argTypes);
                    operatorCache.remove(id);
                    operatorTenantCache.clear();
                } catch (Exception e) {
                    logger.debug(e);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see at.researchstudio.sat.recommender.remote.store.dao.OperatorDAO#deactivate(java.lang.String)
     */
    public boolean deactivate(String operatorId) {
        Operator o = get(operatorId);

        if (o != null) {

            Object[] args = {operatorId};
            int[] argTypes = {Types.VARCHAR};

            try {
                getJdbcTemplate().update(SQL_DEACTIVATE_OPERATOR, args, argTypes);
                operatorCache.remove(operatorId);
                operatorTenantCache.clear();
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see at.researchstudio.sat.recommender.remote.store.dao.OperatorDAO#signedIn(java.lang.String,
     *      java.lang.String)
     */
    public Operator signIn(String operatorId, String password) {
        try {
            if (getJdbcTemplate().queryForInt(SQL_SIGN_IN_OPERATOR, new Object[]{operatorId, password},
                    new int[]{Types.VARCHAR, Types.VARCHAR}) > 0) {
                getJdbcTemplate().update(SQL_UPDATE_LOGIN, new Object[]{operatorId}, new int[]{Types.VARCHAR});
                operatorCache.remove(operatorId);
                operatorTenantCache.clear();
                return get(operatorId);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see at.researchstudio.sat.recommender.remote.store.dao.CoreTenantDAO#getTenants()
     */
    public List<Operator> getOperators(int offset, int limit) {
        try {
            return getJdbcTemplate()
                    .query(SQL_GET_OPERATORS, new Object[]{offset, limit}, new int[]{Types.INTEGER, Types.INTEGER},
                            operatorRowMapper);
        } catch (Exception e) {
            logger.debug(e);
            return null;
        }
    }

    /**
     * This function returns true if the given passwort matches to the password
     * of the given operatorid
     */
    public boolean correctPassword(String operatorId, String password) {
        try {
            return (getJdbcTemplate()
                            .queryForInt("SELECT COUNT(1) FROM operator WHERE operatorid= ? AND password = PASSWORD(?)",
                                    new Object[]{operatorId, password}, new int[]{Types.VARCHAR, Types.VARCHAR}) > 0);
        } catch (Exception e) {
            logger.debug(e);
            return false;
        }
    }

    /**
     * Gets an operator for a given security token.
     * A token is genrated if an operator signs in and stays valid until signed out.
     */
    public Operator getOperatorFromToken(String token) {
        Operator o = tokenCache.get(token);
        if (o != null) {
            return o;
        } else {
            try {
                o = getJdbcTemplate()
                        .queryForObject(SQL_GET_OPERATOR_FROM_TOKEN, new Object[]{token}, new int[]{Types.VARCHAR},
                                operatorRowMapper);
                if (o.isActive()) {
                    tokenCache.put(token, o);
                }
                return o;
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Sets a security token for the given operator.
     */
    public void setTokenForOperator(String token, String operatorId) {

        if (!Strings.isNullOrEmpty(operatorId)) {
            Object[] args = {token, operatorId};
            int[] argTypes = {Types.VARCHAR, Types.VARCHAR,};

            try {
                getJdbcTemplate().update(new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME)
                        .append(" SET  token = ? WHERE OPERATORID = ? ").toString(), args, argTypes);
                tokenCache.clear();
            } catch (Exception e) {
                logger.debug(e);
            }
        }
    }


    /**
     * Removes the security token from the given operator.
     * This method may be called, when a session invalidates.
     */
    public void removeTokenFromOperator(String operatorId) {
        setTokenForOperator(null, operatorId);
    }

    /**
     * gets a Token based on the number of actions.
     */
    public int getToken() {
        return getJdbcTemplate().queryForInt("SELECT COUNT(1) FROM action");
    }


    /** *************************************************************************************** */
    /**
     * ************************************ Rowmappers
     * ***************************************
     */
    /**
     * **************************************************************************************
     */

    private class OperatorRowMapper implements RowMapper<Operator> {
        public Operator mapRow(ResultSet rs, int rowNum) throws SQLException {
            //Date lastlogin = DaoUtils.getDateIfPresent(rs,DEFAULT_LAST_LOGIN_DATE_COLUMN_NAME);

            return new Operator(DaoUtils.getStringIfPresent(rs, DEFAULT_OPERATORID_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_PASSWORD_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_FIRSTNAME_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_LASTNAME_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_EMAIL_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_PHONE_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_COMPANY_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_ADDRESS_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_APIKEY_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_IP_COLUMN_NAME),
                    DaoUtils.getBoolean(rs, DEFAULT_ACTIVE_COLUMN_NAME),
                    //DaoUtils.getDateIfPresent(rs, DEFAULT_CREATIONDATE_COLUMN_NAME).toString(),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_CREATIONDATE_COLUMN_NAME),
                    DaoUtils.getIntegerIfPresent(rs, DEFAULT_ACCESSLEVEL_COLUMN_NAME),
                    DaoUtils.getIntegerIfPresent(rs, DEFAULT_LOGIN_COUNT_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_LAST_LOGIN_DATE_COLUMN_NAME)
                    //lastlogin!=null?lastlogin.toString():null
            );
        }
    }
}