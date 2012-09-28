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
package org.easyrec.utils.spring.store;

import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.google.common.base.Objects.firstNonNull;

/**
 * Iterator that allows bulkwise fetching of large result sets for mysql.
 * <p/>
 * <p>
 * Example: <br />
 * <p/>
 * <pre>
 *       StringBuilder sql = new StringBuilder();
 *       sql.append("select * from myTable where id = ?");
 *       Object[] args = { id };
 *       int[] types = { Types.BIGINT };
 *       Iterator<MyClass> resultIt = new ResultSetIterator<MyClass>(
 *               myDataSource,bulkSize, sql.toString(),
 *               args, types, commandQueueEntryRowMapper);
 * <p/>
 *       while (resultIt.hasNext()){
 *          MyClass obj = resultIt.next();
 *       }
 * </pre>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2006</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Florian Kleedorfer
 */

public class ResultSetIteratorMysql<T> implements Iterator<T> {
    //the logger
    protected final Log logger = LogFactory.getLog(getClass());

    //internal iterator that is fetched from the currentBulk
    protected Iterator<T> currentIterator = null;

    //the current bulk of result objects
    protected List<T> currentBulk = null;

    //the bulk size
    protected int bulkSize = 1000;

    //the current offset in the result
    protected int offsetInResult = 0;

    //the RowMapper that creates the result type
    protected RowMapper<T> rowMapper = null;

    //the jdbcTemplate used to acces the db
    protected JdbcTemplate jdbcTemplate = null;

    //the sql string to execute (prepared statement syntax)
    protected String sql = null;

    //arguments for the sql string
    protected Object[] args = null;

    //argument types
    protected int[] types = null;

    //remember if call to init() has been made
    protected boolean isInitialized = false;

    /**
     * create the result set iterator.
     *
     * @param ds        a DataSource
     * @param bulkSize  number of result rows to fetch each time a query is sent to the db
     * @param sql       sql
     * @param args      args
     * @param types     types
     * @param rowMapper for creating the results
     */
    public ResultSetIteratorMysql(DataSource ds, int bulkSize, String sql, Object[] args, int[] types,
                                  RowMapper<T> rowMapper) {
        this.bulkSize = bulkSize;
        this.currentIterator = null;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.rowMapper = rowMapper;
        this.sql = sql + " limit ?, ?";
        this.args = ObjectArrays.concat(firstNonNull(args, new Object[0]), new Object[]{0, bulkSize}, Object.class);
        this.types = Ints.concat(firstNonNull(types, new int[0]), new int[]{Types.INTEGER, Types.INTEGER});
        init();
    }

    /**
     * create the result set iterator without arguments to the sql statement
     *
     * @param ds        a DataSource
     * @param bulkSize  number of result rows to fetch each time a query is sent to the db
     * @param sql       the sql string (without 'limit' clause!)
     * @param rowMapper for creating the results     *
     */
    public ResultSetIteratorMysql(DataSource ds, int bulkSize, String sql, RowMapper<T> rowMapper) {
        this(ds, bulkSize, sql, null, null, rowMapper);
    }

    protected void init() {
        this.isInitialized = true;
        loadNextBulkIfPossible();
        //initialize current object, so that hasNext() returns true if there's anything
        //to return
        if (this.currentBulk != null && currentBulk.size() > 0) {
            currentIterator = currentBulk.iterator();
        }
    }

    private void checkInitialized() {
        if (!isInitialized) throw new IllegalStateException("init() has not been called by implementing class");
    }

    public T next() {
        checkInitialized();
        if (currentIterator == null) {
            throw new NoSuchElementException("no element to return");
        }
        return currentIterator.next();
    }

    public boolean hasNext() {
        checkInitialized();
        if (currentIterator == null) {
            return false;
        }
        if (!currentIterator.hasNext()) {
            loadNextBulkIfPossible();
        }
        return currentIterator.hasNext();
    }

    public void remove() {
        throw new UnsupportedOperationException("This iterator does not support removal");
    }

    private void loadNextBulkIfPossible() {
        setLimit(offsetInResult, bulkSize);
        if (logger.isDebugEnabled()) {
            logger.debug("loading next bulk. offset= " + offsetInResult + ", bulkSize=" + bulkSize);
        }
        offsetInResult += bulkSize;
        this.currentBulk = jdbcTemplate.query(sql, args, types, rowMapper);
        if (currentBulk != null && currentBulk.size() > 0) {
            currentIterator = currentBulk.iterator();
        }
    }

    protected void setLimit(int offset, int count) {
        args[args.length - 2] = offset;
        args[args.length - 1] = count;
    }
}
