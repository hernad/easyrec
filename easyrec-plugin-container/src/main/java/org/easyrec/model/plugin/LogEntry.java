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

package org.easyrec.model.plugin;

import com.eaio.util.text.HumanTime;
import com.google.common.base.Objects;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.plugin.stats.StatisticsConstants;

import java.net.URI;
import java.util.Date;

/**
 * log entries written by plugins
 *
 * @author pmarschik
 */
public class LogEntry {
    public static enum Status {
        RUNNING,
        FINISHED,
        ABORTED
    }

    private int id;
    private int tenantId;
    private PluginId pluginId;
    private Date startDate;
    private Date endDate;
    private int assocTypeId;
    private GeneratorConfiguration configuration;
    private GeneratorStatistics statistics;
    private HumanTime duration;

    public LogEntry(int id, int tenantId, PluginId pluginId, Date startDate, Date endDate,
                    int assocTypeId, GeneratorConfiguration configuration, GeneratorStatistics statistics) {
        this.id = id;
        this.tenantId = tenantId;
        this.pluginId = pluginId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assocTypeId = assocTypeId;
        this.configuration = configuration;
        this.statistics = statistics;
    }

    public LogEntry(int id, int tenantId, URI pluginId, Version pluginVersion, Date startDate, Date endDate,
                    int assocTypeId, GeneratorConfiguration configuration, GeneratorStatistics statistics) {
        this(-1, tenantId, new PluginId(pluginId, pluginVersion), startDate, endDate, assocTypeId, configuration,
                statistics);
    }

    public LogEntry(int tenantId, URI pluginId, Version pluginVersion, Date startDate, Date endDate, int assocTypeId,
                    GeneratorConfiguration configuration, GeneratorStatistics statistics) {
        this(-1, tenantId, pluginId, pluginVersion, startDate, endDate, assocTypeId, configuration, statistics);
    }

    public LogEntry(int tenantId, PluginId pluginId, Date startDate, Date endDate, int assocTypeId,
                    GeneratorConfiguration configuration, GeneratorStatistics statistics) {
        this(-1, tenantId, pluginId, startDate, endDate, assocTypeId, configuration, statistics);
    }

    public LogEntry(int tenantId, URI pluginId, Version pluginVersion, Date startDate, int assocTypeId,
                    GeneratorConfiguration configuration) {
        this(-1, tenantId, pluginId, pluginVersion, startDate, null, assocTypeId, configuration, null);
    }

    public LogEntry(int tenantId, PluginId pluginId, Date startDate, int assocTypeId,
                    GeneratorConfiguration configuration) {
        this(-1, tenantId, pluginId, startDate, null, assocTypeId, configuration, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public PluginId getPluginId() {
        return pluginId;
    }

    public void setPluginId(PluginId pluginId) {
        this.pluginId = pluginId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getAssocTypeId() {
        return assocTypeId;
    }

    public void setAssocTypeId(int assocTypeId) {
        this.assocTypeId = assocTypeId;
    }

    public GeneratorConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(GeneratorConfiguration configuration) {
        this.configuration = configuration;
    }

    public GeneratorStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(GeneratorStatistics statistics) {
        this.statistics = statistics;
    }

    public HumanTime getDuration() {
        if (endDate == null) return null;

        if (duration == null)
            duration = new HumanTime(endDate.getTime() - startDate.getTime());

        return duration;
    }

    public String getDurationString() {
        if (getDuration() == null) return "";

        if (duration.getDelta() == 0) return "0 s";

        return duration.getExactly();
    }

    public Status getStatus() {
        if (endDate == null || statistics == null) return Status.RUNNING;

        if (StatisticsConstants.ErrorStatistics.class.isAssignableFrom(statistics.getClass()))
            return Status.ABORTED;

        return Status.FINISHED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogEntry logEntry = (LogEntry) o;

        if (assocTypeId != logEntry.assocTypeId) return false;
        if (id != logEntry.id) return false;
        if (tenantId != logEntry.tenantId) return false;
        // if (!configuration.equals(logEntry.configuration)) return false;
        if (!Objects.equal(endDate, logEntry.endDate)) return false;
        if (!pluginId.equals(logEntry.pluginId)) return false;
        if (!startDate.equals(logEntry.startDate)) return false;
        // if (!Objects.equal(statistics, logEntry.statistics)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, tenantId, pluginId, startDate, endDate, assocTypeId, configuration,
                statistics);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("tenantId", tenantId)
                .add("pluginId", pluginId)
                .add("startDate", startDate)
                .add("endDate", endDate)
                .add("assocTypeId", assocTypeId)
                .add("configuration", configuration)
                .add("statistics", statistics)
                .toString();
    }
}
