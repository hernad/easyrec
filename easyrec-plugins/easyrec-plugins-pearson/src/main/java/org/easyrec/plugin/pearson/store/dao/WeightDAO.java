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

package org.easyrec.plugin.pearson.store.dao;

import org.easyrec.plugin.pearson.model.Weight;
import org.easyrec.utils.spring.store.dao.TableCreatingDAO;

import java.util.List;

public interface WeightDAO extends TableCreatingDAO {
    public static final String DEFAULT_ID_COLUMN_NAME = "id";
    public static final String DEFAULT_TABLE_NAME = "p_weight";
    public static final String DEFAULT_TENANT_COLUMN_NAME = "tenantId";
    public static final String DEFAULT_USER1_COLUMN_NAME = "user1Id";
    public static final String DEFAULT_USER2_COLUMN_NAME = "user2Id";
    public static final String DEFAULT_WEIGHT_COLUMN_NAME = "weight";

    public List<Weight> getWeightsForUser1(Integer tenantId, Integer user1Id);

    public List<Weight> getWeightsForUser1AndItem(Integer tenantId, Integer user1Id, Integer itemId,
                                                  Integer itemTypeId);

    public void insertOrUpdateWeightSymmetric(Weight weight);
}
