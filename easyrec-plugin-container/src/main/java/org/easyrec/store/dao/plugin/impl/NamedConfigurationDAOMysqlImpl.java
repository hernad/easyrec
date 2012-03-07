/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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

import com.google.common.base.Preconditions;
import org.easyrec.model.plugin.NamedConfiguration;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.generator.GeneratorConfigurationConstants;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.store.dao.plugin.NamedConfigurationDAO;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * @author pmarschik
 */
@DAO
public class NamedConfigurationDAOMysqlImpl extends AbstractTableCreatingDAOImpl implements NamedConfigurationDAO {

    private SqlUpdate createConfiguration;
    private SqlUpdate updateConfigurationActive;
    private SqlUpdate updateConfigurationInactive;
    private SqlUpdate updateAllInactive;
    private SqlUpdate deleteConfiguration;
    private SqlUpdate updateSetInactiveByPluginId;
    private MappingSqlQuery<NamedConfiguration> readConfiguration;
    private MappingSqlQuery<NamedConfiguration> readActiveConfiguration;
    private MappingSqlQuery<NamedConfiguration> readConfigurations;

    public NamedConfigurationDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService,
                                          PluginRegistry pluginRegistry) {
        super(sqlScriptService);
        setDataSource(dataSource);

        createConfiguration = new SqlUpdate(dataSource,
                "INSERT INTO plugin_configuration(tenantId, assocTypeId, pluginId, pluginVersion, name, " +
                        "configuration, active) VALUES(?, ?, ?, ?, ?, ?, ?)",
                new int[]{Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB,
                        Types.BIT}, 1);
        createConfiguration.compile();

        updateConfigurationActive = new SqlUpdate(dataSource,
                "UPDATE plugin_configuration SET name = ?, configuration = ?, active = b'1' " +
                        "WHERE tenantId = ? AND assocTypeId = ? AND pluginId = ? AND pluginVersion = ? AND name = ?",
                new int[]{Types.VARCHAR, Types.BLOB, Types.INTEGER, Types.INTEGER, Types.VARCHAR,
                        Types.VARCHAR, Types.VARCHAR}, 1);
        updateConfigurationActive.compile();

        updateConfigurationInactive = new SqlUpdate(dataSource,
                "UPDATE plugin_configuration SET name = ?, configuration = ? " +
                        "WHERE tenantId = ? AND assocTypeId = ? AND pluginId = ? AND pluginVersion = ? AND name = ?",
                new int[]{Types.VARCHAR, Types.BLOB, Types.INTEGER, Types.INTEGER, Types.VARCHAR,
                        Types.VARCHAR, Types.VARCHAR}, 1);
        updateConfigurationInactive.compile();

        updateAllInactive = new SqlUpdate(dataSource,
                "UPDATE plugin_configuration SET active = b'0' WHERE tenantId = ? AND assocTypeId = ?",
                new int[]{Types.INTEGER, Types.INTEGER});

        updateSetInactiveByPluginId = new SqlUpdate(dataSource,
                "UPDATE plugin_configuration SET active = b'0' WHERE pluginId = ? AND pluginVersion = ?",
                new int[]{Types.VARCHAR, Types.VARCHAR});
        
        deleteConfiguration = new SqlUpdate(dataSource,
                "DELETE FROM plugin_configuration WHERE tenantId = ? AND assocTypeId = ? AND pluginId = ? AND " +
                        "pluginVersion = ? AND name = ? AND active = b'0'",
                new int[]{Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR}, 1);
        deleteConfiguration.compile();

        readConfiguration = new NamedConfigurationMappingStatement(dataSource,
                "SELECT * FROM plugin_configuration WHERE tenantId = ? AND assocTypeId = ? AND pluginId = ? AND " +
                        "pluginVersion = ? AND name = ?", pluginRegistry);
        readConfiguration.declareParameter(new SqlParameter("tenantId", Types.INTEGER));
        readConfiguration.declareParameter(new SqlParameter("assocTypeId", Types.INTEGER));
        readConfiguration.declareParameter(new SqlParameter("pluginId", Types.VARCHAR));
        readConfiguration.declareParameter(new SqlParameter("pluginVersion", Types.VARCHAR));
        readConfiguration.declareParameter(new SqlParameter("name", Types.VARCHAR));
        readConfiguration.compile();

        readConfigurations = new NamedConfigurationMappingStatement(dataSource,
                "SELECT * FROM plugin_configuration WHERE tenantId = ? AND assocTypeId = ? AND pluginId = ? AND " +
                        "pluginVersion = ?", pluginRegistry);
        readConfigurations.declareParameter(new SqlParameter("tenantId", Types.INTEGER));
        readConfigurations.declareParameter(new SqlParameter("assocTypeId", Types.INTEGER));
        readConfigurations.declareParameter(new SqlParameter("pluginId", Types.VARCHAR));
        readConfigurations.declareParameter(new SqlParameter("pluginVersion", Types.VARCHAR));
        readConfigurations.compile();

        readActiveConfiguration = new NamedConfigurationMappingStatement(dataSource,
                "SELECT * FROM plugin_configuration WHERE tenantId = ? AND assocTypeId = ? AND active = b'1'",
                pluginRegistry);
        readActiveConfiguration.declareParameter(new SqlParameter("tenantId", Types.INTEGER));
        readActiveConfiguration.declareParameter(new SqlParameter("assocTypeId", Types.INTEGER));
        readActiveConfiguration.compile();
    }

    @Override
    public String getDefaultTableName() {
        return "plugin_configuration";
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return "classpath:sql/pluginContainer/PluginConfiguration.sql";
    }

    public int updateConfiguration(NamedConfiguration namedConfiguration) {
        Preconditions.checkNotNull(namedConfiguration);
        Preconditions.checkNotNull(namedConfiguration.getPluginId());
        Preconditions.checkNotNull(namedConfiguration.getPluginId().getUri());
        Preconditions.checkNotNull(namedConfiguration.getPluginId().getVersion());
        Preconditions.checkNotNull(namedConfiguration.getName());
        Preconditions.checkNotNull(namedConfiguration.getConfiguration());
        Preconditions.checkNotNull(namedConfiguration.getConfiguration().getConfigurationName());

        String oldName = namedConfiguration.getName();
        String newName = namedConfiguration.getConfiguration().getConfigurationName();

        int rowsAffected;

        if (namedConfiguration.isActive()) {
            updateAllInactive.update(namedConfiguration.getTenantId(), namedConfiguration.getAssocTypeId());
            rowsAffected =
                    updateConfigurationActive.update(newName, namedConfiguration.getConfiguration().marshal(false),
                            namedConfiguration.getTenantId(), namedConfiguration.getAssocTypeId(),
                            namedConfiguration.getPluginId().getUri(), namedConfiguration.getPluginId().getVersion(),
                            oldName);
        } else
            rowsAffected =
                    updateConfigurationInactive.update(newName, namedConfiguration.getConfiguration().marshal(false),
                            namedConfiguration.getTenantId(), namedConfiguration.getAssocTypeId(),
                            namedConfiguration.getPluginId().getUri(), namedConfiguration.getPluginId().getVersion(),
                            oldName);

        namedConfiguration.setName(newName);

        return rowsAffected;
    }

    public int createConfiguration(NamedConfiguration namedConfiguration) {
        Preconditions.checkNotNull(namedConfiguration);
        Preconditions.checkNotNull(namedConfiguration.getPluginId());
        Preconditions.checkNotNull(namedConfiguration.getPluginId().getUri());
        Preconditions.checkNotNull(namedConfiguration.getPluginId().getVersion());
        Preconditions.checkNotNull(namedConfiguration.getName());
        Preconditions.checkNotNull(namedConfiguration.getConfiguration());

        if (namedConfiguration.isActive())
            updateAllInactive.update(namedConfiguration.getTenantId(), namedConfiguration.getAssocTypeId());

        namedConfiguration.getConfiguration().setConfigurationName(namedConfiguration.getName());

        return createConfiguration.update(namedConfiguration.getTenantId(), namedConfiguration.getAssocTypeId(),
                namedConfiguration.getPluginId().getUri().toASCIIString(),
                namedConfiguration.getPluginId().getVersion().toString(), namedConfiguration.getName(),
                namedConfiguration.getConfiguration().marshal(false), namedConfiguration.isActive());
    }

    public int deleteConfiguration(NamedConfiguration namedConfiguration) {
        Preconditions.checkNotNull(namedConfiguration);
        Preconditions.checkNotNull(namedConfiguration.getName());
        Preconditions.checkNotNull(namedConfiguration.getPluginId());
        Preconditions.checkNotNull(namedConfiguration.getPluginId().getUri());
        Preconditions.checkNotNull(namedConfiguration.getPluginId().getVersion());

        return deleteConfiguration.update(namedConfiguration.getTenantId(), namedConfiguration.getAssocTypeId(),
                namedConfiguration.getPluginId().getUri().toASCIIString(),
                namedConfiguration.getPluginId().getVersion().toString(), namedConfiguration.getName());
    }

    public NamedConfiguration readConfiguration(int tenantId, int assocTypeId, PluginId pluginId, String name) {
        Preconditions.checkNotNull(pluginId);
        Preconditions.checkNotNull(pluginId.getUri());
        Preconditions.checkNotNull(pluginId.getVersion());
        Preconditions.checkNotNull(name);

        return readConfiguration.findObject(tenantId, assocTypeId, pluginId.getUri().toASCIIString(),
                pluginId.getVersion().toString(), name);
    }

    public List<NamedConfiguration> readConfigurations(int tenantId, int assocTypeId, PluginId pluginId) {
        Preconditions.checkNotNull(pluginId);
        Preconditions.checkNotNull(pluginId.getUri());
        Preconditions.checkNotNull(pluginId.getVersion());

        return readConfigurations.execute(tenantId, assocTypeId, pluginId.getUri().toASCIIString(),
                pluginId.getVersion().toString());
    }

    public NamedConfiguration readActiveConfiguration(int tenantId, int assocTypeId) {
        return readActiveConfiguration.findObject(tenantId, assocTypeId);
    }

    public int deactivateByPlugin(PluginId pluginId) {
        Preconditions.checkNotNull(pluginId);
        Preconditions.checkNotNull(pluginId.getUri());
        Preconditions.checkNotNull(pluginId.getVersion());

        if (logger.isDebugEnabled()) {
            logger.debug("inActivating plugin configs: " + pluginId.getUri() + "-" + pluginId.getVersion());
        }

        return updateSetInactiveByPluginId.update(pluginId.getUri(), pluginId.getVersion());
    }

    public int deactivateByAssocType(Integer tenantId, Integer assocTypeId) {
        Preconditions.checkNotNull(tenantId);
        Preconditions.checkNotNull(assocTypeId);

        if (logger.isDebugEnabled()) {
            logger.debug("inActivating all plugin configs for assocType: " + assocTypeId);
        }

        return updateAllInactive.update(tenantId, assocTypeId);
    }
    
    private static class NamedConfigurationMappingStatement extends MappingSqlQuery<NamedConfiguration> {
        private PluginRegistry pluginRegistry;

        private NamedConfigurationMappingStatement(DataSource ds, String sql,
                                                   PluginRegistry pluginRegistry) {
            super(ds, sql);
            this.pluginRegistry = pluginRegistry;
        }

        @Override
        protected NamedConfiguration mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("id");
            int tenantId = rs.getInt("tenantId");
            int assocTypeId = rs.getInt("assocTypeId");
            String pluginIdStr = rs.getString("pluginId");
            String pluginVersionStr = rs.getString("pluginVersion");
            String name = rs.getString("name");
            String configurationStr = rs.getString("configuration");
            boolean active = rs.getBoolean("active");

            PluginId pluginId = new PluginId(pluginIdStr, pluginVersionStr);

            GeneratorConfiguration configuration;

            Generator<?, ?> generator = pluginRegistry.getGenerators().get(pluginId);
            if (generator != null) {
                try {
                    StringReader xmlRepresentation = new StringReader(configurationStr);
                    StreamSource streamSource = new StreamSource(xmlRepresentation);
                    JAXBContext jaxbContext =
                            JAXBContext.newInstance(generator.getConfigurationClass(),
                                    GeneratorConfigurationConstants.CONF_MARSHAL_FAILED.getClass(),
                                    GeneratorConfigurationConstants.CONF_UNMARSHAL_FAILED.getClass());
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                    JAXBElement<? extends GeneratorConfiguration> jaxbElement =
                            unmarshaller.unmarshal(streamSource, generator.getConfigurationClass());

                    configuration = jaxbElement.getValue();
                } catch (JAXBException e) {
                    logger.warn("Unable to unmarshal configuration", e);
                    configuration = GeneratorConfigurationConstants.CONF_UNMARSHAL_FAILED;
                }

                if (!name.equals(configuration.getConfigurationName()))
                    logger.warn("Configuration name mismatch between column \"name\" and serialized configuration.");

                return new NamedConfiguration(id, tenantId, assocTypeId, pluginId, name, configuration, active);
                }
            else
                return null;
        }
    }
}
