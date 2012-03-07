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
package org.easyrec.store.dao.core.impl;


import com.google.common.base.Strings;
import org.easyrec.store.dao.core.ArchiveDAO;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Date;

/**
 * The Class implements the dao operations for archiving actions.
 * <p/>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2007
 * <p/>
 * <b>last modified:</b><br/> $Author: pmarschik $<br/> $Date: 2008-07-17
 * 20:00:46 +0200 (Do, 17 Jul 2008) $<br/> $Revision: 17807 $
 * </p>
 *
 * @author <AUTHOR>
 */

public class ArchiveDAOMysqlImpl extends JdbcDaoSupport implements ArchiveDAO {
    private static final int MAX_TABLE_RECORDS = 50 * 1000 * 1000;

    //////////////////////////////////////////////////////////////////////////////
    // constructor
    public ArchiveDAOMysqlImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }


    /**
     * Returns the tablename of the actual archive.
     * An archive contains MAX_TABLE_RECORDS. If the archive reached this
     * limit, a new archive with an autoincrement number will be created.
     *
     * @return
     */
    public String getActualArchiveTableName() {

        String actualArchiveTableName = "actionarchive";
        String oldArchiveTableName = actualArchiveTableName;

        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            try {
                actualArchiveTableName = (String) getJdbcTemplate()
                        .queryForObject("SHOW TABLES LIKE 'actionarchive" + i + "'", String.class);
            } catch (Exception e) {
                actualArchiveTableName = "";
            }

            if (Strings.isNullOrEmpty(actualArchiveTableName))
                return oldArchiveTableName;
            else
                oldArchiveTableName = actualArchiveTableName;
        }
        return null;
    }

    /**
     * Generates a new archive table, which is named actionarchive[i++].
     * where i is an autoincrementing number.
     *
     * @param actualArchiveTableName
     * @return
     */
    public String generateNewArchive(String actualArchiveTableName) {

        Integer getNextIndex = null;
        try {
            String index = actualArchiveTableName.replace("actionarchive", "");

            if(Strings.isNullOrEmpty(index))
                getNextIndex = 1;
            else
                getNextIndex = Integer.valueOf(index) + 1;

            StringBuilder sql = new StringBuilder().append("CREATE TABLE actionarchive").append(getNextIndex)
                    .append(" ( ").append("  id int(11) unsigned NOT NULL auto_increment, ")
                    .append("  tenantId int(11) NOT NULL, ").append("  userId int(11) default NULL, ")
                    .append("  sessionId varchar(50) default NULL, ").append("  ip varchar(45) default NULL, ")
                    .append("  itemId int(11) default NULL, ").append("  itemTypeId int(11) NOT NULL, ")
                    .append("  actionTypeId int(11) NOT NULL, ").append("  ratingValue int(11) default NULL, ")
                    .append("  searchSucceeded tinyint(1) default NULL, ")
                    .append("  numberOfFoundItems int(11) default NULL, ")
                    .append("  description varchar(250) default NULL, ").append("  actionTime datetime NOT NULL, ")
                    .append("  PRIMARY KEY  (id), ")
                    .append("  KEY action_reader (tenantId,userId,actionTypeId,itemTypeId), ")
                    .append("  KEY tenantId (tenantId,actionTime) ")
                    .append(") ENGINE=MyISAM COMMENT='Table containing archived actions'; ");
            getJdbcTemplate().update(sql.toString());

            return "actionarchive" + getNextIndex;
        } catch (Exception e) {
            return actualArchiveTableName;
        }
    }

    public Integer getNumberOfActionsToArchive(int tenantId, Date refDate) {

        Object[] args = null;
        int[] argTypes = null;

        StringBuilder sql = new StringBuilder().append(" SELECT").append("   Count(1) as c ").append(" FROM action ")
                .append(" WHERE tenantId = ? AND ").append(" actiontime < ? ");

        args = new Object[]{tenantId, refDate};
        argTypes = new int[]{Types.INTEGER, Types.DATE};

        try {
            return getJdbcTemplate().queryForInt(sql.toString(), args, argTypes);
        } catch (Exception e) {
            logger.debug(e);
            return -1;
        }
    }

    public Integer getArchiveSize(String tablename) {
        StringBuilder sql = new StringBuilder().append(" SELECT").append("   Count(1) as c ").append(" FROM ")
                .append(tablename);

        try {
            return getJdbcTemplate().queryForInt(sql.toString());
        } catch (Exception e) {
            logger.debug(e);
            return -1;
        }
    }

    public boolean isArchiveFull(String tablename, Integer actionsToAdd) {
        return getArchiveSize(tablename) > MAX_TABLE_RECORDS - actionsToAdd;
    }

    public void moveActions(String tablename, int tenantId, Date refDate) {

        Object[] args = null;
        int[] argTypes = null;

        StringBuilder sql = new StringBuilder().append(" INSERT INTO ").append(tablename).append(" SELECT * ")
                .append(" FROM action ").append(" WHERE tenantId = ? AND ").append(" actiontime < ? ");

        args = new Object[]{tenantId, refDate};
        argTypes = new int[]{Types.INTEGER, Types.DATE};
        try {
            getJdbcTemplate().update(sql.toString(), args, argTypes);

            sql = new StringBuilder().append(" DELETE FROM action ").append(" WHERE tenantId = ? AND ")
                    .append(" actiontime < ? ");

            getJdbcTemplate().update(sql.toString(), args, argTypes);

        } catch (Exception e) {
            logger.error(e);
        }
    }
}