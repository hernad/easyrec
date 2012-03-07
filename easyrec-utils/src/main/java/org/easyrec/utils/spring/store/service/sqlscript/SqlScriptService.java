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
package org.easyrec.utils.spring.store.service.sqlscript;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * Allows for parsing and execution of sql scripts.
 * </p>
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

public interface SqlScriptService {
    /**
     * parse the given script and return all contained statements in a
     * <code>List</code> of <code>String</code>.
     *
     * @param filename, also supports classpath:pseudourl (for eg: classpath:sql/MyTable.sql) from the spring framework
     * @return a list of strings, one for each sql statement
     * @throws RuntimeException if no file is found under the given filename
     */
    public List<String> parseSqlScript(String filename) throws RuntimeException;

    /**
     * executes the given sql script
     *
     * @param filename, also supports classpath:pseudourl (for eg: classpath:sql/MyTable.sql) from the spring framework
     * @throws RuntimeException if no file is found under the given filename
     */
    public void executeSqlScript(String filename) throws RuntimeException;

    /**
     * parse the given script and return all contained statements in a
     * <code>List</code> of <code>String</code>.
     * The script is accessed through the given <code>InputStream</code>
     *
     * @param in
     * @return a list of strings, one for each sql statement
     */
    public List<String> parseSqlScript(InputStream in);

    /**
     * executes the sql script which is accessed through the given <code>InputStream</code>
     *
     * @param in
     */
    public void executeSqlScript(InputStream in);

}
