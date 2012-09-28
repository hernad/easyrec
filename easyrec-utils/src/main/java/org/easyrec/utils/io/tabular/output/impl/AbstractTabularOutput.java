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
package org.easyrec.utils.io.tabular.output.impl;

import org.easyrec.utils.io.tabular.output.TabularOutput;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Abstract base class for implementations of <code>TabularOutput</code>.
 * </p>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2005</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Florian Kleedorfer
 * @author Roman Cerny
 */

public abstract class AbstractTabularOutput implements TabularOutput {
    protected int columnCount = 0;
    protected List<String> header;
    protected List<String> defaults;
    protected List<String> currentRow;
    protected PrintWriter out;


    public AbstractTabularOutput(int columnCount, String outFileName) throws FileNotFoundException {
        this(columnCount, new PrintWriter(outFileName));
    }

    public AbstractTabularOutput(int columnCount, File outFile) throws FileNotFoundException {
        this(columnCount, new PrintWriter(outFile));
    }


    public AbstractTabularOutput(int columnCount, PrintWriter out) {
        header = new ArrayList<String>();
        defaults = new ArrayList<String>();
        this.columnCount = columnCount;
        for (int i = 0; i < columnCount; i++) {
            header.add("col " + i);
            defaults.add("");
        }
        this.out = out;
    }

    public AbstractTabularOutput(int columnCount, PrintStream out) {
        this(columnCount, new PrintWriter(out));
    }

    public AbstractTabularOutput(int columnCount, OutputStream out) {
        this(columnCount, new PrintWriter(out));
    }


    protected abstract String makeHeaderRow(List<String> header, List<String> defaults);


    protected abstract String makeDataRow(List<? extends Object> content);


    /* (non-Javadoc)
    * @see at.researchstudio.sat.utils.io.tabular.TabularOutput#printHeader()
    */
    public void printHeader() {
        out.println(makeHeaderRow(header, defaults));
        out.flush();
    }

    /* (non-Javadoc)
    * @see at.researchstudio.sat.utils.io.tabular.TabularOutput#printRow()
    */
    public void printRow() {
        out.println(makeDataRow(currentRow));
        out.flush();
        prepareNewRow();
    }

    /**
     * empty implementation
     */
    public void printFooter() {
        //empty implementation
    }

    /* (non-Javadoc)
    * @see at.researchstudio.sat.utils.io.tabular.TabularOutput#close()
    */
    public void close() {
        out.close();
    }

    public int getColumnCount() {
        return columnCount;
    }

    /* (non-Javadoc)
    * @see at.researchstudio.sat.utils.io.tabular.TabularOutput#setColumnName(int, java.lang.String)
    */
    public void setColumnName(int columnIndex, String title) {
        this.header.set(columnIndex, title);
    }

    /* (non-Javadoc)
    * @see at.researchstudio.sat.utils.io.tabular.TabularOutput#setDefault(java.lang.String, java.lang.String)
    */
    public void setDefault(String columnName, String defaultValue) {
        this.setDefault(columnName, defaultValue, null);
    }

    /* (non-Javadoc)
    * @see at.researchstudio.sat.utils.io.tabular.TabularOutput#setDefault(int, java.lang.String)
    */
    public void setDefault(int columnIndex, String defaultValue) {
        this.setDefault(columnIndex, defaultValue, null);
    }

    /* (non-Javadoc)
    * @see at.researchstudio.sat.utils.io.tabular.TabularOutput#setDefault(java.lang.String, java.lang.String, java.lang.String)
    */
    public void setDefault(String columnName, String defaultValue, String comment) {
        int colIndex = header.indexOf(columnName);
        if (colIndex == -1) {
            throw new IllegalArgumentException("no column with name " + columnName + " found");
        }
        this.setDefault(colIndex, defaultValue, comment);
    }

    /* (non-Javadoc)
    * @see at.researchstudio.sat.utils.io.tabular.TabularOutput#setDefault(int, java.lang.String, java.lang.String)
    */
    public abstract void setDefault(int columnIndex, String defaultValue, String comment);

    /* (non-Javadoc)
    * @see at.researchstudio.sat.utils.io.tabular.TabularOutput#setField(java.lang.String, java.lang.Object)
    */
    public void setField(String name, Object value) {
        int colIndex = header.indexOf(name);
        if (colIndex == -1) {
            throw new IllegalArgumentException("no column with name " + name + " found");
        }
        this.setField(colIndex, value);
    }

    /* (non-Javadoc)
    * @see at.researchstudio.sat.utils.io.tabular.TabularOutput#setField(int, java.lang.Object)
    */
    public void setField(int columnIndex, Object value) {
        if (currentRow == null) {
            prepareNewRow();
        }
        if (value == null) {
            currentRow.set(columnIndex, "null");
        } else {
            currentRow.set(columnIndex, value.toString());
        }

    }

    protected void prepareNewRow() {
        currentRow = new ArrayList<String>();
        for (int i = 0; i < columnCount; i++) {
            currentRow.add("");
        }
    }

    protected String makeHeaderLine(List<String> header) {
        return makeHeaderRow(header, defaults);
    }


}
