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
package org.easyrec.store.dao.plugin.impl;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.plugin.PluginVO;
import org.easyrec.plugin.model.Version;
import org.easyrec.store.dao.plugin.PluginDAO;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;

import java.net.URI;
import java.sql.*;
import java.util.List;

/**
 * @author szavrel
 */
@DAO
public class PluginDAOMysqlImpl extends JdbcDaoSupport implements PluginDAO {

    private final Log logger = LogFactory.getLog(this.getClass());
    private DefaultLobHandler lobHandler;
    private PlugInRowMapper pluginRowMapper = new PlugInRowMapper();
    private PlugInInfoRowMapper pluginInfoRowMapper = new PlugInInfoRowMapper();

    private static final String SQL_ADD_PLUGIN;
    private static final String SQL_UPDATE_PLUGIN;
    private static final String SQL_LOAD_PLUGIN;
    private static final String SQL_LOAD_PLUGINS;
    private static final String SQL_UPDATE_PLUGIN_STATE;
    private static final String SQL_DELETE_PLUGIN;

    static {

        SQL_ADD_PLUGIN = new StringBuilder().append("INSERT INTO ").append(DEFAULT_TABLE_NAME).append(" (")
                .append(DEFAULT_DISPLAYNAME_COLUMN_NAME).append(",").append(DEFAULT_PLUGINID_COLUMN_NAME).append(",")
                .append(DEFAULT_VERSION_COLUMN_NAME).append(",").append(DEFAULT_ORIG_FILENAME_COLUMN_NAME).append(",")
                .append(DEFAULT_STATE_COLUMN_NAME).append(",").append(DEFAULT_FILE_COLUMN_NAME).append(", ")
                .append(DEFAULT_CHANGEDATE_COLUMN_NAME).append(") ").append("VALUES (?,?,?,?,?,?,?)").toString();

        SQL_UPDATE_PLUGIN = new StringBuilder().append("UPDATE ").append(DEFAULT_TABLE_NAME).append(" SET ")
                .append(DEFAULT_DISPLAYNAME_COLUMN_NAME).append("=?, ").append(DEFAULT_ORIG_FILENAME_COLUMN_NAME)
                .append("=?, ").append(DEFAULT_STATE_COLUMN_NAME).append("=?, ").append(DEFAULT_FILE_COLUMN_NAME)
                .append("=?, ").append(DEFAULT_CHANGEDATE_COLUMN_NAME).append("=? WHERE ")
                .append(DEFAULT_PLUGINID_COLUMN_NAME).append("=? AND ").append(DEFAULT_VERSION_COLUMN_NAME).append("=?")
                .toString();

        SQL_LOAD_PLUGIN = new StringBuilder().append(" SELECT * FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ")
                .append(DEFAULT_PLUGINID_COLUMN_NAME).append("=? AND ").append(DEFAULT_VERSION_COLUMN_NAME).append("=?")
                .toString();

        SQL_LOAD_PLUGINS = new StringBuilder().append(" SELECT * FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ")
                .append(DEFAULT_STATE_COLUMN_NAME).append(" LIKE ?").toString();

        SQL_UPDATE_PLUGIN_STATE = new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME).append(" SET ")
                .append(DEFAULT_STATE_COLUMN_NAME).append("=? WHERE ").append(DEFAULT_PLUGINID_COLUMN_NAME)
                .append("=? AND ").append(DEFAULT_VERSION_COLUMN_NAME).append("=?").toString();

        SQL_DELETE_PLUGIN = new StringBuilder().append("DELETE FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ")
                .append(DEFAULT_PLUGINID_COLUMN_NAME).append("=? AND ").append(DEFAULT_VERSION_COLUMN_NAME).append("=?")
                .toString();

    }

    public PluginDAOMysqlImpl(BasicDataSource dataSource) {
        setDataSource(dataSource);
        lobHandler = new DefaultLobHandler();
    }

    public void storePlugin(PluginVO plugin) {

        final PluginVO pluginParam = plugin;
        try {


            getJdbcTemplate().execute(SQL_ADD_PLUGIN, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {

                @Override
                protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                        throws SQLException, DataAccessException {
                    ps.setString(1, pluginParam.getDisplayName());
                    ps.setString(2, pluginParam.getPluginId().getUri().toString());
                    ps.setString(3, pluginParam.getPluginId().getVersion().toString());
                    ps.setString(4, pluginParam.getOrigFilename());
                    ps.setString(5, pluginParam.getState());
                    lobCreator.setBlobAsBytes(ps, 6, pluginParam.getFile());
                    ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
                }
            });

        } catch (DataIntegrityViolationException e) {

            logger.info("Updating plugin!");
            getJdbcTemplate().execute(SQL_UPDATE_PLUGIN, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {

                @Override
                protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                        throws SQLException, DataAccessException {
                    ps.setString(1, pluginParam.getDisplayName());
                    ps.setString(2, pluginParam.getOrigFilename());
                    ps.setString(3, pluginParam.getState());
                    lobCreator.setBlobAsBytes(ps, 4, pluginParam.getFile());
                    ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    ps.setString(6, pluginParam.getPluginId().getUri().toString());
                    ps.setString(7, pluginParam.getPluginId().getVersion().toString());
                }
            });
        } catch (Exception ex) {
            logger.error("An error occured storing the plugin! " + ex);
        }
    }

    public void deletePlugin(URI pluginId, Version version) {
        try {
            Object[] args = {pluginId.toString(), version.toString()};
            int[] argTypes = {Types.VARCHAR, Types.VARCHAR};

            int rowsAffected = getJdbcTemplate().update(SQL_DELETE_PLUGIN, args, argTypes);

            if (logger.isDebugEnabled()) {
                logger.debug("Deleted " + rowsAffected + " plugins");
            }
        } catch (Exception e) {
            logger.error("An error occured deleting a plugin");
        }
    }

    public void updatePluginState(URI pluginId, Version version, String state) {
        try {
            Object[] args = {state, pluginId.toString(), version.toString()};
            int[] argTypes = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};

            int rowsaffected = getJdbcTemplate().update(SQL_UPDATE_PLUGIN_STATE, args, argTypes);
            if (logger.isDebugEnabled()) {
                logger.debug("Updated " + rowsaffected + " plugin state");
            }
        } catch (Exception e) {
            logger.error("An error occured updating a plugin state! " + e);
        }
    }


    public PluginVO loadPlugin(URI pluginId, Version version) {
        try {
            Object[] args = {pluginId.toString(), version.toString()};
            int[] argTypes = {Types.VARCHAR, Types.VARCHAR};

            return getJdbcTemplate().query(SQL_LOAD_PLUGIN, args, argTypes, pluginRowMapper).get(0);
        } catch (Exception e) {
            logger.error("An error occured loading a plugin! " + e);
        }
        return null;
    }

    public List<PluginVO> loadPlugins() {

        return loadPlugins(null);
    }

    public List<PluginVO> loadPlugins(String state) {
        try {
            if (state == null) state = "%";
            Object[] args = {state};
            int[] argTypes = {Types.VARCHAR};

            return getJdbcTemplate().query(SQL_LOAD_PLUGINS, args, argTypes, pluginRowMapper);
        } catch (Exception e) {
            logger.error("An error occured loading all plugins! " + e);
        }
        return null;
    }

    public List<PluginVO> loadPluginInfos() {
        return loadPluginInfos(null);
    }

    public List<PluginVO> loadPluginInfos(String state) {
        try {
            if (state == null) state = "%";
            Object[] args = {state};
            int[] argTypes = {Types.VARCHAR};

            return getJdbcTemplate().query(SQL_LOAD_PLUGINS, args, argTypes, pluginInfoRowMapper);
        } catch (Exception e) {
            logger.error("An error occured loading all plugin Infos! " + e);
        }
        return null;
    }

    /******************************************************************************************/
    /************************************** Rowmappers ****************************************/
    /**
     * **************************************************************************************
     */

    private class PlugInRowMapper implements RowMapper<PluginVO> {

        public PluginVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            URI pluginId = null;
            try {
                pluginId = new URI(DaoUtils.getStringIfPresent(rs, DEFAULT_PLUGINID_COLUMN_NAME));
            } catch (Exception e) {
                logger.debug(e);
            }

            return new PluginVO(DaoUtils.getIntegerIfPresent(rs, DEFAULT_ID_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_DISPLAYNAME_COLUMN_NAME), pluginId,
                    new Version(DaoUtils.getStringIfPresent(rs, DEFAULT_VERSION_COLUMN_NAME)),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_STATE_COLUMN_NAME),
                    lobHandler.getBlobAsBytes(rs, DEFAULT_FILE_COLUMN_NAME),
                    DaoUtils.getDateIfPresent(rs, DEFAULT_CHANGEDATE_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_ORIG_FILENAME_COLUMN_NAME));
        }

    }

    private class PlugInInfoRowMapper implements RowMapper<PluginVO> {

        public PluginVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            URI pluginId = null;
            try {
                pluginId = new URI(DaoUtils.getStringIfPresent(rs, DEFAULT_PLUGINID_COLUMN_NAME));
            } catch (Exception e) {
                logger.debug(e);
            }

            return new PluginVO(DaoUtils.getIntegerIfPresent(rs, DEFAULT_ID_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_DISPLAYNAME_COLUMN_NAME), pluginId,
                    new Version(DaoUtils.getStringIfPresent(rs, DEFAULT_VERSION_COLUMN_NAME)),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_STATE_COLUMN_NAME), null,
                    DaoUtils.getDateIfPresent(rs, DEFAULT_CHANGEDATE_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_ORIG_FILENAME_COLUMN_NAME));
        }

    }


}
