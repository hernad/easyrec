/**Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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
package org.easyrec.utils.io.tabular.input.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.utils.io.tabular.input.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reads tabular data into lists of String. There are three ways to use this
 * class: via its Iterator methods, via its readAll() method or by implementing
 * a TabularInputObserver (and calling visitAll() or using one of the other two
 * methods as well).
 * <p>
 * Note: empty lines are quietly omitted.
 * </p>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2007
 * </p>
 * <p/>
 * <p>
 * <b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fr, 12 Aug 2011) $<br/>
 * $Revision: 119 $
 * </p>
 *
 * @author Florian Kleedorfer
 */

public abstract class AbstractTabularInput implements TabularInput {

    private static final String DEFAULT_CHARSET = "ISO-8859-1";
    protected BufferedReader reader;
    protected Log logger = LogFactory.getLog(getClass());
    protected int rowNum = 0;
    protected String currentLine = null;
    private List<String> currentFields = null;
    protected int columnCount = 0;

    // this is just a copy of the first currentFields list
    private List<String> columnNames = null;

    protected Set<TabularInputObserver> observers = new HashSet<TabularInputObserver>();
    private boolean endOfStream = false;

    /*
      * (non-Javadoc)
      *
      * @see
      * at.researchstudio.sat.utils.io.tabular.input.TabularInput#addObserver
      * (at.researchstudio.sat.utils.io.tabular.input.TabularInputObserver)
      */
    public void addObserver(TabularInputObserver observer) {
        observers.add(observer);
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * at.researchstudio.sat.utils.io.tabular.input.TabularInput#removeObserver
      * (at.researchstudio.sat.utils.io.tabular.input.TabularInputObserver)
      */
    public void removeObserver(TabularInputObserver observer) {
        observers.remove(observer);
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * at.researchstudio.sat.utils.io.tabular.input.TabularInput#clearObservers
      * ()
      */
    public void clearObservers() {
        observers.clear();
    }

    /*
      * (non-Javadoc)
      *
      * @see java.util.Iterator#hasNext()
      */
    public boolean hasNext() {
        if (this.reader == null) throw new IllegalStateException("No reader available. Did you call setSource(...)?");
        boolean ret = this.currentFields != null;
        if (!ret) fireOnFinish();
        return ret;
    }

    /*
      * (non-Javadoc)
      *
      * @see java.util.Iterator#next()
      */
    public List<String> next() {
        if (this.reader == null) throw new IllegalStateException("No reader available. Did you call setSource(...)?");
        List<String> ret = this.currentFields;
        prepareNext();
        return ret;
    }

    /* (non-Javadoc)
      * @see at.researchstudio.sat.utils.io.tabular.input.TabularInput#nextMappedObject(at.researchstudio.sat.utils.io.tabular.input.TabularInputRowMapper)
      */
    public <T> T next(TabularInputRowMapper<T> mapper) {
        //subtracting 1 from rowNum as the call to next() advances the num once more, so we are
        //here 1 ahead
        return mapper.mapRow(next(), this.rowNum - 1, this.rowNum - 1 == 1, !hasNext());
    }


    /**
     * Reads all data from the current position on into a List<List<String>>.
     *
     * @return
     */
    public List<List<String>> readAll() {
        List<List<String>> data = new ArrayList<List<String>>();
        while (hasNext()) {
            data.add(next());
        }
        return data;
    }

    /* (non-Javadoc)
      * @see at.researchstudio.sat.utils.io.tabular.input.TabularInput#mapRows(at.researchstudio.sat.utils.io.tabular.input.TabularInputRowMapper)
      */
    public <T> List<T> readAll(TabularInputRowMapper<T> mapper) {
        List<T> data = new ArrayList<T>();
        while (hasNext()) {
            T mapped = next(mapper);
            if (mapped != null) data.add(mapped);
        }
        return data;
    }

    /**
     * Reads all data from the current position but does not return anything.
     * This method is only useful in connection with registering observers.
     *
     * @return
     */
    public void visitAll() {
        while (hasNext()) {
            next();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see java.util.Iterator#remove()
      */
    public void remove() {
        throw new UnsupportedOperationException("This iterator does not support remove()");
    }

    private void prepareNext() throws TabularInputException {
        try {
            if (this.endOfStream) return;
            this.rowNum++;
            while ("".equals(this.currentLine = reader.readLine())) ;
            if (this.currentLine == null) {
                this.currentFields = null;
                this.endOfStream = true;
                fireOnFinish();
                freeSourceSpecificResources();
                return;
            }
            this.currentFields = parseLine(this.currentLine);
            if (rowNum == 1) {
                this.columnNames = new ArrayList<String>(this.currentFields);
                this.columnCount = columnNames.size();
                this.fireOnStart();
            } else if (this.currentFields.size() != this.columnCount) {
                throw new InconsistentFieldCountException(rowNum, -1, this.columnCount, this.currentFields.size());
            }
            fireOnDataRow();
        } catch (TabularInputException e) {
            throw e;
        } catch (Exception e) {
            this.fireOnAbort();
            this.currentFields = null;
            closeReader();
            throw new TabularInputException(e, this.rowNum, -1);
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see at.researchstudio.sat.utils.io.tabular.input.TabularInput#abort()
      */
    public void freeSourceSpecificResources() {
        closeReader();
        this.columnNames = null;
        this.currentFields = null;
        this.currentLine = null;
    }

    /**
     *
     */
    private void closeReader() throws TabularInputException {
        if (this.reader != null) {
            try {
                this.reader.close();
            } catch (IOException e) {
                throw new TabularInputException(e, this.rowNum, -1);
            }
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * at.researchstudio.sat.utils.io.tabular.input.TabularInput#setSource(java
      * .io.InputStream, java.lang.String)
      */
    public void setSource(InputStream stream, String charsetName) {
        this.rowNum = 0;
        this.currentLine = null;
        this.currentFields = null;
        this.columnNames = null;
        this.endOfStream = false;
        this.columnCount = 0;
        try {
            this.reader = new BufferedReader(new InputStreamReader(stream, charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new TabularInputException(e, -1, -1);
        }
        prepareNext();
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * at.researchstudio.sat.utils.io.tabular.input.TabularInput#process(java
      * .io.File)
      */
    public void setSource(File file, String charsetName) throws TabularInputException {
        try {
            setSource(new FileInputStream(file), charsetName);
        } catch (FileNotFoundException e) {
            throw new TabularInputException(e, -1, -1);
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * at.researchstudio.sat.utils.io.tabular.input.TabularInput#process(java
      * .io.File)
      */
    public void setSource(File file) throws TabularInputException {
        try {
            setSource(new FileInputStream(file), DEFAULT_CHARSET);
        } catch (FileNotFoundException e) {
            throw new TabularInputException(e, 0, 0);
        }
    }

    protected abstract List<String> parseLine(String line);

    protected void fireOnStart() {
        for (TabularInputObserver o : this.observers) {
            o.onStart(this.currentFields.size(), this.columnNames);
        }
    }

    protected void fireOnDataRow() {
        for (TabularInputObserver o : this.observers) {
            o.onDataRow(this.rowNum, this.currentFields);
        }
    }

    protected void fireOnFinish() {
        for (TabularInputObserver o : this.observers) {
            o.onFinish(this.rowNum);
        }
    }

    protected void fireOnAbort() {
        for (TabularInputObserver o : this.observers) {
            o.onAbort(this.rowNum);
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * at.researchstudio.sat.utils.io.tabular.input.TabularInput#getColumnNames
      * ()
      */
    public List<String> getColumnNames() {
        return this.columnNames;
    }

}
