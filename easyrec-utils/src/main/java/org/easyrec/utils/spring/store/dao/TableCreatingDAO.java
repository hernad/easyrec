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
package org.easyrec.utils.spring.store.dao;

/**
 * This interface provides methods to create an SQL database table using a given script.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fr, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Roman Cerny
 */
public interface TableCreatingDAO {
    // methods

    /**
     * @return the default name of the SQL table
     */
    public String getDefaultTableName();

    /**
     * @return the absolute path to the .sql file containing the CREATE statement
     *         eg: "classpath:sql/Action.sql"
     *         Note: this file should NOT contain any other SQL statements
     */
    public String getTableCreatingSQLScriptName();

    /**
     * this method creates the SQL table, using the .sql script retrieve via <code>getTableCreatingSQLScriptName()<code>.
     */
    public void createTable();

    /**
     * checks if the table (name retrieved from <code>getDefaultTableName()</code> exists.
     *
     * @return
     */
    public boolean existsTable();
}
