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
package org.easyrec.utils.spring.store.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.utils.spring.cache.annotation.LongCacheable;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.IDMappingDAO;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * This class provides a Mysql implementation of the {@link org.easyrec.utils.spring.store.dao.IDMappingDAO} interface.
 * It provides methods to map Integer IDs to String IDs;
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fr, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Stephan Zavrel
 */

@DAO
public class IDMappingDAOMysqlImpl extends AbstractTableCreatingDAOImpl implements IDMappingDAO {
    // constants
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:/sql/IDMapping.sql";
    private final static String LOOKUP_BY_INT_QUERY;
    private final static String INSERT_BY_STRING_QUERY;
    private final static String LOOKUP_BY_STRING_QUERY;

    private final static int[] ARG_TYPES_INSERT_BY_STRING;
    private final static int[] ARG_TYPES_LOOKUP_BY_STRING;
    private final static int[] ARG_TYPES_LOOKUP_BY_INT;
    private static final PreparedStatementCreatorFactory PS_INSERT_BY_STRING;
    private static final PreparedStatementCreatorFactory PS_LOOKUP_BY_STRING;
    private static final PreparedStatementCreatorFactory PS_LOOKUP_BY_INT;


    private final Object dbLock = new Object();

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    private IntRowMapper intRowMapper = new IntRowMapper();
    private StringRowMapper stringRowMapper = new StringRowMapper();


    static {

        LOOKUP_BY_INT_QUERY = new StringBuilder().append("SELECT ").append(DEFAULT_STRING_ID_COLUMN_NAME)
                .append(" FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ").append(DEFAULT_INT_ID_COLUMN_NAME)
                .append("=?").toString();

        INSERT_BY_STRING_QUERY = new StringBuilder().append("INSERT INTO ").append(DEFAULT_TABLE_NAME).append(" (")
                .append(DEFAULT_STRING_ID_COLUMN_NAME).append(") VALUES(?)").toString();

        LOOKUP_BY_STRING_QUERY = new StringBuilder().append("SELECT ").append(DEFAULT_INT_ID_COLUMN_NAME)
                .append(" FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ").append(DEFAULT_STRING_ID_COLUMN_NAME)
                .append("=?").toString();

        ARG_TYPES_INSERT_BY_STRING = new int[]{Types.VARCHAR};
        ARG_TYPES_LOOKUP_BY_STRING = new int[]{Types.VARCHAR};
        ARG_TYPES_LOOKUP_BY_INT = new int[]{Types.INTEGER};

        PS_INSERT_BY_STRING = new PreparedStatementCreatorFactory(INSERT_BY_STRING_QUERY, ARG_TYPES_INSERT_BY_STRING);
        PS_INSERT_BY_STRING.setReturnGeneratedKeys(true);

        PS_LOOKUP_BY_STRING = new PreparedStatementCreatorFactory(LOOKUP_BY_STRING_QUERY, ARG_TYPES_LOOKUP_BY_STRING);

        PS_LOOKUP_BY_INT = new PreparedStatementCreatorFactory(LOOKUP_BY_INT_QUERY, ARG_TYPES_LOOKUP_BY_INT);
    }

    // constructor
    public IDMappingDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);

        // output connection information
        if (logger.isInfoEnabled()) {
            try {
                logger.info(DaoUtils.getDatabaseURLAndUserName(dataSource));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    @Override
    public String getDefaultTableName() {
        return DEFAULT_TABLE_NAME;
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SQL_SCRIPT_NAME;
    }

    @LongCacheable
    public Integer lookup(String id) {
        List<Integer> retList;
        if (id == null) {
            return null;
        }
        if (id.length() == 0) {
            throw new IllegalArgumentException("id must not be an empty String!");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("looking up mapping for String: " + id);
        }

        Object[] args = new Object[]{id};
        KeyHolder keyHolder = new GeneratedKeyHolder();
        synchronized (dbLock) {
            try {

                int rowsAffected = getJdbcTemplate()
                        .update(PS_INSERT_BY_STRING.newPreparedStatementCreator(args), keyHolder);
                assert rowsAffected == 1;
                return keyHolder.getKey().intValue();
            } catch (DataIntegrityViolationException e) {
                retList = getJdbcTemplate().query(PS_LOOKUP_BY_STRING.newPreparedStatementCreator(args), intRowMapper);
            }
        }
        return (!retList.isEmpty()) ? retList.get(0) : null;
    }

    @LongCacheable
    public String lookup(Integer id) {
        List<String> retList;
        if (id == null) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("looking up mapping for Integer: " + id);
        }
        Object[] args = new Object[]{id};
        synchronized (dbLock) {
            //PS_LOOKUP_BY_INT
            retList = getJdbcTemplate().query(PS_LOOKUP_BY_INT.newPreparedStatementCreator(args), stringRowMapper);
        }

        return (!retList.isEmpty()) ? retList.get(0) : null;
    }


    /******************************************************************************************/
    /************************************** Rowmappers ****************************************/
    /**
     * **************************************************************************************
     */

    private class IntRowMapper implements RowMapper<Integer> {
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            //return DaoUtils.getIntegerIfPresent(rs, DEFAULT_INT_ID_COLUMN_NAME);
            return rs.getInt(DEFAULT_INT_ID_COLUMN_NAME);
        }
    }

    private class StringRowMapper implements RowMapper<String> {
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            //return DaoUtils.getStringIfPresent(rs, DEFAULT_STRING_ID_COLUMN_NAME);
            return rs.getString(DEFAULT_STRING_ID_COLUMN_NAME);
        }
    }
}
