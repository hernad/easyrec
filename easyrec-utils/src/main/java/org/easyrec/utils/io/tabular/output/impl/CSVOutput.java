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
package org.easyrec.utils.io.tabular.output.impl;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This class is capable of generating a comma separeted values file (.csv).
 * Methods for header generation and printing rows are provided.
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

public class CSVOutput extends AbstractTabularOutput {

    public final static char SEP_SEMICOLON = ';';

    public final static char SEP_COMMA = ',';

    public final static char DEF_SEPARATOR = '=';

    public final static char SEP_COMMENT_BEGIN = '(';

    public final static char SEP_COMMENT_END = ')';

    public final static char COMMENT_CHAR = '#';


    protected char separator = SEP_SEMICOLON;


    /**
     * creates a new CSVOutput with semicolon as separator
     * and a default header for the given number of columns
     */
    public CSVOutput(int columnCount, File outFile) throws FileNotFoundException {
        super(columnCount, outFile);
    }

    /**
     * creates a new CSVOutput with semicolon as separator
     * and a default header for the given number of columns
     */
    public CSVOutput(int columnCount, PrintStream out) {
        super(columnCount, out);
    }

    /**
     * creates a new CSVOutput with semicolon as separator
     * and a default header for the given number of columns
     */
    public CSVOutput(int columnCount, String outFileName) throws FileNotFoundException {
        super(columnCount, outFileName);
    }

    /**
     * creates a new CSVOutput with semicolon as separator
     * and a default header for the given number of columns
     */
    public CSVOutput(int columnCount, PrintWriter out) {
        super(columnCount, out);
    }

    /**
     * creates a new CSVOutput with semicolon as separator
     * and a default header for the given number of columns
     */
    public CSVOutput(int columnCount, OutputStream out) {
        super(columnCount, out);
    }

    public CSVOutput(int columnCount, PrintWriter out, char separator) {
        super(columnCount, out);
        this.separator = separator;
    }


    /* (non-Javadoc)
    * @see at.researchstudio.sat.utils.io.tabular.TabularOutput#printComment(java.lang.String)
    */
    public void printComment(String comment) {
        StringBuilder buf = new StringBuilder();
        buf.append(COMMENT_CHAR);
        buf.append(" ");
        buf.append(comment);
        out.println(buf.toString());
        out.flush();
    }

    /* (non-Javadoc)
    * @see at.researchstudio.sat.utils.io.tabular.TabularOutput#setDefault(int, java.lang.String, java.lang.String)
    */
    @Override
    public void setDefault(int columnIndex, String defaultValue, String comment) {
        if (comment != null) {
            this.defaults.set(columnIndex, defaultValue + SEP_COMMENT_BEGIN + comment + SEP_COMMENT_END);
        } else {
            this.defaults.set(columnIndex, defaultValue);
        }
    }

    @Override
    protected void prepareNewRow() {
        currentRow = new ArrayList<String>();
        for (int i = 0; i < columnCount; i++) {
            currentRow.add("");
        }
    }

    @Override
    protected String makeHeaderRow(List<String> header, List<String> defaults) {
        StringBuilder buf = new StringBuilder();
        Iterator<String> iterHead = header.iterator();
        Iterator<String> iterDef = defaults.iterator();
        for (; iterHead.hasNext() && iterDef.hasNext();) {
            String headerElement = iterHead.next();
            String defaultElement = iterDef.next();
            buf.append(headerElement);
            if (!"".equals(defaultElement)) {
                buf.append(DEF_SEPARATOR);
                buf.append(defaultElement);
            }
            buf.append(separator);
        }
        if (buf.length() > 0) {
            buf.delete(buf.length() - 1, buf.length());
        }
        return buf.toString();
    }

    @Override
    protected String makeDataRow(List<? extends Object> content) {
        if (content == null) {
            //allow printing of empty lines
            prepareNewRow();
            content = this.currentRow;
        }
        StringBuilder buf = new StringBuilder();
        for (Iterator<? extends Object> iter = content.iterator(); iter.hasNext();) {
            buf.append(iter.next());
            buf.append(separator);
        }
        if (buf.length() > 0) {
            buf.delete(buf.length() - 1, buf.length());
        }
        return buf.toString();
    }


}
