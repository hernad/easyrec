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
package org.easyrec.store.dao.plugin;

import org.easyrec.model.plugin.LogEntry;
import org.easyrec.utils.spring.store.dao.TableCreatingDAO;

import java.util.Date;
import java.util.List;


/**
 * Writes runtime information of plugins.
 * <p/>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2011</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author$<br/>
 * $Date$<br/>
 * $Revision$</p>
 *
 * @author pmarschik
 */
public interface LogEntryDAO extends TableCreatingDAO {
    /**
     * Writes a new {@link LogEntry} with the {@code endDate} and {@code statistics} not being written.
     *
     * @param entry The log entry to start.
     */
    public void startEntry(LogEntry entry);

    /**
     * Updates an existing log entry and only write {@code endDate} and {@code statistics}.
     *
     * @param entry The log entry to end.
     */
    public void endEntry(LogEntry entry);

    /**
     * Updates all existing log entries that do not have an {@code endDate} yet.
     *
     * @param endDate The date to set {@code endDate} to.
     */
    public void endAllEntries(Date endDate);

    /**
     * Updates all existing log entries that do not have an {@code endDate} yet.
     * Sets {@code endDate} to current date.
     *
     * @see #endAllEntries(java.util.Date)
     */
    public void endAllEntries();

    /**
     * Deletes a single log entry.
     *
     * @param entry The entry to delete;
     */
    public void deleteEntry(LogEntry entry);

    /**
     * Returns the tenant id of all running log entries -- i.e. the ones not having an {@code endDate}.
     *
     * @return List containing all tenant ids of tenants that don't have the {@code endDate} set.
     */
    public List<Integer> getRunningTenants();

    /**
     * Get all log entries -- paged.
     *
     * @param offset The starting offset. Must be a positive number.
     * @param limit  The number of items to return. Must be a positive number
     * @return All log entries, maximum {@code limit} starting after {@code offset}.
     */
    public List<LogEntry> getLogEntries(int offset, int limit);

    /**
     * Get all log entries for a single tenant -- paged.
     *
     * @param tenantId The tenant to get the log entries for.
     * @param offset   The starting offset. Must be a positive number.
     * @param limit    The number of items to return. Must be a positive number
     * @return All log entries for {@code tenantId}, maximum {@code limit} starting after {@code offset}.
     */
    public List<LogEntry> getLogEntriesForTenant(int tenantId, int offset, int limit);

    /**
     * Get all log entries having a specified association type -- paged.
     *
     * @param assocTypeId The association type to get log entries for
     * @param offset      The starting offset. Must be a positive number.
     * @param limit       The number of items to return. Must be a positive number
     * @return All log entries for {@code tenantId}, maximum {@code limit} starting after {@code offset}.
     */
    public List<LogEntry> getLogEntries(int assocTypeId, int offset, int limit);

    /**
     * Get all log entries for a single having a specified association type -- paged.
     *
     * @param tenantId    The tenant to get the log entries for.
     * @param assocTypeId The association type to get log entries for
     * @param offset      The starting offset. Must be a positive number.
     * @param limit       The number of items to return. Must be a positive number
     * @return All log entries for {@code tenantId}, maximum {@code limit} starting after {@code offset}.
     */
    public List<LogEntry> getLogEntriesForTenant(int tenantId, int assocTypeId, int offset, int limit);

    /**
     * Get the total number of log entries.
     *
     * @return The number of total log entries.
     */
    int getNumberOfLogEntries();

    /**
     * Get the number of log entries a specified tenant has.
     *
     * @param tenantId The tenant to get the log entries for.
     * @return The number of log entries for {@code tenantId}.
     */
    public int getNumberOfLogEntriesForTenant(int tenantId);

    /**
     * Deletes all log entries in the table.
     */
    public void deleteLogEntries();

    /**
     * Gets the number of seconds of computation done by plugins on a specific date.
     * <p/>
     * Gets all log entries where {@code endDate} is {@code date} and sums {@code endDate - startDate}.
     *
     * @return number of log entries on date
     */
    public int getComputationDurationForDate(Date date);

    /**
     * Gets the number of seconds of computation done by plugins today.
     * <p/>
     * Gets all log entries where {@code endDate} is {@code date} and sums {@code endDate - startDate}.
     *
     * @return number of log entries on date
     */
    public int getComputationDurationForDate();
}
