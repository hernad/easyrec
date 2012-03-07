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
package org.easyrec.utils.spring.store.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Provides static utility methods for JDBC code like creating SQL lists etc.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2005</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fr, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Florian Kleedorfer
 */

public class DaoUtils {
    ///////////////////////////////////////////////////////////////////////////////
    // constants
    public static final String ORDER_ASC = "ASC";
    public static final String ORDER_DESC = "DESC";

    ///////////////////////////////////////////////////////////////////////////////
    // members
    private static final Log logger = LogFactory.getLog(DaoUtils.class);

    // --------------------------------------------------------------------------
    // getter methods
    // --------------------------------------------------------------------------


    /**
     * get a Long object from a result set column with given name
     *
     * @param rs
     * @param columnName
     * @return the value, which may be <code>null</code>
     * @throws SQLException
     */
    public static Long getLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    /**
     * get an Integer object from a result set column with given name
     *
     * @param rs
     * @param columnName
     * @return the value, which may be <code>null</code>
     * @throws SQLException
     */
    public static Integer getInteger(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    /**
     * get a Boolean object from a result set column with given name
     *
     * @param rs
     * @param columnName
     * @return the value, which may be <code>null</code>
     * @throws SQLException
     */
    public static Boolean getBoolean(ResultSet rs, String columnName) throws SQLException {
        boolean value = rs.getBoolean(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    /**
     * get a java.util.Date object from a result set column with sql type TIMESTAMP and given name
     *
     * @param rs
     * @param columnName
     * @return the value, which may be <code>null</code>
     * @throws SQLException
     */
    public static Date getDate(ResultSet rs, String columnName) throws SQLException {
        Timestamp ts = rs.getTimestamp(columnName);
        if (ts == null) {
            return null;
        }
        return new Date(ts.getTime());
    }

    /**
     * get a Double from a result set column with given name
     *
     * @param rs
     * @param columnName
     * @return the value, which may be <code>null</code>
     * @throws SQLException
     */
    public static Double getDouble(ResultSet rs, String columnName) throws SQLException {
        double val = rs.getDouble(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return val;
    }


    // --------------------------------------------------------------------------
    // 'getIfPresent' methods
    // --------------------------------------------------------------------------

    /**
     * get a Long object from a result set column with given name or null if no such
     * column exists in the result set
     *
     * @param rs
     * @param columnName
     * @return the value, which may be <code>null</code>
     * @throws SQLException
     */
    public static Long getLongIfPresent(ResultSet rs, String columnName) throws SQLException {
        try {
            rs.findColumn(columnName);
        } catch (SQLException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("getLong() failed for column '" + columnName + "'. ResultSet doesn't contain that column");
            }
            return null;
        }
        return getLong(rs, columnName);
    }

    /**
     * get a Integer object from a result set column with given name or null if no such
     * column exists in the result set
     *
     * @param rs
     * @param columnName
     * @return the value, which may be <code>null</code>
     * @throws SQLException
     */
    public static Integer getIntegerIfPresent(ResultSet rs, String columnName) throws SQLException {
        try {
            rs.findColumn(columnName);
        } catch (SQLException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "getInteger() failed for column '" + columnName + "'. ResultSet doesn't contain that column");
            }
            return null;
        }
        return getInteger(rs, columnName);
    }

    /**
     * get a Boolean object from a result set column with given name or null if no such
     * column exists in the result set
     *
     * @param rs
     * @param columnName
     * @return the value, which may be <code>null</code>
     * @throws SQLException
     */
    public static Boolean getBooleanIfPresent(ResultSet rs, String columnName) throws SQLException {
        try {
            rs.findColumn(columnName);
        } catch (SQLException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "getBoolean() failed for column '" + columnName + "'. ResultSet doesn't contain that column");
            }
            return null;
        }
        return getBoolean(rs, columnName);
    }

    /**
     * get a Date object from a result set column with given name or null if no such
     * column exists in the result set
     *
     * @param rs
     * @param columnName
     * @return the value, which may be <code>null</code>
     * @throws SQLException
     */
    public static Date getDateIfPresent(ResultSet rs, String columnName) throws SQLException {
        try {
            rs.findColumn(columnName);
        } catch (SQLException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("getDate() failed for column '" + columnName + "'. ResultSet doesn't contain that column");
            }
            return null;
        }
        return getDate(rs, columnName);
    }


    /**
     * get a Double object from a result set column with given name or null if no such
     * column exists in the result set
     *
     * @param rs
     * @param columnName
     * @return the value, which may be <code>null</code>
     * @throws SQLException
     */
    public static Double getDoubleIfPresent(ResultSet rs, String columnName) throws SQLException {
        try {
            rs.findColumn(columnName);
        } catch (SQLException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "getDouble() failed for column '" + columnName + "'. ResultSet doesn't contain that column");
            }
            return null;
        }
        return getDouble(rs, columnName);
    }


    /**
     * get a String object from a result set column with given name or null if no such
     * column exists in the result set
     *
     * @param rs         rs
     * @param columnName columnName
     * @return the value, which may be <code>null</code>
     * @throws SQLException SQLException
     */
    public static String getStringIfPresent(ResultSet rs, String columnName) throws SQLException {
        int colIndex;
        try {
            colIndex = rs.findColumn(columnName);
        } catch (SQLException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "getString() failed for column '" + columnName + "'. ResultSet doesn't contain that column");
            }
            return null;
        }
        return rs.getString(colIndex);
    }
    // --------------------------------------------------------------------------
    // setter methods
    // --------------------------------------------------------------------------


    /**
     * set a java.lang.Long value in the given preparedStatement object, or set it to null if the
     * given Long is null
     *
     * @param stmt
     * @param value
     * @param index
     * @throws SQLException
     */
    public static void setLong(PreparedStatement stmt, Long value, int index) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.BIGINT);
            return;
        }
        stmt.setLong(index, value);
    }

    /**
     * set a java.lang.Integer value in the given preparedStatement object, or set it to null if the
     * given Integer is null
     *
     * @param stmt
     * @param value
     * @param index
     * @throws SQLException
     */
    public static void setInteger(PreparedStatement stmt, Integer value, int index) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.INTEGER);
            return;
        }
        stmt.setInt(index, value);
    }

    /**
     * set a java.lang.Integer value in the given preparedStatement object, or set it to null if the
     * given Integer is null
     *
     * @param stmt
     * @param value
     * @param index
     * @throws SQLException
     */
    public static void setShort(PreparedStatement stmt, Short value, int index) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.SMALLINT);
            return;
        }
        stmt.setShort(index, value);
    }

    /**
     * set a java.util.Date value in the given PreparedStatement object, or set it to null if the
     * given Date is null
     *
     * @param stmt
     * @param value
     * @param index
     * @throws SQLException
     */
    public static void setDate(PreparedStatement stmt, Date value, int index) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.TIMESTAMP);
            return;
        }
        Timestamp ts = new Timestamp(value.getTime());
        stmt.setTimestamp(index, ts);
    }

    public static void setBoolean(PreparedStatement stmt, Boolean value, int index) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.BOOLEAN);
            return;
        }
        stmt.setBoolean(index, value);
    }

    public static void setDouble(PreparedStatement stmt, Double value, int index) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.DOUBLE);
            return;
        }
        stmt.setDouble(index, value);
    }


    // --------------------------------------------------------------------------
    // sql list methods
    // --------------------------------------------------------------------------


    /**
     * Creates a list that can be used for IN queries in sql.
     * e.g. if the given collection is a Vector containing the
     * values 1, 2, 4, 5, "(1,2,4,5)" will be returned
     *
     * @param values
     * @param quoteValues if true, values are quoted.
     * @return
     */
    public static String createSqlList(Iterable values, boolean quoteValues) {
        if (values == null || !values.iterator().hasNext()) {
            return "()";
        }
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (final Object value : values) {
            Object o = value;
            if (quoteValues) {
                buf.append("'");
            }
            buf.append(o);
            if (quoteValues) {
                buf.append("'");
            }
            buf.append(',');
        }
        buf.delete(buf.length() - 1, buf.length());
        buf.append(')');
        return buf.toString();
    }

    /**
     * Creates a list that can be used for IN queries in sql.
     * e.g. if the given array containing the
     * values 1, 2, 4, 5, "(1,2,4,5)" will be returned
     *
     * @param values
     * @param quoteValues f true, values are quoted.
     * @return
     */
    public static String createSqlList(Object[] values, boolean quoteValues) {
        if (values == null || values.length == 0) {
            return "()";
        }
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (Object o : values) {
            if (quoteValues) {
                buf.append("'");
            }
            buf.append(o);
            if (quoteValues) {
                buf.append("'");
            }
            buf.append(',');
        }
        buf.delete(buf.length() - 1, buf.length());
        buf.append(')');
        return buf.toString();
    }

    /**
     * Checks if a given database table is found in the given <code>DataSource</code>.
     *
     * @param dataSource
     * @param tableName
     * @return true if table exists, false if not
     */
    public static boolean existsTable(DataSource dataSource, final String tableName) {
        DatabaseMetaDataCallback callback = new DatabaseMetaDataCallback() {
            public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
                ResultSet rs = dbmd.getTables(null, null, tableName, null);
                return rs.next();
            }
        };
        try {
            return (Boolean) JdbcUtils.extractDatabaseMetaData(dataSource, callback);
        } catch (Exception e) {
            throw new RuntimeException("unable to read database metadata", e);
        }
    }

    /**
     * @return the current database url and user name (for a given Datasource)
     * @throws RuntimeException when the database metadata cannot be retrieved
     */
    public static String getDatabaseURLAndUserName(DataSource dataSource) {
        DatabaseMetaDataCallback callback = new DatabaseMetaDataCallback() {
            public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
                String url = dbmd.getURL();
                String userName = dbmd.getUserName();

                StringBuilder s = new StringBuilder(url);
                s.append(" (userName='");
                s.append(userName);
                s.append("')");
                return s.toString();
            }
        };
        try {
            return (String) JdbcUtils.extractDatabaseMetaData(dataSource, callback);
        } catch (Exception e) {
            throw new RuntimeException("unable to read database metadata", e);
        }
    }

    // --------------------------------------------------------------------------
    // public inner classes
    // --------------------------------------------------------------------------
    public static class ArgsAndTypesHolder {
        private Object[] args;
        private int[] argTypes;

        public ArgsAndTypesHolder(Object[] args, int[] argTypes) {
            this.args = args;
            this.argTypes = argTypes;
        }

        public Object[] getArgs() {
            return args;
        }

        public void setArgs(Object[] args) {
            this.args = args;
        }

        public int[] getArgTypes() {
            return argTypes;
        }

        public void setArgTypes(int[] argTypes) {
            this.argTypes = argTypes;
        }
    }
}
