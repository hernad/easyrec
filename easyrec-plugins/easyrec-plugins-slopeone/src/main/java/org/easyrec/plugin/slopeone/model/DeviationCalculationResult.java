/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.slopeone.model;

import java.util.List;

/**
 * Stores the result of a deviation calculation for {@link org.easyrec.plugin.slopeone.DeviationCalculationStrategy}
 * <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last
 * modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class DeviationCalculationResult {
    private List<Deviation> deviations;
    private int created;
    private int modified;

    public DeviationCalculationResult(final List<Deviation> deviations, final int created, final int modified) {
        this.deviations = deviations;
        this.created = created;
        this.modified = modified;
    }

    public int getCreated() {
        return created;
    }

    public List<Deviation> getDeviations() {
        return deviations;
    }

    public int getModified() {
        return modified;
    }
}
