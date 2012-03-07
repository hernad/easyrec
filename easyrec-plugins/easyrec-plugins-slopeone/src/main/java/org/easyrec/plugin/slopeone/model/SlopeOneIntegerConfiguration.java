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

package org.easyrec.plugin.slopeone.model;

import com.google.common.base.Objects;
import gnu.trove.set.TIntSet;

import javax.annotation.Nullable;

/**
 * Configuration with all runtime information for Slope One.
 * Basically all Strings that can be resolved with the typeMappingService have been resolved in this configuration.
 * <p/>
 * <p><b>Company:</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:</b>
 * (c) 2011</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: $<br/>
 * $Date: $<br/>
 * $Revision: $</p>
 *
 * @author patrick
 */
public class SlopeOneIntegerConfiguration {
    @Nullable
    private Integer maxRecsPerItem;
    @Nullable
    private Long minRatedCount;
    private String nonPersonalizedSourceInfo;
    private int actionType;
    private int assocType;
    private TIntSet itemTypes;
    private int viewType;
    private int sourceType;
    private int tenant;

    public SlopeOneIntegerConfiguration(@Nullable Integer maxRecsPerItem, @Nullable Long minRatedCount,
                                        String nonPersonalizedSourceInfo, int actionType, TIntSet itemTypes,
                                        int viewType, int assocType, int sourceType, int tenant) {
        this.maxRecsPerItem = maxRecsPerItem;
        this.minRatedCount = minRatedCount;
        this.nonPersonalizedSourceInfo = nonPersonalizedSourceInfo;
        this.actionType = actionType;
        this.itemTypes = itemTypes;
        this.viewType = viewType;
        this.assocType = assocType;
        this.sourceType = sourceType;
        this.tenant = tenant;
    }

    @Nullable
    public Integer getMaxRecsPerItem() { return maxRecsPerItem; }

    @Nullable
    public Long getMinRatedCount() { return minRatedCount; }

    public String getNonPersonalizedSourceInfo() { return nonPersonalizedSourceInfo; }

    public int getActionType() { return actionType; }

    public TIntSet getItemTypes() { return itemTypes; }

    public int getViewType() { return viewType; }

    public int getAssocType() { return assocType; }

    public int getSourceType() { return sourceType; }

    public int getTenant() { return tenant; }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SlopeOneIntegerConfiguration that = (SlopeOneIntegerConfiguration) o;

        return Objects.equal(actionType, that.actionType) &&
                Objects.equal(itemTypes, that.itemTypes) &&
                Objects.equal(maxRecsPerItem, that.maxRecsPerItem) &&
                Objects.equal(minRatedCount, that.minRatedCount) &&
                Objects.equal(nonPersonalizedSourceInfo, that.nonPersonalizedSourceInfo) &&
                Objects.equal(viewType, that.viewType) &&
                Objects.equal(assocType, that.assocType) &&
                Objects.equal(sourceType, that.sourceType) &&
                Objects.equal(tenant, that.tenant);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(actionType, itemTypes, maxRecsPerItem, minRatedCount, nonPersonalizedSourceInfo,
                viewType, assocType, sourceType, tenant);
    }
}
