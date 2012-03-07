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
package org.easyrec.plugin.slopeone;

import org.easyrec.plugin.slopeone.model.LogEntry;
import org.easyrec.plugin.slopeone.model.SlopeOneIntegerConfiguration;
import org.easyrec.plugin.slopeone.model.SlopeOneStats;
import org.easyrec.plugin.slopeone.model.TenantItem;
import org.easyrec.plugin.support.ExecutablePluginSupport;

import java.util.Date;
import java.util.Set;


/**
 * Defines methods for the steps required to perform Slope One [1]. <p/> [1] Lemire and Maclachlan 2005. Slope One
 * Predictors for Online Rating-Base Collaborative Filtering. In SIAM Data Mining (SDM'05), Newport Beach, California,
 * April 21-23, 2005. <p/> <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p/> <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p> <p/> <p><b>last modified:</b><br/> $Author: pmarschik $<br/> $Date: 2011-06-14 15:02:31 +0200 (Di, 14 Jun 2011) $<br/> $Revision: 18436 $</p>
 *
 * @author Patrick Marschik
 */
public interface SlopeOneService {
    // -------------------------- OTHER METHODS --------------------------

    /**
     * Calculate all new deviations since {@code lastRun}. All newly created deviation's items are optionally stored in
     * {@code changedItemIds}.
     *
     * @param tenant         Tenant to calculate deviations for.
     * @param config         Configuration used.
     * @param lastRun        Time of the last run, used to filter actions that happend before.
     * @param stats          Statistics.
     * @param changedItemIds If a non-null set is supplied all changed items will be stored in the set.
     * @param control        Control to update progress.
     */
    void calculateDeviations(SlopeOneIntegerConfiguration config, Date lastRun, SlopeOneStats stats,
                             Set<TenantItem> changedItemIds, final ExecutablePluginSupport.ExecutionControl control);

    /**
     * Generate actions from original actions table (temporary until generator interface is finished)
     *
     * @param tenant  Tenant to generate actions for.
     * @param config  Current configuration.
     * @param lastRun Last run.
     * @param stats   Statistics.
     */
    void generateActions(SlopeOneIntegerConfiguration config, LogEntry lastRun, SlopeOneStats stats);

    /**
     * Generate non personalized recommendations.
     * <p/>
     * Based on sorting of the deviation table to generate item->item recommendations.
     *
     * @param tenant         Tenant to calculate deviations for.
     * @param config         Configuration used.
     * @param stats          Statistics.
     * @param execution      Time to assign to newly created {@link org.easyrec.model.core.ItemAssocVO}s.
     * @param changedItemIds If a non-null set is supplied all changed items will be stored in the set.
     * @param sourceType     Source type for storing recommendations.
     * @param control        Control for updating progress.
     */
    void nonPersonalizedRecommendations(SlopeOneIntegerConfiguration config, SlopeOneStats stats, Date execution,
                                        Set<TenantItem> changedItemIds,
                                        final ExecutablePluginSupport.ExecutionControl control);
}
