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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.web.Operator;
import org.easyrec.store.dao.web.LoaderDAO;
import org.easyrec.store.dao.web.OperatorDAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * @author szavrel
 */
public class LoaderDAOMysqlImpl extends JdbcDaoSupport
        implements LoaderDAO, ApplicationListener, ApplicationContextAware {

    private final Log logger = LogFactory.getLog(LoaderDAOMysqlImpl.class);

    private SqlScriptService sqlScriptService;
    private Properties properties;
    private Resource overrideFolder;
    private Resource dbCreationFile;
    private Resource dbMigrateFolder;
    private List<String> migrateFiles;
    private ApplicationContext applicationContext;
    private HashMap<String, String> configLocations;
    private String currentVersion;

    private static final String SQL_ADD_OPERATOR;

    static {
        SQL_ADD_OPERATOR = new StringBuilder().append(" INSERT INTO ").append(OperatorDAO.DEFAULT_TABLE_NAME)
                .append("    (").append(OperatorDAO.DEFAULT_TABLE_KEY).append(", PASSWORD, FIRSTNAME, LASTNAME, ")
                .append("     EMAIL, PHONE, COMPANY, ADDRESS, APIKEY, IP, CREATIONDATE, ACTIVE, ACCESSLEVEL) VALUES ")
                .append("    (?,PASSWORD(?),?,?,?,?,?,?,?,?,?,?,?) ").toString();
    }

    public LoaderDAOMysqlImpl(BasicDataSource dataSource, SqlScriptService sqlScriptService, Resource dbCreationFile,
                              Resource dbMigrateFolder) {
        setDataSource(dataSource);
        this.sqlScriptService = sqlScriptService;
        this.dbCreationFile = dbCreationFile;
        this.dbMigrateFolder = dbMigrateFolder;
    }

    @Override
    public void testConnection(String url, String username, String password) throws Exception {

        BasicDataSource bds = (BasicDataSource) getDataSource();
        bds.setUrl(url);
        bds.setUsername(username);
        bds.setPassword(password);

        boolean tablesOk = false;

        DatabaseMetaDataCallback callback = new DatabaseMetaDataCallback() {
            public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
                ResultSet rs = dbmd.getTables(null, null, "operator", null);
                return rs.next();
            }
        };

        tablesOk = (Boolean) JdbcUtils.extractDatabaseMetaData(bds, callback);
    }

    @Override
    public void createDB() throws Exception {

        BasicDataSource bds = (BasicDataSource) getDataSource();
        boolean tablesOk = false;

        DatabaseMetaDataCallback callback = new DatabaseMetaDataCallback() {
            public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
                ResultSet rs = dbmd.getTables(null, null, "operator", null);
                return rs.next();
            }
        };

        tablesOk = (Boolean) JdbcUtils.extractDatabaseMetaData(bds, callback);
        sqlScriptService.executeSqlScript(dbCreationFile.getInputStream());
    }

    @Override
    public void migrateDB() throws Exception {

        BasicDataSource bds = (BasicDataSource) getDataSource();
        boolean tablesOk = false;

        DatabaseMetaDataCallback callback = new DatabaseMetaDataCallback() {
            public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
                ResultSet rs = dbmd.getTables(null, null, "operator", null);
                return rs.next();
            }
        };

        tablesOk = (Boolean) JdbcUtils.extractDatabaseMetaData(bds, callback);
        Float installedVersion = checkVersion();

        for (String migrateFile : migrateFiles) {
            logger.info("migrate File: " + migrateFile);
            Float scriptVersion = Float.parseFloat(migrateFile.substring(migrateFile.lastIndexOf("_") + 1));
            logger.info("scriptVersion: " + scriptVersion);
            if (installedVersion < scriptVersion) {
                File f = new File(dbMigrateFolder.getFile(), migrateFile + ".sql");
                if (f.exists()) {
                    logger.info("Executing migrate script: " + f.getName());
                    sqlScriptService.executeSqlScript(new FileSystemResource(f).getInputStream());
                }
            }
        }

        if (installedVersion < 0.96f) {
            // easyrec pre 0.96 stored settings for plugins in the tenantConfig column of the tenantsTable from 0.96
            // onwards they are stored as XML serialized GeneratorConfigurations in the plugin_configuration  table
            // this snippet converts the existing ARM configurations (and only ARM configurations) to the new XML
            // version.

             final String RENAME_SOURCETYPE_QUERY =
                            "UPDATE sourcetype SET name=? WHERE name=?";
            getJdbcTemplate().update(RENAME_SOURCETYPE_QUERY, "http://www.easyrec.org/plugins/ARM/0.96", "ARM");
            getJdbcTemplate().update(RENAME_SOURCETYPE_QUERY, "http://www.easyrec.org/plugins/slopeone/0.96", "http://www.easyrec.org/plugins/slopeone/0.95");

            ResultSetExtractor<Map<Integer, String>> rse = new ResultSetExtractor<Map<Integer, String>>() {
                public Map<Integer, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
                    Map<Integer, String> result = Maps.newHashMap();

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String config = rs.getString("tenantConfig");
                        result.put(id, config);
                    }

                    return result;
                }
            };

            Map<Integer, String> tenantConfigs = getJdbcTemplate().query("SELECT id, tenantConfig FROM tenant", rse);

            for (Map.Entry<Integer, String> tenantConfig : tenantConfigs.entrySet()) {
                String tenantConfigString = tenantConfig.getValue();

                if (tenantConfigString == null)
                    tenantConfigString = "";

                int tenantId = tenantConfig.getKey();
                StringReader reader = new StringReader(tenantConfigString);

                Properties propertiesConfig = new Properties();
                propertiesConfig.load(reader);

                String configViewedTogether =
                        generateXmlConfigurationFromProperties(propertiesConfig, "VIEWED_TOGETHER", "VIEW");
                String configGoodRatedTogether =
                        generateXmlConfigurationFromProperties(propertiesConfig, "GOOD_RATED_TOGETHER", "RATE");
                String configBoughtTogether =
                        generateXmlConfigurationFromProperties(propertiesConfig, "BOUGHT_TOGETHER", "BUY");

                // write back config with arm settings removed
//                getJdbcTemplate().update("UPDATE tenant SET tenantConfig = ? WHERE id = ?",
//                        propertiesConfig.toString(), tenantConfig.getKey());

                final String CONFIG_QUERY =
                        "INSERT INTO plugin_configuration(tenantId, assocTypeId, pluginId, pluginVersion, name, configuration, active) VALUES " +
                                "(?, ?, 'http://www.easyrec.org/plugins/ARM', ?, 'Default Configuration', ?, b'1')";

                // generate configuration entries
                getJdbcTemplate().update(CONFIG_QUERY, tenantId, 1, currentVersion, configViewedTogether);
                getJdbcTemplate().update(CONFIG_QUERY, tenantId, 2, currentVersion, configGoodRatedTogether);
                getJdbcTemplate().update(CONFIG_QUERY, tenantId, 3, currentVersion, configBoughtTogether);

                final String slopeOneXmlConfig =
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<slopeOneConfiguration>" +
                                "<configurationName>Default Configuration</configurationName>" +
                                "<associationType>IS_RELATED</associationType>" +
                                "<maxRecsPerItem>10</maxRecsPerItem>" +
                                "<minRatedCount xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                                "<nonPersonalizedSourceInfo>slopeone-nonpersonalized</nonPersonalizedSourceInfo>" +
                                "<actionType>RATE</actionType><itemTypes/>" +
                                "<viewType>COMMUNITY</viewType>" +
                                "</slopeOneConfiguration>";

                final String GET_ISRELATED_ASSOCTYPE_QUERY =
                        "SELECT id FROM assoctype WHERE name = 'IS_RELATED' AND tenantId = ?";
                int isRelatedAssocType = 0;
                // for tenants without a type 'IS_RELATED' this throws an exception - so catch!!
                try {
                    isRelatedAssocType = getJdbcTemplate().queryForInt(GET_ISRELATED_ASSOCTYPE_QUERY, tenantId);
                } catch (EmptyResultDataAccessException erdar) {
                    isRelatedAssocType = 0;
                }

                if (isRelatedAssocType == 0) {
                    final String GET_MAX_ASSOCTYPE_QUERY =
                            "SELECT MAX(id) FROM assoctype WHERE tenantId = ?";
                    isRelatedAssocType = getJdbcTemplate().queryForInt(GET_MAX_ASSOCTYPE_QUERY, tenantId) + 1;

                    final String INSERT_ASSOCTYPE_QUERY =
                            "INSERT INTO assoctype(tenantId, name, id, visible) VALUES (?, 'IS_RELATED', ?, b'1')";
                    getJdbcTemplate().update(INSERT_ASSOCTYPE_QUERY, tenantId, isRelatedAssocType);
                }
                // add sourcetype for slopeone where missing
                final String GET_SLOPEONE_SOURCETYPE_QUERY =
                        "SELECT id FROM sourcetype WHERE name = 'http://www.easyrec.org/plugins/slopeone/0.96' AND tenantId = ?";
                int slopeOneSourceType = 0;
                // for tenants without a type 'http://www.easyrec.org/plugins/slopeone/0.96' this throws an exception - so catch!!
                try {
                    slopeOneSourceType = getJdbcTemplate().queryForInt(GET_SLOPEONE_SOURCETYPE_QUERY, tenantId);
                } catch (EmptyResultDataAccessException erdar) {
                    slopeOneSourceType = 0;
                }

                if (slopeOneSourceType == 0) { // this means sourcetype not found, so update
                    final String GET_MAX_SOURCETYPE_QUERY =
                            "SELECT MAX(id) FROM sourcetype WHERE tenantId = ?";
                    slopeOneSourceType = getJdbcTemplate().queryForInt(GET_MAX_SOURCETYPE_QUERY, tenantId) + 1;

                    final String INSERT_SOURCETYPE_QUERY =
                            "INSERT INTO sourcetype(tenantId, name, id) VALUES (?, 'http://www.easyrec.org/plugins/slopeone/0.96', ?)";
                    getJdbcTemplate().update(INSERT_SOURCETYPE_QUERY, tenantId, slopeOneSourceType);
                }

                final String SLOPEONE_CONFIG_QUERY =
                        "INSERT INTO plugin_configuration(tenantId, assocTypeId, pluginId, pluginVersion, name, configuration, active) VALUES " +
                                "(?, ?, 'http://www.easyrec.org/plugins/slopeone', ?, 'Default Configuration', ?, b'1')";
                getJdbcTemplate().update(SLOPEONE_CONFIG_QUERY, tenantId, isRelatedAssocType, currentVersion,
                        slopeOneXmlConfig);
            }

            // logs are not converted from ruleminerlog -> plugin_log
        }

        //updateVersion(); // done in migrate script!
    }

    private String generateXmlConfigurationFromProperties(final Properties propertiesConfig, final String assocType,
                                                          final String actionType) {
        String supportMinAbs = propertiesConfig.getProperty(assocType + ".ARM.supportMinAbs", "2");
        String supportPercnt = propertiesConfig.getProperty(assocType + ".ARM.supportPrcnt", "0.0");
        String confidencePercnt = propertiesConfig.getProperty(assocType + ".ARM.confidencePrcnt", "0.0");

        // no harm to keep the old values - maybe useful as fallback
//        propertiesConfig.remove(assocType + ".ARM.supportMinAbs");
//        propertiesConfig.remove(assocType + ".ARM.supportPrcnt");
//        propertiesConfig.remove(assocType + ".ARM.confidencePrcnt");

        final String xmlConfigurationTemplate =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<armConfiguration>" +
                        "<configurationName>Default Configuration</configurationName>" +
                        "<associationType>%s</associationType>" +
                        "<actionType>%s</actionType>" +
                        "<confidencePrcnt>%s</confidencePrcnt>" +
                        "<doDeltaUpdate>false</doDeltaUpdate>" +
                        "<excludeSingleItemBaskests>false</excludeSingleItemBaskests>" +
                        "<itemTypes>ITEM</itemTypes>" +
                        "<maxRulesPerItem>50</maxRulesPerItem>" +
                        "<maxSizeL1>5000</maxSizeL1>" +
                        "<metricType>CONFIDENCE</metricType>" +
                        "<ratingNeutral>5.5</ratingNeutral>" +
                        "<supportMinAbs>%s</supportMinAbs>" +
                        "<supportPrcnt>%s</supportPrcnt>" +
                        "</armConfiguration>";

        return String.format(xmlConfigurationTemplate, assocType, actionType, confidencePercnt, supportMinAbs,
                supportPercnt);
    }

    /**
     * This function returns the current version of easyrec,
     * depending on the presence of a version table. If
     * no version table is present return the inital version
     *
     * @return
     */
    @Override
    public Float checkVersion() throws Exception {

        BasicDataSource bds = (BasicDataSource) getDataSource();
        float tableCount;

        DatabaseMetaDataCallback callback = new DatabaseMetaDataCallback() {
            public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
                ResultSet rs = dbmd.getTables(null, null, "%", null);
                float f = 0;
                while (rs.next()) {
                    f++;
                }
                return f;
            }
        };

        tableCount = (Float) JdbcUtils.extractDatabaseMetaData(bds, callback);

        if (tableCount != 0) {
            try {
                return getJdbcTemplate().queryForObject("SELECT MAX(VERSION) FROM easyrec ", Float.class);
            } catch (Exception e) {
                // else return initial version 0.9
                return INITIAL_VERSION;
            }
        } else {
            return tableCount;
        }
    }

    public void updateVersion() {
        try {
            getJdbcTemplate().update("INSERT INTO easyrec(version) VALUES (?)", currentVersion);
        } catch (Exception e) {
            logger.warn("unable to update version", e);
        }
    }

    @Override
    public Operator addOperator(String id, String password, String firstName, String lastName, String email,
                                String phone, String company, String address, String apiKey, String ip) {

        Object[] args = {id, password, firstName, lastName, email, phone, company, address, apiKey, ip, new Date(),
                true, 1};

        int[] argTypes = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.BOOLEAN,
                Types.INTEGER};

        try {
            getJdbcTemplate().update(SQL_ADD_OPERATOR, args, argTypes);

            return new Operator(id, password, firstName, lastName, email, phone, company, address, apiKey, ip, true,
                    // active
                    new Date().toString(), // creation date
                    1,                     // acceslevel
                    0,                     // login count
                    null                   // last login date
            );
        } catch (Exception e) {
            logger.debug(e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            // after context start, check if properties file in folder /override exists
            try {
                File f = new File(overrideFolder.getFile(), "easyrec.database.properties");
                // if file exists, config was already written, so boot application as normal
                if (f.exists()) {
                    reloadContext();
                }
            } catch (IOException ex) {
                logger.error("Error looking for config file! Running in installer mode as fallback!");
            }
        }
    }

    @Override
    public void reloadContext() {
        reloadBackend();
        reloadFrontend();
    }

    @Override
    public void reloadBackend() {
        List<String> configLocs = Lists.newArrayList("classpath:spring/web/commonContext.xml");

        if ("on".equals(properties.getProperty("easyrec.rest"))) {
            configLocs.add(configLocations.get("easyrec.rest"));
        }

        if ("on".equals(properties.getProperty("easyrec.soap"))) {
            configLocs.add(configLocations.get("easyrec.soap"));
        }

        // if no config found use default
        if (configLocs.size() == 1) {
            configLocs.add(configLocations.get("easyrec.rest"));
        }

        if ("on".equals(properties.getProperty("easyrec.dev"))) {
            configLocs.add(configLocations.get("easyrec.dev"));
        }

        ApplicationContext webctx = applicationContext;

        ApplicationContext parent = webctx.getParent();

        if (parent instanceof ConfigurableWebApplicationContext) {

            ((ConfigurableWebApplicationContext) parent).setConfigLocations(
                    configLocs.toArray(new String[configLocs.size()]));
            ((ConfigurableWebApplicationContext) parent).refresh();

        }
    }

    @Override
    public void reloadFrontend() {

        ApplicationContext webctx = applicationContext;

        if (webctx instanceof ConfigurableWebApplicationContext) {

            ((ConfigurableWebApplicationContext) webctx)
                    .setConfigLocation("classpath:spring/web/easyrecContext.xml");
            ((ConfigurableWebApplicationContext) webctx).refresh();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setDriver(String driver) {
        BasicDataSource bds = (BasicDataSource) getDataSource();
        bds.setDriverClassName(driver);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setOverrideFolder(Resource resource) {
        this.overrideFolder = resource;
    }

    public void setConfigLocations(HashMap<String, String> configLocations) {
        this.configLocations = configLocations;
    }

    public List<String> getMigrateFiles() {
        return migrateFiles;
    }

    public void setMigrateFiles(List<String> migrateFiles) {
        this.migrateFiles = migrateFiles;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }
}
