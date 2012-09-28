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

import org.easyrec.utils.io.CommandLineTools;

import java.io.*;
import java.util.List;

/**
 * <p>
 * This class allows formatting tabular data as plain text. E.g.:
 * <p/>
 * <pre>
 *      +=====+=====+=====+
 *      |one  |two  |three|
 *      +=====+=====+=====+
 *      |the  |quick|brown|
 *      |fox  |jumps|over |
 *      |the  |lazy |dog  |
 *      +=====+=====+=====+
 * </pre>
 * <p/>
 * The characters used for the table borders can be configured, column width can
 * be set individually. Optionally, a horizontal separator can be displayed
 * between the data rows.
 * </p>
 * <p>
 * The above table is produced using this code:
 * <p/>
 * <pre>
 *       TabularTextOutput output = new TabularTextOutput(9,System.out);
 *       output.setColumnWidth(0,5);                    //set column widths
 *       output.setColumnWidth(1,5);
 *       output.setColumnWidth(2,5;
 *       output.setColumnName(0,&quot;one&quot;);      //set the header (column names)
 *       output.setColumnName(1,&quot;two&quot;);
 *       output.setColumnName(2,&quot;three&quot;);
 *       output.printHeader();
 *       output.setField(&quot;one&quot;,&quot;the&quot;);         //access fields by header
 *       output.setField(&quot;two&quot;,&quot;quick&quot;);
 *       output.setField(&quot;three&quot;,&quot;brown&quot;);
 *       output.printRow();                              //print the row
 *       output.setField(0,&quot;fox&quot;);             //access fields by column index
 *       output.setField(1,&quot;jumps&quot;);
 *       output.setField(2,&quot;over&quot;);
 *       output.printRow();                              //print the row
 *       output.setField(0,&quot;the&quot;);             //access fields by column index
 *       output.setField(1,&quot;lazy&quot;);            //
 *       output.setField(2,&quot;dog&quot;);             //
 *       output.printRow();                              //print the row
 *       output.printFooter();                           //print the footer row
 *       //output.close();          //normally, we would close, but on stdout,we don't do that
 * <p/>
 * </pre>
 * <p/>
 * </p>
 * <p/>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2005
 * </p>
 * <p/>
 * <p>
 * <b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $
 * </p>
 *
 * @author Florian Kleedorfer
 */

public class TabularTextOutput extends AbstractTabularOutput {
    public static final int DEFAULT_COLUMN_WIDTH = 10;

    private int[] columnWidths;

    // cache for the row separator (lazily initialized)
    private String rowSeparator;

    // cache for the header row separator (lazily initialized)
    private String headerRowSeparator;

    // cache for the outer row separator (lazily initialized)
    private String outerRowSeparator;

    private char verticalOuter = '|';

    private char horizontalOuter = '=';

    private char verticalInner = '|';

    private char horizontalInner = '-';

    private char cornerOuter = '+';

    private char cornerInner = '+';

    private char horizontalHeader = '=';

    private char cornerHeader = '+';

    /**
     * create new instance that will write to <code>outFile</code>
     *
     * @param columnCount number of columns
     * @param outFile
     * @throws FileNotFoundException
     */
    public TabularTextOutput(int columnCount, File outFile) throws FileNotFoundException {
        super(columnCount, outFile);
        setAllColumnWidthsToDefault();
    }

    /**
     * create new instance that will write to the given PrintWriter
     *
     * @param columnCount
     * @param out
     */
    public TabularTextOutput(int columnCount, PrintWriter out) {
        super(columnCount, out);
        setAllColumnWidthsToDefault();
    }

    /**
     * create new instance that will write to the given PrintStream
     *
     * @param columnCount
     * @param out
     */
    public TabularTextOutput(int columnCount, PrintStream out) {
        super(columnCount, out);
        setAllColumnWidthsToDefault();
    }

    public TabularTextOutput(int columnCount, OutputStream out) {
        super(columnCount, out);
        setAllColumnWidthsToDefault();
    }

    public TabularTextOutput(int columnCount, String outFileName) throws FileNotFoundException {
        super(columnCount, outFileName);
        setAllColumnWidthsToDefault();
    }

    /**
     * set display width of the column <code>columnIndex</code> (0-based)
     *
     * @param columnIndex
     * @param width
     */
    public void setColumnWidth(int columnIndex, int width) {
        if (columnIndex < 0 || columnIndex > columnCount)
            throw new IllegalArgumentException("column index must be >= 0 and < " + columnCount);
        if (width < 0) throw new IllegalArgumentException("width must not be < 0");
        this.columnWidths[columnIndex] = width;
        // set rowSeparator to null so that it will be regenerated when needed
        this.rowSeparator = null;
        this.headerRowSeparator = null;
        this.outerRowSeparator = null;
    }

    /**
     * Set the char that is used for the corner points in the separator below
     * the header. In this example, this char is set to '0':
     * <p/>
     * <pre>
     *      +=====+=====+=====+
     *      |one  |two  |three|
     *      +=====0=====0=====+
     *      |the  |quick|brown|
     *      |fox  |jumps|over |
     *      |the  |lazy |dog  |
     *      +=====+=====+=====+
     * </pre>
     *
     * @param cornerHeader the cornerHeader to set
     */
    public void setCornerHeader(char cornerHeader) {
        this.cornerHeader = cornerHeader;
    }

    /**
     * Set the char that is used for the inner corner points in the separators
     * below each data row. This only has an effect when
     * <code>setUseRowSeparator(true)</code> has been called. In this example,
     * this char is set to '0':
     * <p/>
     * <pre>
     *      +=====+=====+=====+
     *      |one  |two  |three|
     *      +=====+=====+=====+
     *      |the  |quick|brown|
     *      +-----0-----0-----+
     *      |fox  |jumps|over |
     *      +-----0-----0-----+
     *      |the  |lazy |dog  |
     *      +=====+=====+=====+
     * </pre>
     *
     * @param cornerInner the cornerInner to set
     */
    public void setCornerInner(char cornerInner) {
        this.cornerInner = cornerInner;
    }

    /**
     * Set the char that is used for all outer corner points. In this example,
     * this char is set to '0':
     * <p/>
     * <pre>
     *      0=====0=====0=====0
     *      |one  |two  |three|
     *      0=====+=====+=====0
     *      |the  |quick|brown|
     *      0-----+-----+-----0
     *      |fox  |jumps|over |
     *      0-----+-----+-----0
     *      |the  |lazy |dog  |
     *      0=====0=====0=====0
     * </pre>
     *
     * @param cornerOuter the cornerOuter to set
     */
    public void setCornerOuter(char cornerOuter) {
        this.cornerOuter = cornerOuter;
    }

    /**
     * Set the char that is used for horizontal bar in the separator below the
     * header.
     * <p/>
     * In this example, this char is set to '0':
     * <p/>
     * <pre>
     *      +=====+=====+=====+
     *      |one  |two  |three|
     *      +00000+00000+00000+
     *      |the  |quick|brown|
     *      +-----+-----+-----+
     *      |fox  |jumps|over |
     *      +-----+-----+-----+
     *      |the  |lazy |dog  |
     *      +=====+=====+=====+
     * </pre>
     *
     * @param horizontalHeader the horizontalHeader to set
     */
    public void setHorizontalHeader(char horizontalHeader) {
        this.horizontalHeader = horizontalHeader;
    }

    /**
     * Set the char that is used for the horizontal bar inside the table.
     * <p/>
     * In this example, this char is set to '0':
     * <p/>
     * <pre>
     *      +=====+=====+=====+
     *      |one  |two  |three|
     *      +=====+=====+=====+
     *      |the  |quick|brown|
     *      +00000+00000+00000+
     *      |fox  |jumps|over |
     *      +00000+00000+00000+
     *      |the  |lazy |dog  |
     *      +=====+=====+=====+
     * </pre>
     *
     * @param horizontalInner the horizontalInner to set
     */
    public void setHorizontalInner(char horizontalInner) {
        this.horizontalInner = horizontalInner;
    }

    /**
     * Set the char that is used for the horizontal bar in the outer table
     * border.
     * <p/>
     * In this example, this char is set to '0':
     * <p/>
     * <pre>
     *      +00000+00000+00000+
     *      |one  |two  |three|
     *      +=====+=====+=====+
     *      |the  |quick|brown|
     *      +-----+-----+-----+
     *      |fox  |jumps|over |
     *      +-----+-----+-----+
     *      |the  |lazy |dog  |
     *      +00000+00000+00000+
     * </pre>
     *
     * @param horizontalOuter the horizontalOuter to set
     */
    public void setHorizontalOuter(char horizontalOuter) {
        this.horizontalOuter = horizontalOuter;
    }

    /**
     * Set the char that is used for the vertical bar inside the table.
     * <p/>
     * In this example, this char is set to '0':
     * <p/>
     * <pre>
     *      +=====+=====+=====+
     *      |one  0two  0three|
     *      +=====+=====+=====+
     *      |the  0quick0brown|
     *      +-----+-----+-----+
     *      |fox  0jumps0over |
     *      +-----+-----+-----+
     *      |the  0lazy 0dog  |
     *      +=====+=====+=====+
     * </pre>
     *
     * @param verticalInner the verticalInner to set
     */
    public void setVerticalInner(char verticalInner) {
        this.verticalInner = verticalInner;
    }

    /**
     * Set the char that is used for the vertical bar at the outer table border.
     * <p/>
     * In this example, this char is set to '0':
     * <p/>
     * <pre>
     *      +=====+=====+=====+
     *      0one  |two  |three0
     *      +=====+=====+=====+
     *      0the  |quick|brown0
     *      +-----+-----+-----+
     *      0fox  |jumps|over 0
     *      +-----+-----+-----+
     *      0the  |lazy |dog  0
     *      +=====+=====+=====+
     * </pre>
     *
     * @param verticalOuter the verticalOuter to set
     */
    public void setVerticalOuter(char verticalOuter) {
        this.verticalOuter = verticalOuter;
    }

    /**
     * set a default value for the given column. The <code>comment</code> will
     * be put in brackets and appended to the default value.
     */
    @Override
    public void setDefault(int columnIndex, String defaultValue, String comment) {
        if (comment != null) {
            this.defaults.set(columnIndex, defaultValue + "(" + comment + ")");
        } else {
            this.defaults.set(columnIndex, defaultValue);
        }
    }

    /**
     * Output the lower border of the table.
     * <p/>
     * <pre>
     *      +=====+=====+=====+
     *      |one  |two  |three|
     *      +=====+=====+=====+
     *      |the  |quick|brown|
     *      +-----+-----+-----+
     *      |fox  |jumps|over |
     *      +-----+-----+-----+
     *      |the  |lazy |dog  |
     *      +=====+=====+=====+    &lt;--- Footer
     * </pre>
     */
    @Override
    public void printFooter() {
        out.println(getOuterRowSeparator());
        out.flush();
    }

    /**
     * not implemented.
     */
    public void printComment(String comment) {
        // ignore
    }

    /**
     * print a row separator.
     * <p/>
     * <pre>
     *      +=====+=====+=====+
     *      |one  |two  |three|
     *      +=====+=====+=====+
     *      |the  |quick|brown|
     *      |fox  |jumps|over |
     *      +-----+-----+-----+    lt;--- row separator
     *      |the  |lazy |dog  |
     *      +=====+=====+=====+
     * </pre>
     */
    public void printRowSeparator() {
        out.println(getRowSeparator());
        out.flush();
    }

    /**
     * print a row separator with given char as horizontal bar. In this example,
     * '0' has been passed as argument:
     * <p/>
     * <pre>
     *      +=====+=====+=====+
     *      |one  |two  |three|
     *      +=====+=====+=====+
     *      |the  |quick|brown|
     *      |fox  |jumps|over |
     *      +00000+00000+00000+    lt;--- row separator
     *      |the  |lazy |dog  |
     *      +=====+=====+=====+
     * </pre>
     */
    public void printRowSeparator(char horizontalInner) {
        out.println(makeRowSeparator(horizontalInner));
        out.flush();
    }

    // --------------------------------------------------------------------------
    // protected methods
    // --------------------------------------------------------------------------

    @Override
    protected String makeDataRow(List<? extends Object> content) {
        int columnIndex = 0;
        if (content == null) {
            //allow printing of empty lines
            prepareNewRow();
            content = this.currentRow;
        }
        StringBuilder row = new StringBuilder();
        row.append(verticalOuter);
        for (Object item : content) {
            int width = columnWidths[columnIndex];
            if (item == null) {
                item = defaults.get(columnIndex);
            }
            row.append(CommandLineTools.pad(item, width));
            row.append(verticalInner);
            columnIndex++;
        }
        replaceLastChar(row, verticalOuter);
        return row.toString();
    }

    @Override
    protected String makeHeaderRow(List<String> header, List<String> defaults) {
        StringBuilder buf = new StringBuilder();
        buf.append(getOuterRowSeparator());
        buf.append("\n");
        buf.append(verticalOuter);

        for (int i = 0; i < columnCount; i++) {
            String headerElement = header.get(i);
            int width = columnWidths[i];
            buf.append(CommandLineTools.pad(headerElement, width));
            buf.append(verticalInner);
        }
        replaceLastChar(buf, verticalOuter);
        buf.append("\n");
        buf.append(getHeaderRowSeparator());
        return buf.toString();
    }

    // --------------------------------------------------------------------------
    // private methods
    // --------------------------------------------------------------------------

    private void setAllColumnWidthsToDefault() {
        this.columnWidths = new int[columnCount];
        for (int i = 0; i < this.columnCount; i++) {
            columnWidths[i] = DEFAULT_COLUMN_WIDTH;
        }
    }

    private String getRowSeparator() {
        if (rowSeparator == null) {
            rowSeparator = makeRowSeparator();
        }
        return rowSeparator;
    }

    private String getHeaderRowSeparator() {
        if (headerRowSeparator == null) {
            headerRowSeparator = makeHeaderRowSeparator();
        }
        return headerRowSeparator;
    }

    private String getOuterRowSeparator() {
        if (outerRowSeparator == null) {
            outerRowSeparator = makeOuterRowSeparator();
        }
        return outerRowSeparator;
    }

    private String makeRowSeparator() {
        return makeRowSeparator(horizontalInner);
    }

    private String makeRowSeparator(char inner) {
        StringBuilder row = new StringBuilder();
        row.append(cornerOuter);
        for (int i = 0; i < columnWidths.length; i++) {
            row.append(CommandLineTools.pad("", columnWidths[i], inner));
            row.append(cornerInner);
        }
        replaceLastChar(row, cornerOuter);
        return row.toString();
    }

    private String makeHeaderRowSeparator() {
        StringBuilder row = new StringBuilder();
        row.append(cornerOuter);
        for (int i = 0; i < columnWidths.length; i++) {
            row.append(CommandLineTools.pad("", columnWidths[i], horizontalHeader));
            row.append(cornerHeader);
        }
        replaceLastChar(row, cornerOuter);
        return row.toString();
    }

    private String makeOuterRowSeparator() {
        StringBuilder row = new StringBuilder();
        row.append(this.cornerOuter);
        for (int i = 0; i < columnWidths.length; i++) {
            row.append(CommandLineTools.pad("", columnWidths[i], horizontalOuter));
            row.append(cornerOuter);
        }
        return row.toString();
    }

    /**
     * replaces the last character of the given StringBuilder with replacement
     *
     * @param row
     */
    private void replaceLastChar(StringBuilder row, char replacement) {
        row.deleteCharAt(row.length() - 1);
        row.append(replacement);
    }

}
