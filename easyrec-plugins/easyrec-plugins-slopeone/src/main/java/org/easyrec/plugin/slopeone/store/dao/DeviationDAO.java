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
package org.easyrec.plugin.slopeone.store.dao;

import gnu.trove.set.TIntSet;
import org.easyrec.plugin.slopeone.model.Deviation;
import org.easyrec.plugin.slopeone.model.TenantItem;
import org.easyrec.utils.spring.store.dao.TableCreatingDroppingDAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;


/**
 * DAO for storing Slope One deviations. <p/> <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p/>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p/> <p><b>last modified:</b><br/> $Author: pmarschik $<br/> $Date: 2011-06-14 15:02:43 +0200 (Di, 14 Jun 2011) $<br/> $Revision: 18438 $</p>
 *
 * @author Patrick Marschik
 */
public interface DeviationDAO extends TableCreatingDroppingDAO {
    static final String TABLE_NAME = "so_deviation";
    static final String COLUMN_ID = "id";
    static final String COLUMN_TENANTID = "tenantId";
    static final String COLUMN_ITEM1ID = "item1Id";
    static final String COLUMN_ITEM2ID = "item2Id";
    static final String COLUMN_ITEMTYPEID = "itemTypeId";
    static final String COLUMN_NUMERATOR = "numerator";
    static final String COLUMN_DENOMINATOR = "denominator";
    static final String COLUMN_DEVIATION = "deviation";

    /**
     * Indicates tart of a generator run, might create temp-tables or whatever.
     */
    void starting();

    /**
     * Signifies the end of a row of inserts.
     * <p/>
     * Should be called when a run is finished an no further insertions happen for a while. Can be (and is) used for
     * caches.
     */
    void endUpdate();

    /**
     * Signifies the end of the calculation for a tenant.
     * <p/>
     * Might free the cache from GC.
     *
     * @param tenantId   The tenant.
     * @param itemTypeId The item type.
     */
    void finished(int tenantId, final int itemTypeId);

    /**
     * Get a specific deviation.
     *
     * @param tenantId    Tenant.
     * @param item1Id     Item 1.
     * @param item1TypeId Type of item 1.
     * @param item2Id     Item 2.
     * @param item2TypeId Type of item 2.
     * @return The deviation.
     */
    @Nullable
    Deviation getDeviation(int tenantId, int item1Id, int item1TypeId, int item2Id, int item2TypeId);

    /**
     * Gets deviations in descending order.
     *
     * @param tenantId           Tenant.
     * @param itemTypeId         Item type.
     * @param itemId             Item (either item 1 or item 2).
     * @param minCountConstraint Minimum "support" of deviations, uses the denominator column to filter.
     * @param maxNumberOfDeviationsConstraint
     *                           Limits the number of results returned.
     * @return Ordered list of deviations.
     */
    List<Deviation> getDeviationsOrdered(int tenantId, int itemTypeId, int itemId, @Nullable Long minCountConstraint,
                                         @Nullable Integer maxNumberOfDeviationsConstraint);

    @Nonnull
    Set<TenantItem> getItemIds(int tenantId, TIntSet itemTypeIds);

    /**
     * Inserts or replaces a deviation.
     *
     * @param deviation Deviation.
     * @return {@code 1} if insert happened, {@code 0} if replace occured.
     */
    int insertDeviation(Deviation deviation);

    /**
     * Inserts or replaces a list of deviations.
     *
     * @param deviations Deviations.
     * @return Number of deviations inserted. {@code deviations.size() - returnedValue} is the number of items
     *         replaced.
     */
    int insertDeviations(List<Deviation> deviations);
}
