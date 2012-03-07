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

package org.easyrec.model.plugin.archive;

import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.support.GeneratorPluginSupport;
import org.easyrec.store.dao.core.ArchiveDAO;

import java.util.Date;

/**
 * @author pmarschik
 */
public class ArchivePseudoGenerator
        extends GeneratorPluginSupport<ArchivePseudoConfiguration, ArchivePseudoStatistics> {

    public static final String DISPLAY_NAME = "Archiving";
    public static final PluginId ID = new PluginId("http://www.easyrec.org/internal/Archive", "0.96");

    private ArchiveDAO archiveDAO;

    public void setArchiveDAO(ArchiveDAO archiveDAO) {
        this.archiveDAO = archiveDAO;
    }

    public ArchivePseudoGenerator() {
        super(DISPLAY_NAME, ID.getUri(), ID.getVersion(), ArchivePseudoConfiguration.class,
                ArchivePseudoStatistics.class);
    }

    private void init() {
        install(false);
        initialize();
    }

    @Override
    protected void doExecute(ExecutionControl executionControl, ArchivePseudoStatistics stats) throws Exception {
        String actualArchiveTableName = archiveDAO.getActualArchiveTableName();

        // convert days to millis
        Date refDate = new Date(System.currentTimeMillis() - (getConfiguration().getDays() * 86400000l));

        logger.info("Cutoff date: " + refDate);

        Integer numberOfActionsToArchive =
                archiveDAO.getNumberOfActionsToArchive(getConfiguration().getTenantId(), refDate);

        logger.info("Number of actions to archive:" + numberOfActionsToArchive);

        if (numberOfActionsToArchive > 0) {
            if (archiveDAO.isArchiveFull(actualArchiveTableName, numberOfActionsToArchive)) {
                //generate new archive
                actualArchiveTableName = archiveDAO.generateNewArchive(actualArchiveTableName);
            }
            // move actions to archive
            archiveDAO.moveActions(actualArchiveTableName, getConfiguration().getTenantId(), refDate);
        }

        stats.setReferenceDate(refDate);
        stats.setNumberOfArchivedActions(numberOfActionsToArchive);
    }

    public String getPluginDescription() {
        return "easyrec internal";
    }
}
