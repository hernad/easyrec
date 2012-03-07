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

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.plugin.LogEntry;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.generator.GeneratorConfigurationConstants;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.plugin.stats.StatisticsConstants;
import org.easyrec.store.dao.plugin.LogEntryDAO;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlFunction;
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

/**
 * @author patrick
 */
public class LogEntryDAOMysqlImpl extends AbstractTableCreatingDAOImpl implements LogEntryDAO {

    private static Log logger = LogFactory.getLog(LogEntryDAOMysqlImpl.class);

    private static GeneratorConfiguration unmarshalConfiguration(String pluginIdAndVersion, String configurationString,
                                                                 PluginRegistry pluginRegistry) {
        try {
            Generator<?, ?> generator =
                    Preconditions.checkNotNull(pluginRegistry.getGenerators().get(PluginId.parsePluginId(
                            pluginIdAndVersion)));

            StringReader xmlRepresentation = new StringReader(configurationString);
            StreamSource streamSource = new StreamSource(xmlRepresentation);
            JAXBContext jaxbContext =
                    JAXBContext.newInstance(generator.getConfigurationClass(),
                            GeneratorConfigurationConstants.CONF_MARSHAL_FAILED.getClass(),
                            GeneratorConfigurationConstants.CONF_UNMARSHAL_FAILED.getClass());
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            JAXBElement<? extends GeneratorConfiguration> jaxbElement =
                    unmarshaller.unmarshal(streamSource, generator.getConfigurationClass());

            return jaxbElement.getValue();
        } catch (Exception e) {
            logger.warn("Unable to unmarshal configuration", e);
            return GeneratorConfigurationConstants.CONF_UNMARSHAL_FAILED;
        }
    }

    private static GeneratorStatistics unmarshalStatistics(String pluginIdAndVersion, String statisticsString,
                                                           PluginRegistry pluginRegistry) {
        try {
            Generator<?, ?> generator = Preconditions.checkNotNull(
                    pluginRegistry.getGenerators().get(PluginId.parsePluginId(pluginIdAndVersion)));

            StringReader xmlRepresentation = new StringReader(statisticsString);
            StreamSource streamSource = new StreamSource(xmlRepresentation);
            JAXBContext jaxbContext =
                    JAXBContext.newInstance(generator.getStatisticsClass(),
                            StatisticsConstants.STATS_MARSHAL_FAILED.getClass(),
                            StatisticsConstants.STATS_FORCED_END.getClass(),
                            StatisticsConstants.STATS_UNMARSHAL_FAILED.getClass(),
                            StatisticsConstants.STATS_EXECUTION_FAILED.getClass());
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            JAXBElement<? extends GeneratorStatistics> jaxbElement =
                    unmarshaller.unmarshal(streamSource, generator.getStatisticsClass());

            return jaxbElement.getValue();
        } catch (Exception e) {
            logger.warn("Unable to unmarshal configuration", e);
            return StatisticsConstants.STATS_UNMARSHAL_FAILED;
        }
    }

    private static final String FORCED_END_MARSHALED = StatisticsConstants.STATS_FORCED_END.marshal();

    private SqlUpdate startEntry;
    private SqlUpdate endEntry;
    private SqlUpdate endAllEntries;
    private MappingSqlQuery<Integer> getRunningTenants;
    private GetLogEntriesStatement getLogEntries;
    private GetLogEntriesStatement getLogEntriesWithAssocType;
    private GetLogEntriesStatement getLogEntriesForTenant;
    private SqlFunction<Integer> getNumberOfLogEntries;
    private SqlFunction<Integer> getNumberOfLogEntriesForTenant;
    private GetLogEntriesStatement getLogEntriesForTenantWithAssocType;
    private SqlUpdate deleteLogEntries;
    private SqlFunction<Integer> getComputationDurationForDate;
    private SqlUpdate deleteLogEntryStatement;

    protected LogEntryDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService,
                                   PluginRegistry pluginRegistry) {
        super(sqlScriptService);
        setDataSource(dataSource);


        startEntry = new SqlUpdate(dataSource,
                "INSERT INTO plugin_log(tenantId, pluginId, pluginVersion, startDate, assocTypeId, " +
                        "configuration) VALUES (?, ?, ?, ?, ?, ?)",
                new int[]{Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR, Types.BLOB});
        startEntry.compile();


        endEntry = new SqlUpdate(dataSource,
                "INSERT INTO plugin_log(tenantId, pluginId, pluginVersion, startDate, assocTypeId, configuration, " +
                        "endDate, statistics) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE endDate = ?, statistics = ?",
                new int[]{Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP,
                        Types.VARCHAR, Types.BLOB, Types.TIMESTAMP, Types.BLOB, Types.TIMESTAMP, Types.BLOB});
        endEntry.compile();


        endAllEntries = new SqlUpdate(dataSource,
                "UPDATE plugin_log SET endDate = ?, statistics = ? WHERE endDate IS NULL",
                new int[]{Types.TIMESTAMP, Types.BLOB});
        endAllEntries.compile();


        getRunningTenants = new MappingSqlQuery<Integer>(dataSource,
                "SELECT tenantId FROM plugin_log WHERE endDate IS NULL") {
            @Override
            protected Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("tenantId");
            }
        };
        getRunningTenants.compile();


        getLogEntries = new GetLogEntriesStatement(dataSource, pluginRegistry,
                "SELECT * FROM plugin_log ORDER BY startDate DESC, id DESC LIMIT ?, ?");
        getLogEntries.declareParameter(new SqlParameter("offset", Types.INTEGER));
        getLogEntries.declareParameter(new SqlParameter("limit", Types.INTEGER));
        getLogEntries.compile();


        getLogEntriesForTenant = new GetLogEntriesStatement(dataSource, pluginRegistry,
                "SELECT * FROM plugin_log WHERE tenantId = ? ORDER BY startDate DESC, id DESC LIMIT ?, ?");
        getLogEntriesForTenant.declareParameter(new SqlParameter("tenantId", Types.INTEGER));
        getLogEntriesForTenant.declareParameter(new SqlParameter("offset", Types.INTEGER));
        getLogEntriesForTenant.declareParameter(new SqlParameter("limit", Types.INTEGER));
        getLogEntriesForTenant.compile();


        getLogEntriesWithAssocType = new GetLogEntriesStatement(dataSource, pluginRegistry,
                "SELECT * FROM plugin_log WHERE assocTypeId = ? ORDER BY startDate DESC, id DESC LIMIT ?, ?");
        getLogEntriesWithAssocType.declareParameter(new SqlParameter("assocTypeId", Types.INTEGER));
        getLogEntriesWithAssocType.declareParameter(new SqlParameter("offset", Types.INTEGER));
        getLogEntriesWithAssocType.declareParameter(new SqlParameter("limit", Types.INTEGER));
        getLogEntriesWithAssocType.compile();


        getLogEntriesForTenantWithAssocType = new GetLogEntriesStatement(dataSource, pluginRegistry,
                "SELECT * FROM plugin_log WHERE tenantId = ? AND assocTypeId = ? ORDER BY startDate DESC, id DESC LIMIT ?, ?");
        getLogEntriesForTenantWithAssocType.declareParameter(new SqlParameter("tenantId", Types.INTEGER));
        getLogEntriesForTenantWithAssocType.declareParameter(new SqlParameter("assocTypeId", Types.INTEGER));
        getLogEntriesForTenantWithAssocType.declareParameter(new SqlParameter("offset", Types.INTEGER));
        getLogEntriesForTenantWithAssocType.declareParameter(new SqlParameter("limit", Types.INTEGER));
        getLogEntriesForTenantWithAssocType.compile();


        getNumberOfLogEntries = new SqlFunction<Integer>(dataSource, "SELECT count(*) AS entry_count FROM plugin_log");
        getNumberOfLogEntries.compile();


        getNumberOfLogEntriesForTenant = new SqlFunction<Integer>(dataSource,
                "SELECT count(*) AS entry_count FROM plugin_log WHERE tenantId = ?");
        getNumberOfLogEntriesForTenant.setResultType(Integer.class);
        getNumberOfLogEntriesForTenant.declareParameter(new SqlParameter("tenantId", Types.INTEGER));
        getNumberOfLogEntriesForTenant.compile();


        deleteLogEntries = new SqlUpdate(dataSource,
                "TRUNCATE plugin_log");
        deleteLogEntries.compile();


        getComputationDurationForDate = new SqlFunction<Integer>(dataSource,
                "SELECT sum(timestampdiff(second, startDate, endDate)) AS sum_seconds FROM plugin_log WHERE DATE(endDate) = ?");
        getComputationDurationForDate.setResultType(Integer.class);
        getComputationDurationForDate.declareParameter(new SqlParameter("endDate", Types.DATE));
        getComputationDurationForDate.compile();


        deleteLogEntryStatement = new SqlUpdate(dataSource,
                "DELETE FROM plugin_log WHERE tenantId = ? AND pluginId = ? AND pluginVersion = ? AND startDate = ? AND assocTypeId = ?");
        deleteLogEntryStatement.declareParameter(new SqlParameter("tenantId", Types.INTEGER));
        deleteLogEntryStatement.declareParameter(new SqlParameter("pluginId", Types.VARCHAR));
        deleteLogEntryStatement.declareParameter(new SqlParameter("pluginVersion", Types.VARCHAR));
        deleteLogEntryStatement.declareParameter(new SqlParameter("startDate", Types.TIMESTAMP));
        deleteLogEntryStatement.declareParameter(new SqlParameter("assocTypeId", Types.VARCHAR));
        deleteLogEntryStatement.compile();
    }

    @Override
    public String getDefaultTableName() {
        return "plugin_log";
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return "classpath:sql/pluginContainer/PluginLog.sql";
    }

    public void startEntry(LogEntry entry) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(entry.getPluginId());
        Preconditions.checkNotNull(entry.getPluginId().getUri());
        Preconditions.checkNotNull(entry.getPluginId().getVersion());
        Preconditions.checkNotNull(entry.getStartDate());
        Preconditions.checkNotNull(entry.getConfiguration());

        String serializedConfiguration = entry.getConfiguration().marshal();

        startEntry.update(entry.getTenantId(), entry.getPluginId().getUri().toASCIIString(),
                entry.getPluginId().getVersion().toString(), entry.getStartDate(), entry.getAssocTypeId(),
                serializedConfiguration);
    }

    public void endEntry(LogEntry entry) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(entry.getPluginId());
        Preconditions.checkNotNull(entry.getPluginId().getUri());
        Preconditions.checkNotNull(entry.getPluginId().getVersion());
        Preconditions.checkNotNull(entry.getStartDate());
        Preconditions.checkNotNull(entry.getConfiguration());
        Preconditions.checkNotNull(entry.getStatistics());
        Preconditions.checkArgument(entry.getStartDate().before(entry.getEndDate()) ||
                entry.getStartDate().equals(entry.getEndDate()),
                "startDate must be before endDate, or startDate = endDate");

        String serializedConfiguration = entry.getConfiguration().marshal();
        String serializedStatistics = entry.getStatistics().marshal();

        endEntry.update(entry.getTenantId(), entry.getPluginId().getUri().toASCIIString(),
                entry.getPluginId().getVersion().toString(), entry.getStartDate(), entry.getAssocTypeId(),
                serializedConfiguration, entry.getEndDate(), serializedStatistics, entry.getEndDate(),
                serializedStatistics);
    }

    public void endAllEntries(Date endDate) {
        Preconditions.checkNotNull(endDate);

        endAllEntries.update(endDate, FORCED_END_MARSHALED);
    }

    public void endAllEntries() {
        Date endDate = new Date();

        endAllEntries(endDate);
    }

    public void deleteEntry(LogEntry entry) {
        deleteLogEntryStatement.update(entry.getTenantId(), entry.getPluginId().getUri().toASCIIString(),
                entry.getPluginId().getVersion().toString(), entry.getStartDate(), entry.getAssocTypeId());
    }

    public List<Integer> getRunningTenants() {
        return getRunningTenants.execute();
    }

    public List<LogEntry> getLogEntries(int offset, int limit) {
        Preconditions.checkArgument(offset >= 0, "offset must be greater than or equal to 0");
        Preconditions.checkArgument(limit >= 0, "limit must be greater than or equal to 0");

        return getLogEntries.execute(offset, limit);
    }

    public List<LogEntry> getLogEntriesForTenant(int tenantId, int offset, int limit) {
        Preconditions.checkArgument(offset >= 0, "offset must be greater than or equal to 0");
        Preconditions.checkArgument(limit >= 0, "limit must be greater than or equal to 0");

        return getLogEntriesForTenant.execute(tenantId, offset, limit);
    }

    public List<LogEntry> getLogEntries(int assocTypeId, int offset, int limit) {
        Preconditions.checkArgument(offset >= 0, "offset must be greater than or equal to 0");
        Preconditions.checkArgument(limit >= 0, "limit must be greater than or equal to 0");

        return getLogEntriesWithAssocType.execute(assocTypeId, offset, limit);
    }

    public List<LogEntry> getLogEntriesForTenant(int tenantId, int assocTypeId, int offset, int limit) {
        Preconditions.checkArgument(offset >= 0, "offset must be greater than or equal to 0");
        Preconditions.checkArgument(limit >= 0, "limit must be greater than or equal to 0");

        return getLogEntriesForTenantWithAssocType.execute(tenantId, assocTypeId, offset, limit);
    }

    public int getNumberOfLogEntries() {
        return getNumberOfLogEntries.run();
    }

    public int getNumberOfLogEntriesForTenant(int tenantId) {
        return getNumberOfLogEntriesForTenant.run(tenantId);
    }

    public void deleteLogEntries() {
        deleteLogEntries.update();
    }

    public int getComputationDurationForDate(Date date) {
        Preconditions.checkNotNull(date);

        Integer result = getComputationDurationForDate.findObject(date);

        return Objects.firstNonNull(result, 0);
    }

    public int getComputationDurationForDate() {
        Date date = new Date();

        return getComputationDurationForDate(date);
    }

    private static class GetLogEntriesStatement extends MappingSqlQuery<LogEntry> {
        private PluginRegistry pluginRegistry;

        public GetLogEntriesStatement(DataSource ds, PluginRegistry pluginRegistry, String sql) {
            super(ds, sql);
            this.pluginRegistry = pluginRegistry;
        }

        @Override
        public LogEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("id");
            int tenantId = rs.getInt("tenantId");
            Date startDate = DaoUtils.getDateIfPresent(rs, "startDate");
            Date endDate = DaoUtils.getDateIfPresent(rs, "endDate");
            int assocTypeId = rs.getInt("assocTypeId");
            String pluginIdStr = rs.getString("pluginId");
            String pluginVersionStr = rs.getString("pluginVersion");
            String configurationStr = rs.getString("configuration");
            String statisticsStr = rs.getString("statistics");
            PluginId pluginId = null;

            try {
                pluginId = new PluginId(pluginIdStr, pluginVersionStr);
            } catch (IllegalArgumentException e) {
                logger.warn("unable to parse pluginId", e);
            }

            String pluginIdAndVersion = pluginIdStr + "/" + pluginVersionStr;

            GeneratorConfiguration configuration =
                    unmarshalConfiguration(pluginIdAndVersion, configurationStr, pluginRegistry);
            GeneratorStatistics statistics = null;

            if (statisticsStr != null)
                statistics = unmarshalStatistics(pluginIdAndVersion, statisticsStr, pluginRegistry);

            return new LogEntry(id, tenantId, pluginId, startDate, endDate, assocTypeId, configuration, statistics);
        }
    }
}
