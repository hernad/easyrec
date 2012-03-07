package org.easyrec.plugin.configuration;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.plugin.LogEntry;
import org.easyrec.model.plugin.NamedConfiguration;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.plugin.stats.StatisticsConstants;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.store.dao.plugin.LogEntryDAO;
import org.easyrec.store.dao.plugin.NamedConfigurationDAO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class GeneratorContainer {

    private static final Log logger = LogFactory.getLog(GeneratorContainer.class);

    public GeneratorContainer(AssocTypeDAO assocTypeDAO, NamedConfigurationDAO namedConfigurationDAO,
                              LogEntryDAO logEntryDAO, PluginRegistry registry) {
        this.assocTypeDAO = assocTypeDAO;
        this.namedConfigurationDAO = namedConfigurationDAO;
        this.logEntryDAO = logEntryDAO;
        this.registry = registry;
    }

    private AssocTypeDAO assocTypeDAO;
    private NamedConfigurationDAO namedConfigurationDAO;
    private LogEntryDAO logEntryDAO;
    private PluginRegistry registry;

    public LogEntry runGenerator(NamedConfiguration namedConfiguration) {
        return runGenerator(namedConfiguration, Predicates.<GeneratorStatistics>alwaysTrue());
    }

    public LogEntry runGenerator(NamedConfiguration namedConfiguration,
                                 Predicate<GeneratorStatistics> writeLog) {
        return runGenerator(namedConfiguration, writeLog, false);
    }

    /**
     * Runs a generator and writes a log entry for the run.
     *
     * @param namedConfiguration Configuration for the generator
     * @param writeLog           When this predicate evaluates to {@code true} a log entry is written (with {@link
     *                           LogEntryDAO}), if it evaluates to {@code false} the started log entry will be deleted.
     * @param writeLogLast       If this flag is {@code true} the log entry is started only after the plugin was run (i.e.
     *                           the duration will not be correct but the AUTO_INCREMENT column in the DB will not be increased in case {@code
     *                           writeLog} evaluated to false..
     * @return The statistics returned by the generator or {@link StatisticsConstants.ExecutionFailedStatistics} if
     *         an exception was thrown by the generator.
     */
    public LogEntry runGenerator(NamedConfiguration namedConfiguration,
                                 Predicate<GeneratorStatistics> writeLog, boolean writeLogLast) {
        Preconditions.checkNotNull(namedConfiguration);
        Preconditions.checkNotNull(namedConfiguration.getConfiguration());
        Preconditions.checkNotNull(namedConfiguration.getName());
        Preconditions.checkNotNull(namedConfiguration.getPluginId());
        Preconditions.checkNotNull(writeLog);

        Generator<GeneratorConfiguration, GeneratorStatistics> generator =
                registry.getGenerators().get(namedConfiguration.getPluginId());
        GeneratorConfiguration configuration = namedConfiguration.getConfiguration();

        // is this needed? tenant should be set in the configuration stored in
        configuration.setTenantId(namedConfiguration.getTenantId());
        generator.setConfiguration(configuration);

        LogEntry logEntry = new LogEntry(namedConfiguration.getTenantId(), namedConfiguration.getPluginId(), new Date(),
                namedConfiguration.getAssocTypeId(), configuration);
        GeneratorStatistics statistics;

        try {
            if (!writeLogLast) logEntryDAO.startEntry(logEntry);

            statistics = generator.execute();
        } catch (Throwable e) {
            logger.error(
                    String.format("Running plugin %s with configuration \"%s\" for tenant %d with assocType %d failed",
                            namedConfiguration.getPluginId(), namedConfiguration.getName(),
                            namedConfiguration.getTenantId(), namedConfiguration.getAssocTypeId()), e);
            statistics = new StatisticsConstants.ExecutionFailedStatistics(e);
        }

        boolean doWriteLog = writeLog.apply(statistics);

        logEntry.setStatistics(statistics);
        logEntry.setEndDate(new Date());

        if (doWriteLog) {
            if (writeLogLast)
                logEntryDAO.startEntry(logEntry);
            logEntryDAO.endEntry(logEntry);
        } else if (!writeLogLast)
            logEntryDAO.deleteEntry(logEntry);

        return logEntry;
    }

    public List<LogEntry> runGeneratorsForTenant(int tenantId) {
        return runGeneratorsForTenant(tenantId, Predicates.<GeneratorStatistics>alwaysTrue());
    }

    public List<LogEntry> runGeneratorsForTenant(int tenantId, Predicate<GeneratorStatistics> writeLog) {
        return runGeneratorsForTenant(tenantId, writeLog, false);
    }

    public List<LogEntry> runGeneratorsForTenant(int tenantId, Predicate<GeneratorStatistics> writeLog,
                                                 boolean writeLogLast) {
        Map<String, Integer> assocTypes = assocTypeDAO.getMapping(tenantId);
        List<LogEntry> result = Lists.newArrayList();

        for (Integer assocTypeId : assocTypes.values()) {
            NamedConfiguration namedConfiguration = namedConfigurationDAO.readActiveConfiguration(tenantId,
                    assocTypeId);

            if (namedConfiguration == null) continue;

            result.add(runGenerator(namedConfiguration, writeLog, writeLogLast));
        }

        return result;
    }

    /*
    earlier draft

    private Map<String, Generator> generators;

    //
    // Cancels all planned Generator executions (if any) and re-schedules them.
    //
    public void reScheduleAllRuns() {
        // cancel all schedules if there are any

        // walk over all tenant configs

        // start scheduler
    }

    //
    // Cancels all planned Generator executions for the specified tenant (if
    // any) and re-schedules them.
    //
    // @param tenant
    //
    public void reScheduleTenantRuns(TenantVO tenant) {
        // cancel all timers for tenant

        // read configuration for tenant

        // create quartz triggers from the scheduling properties

        // create GeneratorTask instance

        // somehow pack this together into quarts job / jobInfo / trigger

        // add to scheduler
    }

    //
    // Cancels all planned Generator executions.
    //
    public void cancelAllRuns() {
        // just shut down the scheduler
    }

    public void cancelScheduledRunsForTenant(TenantVO tenant) {
        // walk over scheduled jobs, check if they
        // are meant for the specified tenant

    }

    */

}
