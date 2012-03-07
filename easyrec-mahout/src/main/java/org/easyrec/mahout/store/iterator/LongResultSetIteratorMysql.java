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

package org.easyrec.mahout.store.iterator;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.easyrec.utils.spring.store.ResultSetIteratorMysql;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: dmann
 * Date: 14.09.11
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
public class LongResultSetIteratorMysql implements LongPrimitiveIterator {

    DataSource dataSource;
    String sql;
    Object[] args;
    int[] argTypes;
    private Long peekValue = null;
    private ResultSetIteratorMysql<Long> delegate;

    public LongResultSetIteratorMysql(DataSource dataSource, String sql, Object[] args, int[] argTypes) {
        this.dataSource = dataSource;
        this.sql = sql;
        this.args = args;
        this.argTypes = argTypes;
        this.delegate = new ResultSetIteratorMysql<Long>(dataSource, 1000, sql, args, argTypes, new RowMapper<Long>() {

            @Override
            public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getLong(1);
            }
        });
    }


    @Override
    public long nextLong() {
        return next().longValue();
    }

    @Override
    public long peek() {
        if (peekValue == null) {
            //throws a NoSuchElementException if hasNext() would return false
            peekValue = delegate.next();
        }
        return peekValue.longValue();
    }

    @Override
    public void skip(int n) {
        for (int i = 0; i < n; i++){
            if (delegate.hasNext())
                delegate.next();
        }
    }

    @Override
    public boolean hasNext() {
        if (peekValue != null) {
            return true;
        } else {
            return delegate.hasNext();
        }
    }

    @Override
    public Long next() {
        if (peekValue != null) {
            Long tmpPeekValue = peekValue;
            peekValue = null;
            return tmpPeekValue;
        } else {
            peekValue = null;
            return delegate.next();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove is not supported by this iterator");
    }
}
