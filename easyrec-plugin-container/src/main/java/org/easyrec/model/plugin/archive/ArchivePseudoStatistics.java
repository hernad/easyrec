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

import org.easyrec.plugin.stats.GeneratorStatistics;

import java.util.Date;

/**
 * @author patrick
 */
public class ArchivePseudoStatistics extends GeneratorStatistics {
    private Date referenceDate;
    private int numberOfArchivedActions;

    public ArchivePseudoStatistics() {
        this(new Date(), 0);
    }

    public ArchivePseudoStatistics(Date referenceDate, int numberOfArchivedActions) {
        this.referenceDate = referenceDate;
        this.numberOfArchivedActions = numberOfArchivedActions;
    }

    public Date getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(Date referenceDate) {
        this.referenceDate = referenceDate;
    }

    public int getNumberOfArchivedActions() {
        return numberOfArchivedActions;
    }

    public void setNumberOfArchivedActions(int numberOfArchivedActions) {
        this.numberOfArchivedActions = numberOfArchivedActions;
    }
}
