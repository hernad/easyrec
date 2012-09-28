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

import org.easyrec.utils.io.tabular.input.TabularInputObserver;

import java.util.List;


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

public abstract class AbstractTabularInputObserver implements TabularInputObserver {

    /* (non-Javadoc)
      * @see at.researchstudio.sat.utils.io.tabular.input.TabularInputObserver#onAbort(int)
      */
    public void onAbort(int rowNum) {
        ///empty implementation
    }

    /* (non-Javadoc)
      * @see at.researchstudio.sat.utils.io.tabular.input.TabularInputObserver#onDataRow(int, java.util.List)
      */
    public void onDataRow(int rowNum, List<String> values) {
        ///empty implementation
    }

    /* (non-Javadoc)
      * @see at.researchstudio.sat.utils.io.tabular.input.TabularInputObserver#onFinish(int)
      */
    public void onFinish(int rowCount) {
        ///empty implementation
    }

    /* (non-Javadoc)
      * @see at.researchstudio.sat.utils.io.tabular.input.TabularInputObserver#onStart(int, java.util.List)
      */
    public void onStart(int columnCount, List<String> columnNames) {
        ///empty implementation
    }

}
