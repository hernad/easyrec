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

import org.easyrec.model.plugin.LogEntry;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.plugin.waiting.WaitingGenerator;
import org.easyrec.plugin.waiting.WaitingGeneratorStats;
import org.easyrec.store.dao.plugin.LogEntryDAO;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DuplicateKeyException;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author pmarschik
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({"/spring/easyrecDataSource.xml", "/spring/pluginContainer/Plugins_AllInOne.xml"})
@DataSet("/dbunit/pluginContainer/plugin_log.xml")
public class LogEntryDAOMysqlImplTest {

    private static class MatchesLogEntryWithoutId extends BaseMatcher<LogEntry> {

        private LogEntry logEntry;

        public MatchesLogEntryWithoutId(LogEntry logEntry) {
            this.logEntry = logEntry;
        }

        public boolean matches(Object item) {
            if (logEntry == item) return true;
            if (item == null || logEntry.getClass() != item.getClass()) return false;

            LogEntry other = (LogEntry) item;
            logEntry.setId(other.getId());

            return logEntry.equals(other);
        }

        public void describeTo(Description description) {
            description.appendText("log entry w/o id").appendValue(logEntry);
        }
    }

    private static Matcher<LogEntry> matchesLogEntryWithoutId(LogEntry logEntry) {
        return new MatchesLogEntryWithoutId(logEntry);
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // TODO add tests for getLogEntries(assocTypeId, offset, limit)
    // TODO add tests for getLogEntriesForTenant(tenantId, assocTypeId, offset, limit)

    private static Date date(String date) {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            fail("Could not parse date!");
            return null;
        }
    }

    @SpringBeanByName
    private LogEntryDAO logEntryDAO;

    @SpringBeanByName
    private PluginRegistry pluginRegistry;

    private WaitingGenerator waitingGenerator;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        waitingGenerator = new WaitingGenerator();

        pluginRegistry.getGenerators().put(waitingGenerator.getId(),
                // fu generics ...
                (Generator<GeneratorConfiguration, GeneratorStatistics>)
                        (Generator<? extends GeneratorConfiguration, ? extends GeneratorStatistics>) waitingGenerator);
    }

    @Test
    @ExpectedDataSet("/dbunit/pluginContainer/plugin_log+1new_started.xml")
    public void startEntry_shouldStartNewEntry() {
        LogEntry newEntry =
                new LogEntry(2, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 16:00:00"), 1,
                        waitingGenerator.newConfiguration());

        logEntryDAO.startEntry(newEntry);
    }

    @Test(expected = DuplicateKeyException.class)
    public void startEntry_shouldThrowOnNonUnique() {
        LogEntry newEntry =
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 15:00:00"), 1,
                        waitingGenerator.newConfiguration());

        logEntryDAO.startEntry(newEntry);
    }

    @Test(expected = NullPointerException.class)
    public void startEntry_shouldThrowOnNullArgument() {
        logEntryDAO.startEntry(null);
    }

    @Test(expected = NullPointerException.class)
    public void startEntry_shouldThrowOnNullPluginId() {
        LogEntry newEntry = new LogEntry(1, null, waitingGenerator.getId().getVersion(), date("2011-02-23 15:00:00"), 1,
                waitingGenerator.newConfiguration());

        logEntryDAO.startEntry(newEntry);
    }

    @Test(expected = NullPointerException.class)
    public void startEntry_shouldThrowOnNullPluginVersion() {
        LogEntry newEntry = new LogEntry(1, waitingGenerator.getId().getUri(), null, date("2011-02-23 15:00:00"), 1,
                waitingGenerator.newConfiguration());

        logEntryDAO.startEntry(newEntry);
    }

    @Test(expected = NullPointerException.class)
    public void startEntry_shouldThrowOnNullStartDate() {
        LogEntry newEntry =
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(), null, 1,
                        waitingGenerator.newConfiguration());

        logEntryDAO.startEntry(newEntry);
    }

    @Test(expected = NullPointerException.class)
    public void startEntry_shouldThrowOnNullConfiguration() {
        LogEntry newEntry =
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 15:00:00"), 1,
                        null);

        logEntryDAO.startEntry(newEntry);
    }

    @Test
    @ExpectedDataSet("/dbunit/pluginContainer/plugin_log+1existing_ended.xml")
    public void endEntry_shouldEndExistingEntry() {
        LogEntry newEntry =
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 15:20:00"), 1,
                        waitingGenerator.newConfiguration());

        newEntry.setEndDate(date("2011-02-23 15:30:00"));
        newEntry.setStatistics(new WaitingGeneratorStats());

        logEntryDAO.endEntry(newEntry);
    }

    @Test
    @ExpectedDataSet("/dbunit/pluginContainer/plugin_log+1new_ended.xml")
    public void endEntry_shouldInsertNewEntryOnNonExistingEntry() {
        LogEntry newEntry =
                new LogEntry(2, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 16:00:00"), 1,
                        waitingGenerator.newConfiguration());

        newEntry.setEndDate(date("2011-02-23 16:10:00"));
        newEntry.setStatistics(new WaitingGeneratorStats());

        logEntryDAO.endEntry(newEntry);
    }

    @Test(expected = NullPointerException.class)
    public void endEntry_shouldThrowOnNullArgument() {
        logEntryDAO.endEntry(null);
    }

    @Test(expected = NullPointerException.class)
    public void endEntry_shouldThrowOnNullPluginId() {
        LogEntry newEntry = new LogEntry(1, null, waitingGenerator.getId().getVersion(), date("2011-02-23 15:00:00"), 1,
                waitingGenerator.newConfiguration());
        newEntry.setEndDate(date("2011-02-23 15:10:00"));
        newEntry.setStatistics(new WaitingGeneratorStats());

        logEntryDAO.endEntry(newEntry);
    }

    @Test(expected = NullPointerException.class)
    public void endEntry_shouldThrowOnNullPluginVersion() {
        LogEntry newEntry = new LogEntry(1, waitingGenerator.getId().getUri(), null, date("2011-02-23 15:00:00"), 1,
                waitingGenerator.newConfiguration());
        newEntry.setEndDate(date("2011-02-23 15:10:00"));
        newEntry.setStatistics(new WaitingGeneratorStats());

        logEntryDAO.endEntry(newEntry);
    }

    @Test(expected = NullPointerException.class)
    public void endEntry_shouldThrowOnNullStartDate() {
        LogEntry newEntry =
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(), null, 1,
                        waitingGenerator.newConfiguration());
        newEntry.setEndDate(date("2011-02-23 15:10:00"));
        newEntry.setStatistics(new WaitingGeneratorStats());

        logEntryDAO.endEntry(newEntry);
    }

    @Test(expected = NullPointerException.class)
    public void endEntry_shouldThrowOnNullEndDate() {
        LogEntry newEntry =
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 15:00:00"), 1,
                        waitingGenerator.newConfiguration());
        newEntry.setEndDate(null);
        newEntry.setStatistics(new WaitingGeneratorStats());

        logEntryDAO.endEntry(newEntry);
    }

    @Test(expected = NullPointerException.class)
    public void endEntry_shouldThrowOnNullConfiguration() {
        LogEntry newEntry =
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 15:00:00"), 1,
                        null);
        newEntry.setEndDate(date("2011-02-23 15:10:00"));
        newEntry.setStatistics(new WaitingGeneratorStats());

        logEntryDAO.endEntry(newEntry);
    }

    @Test(expected = NullPointerException.class)
    public void endEntry_shouldThrowOnNullStatistics() {
        LogEntry newEntry =
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(), null, 1,
                        waitingGenerator.newConfiguration());
        newEntry.setEndDate(date("2011-02-23 15:10:00"));
        newEntry.setStatistics(null);

        logEntryDAO.endEntry(newEntry);
    }

    @Test
    @DataSet("/dbunit/pluginContainer/plugin_log+1new_started.xml")
    @ExpectedDataSet("/dbunit/pluginContainer/plugin_log+all_ended_sameEndDate.xml")
    public void endAllEntries_shouldEndAllEntries() {
        logEntryDAO.endAllEntries(date("2011-02-23 18:00:00"));

        // note: the parameterless version should also be tested but all it does is call the parametered version with
        // {@code new Date()} as parameter.
    }

    @Test(expected = NullPointerException.class)
    public void endAllEntries_shouldThrowOnNullEndDate() {
        logEntryDAO.endAllEntries(null);
    }

    @Test
    @DataSet("/dbunit/pluginContainer/plugin_log+1new_started.xml")
    public void getRunningTenants_shouldReturnAllRunningTenants() {
        List<Integer> runningTenants = logEntryDAO.getRunningTenants();

        assertThat(runningTenants, is(not(nullValue())));
        assertThat(runningTenants.size(), is(2));
        assertThat(runningTenants, hasItem(1));
        assertThat(runningTenants, hasItem(2));
    }

    @Test
    @DataSet("/dbunit/pluginContainer/plugin_log+all_ended.xml")
    public void getRunningTenants_shouldReturnEmptyListIfNoRunningTenants() {
        List<Integer> runningTenants = logEntryDAO.getRunningTenants();

        assertThat(runningTenants, is(not(nullValue())));
        assertThat(runningTenants.size(), is(0));
    }

    @Test
    @DataSet("/dbunit/pluginContainer/plugin_log+all_ended.xml")
    public void getNumberOfLogEntries_shouldReturnNumberOfLogEntriesOrZero() {
        assertThat(logEntryDAO.getNumberOfLogEntriesForTenant(1), is(2));
        assertThat(logEntryDAO.getNumberOfLogEntriesForTenant(2), is(1));
        assertThat(logEntryDAO.getNumberOfLogEntriesForTenant(3), is(0));
    }

    @Test
    public void deleteLogEntries_shouldDeleteAllLogEntries() {
        logEntryDAO.deleteLogEntries();

        // maybe we can use @ExpectedDateSet somehow to check for empty table?
        assertThat(logEntryDAO.getNumberOfLogEntriesForTenant(1), is(0));
        assertThat(logEntryDAO.getNumberOfLogEntriesForTenant(2), is(0));
        assertThat(logEntryDAO.getNumberOfLogEntriesForTenant(3), is(0));

        // second call must still work
        logEntryDAO.deleteLogEntries();

        // maybe we can use @ExpectedDateSet somehow to check for empty table?
        assertThat(logEntryDAO.getNumberOfLogEntriesForTenant(1), is(0));
        assertThat(logEntryDAO.getNumberOfLogEntriesForTenant(2), is(0));
        assertThat(logEntryDAO.getNumberOfLogEntriesForTenant(3), is(0));
    }

    @Test
    @DataSet("/dbunit/pluginContainer/plugin_log+1new_ended.xml")
    public void getLogEntries_shouldReturnAllStartedAndEndedLogEntries() {
        List<LogEntry> logEntries = logEntryDAO.getLogEntries(0, Integer.MAX_VALUE);

        assertThat(logEntries, is(not(nullValue())));
        assertThat(logEntries, hasItem(matchesLogEntryWithoutId(
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 15:00:00"), date("2011-02-23 15:10:00"), 1,
                        waitingGenerator.newConfiguration(),
                        new WaitingGeneratorStats()))));
        assertThat(logEntries, hasItem(matchesLogEntryWithoutId(
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 15:20:00"), null, 1, waitingGenerator.newConfiguration(), null))));
        assertThat(logEntries, hasItem(matchesLogEntryWithoutId(
                new LogEntry(2, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 16:00:00"), date("2011-02-23 16:10:00"), 1,
                        waitingGenerator.newConfiguration(),
                        new WaitingGeneratorStats()))));
    }

    @Test
    public void getLogEntries_shouldReturnEmptyListIfLimitIsZero() {
        List<LogEntry> logEntries = logEntryDAO.getLogEntries(0, 0);

        assertThat(logEntries, is(not(nullValue())));
        assertThat(logEntries.size(), is(0));
    }

    @Test
    public void getLogEntries_shouldReturnEmptyListIfTableEmpty() {
        logEntryDAO.deleteLogEntries();

        List<LogEntry> logEntries = logEntryDAO.getLogEntries(0, Integer.MAX_VALUE);

        assertThat(logEntries, is(not(nullValue())));
        assertThat(logEntries.size(), is(0));
    }

    @Test
    @DataSet("/dbunit/pluginContainer/plugin_log+1new_ended.xml")
    public void getLogEntries_shouldReturnAtMostLimitEntries() {
        List<LogEntry> logEntries = logEntryDAO.getLogEntries(0, 2);

        assertThat(logEntries, is(not(nullValue())));
        assertThat(logEntries.size(), is(lessThanOrEqualTo(2)));
    }

    @Test
    @DataSet("/dbunit/pluginContainer/plugin_log+1new_ended.xml")
    public void getLogEntries_shouldStartAtOffset() {
        List<LogEntry> logEntries = logEntryDAO.getLogEntries(1, Integer.MAX_VALUE);

        LogEntry expected1 = new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                date("2011-02-23 15:20:00"), null, 1, waitingGenerator.newConfiguration(), null);
        LogEntry expected2 = new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                date("2011-02-23 15:00:00"), date("2011-02-23 15:10:00"), 1, waitingGenerator.newConfiguration(),
                new WaitingGeneratorStats());

        assertThat(logEntries, is(not(nullValue())));
        assertThat(logEntries, hasItem(matchesLogEntryWithoutId(expected1)));
        assertThat(logEntries, hasItem(matchesLogEntryWithoutId(expected2)));

        // check ordering: descending date

        Iterator<LogEntry> iteratorAhead = logEntries.iterator();
        Iterator<LogEntry> iterator = logEntries.iterator();

        iteratorAhead.next();

        while (iterator.hasNext() && iteratorAhead.hasNext()) {
            LogEntry entry1 = iteratorAhead.next();
            LogEntry entry2 = iterator.next();

            assertThat("entry 1 must be before entry 2", entry1.getStartDate(),
                    lessThanOrEqualTo(entry2.getStartDate()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLogEntries_shouldThrowOnNegativeOffset() {
        logEntryDAO.getLogEntries(-1, Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLogEntries_shouldThrowOnNegativeLimit() {
        logEntryDAO.getLogEntries(0, -1);
    }

    @Test
    @DataSet("/dbunit/pluginContainer/plugin_log+1new_ended.xml")
    public void getLogEntriesForTenant_shouldReturnAllStartedAndEndedLogEntries() {
        List<LogEntry> logEntries = logEntryDAO.getLogEntriesForTenant(1, 0, Integer.MAX_VALUE);

        assertThat(logEntries, is(not(nullValue())));
        assertThat(logEntries, hasItem(matchesLogEntryWithoutId(
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 15:00:00"), date("2011-02-23 15:10:00"), 1,
                        waitingGenerator.newConfiguration(),
                        new WaitingGeneratorStats()))));
        assertThat(logEntries, hasItem(matchesLogEntryWithoutId(
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 15:20:00"), null, 1, waitingGenerator.newConfiguration(), null))));
        assertThat(logEntries, not(hasItem(
                matchesLogEntryWithoutId(
                        new LogEntry(2, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                                date("2011-02-23 16:00:00"), date("2011-02-23 16:10:00"), 1,
                                waitingGenerator.newConfiguration(),
                                new WaitingGeneratorStats())))));

        logEntries = logEntryDAO.getLogEntriesForTenant(2, 0, Integer.MAX_VALUE);
        assertThat(logEntries, is(not(nullValue())));
        assertThat(logEntries, not(hasItem(
                matchesLogEntryWithoutId(
                        new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                                date("2011-02-23 15:00:00"), date("2011-02-23 15:10:00"), 1,
                                waitingGenerator.newConfiguration(),
                                new WaitingGeneratorStats())))));
        assertThat(logEntries, not(hasItem(
                matchesLogEntryWithoutId(
                        new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                                date("2011-02-23 15:20:00"), null, 1, waitingGenerator.newConfiguration(), null)))));
        assertThat(logEntries, hasItem(matchesLogEntryWithoutId(
                new LogEntry(2, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 16:00:00"), date("2011-02-23 16:10:00"), 1,
                        waitingGenerator.newConfiguration(),
                        new WaitingGeneratorStats()))));
    }

    @Test
    @DataSet("/dbunit/pluginContainer/plugin_log+1new_ended.xml")
    public void getLogEntriesForTenant_shouldReturnAtMostLimitEntries() {
        List<LogEntry> logEntries = logEntryDAO.getLogEntriesForTenant(1, 0, 1);

        assertThat(logEntries, is(not(nullValue())));
        assertThat(logEntries.size(), is(lessThanOrEqualTo(1)));
    }

    @Test
    public void getLogEntriesForTenant_shouldReturnEmptyListIfLimitIsZero() {
        List<LogEntry> logEntries = logEntryDAO.getLogEntriesForTenant(1, 0, 0);

        assertThat(logEntries, is(not(nullValue())));
        assertThat(logEntries.size(), is(0));
    }

    @Test
    public void getLogEntriesForTenant_shouldReturnEmptyListIfNoneMatched() {
        List<LogEntry> logEntries = logEntryDAO.getLogEntriesForTenant(3, 0, 1);

        assertThat(logEntries, is(not(nullValue())));
        assertThat(logEntries.size(), is(0));
    }

    @Test
    @DataSet("/dbunit/pluginContainer/plugin_log+1new_ended.xml")
    public void getLogEntriesForTenant_shouldStartAtOffset() {
        List<LogEntry> logEntries = logEntryDAO.getLogEntriesForTenant(1, 1, Integer.MAX_VALUE);

        assertThat(logEntries, is(not(nullValue())));
        assertThat(logEntries, hasItem(matchesLogEntryWithoutId(
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 15:00:00"), date("2011-02-23 15:10:00"), 1,
                        waitingGenerator.newConfiguration(), null))));
        assertThat(logEntries, not(hasItem(matchesLogEntryWithoutId(
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 15:20:00"), null, 1, waitingGenerator.newConfiguration(),
                        new WaitingGeneratorStats())))));
        assertThat(logEntries, not(hasItem(matchesLogEntryWithoutId(
                new LogEntry(2, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(),
                        date("2011-02-23 16:00:00"), date("2011-02-23 16:10:00"), 1,
                        waitingGenerator.newConfiguration(), new WaitingGeneratorStats())))));

        // check ordering: descending date

        Iterator<LogEntry> iteratorAhead = logEntries.iterator();
        Iterator<LogEntry> iterator = logEntries.iterator();

        iteratorAhead.next();

        while (iterator.hasNext() && iteratorAhead.hasNext()) {
            LogEntry entry1 = iteratorAhead.next();
            LogEntry entry2 = iterator.next();

            assertThat("entry 1 must be before entry 2", entry1.getStartDate(),
                    lessThanOrEqualTo(entry2.getStartDate()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLogEntriesForTenant_shouldThrowOnNegativeOffset() {
        logEntryDAO.getLogEntriesForTenant(1, -1, Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLogEntriesForTenant_shouldThrowOnNegativeLimit() {
        logEntryDAO.getLogEntriesForTenant(1, 0, -1);
    }

    @Test
    @DataSet("/dbunit/pluginContainer/plugin_log+all_ended.xml")
    public void getComputationDurationForDate_shouldReturnSum() {
        long milliseconds = logEntryDAO.getComputationDurationForDate(date("2011-02-23 00:00:01"));

        final long EXPECTED_VALUE =
                3 * 10 * // 30 minutes
                        60; // seconds

        assertThat(milliseconds, is(EXPECTED_VALUE));
    }

    @Test
    public void getComputationDurationForDate_shouldIngoreNonEndedEntries() {
        long milliseconds = logEntryDAO.getComputationDurationForDate(date("2011-02-23 00:00:01"));

        // note: the parameterless version should also be tested but all it does is call the parametered version with
        // {@code new Date()} as parameter.

        final long EXPECTED_VALUE =
                1 * 10 * // 10 minutes
                        60; // seconds

        assertThat(milliseconds, is(EXPECTED_VALUE));
    }

    @Test
    public void getComputationDurationForDate_shouldReturnZeroIfEmptyTable() {
        logEntryDAO.deleteLogEntries();

        long milliseconds = logEntryDAO.getComputationDurationForDate(date("2011-02-23 00:00:01"));

        // note: the parameterless version should also be tested but all it does is call the parametered version with
        // {@code new Date()} as parameter.

        assertThat(milliseconds, is(0L));
    }

    @Test
    public void getComputationDurationForDate_shouldReturnZeroIfNoneMatched() {
        long milliseconds = logEntryDAO.getComputationDurationForDate(date("2011-02-24 00:00:01"));

        // note: the parameterless version should also be tested but all it does is call the parametered version with
        // {@code new Date()} as parameter.

        assertThat(milliseconds, is(0L));
    }

    @Test(expected = NullPointerException.class)
    public void getComputationDurationForDate_shouldThrowOnNulLDate() {
        logEntryDAO.getComputationDurationForDate(null);
    }

    @Test
    public void endLogEntry_withSameStartEndDateShouldWork() {
        Date date = date("2011-04-12 12:00:00");

        LogEntry newEntry =
                new LogEntry(1, waitingGenerator.getId().getUri(), waitingGenerator.getId().getVersion(), date, 1,
                        waitingGenerator.newConfiguration());

        logEntryDAO.startEntry(newEntry);

        newEntry.setEndDate(date);
        newEntry.setStatistics(new WaitingGeneratorStats());

        logEntryDAO.endEntry(newEntry);
    }
}
