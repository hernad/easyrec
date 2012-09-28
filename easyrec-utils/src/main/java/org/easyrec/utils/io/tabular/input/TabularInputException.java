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
package org.easyrec.utils.io.tabular.input;

/**
 * <DESCRIPTION>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Florian Kleedorfer
 */

public class TabularInputException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 5613064832035496072L;
    private int rowIndex;
    private int colIndex;


    /**
     * @param message
     */
    public TabularInputException(String message, int rowIndex, int colIndex) {
        super("Error at row #" + rowIndex + " column #" + colIndex + ": " + message);
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    /**
     * @param message
     */
    public TabularInputException(int rowIndex, int colIndex) {
        super("Error at row #" + rowIndex + " column #" + colIndex);
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    /**
     * @param cause
     */
    public TabularInputException(Throwable cause, int rowIndex, int colIndex) {
        super("Error at row #" + rowIndex + " column #" + colIndex, cause);
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public Integer getColIndex() {
        return colIndex;
    }


}
