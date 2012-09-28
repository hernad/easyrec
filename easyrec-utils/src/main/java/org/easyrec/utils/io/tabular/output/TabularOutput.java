package org.easyrec.utils.io.tabular.output;


/**
 * <p>
 * Interface for row-oriented output of tabular data.
 * </p>
 * <p/>
 * Implementing classes must work like this:
 * <pre>
 *  //set column names
 *  output.setColumnName(0, "id");
 *  output.setColumnName(1, "type");
 *  output.setColumnName(2, "status");
 * <p/>
 *  //print header
 *  output.printHeader();
 * <p/>
 *  //set value for first row
 *  output.setField(0, "1");
 *  output.setField(1, "TYPE_FOO");
 *  output.setField(2, "STATUS_BAR");
 * <p/>
 *  //print first row
 *  output.printRow();
 * <p/>
 *  //continue printing following rows...
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
public interface TabularOutput {

    /**
     * Prints the header of the table using the column names
     * that have been configured using the <code>setColumnName()</code> method.
     */
    public void printHeader();

    /**
     * Prints a comment to the output.
     *
     * @param comment
     */
    public void printComment(String comment);

    /**
     * Prints the current row and prepares the next one.<br>
     * note: this method must be used exactly once for each row
     */
    public void printRow();

    /**
     * Outputs the end of the table
     * Note: Depending on the implementation, this method may not have any effect
     */
    public void printFooter();

    /**
     * Closes output resources and cleans up.
     */
    public void close();

    /**
     * Returns the number of columns that this table is configured to have.
     *
     * @return
     */
    public int getColumnCount();

    /**
     * set the name of the column
     *
     * @param columnIndex the index of the column (0-based)
     * @param title       any string
     */
    public void setColumnName(int columnIndex, String title);

    /**
     * Sets the field in the column with the given <code>name</code>
     * in the current row to the given <code>value</code>.
     *
     * @param name
     * @param value
     */
    public void setField(String name, Object value);

    /**
     * Sets the field in the column with the given <code>index</code>
     * in the current row to the given <code>value</code>.
     *
     * @param columnIndex
     * @param value
     */
    public void setField(int columnIndex, Object value);

    /**
     * Sets the default value for the column with the given <code>name</code>.
     *
     * @param columnName
     * @param defaultValue
     */
    public void setDefault(String columnName, String defaultValue);

    /**
     * Sets the default value for the column with the given index.
     *
     * @param columnIndex
     * @param defaultValue
     */
    public void setDefault(int columnIndex, String defaultValue);

    /**
     * Sets the default value for the column with the given name, adding a comment
     * by default.
     *
     * @param columnName
     * @param defaultValue
     * @param comment
     */
    public void setDefault(String columnName, String defaultValue, String comment);

    /**
     * Sets the default value for the column with the given index, adding a comment
     * by default.
     *
     * @param columnIndex
     * @param defaultValue
     * @param comment
     */
    public void setDefault(int columnIndex, String defaultValue, String comment);


}
