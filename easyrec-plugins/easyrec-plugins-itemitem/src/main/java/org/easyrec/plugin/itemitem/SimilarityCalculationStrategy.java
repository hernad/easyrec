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

/**
 *
 */
package org.easyrec.plugin.itemitem;

import org.easyrec.plugin.itemitem.store.dao.ActionDAO;
import org.easyrec.plugin.support.ExecutablePluginSupport;
import org.easyrec.service.core.ItemAssocService;

import java.util.Date;

/**
 * Strategy for computing similarities.
 *
 * @author Patrick Marschik
 */
public interface SimilarityCalculationStrategy {
    // -------------------------- OTHER METHODS --------------------------

    /**
     * Calculate similarities between all items.
     *
     * @param tenantId     Tenant id.
     * @param actionTypeId Action type id.
     * @param itemTypeId   Item type id.
     * @param assocTypeId  Association type id.
     * @param sourceTypeId Source type id used for storing generated similarities.
     * @param viewTypeId   View type id used for storing generated similarities.
     * @param changeDate   Change date used for storing generated similarities.
     * @param control      Control to update progress.
     */
    int calculateSimilarity(Integer tenantId, Integer actionTypeId, Integer itemTypeId, Integer assocTypeId,
                            Integer sourceTypeId, Integer viewTypeId, Date changeDate,
                            final ExecutablePluginSupport.ExecutionControl control);

    /**
     * The source info to be used when storing generated similarities.
     *
     * @return The source info to be used when storing generated similarities.
     */
    String getSourceInfo();


    /**
     * The action DAO used for quering actions.
     *
     * @param actionDAO The action DAO used for quering actions.
     */
    void setActionDAO(ActionDAO actionDAO);

    /**
     * The item assocation service to use for storing generated similarities.
     *
     * @param itemAssocService The item assocation service to use for storing generated similarities.
     */
    void setItemAssocService(ItemAssocService itemAssocService);
}