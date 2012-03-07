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

package org.easyrec.service.core;

import edu.uci.ics.jung.graph.DelegateTree;
import org.easyrec.exception.core.ClusterException;
import org.easyrec.model.core.ClusterVO;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.TenantVO;

import java.util.List;

/**
 * @author szavrel
 */
public interface ClusterService {

    public static final String ITEMTYPE_CLUSTER = "CLUSTER";
    public static final String ROOT = "CLUSTERS";
    public static final String ASSOCTYPE_CLUSTER = "IS_PARENT_OF";
    public static final String ASSOCTYPE_BELONGSTO = "BELONGS_TO";
    public static final String DEFAULT_STRATEGY = "NEWEST";

    public void initTenantForClusters(TenantVO tenantVO);

    public ClusterVO loadCluster(Integer tenantId, String clusterName);

    public void updateClusterDescription(Integer tenantId, String clusterName, String newDescription)
            throws ClusterException;

    public void renameCluster(Integer tenantId, String clusterName, String newName) throws ClusterException;

    public void removeCluster(Integer tenantId, String name);

    public void addCluster(Integer tenantId, String clusterName, String clusterDescription, String parent)
            throws ClusterException;

    public void moveCluster(Integer tenantId, String clusterName, String newParent);

    public DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> getClustersForTenant(
            Integer tenantId);

    public ClusterVO getParent(Integer tenantId, String clusterName);

    public List<ClusterVO> getSiblings(Integer tenantId, String clusterName);

    public List<ItemVO<Integer, Integer>> getItemsOfCluster(ClusterVO cluster);

    public List<ItemVO<Integer, Integer>> getItemsOfCluster(ClusterVO cluster, Integer itemType);

    public List<ItemVO<Integer, Integer>> getItemsOfCluster(Integer tenantId, String clusterName);

    public List<ItemVO<Integer, Integer>> getItemsOfCluster(Integer tenantId, String clusterName,
                                                                     Integer itemType);

    public List<ItemVO<Integer, Integer>> getItemsOfCluster(ClusterVO cluster, String strategy,
                                                                     Boolean useFallback, int numberOfResults);

    public List<ItemVO<Integer, Integer>> getItemsOfCluster(ClusterVO cluster, String strategy,
                                                                     Boolean useFallback, int numberOfResults,
                                                                     Integer itemType);

    public int addItemToCluster(ClusterVO cluster, ItemVO<Integer, Integer> item) throws ClusterException;

    public int addItemToCluster(Integer tenantId, String clusterName, Integer itemId, Integer itemTypeId)
            throws ClusterException;

    public int removeItemFromCluster(ClusterVO cluster, ItemVO<Integer, Integer> item) throws ClusterException;

    public int removeItemFromCluster(Integer tenantId, String clusterName, Integer itemId, Integer itemTypeId)
            throws ClusterException;
    
    public List<ClusterVO> getClustersForItem(ItemVO<Integer, Integer> item);
}
