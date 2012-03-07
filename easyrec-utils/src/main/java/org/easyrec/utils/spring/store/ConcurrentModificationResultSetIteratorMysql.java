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

import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

/**
 * Iterator that allows bulkwise fetching of large result sets for mysql. This implementation
 * always fetches the first <code>bulkSize</code> data rows and expects the whole result set
 * to decrease in size and be empty eventually
 * <p/>
 * <p>
 * Example: <br />
 * <p/>
 * <pre>
 *       StringBuilder sql = new StringBuilder();
 *       sql.append("select * from myTable where someValue = ? and someOtherValue is null ");
 *       Object[] args = { id };
 *       int[] types = { Types.BIGINT };
 *       Iterator<MyClass> resultIt = new ResultSetIterator<MyClass>(
 *               myDataSource,bulkSize, sql.toString(),
 *               args, types, commandQueueEntryRowMapper);
 * <p/>
 *       while (resultIt.hasNext()){
 *          MyClass obj = resultIt.next();
 *          //do something that puts some value in field someOtherValue so that
 *          //the next result set is smaller...
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
 * $Date: 2011-08-12 18:40:54 +0200 (Fr, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Florian Kleedorfer
 */

public class ConcurrentModificationResultSetIteratorMysql<T> extends ResultSetIteratorMysql<T> {
    public ConcurrentModificationResultSetIteratorMysql(DataSource ds, int bulkSize, String sql, Object[] args,
                                                        int[] types, RowMapper<T> rowMapper) {
        super(ds, bulkSize, sql, args, types, rowMapper);
    }

    public ConcurrentModificationResultSetIteratorMysql(DataSource ds, int bulkSize, String sql,
                                                        RowMapper<T> rowMapper) {
        super(ds, bulkSize, sql, rowMapper);
    }

    /**
     * overwrites <code>setLimit(int,int)</code> in superclass, using only <code>count</code>
     *
     * @param offset
     * @param count
     */
    @Override
    protected void setLimit(int offset, int count) {
        super.setLimit(0, count);
    }
};
