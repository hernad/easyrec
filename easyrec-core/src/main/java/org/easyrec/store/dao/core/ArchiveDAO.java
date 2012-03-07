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
package org.easyrec.store.dao.core;

import java.util.Date;


/**
 * This class manages archiving actions to the archive.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-28 20:03:01 +0100 (Mo, 28 Feb 2011) $<br/>
 * $Revision: 17807 $</p>
 *
 * @author <AUTHOR>
 */
public interface ArchiveDAO {
    /**
     * Returns the tablename of the actual archive.
     * An archive contains MAX_TABLE_RECORDS. If the archive reached this
     * limit, a new archive with an autoincrement number will be created.
     *
     * @return
     */
    public String getActualArchiveTableName();

    /**
     * Returns the number of actions to archive that are older than the given Date.
     *
     * @param tenantId
     * @param refDate
     */
    public Integer getNumberOfActionsToArchive(int tenantId, Date refDate);


    /**
     * moves actions for a given tenant to archive that are older than the given Date.
     *
     * @param tenantId
     * @param refDate
     */
    public void moveActions(String tablename, int tenantId, Date refDate);

    /**
     * Generates a new archive table, which is named actionarchive[i].
     * where is an autoincrementing number.
     *
     * @param actualArchiveTableName
     * @return
     */
    public String generateNewArchive(String actualArchiveTableName);

    /**
     * Returns the number of records in the archive
     *
     * @return
     */
    public Integer getArchiveSize(String tablename);

    /**
     * Returns true, if archive table is not capable of the number of actions.
     *
     * @param actionsToAdd
     * @return
     */
    public boolean isArchiveFull(String tablename, Integer actionsToAdd);

}
