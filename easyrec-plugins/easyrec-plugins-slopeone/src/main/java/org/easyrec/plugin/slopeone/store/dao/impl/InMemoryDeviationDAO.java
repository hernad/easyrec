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

package org.easyrec.plugin.slopeone.store.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gnu.trove.set.TIntSet;
import org.easyrec.plugin.slopeone.model.Deviation;
import org.easyrec.plugin.slopeone.model.TenantItem;
import org.easyrec.plugin.slopeone.store.dao.DeviationDAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Stores all deviations in memory. <p/> Used for testing purposes! <p><b>Company:&nbsp;</b> SAT, Research Studios
 * Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/>
 * $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class InMemoryDeviationDAO implements DeviationDAO {
    private List<Deviation> deviations = Lists.newArrayList();

    @SuppressWarnings("UnusedDeclaration")
    public List<Deviation> getDeviations() { return deviations; }

    public void starting() {}

    public void endUpdate() {}

    public void finished(int tenantId, final int itemTypeId) {}

    @Nullable
    public Deviation getDeviation(int tenantId, int item1Id, int item1TypeId, int item2Id, int item2TypeId) {
        for (Deviation deviation : deviations)
            if (deviation.getTenantId() == tenantId &&
                    deviation.getItem1TypeId() == item1TypeId && deviation.getItem1Id() == item1Id &&
                    deviation.getItem2Id() == item2Id && deviation.getItem2TypeId() == item2TypeId)
                return deviation;

        return null;
    }

    public List<Deviation> getDeviationsOrdered(int tenantId, int itemTypeId, int itemId,
                                                @Nullable final Long minCountConstraint,
                                                @Nullable final Integer maxNumberOfDeviationsConstraint) {
        List<Deviation> result = Lists.newArrayList(deviations);

        if (minCountConstraint != null) {
            ListIterator<Deviation> it = result.listIterator();

            while (it.hasNext()) {
                Deviation next = it.next();

                if (next.getDenominator() < minCountConstraint) it.remove();
            }
        }

        if (maxNumberOfDeviationsConstraint != null && result.size() > maxNumberOfDeviationsConstraint)
            result = result.subList(0, maxNumberOfDeviationsConstraint);

        Collections.sort(result, new Comparator<Deviation>() {
            public int compare(final Deviation o1, final Deviation o2) {
                return Double.compare(o2.getDeviation(), o1.getDeviation());
            }
        });

        return result;
    }

    @Nonnull
    public Set<TenantItem> getItemIds(final int tenantId, final TIntSet itemTypeIds) {
        Set<TenantItem> result = Sets.newHashSet();

        for (Deviation deviation : deviations) {
            if (!itemTypeIds.contains(deviation.getItem1TypeId()) && !itemTypeIds.contains(deviation.getItem2TypeId()))
                continue;

            result.add(new TenantItem(deviation.getItem1Id(), deviation.getItem1TypeId()));
            result.add(new TenantItem(deviation.getItem2Id(), deviation.getItem2TypeId()));
        }

        return result;
    }

    public int insertDeviation(Deviation deviation) {
        Deviation old = getDeviation(deviation.getTenantId(), deviation.getItem1Id(), deviation.getItem1TypeId(),
                deviation.getItem2Id(), deviation.getItem2TypeId());

        if (old != null) {
            old.setNumerator(deviation.getNumerator());
            old.setDenominator(deviation.getDenominator());

            return 0;
        }

        deviations.add(deviation);

        return 1;
    }

    public int insertDeviations(List<Deviation> deviationsToInsert) {
        int result = 0;

        for (Deviation deviation : deviationsToInsert) {
            result += insertDeviation(deviation);
        }

        return result;
    }

    public String getDefaultTableName() { return null; }

    public String getTableCreatingSQLScriptName() { return null; }

    public void createTable() {}

    public boolean existsTable() { return true; }

    public void dropTable() {}
}
