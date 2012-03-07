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

package org.easyrec.plugin.slopeone.model;

import com.google.common.base.Objects;

import java.util.Date;


/**
 * Keeps all data to be logged for a run of Slope One.<p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author: pmarschik $<br/> $Date: 2011-06-14 15:02:31 +0200 (Di, 14 Jun 2011) $<br/> $Revision: 18436 $</p>
 *
 * @author Patrick Marschik
 */
public class LogEntry {
    private SlopeOneConfiguration configuration;
    private Date execution;
    private Integer id;
    private SlopeOneStats statistics;
    private int tenantId;

    public LogEntry(int tenantId, Date execution, SlopeOneConfiguration configuration, SlopeOneStats statistics) {
        this.tenantId = tenantId;
        this.configuration = configuration;
        this.statistics = statistics;
        this.execution = execution;
    }

    /**
     * Configuration used in the run.
     *
     * @return Configuration used in the run.
     */
    public SlopeOneConfiguration getConfiguration() { return configuration; }

    /**
     * Time of execution.
     *
     * @return Time of execution.
     */
    public Date getExecution() { return execution; }

    /**
     * Unique identifier.
     *
     * @return Unique identifier.
     */
    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    /**
     * Various statistics for the run.
     *
     * @return Various statistics for the run.
     */
    public SlopeOneStats getStatistics() { return statistics; }

    /**
     * Tenant the run was for.
     *
     * @return Tenant the run was for.
     */
    public int getTenantId() { return tenantId; }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("tenantId", tenantId)
                .add("configuration", configuration)
                .add("statistics", statistics)
                .add("execution", execution)
                .toString();
    }
}
