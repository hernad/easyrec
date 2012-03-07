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
package org.easyrec.plugin.slopeone.store.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.plugin.slopeone.model.LogEntry;
import org.easyrec.plugin.slopeone.model.SlopeOneConfiguration;
import org.easyrec.plugin.slopeone.model.SlopeOneStats;
import org.easyrec.plugin.slopeone.store.dao.LogEntryDAO;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDroppingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * MySQL implementation of {@link LogEntryDAO}.<p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author: pmarschik $<br/> $Date: 2011-06-14 15:02:31 +0200 (Di, 14 Jun 2011) $<br/> $Revision: 18436 $</p>
 *
 * @author Patrick Marschik
 */
public class LogEntryDAOMysqlImpl extends AbstractTableCreatingDroppingDAOImpl implements LogEntryDAO {
    private static final Log logger = LogFactory.getLog(LogEntryDAOMysqlImpl.class);
    private static final String QUERY_LATEST;
    private static final String QUERY_INSERT;
    private static final int[] ARGT_LATEST = new int[]{Types.INTEGER};
    private static final int[] ARGT_INSERT = new int[]{Types.INTEGER, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR};
    private static final RowMapper<LogEntry> ROWMAPPER_LOGENTRY = new LogEntryRowMapper();
    private static Unmarshaller JAX_UNMARSHALLER;
    private static Marshaller JAX_MARSHALLER;

    static {
        StringBuilder query = new StringBuilder();
        query.append("SELECT\n");
        query.append("  ").append(COLUMN_ID).append(",\n");
        query.append("  ").append(COLUMN_TENANTID).append(",\n");
        query.append("  ").append(COLUMN_EXECUTION).append(",\n");
        query.append("  ").append(COLUMN_CONFIGURATION).append(",\n");
        query.append("  ").append(COLUMN_STATISTICS).append('\n');
        query.append("FROM ").append(TABLE_NAME).append('\n');
        query.append("WHERE\n");
        query.append("  ").append(COLUMN_TENANTID).append(" = ? AND\n");
        query.append("  ").append(COLUMN_STATISTICS).append(" NOT LIKE \'%<exception>%\'");
        query.append("ORDER BY ").append(COLUMN_EXECUTION).append(" DESC\n");
        query.append("LIMIT 1");
        QUERY_LATEST = query.toString();

        query = new StringBuilder();
        query.append("INSERT INTO\n");
        query.append("  ").append(TABLE_NAME).append("(\n");
        query.append("    ").append(COLUMN_TENANTID).append(",\n");
        query.append("    ").append(COLUMN_EXECUTION).append(",\n");
        query.append("    ").append(COLUMN_CONFIGURATION).append(",\n");
        query.append("    ").append(COLUMN_STATISTICS).append(")\n");
        query.append("VALUE\n");
        query.append("  (?, ?, ?, ?)");

        QUERY_INSERT = query.toString();

        final JAXBContext JAX_CONTEXT;
        try {
            JAX_CONTEXT = JAXBContext.newInstance(SlopeOneConfiguration.class, SlopeOneStats.class);
            JAX_UNMARSHALLER = JAX_CONTEXT.createUnmarshaller();
            JAX_MARSHALLER = JAX_CONTEXT.createMarshaller();

            JAX_MARSHALLER.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
            JAX_MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException e) {
            JAX_UNMARSHALLER = null;
            JAX_MARSHALLER = null;

            logger.error("Couldn't create JAX context/marshaller/unmarshaller", e);
        }
    }

    public LogEntryDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
    }

    public LogEntry getLatestLogEntry(int tenantId) {
        Object[] args = new Object[]{tenantId};

        try {
            return getJdbcTemplate().queryForObject(QUERY_LATEST, args, ARGT_LATEST, ROWMAPPER_LOGENTRY);
        } catch (DataAccessException dae) {
            Calendar cal = new GregorianCalendar();
            cal.set(0, 0, 0, 0, 0, 0);

            return new LogEntry(tenantId, cal.getTime(), new SlopeOneConfiguration(), new SlopeOneStats());
        }
    }

    public int insertLogEntry(LogEntry logEntry) {
        StringWriter configWriter = new StringWriter();
        StringWriter statsWriter = new StringWriter();

        try {
            JAX_MARSHALLER.marshal(logEntry.getConfiguration(), configWriter);
        } catch (JAXBException ex) {
            configWriter.write("[marshalling error]");

            logger.error("failed to marshal configuration", ex);
        }

        try {
            JAX_MARSHALLER.marshal(logEntry.getStatistics(), statsWriter);
        } catch (JAXBException ex) {
            configWriter.write("[marshalling error]");

            logger.error("failed to marshal statistics", ex);
        }

        Object[] args = new Object[]{logEntry.getTenantId(), logEntry.getExecution(), configWriter.toString(),
                statsWriter.toString()};

        return getJdbcTemplate().update(QUERY_INSERT, args, ARGT_INSERT);
    }

    @Override
    public String getDefaultTableName() { return TABLE_NAME; }

    @Override
    public String getTableCreatingSQLScriptName() { return "classpath:sql/plugins/slopeone/Log.sql"; }

    private static class LogEntryRowMapper implements RowMapper<LogEntry> {
        public LogEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt(COLUMN_ID);
            int tenantId = rs.getInt(COLUMN_TENANTID);
            Date execution = DaoUtils.getDate(rs, COLUMN_EXECUTION);

            String configurationStr = DaoUtils.getStringIfPresent(rs, COLUMN_CONFIGURATION);
            String statisticsStr = DaoUtils.getStringIfPresent(rs, COLUMN_STATISTICS);

            StringReader configurationReader = new StringReader(configurationStr);
            StringReader statisticsReader = new StringReader(statisticsStr);

            SlopeOneConfiguration config;
            SlopeOneStats stats;

            try {
                config = (SlopeOneConfiguration) JAX_UNMARSHALLER.unmarshal(configurationReader);
            } catch (JAXBException ex) {
                logger.error("failed to unmarshal configuration", ex);
                config = new SlopeOneConfiguration();
            }

            try {
                stats = (SlopeOneStats) JAX_UNMARSHALLER.unmarshal(statisticsReader);
            } catch (JAXBException ex) {
                logger.error("failed to unmarshal statistics", ex);
                stats = new SlopeOneStats();
            }

            LogEntry logEntry = new LogEntry(tenantId, execution, config, stats);
            logEntry.setId(id);

            return logEntry;
        }
    }
}
