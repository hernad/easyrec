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
 * This interface provides methods to access data in a root database. Provides methods to create or delete databases.
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

public interface DBRootDAO {
    /**
     * Checks if the specified database exists.
     *
     * @param databaseName
     * @return
     */
    public boolean existsDatabase(String databaseName);

    /**
     * Creates the given database.
     *
     * @param databaseName
     */
    public void createDatabase(String databaseName);

    /**
     * Creates the given database.
     *
     * @param databaseName
     * @param overwriteIfExists
     */
    public void createDatabase(String databaseName, boolean overwriteIfExists);

    /**
     * Creates the given database.
     *
     * @param databaseName
     * @param overwriteIfExists
     * @param checkExisting
     */
    public void createDatabase(String databaseName, boolean overwriteIfExists, boolean checkExisting);

    /**
     * Creates the given database.
     *
     * @param databaseName
     * @param overwriteIfExists
     * @param checkExisting
     * @param ommitErrorIfExisting
     */
    public void createDatabase(String databaseName, boolean overwriteIfExists, boolean checkExisting,
                               boolean ommitErrorIfExisting);

    /**
     * Deletes the given database.
     *
     * @param databaseName
     */
    public void deleteDatabase(String databaseName);

    /**
     * Deletes the given database.
     *
     * @param databaseName
     * @param checkExisting
     */
    public void deleteDatabase(String databaseName, boolean checkExisting);
}
