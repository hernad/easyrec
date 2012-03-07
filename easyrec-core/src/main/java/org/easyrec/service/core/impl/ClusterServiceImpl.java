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

package org.easyrec.service.core.impl;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import edu.uci.ics.jung.graph.DelegateTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.exception.core.ClusterException;
import org.easyrec.model.core.*;
import org.easyrec.model.core.transfer.IAConstraintVO;
import org.easyrec.service.core.ClusterService;
import org.easyrec.service.core.ClusterStrategy;
import org.easyrec.service.core.ItemAssocService;
import org.easyrec.service.core.TenantService;
import org.easyrec.store.dao.core.ProfileDAO;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.store.dao.core.types.ItemTypeDAO;
import org.easyrec.store.dao.core.types.SourceTypeDAO;
import org.easyrec.store.dao.core.types.ViewTypeDAO;
import org.easyrec.utils.spring.store.dao.IDMappingDAO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DuplicateKeyException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * @author szavrel
 */
public class ClusterServiceImpl implements ClusterService, InitializingBean {


    private final Log logger = LogFactory.getLog(this.getClass());

    private ProfileDAO profileDAO;
    private ItemAssocService itemAssocService;
    private TenantService tenantService;
    private IDMappingDAO idMappingDAO;
    private ItemTypeDAO itemTypeDAO;
    private AssocTypeDAO assocTypeDAO;
    private SourceTypeDAO sourceTypeDAO;
    private ViewTypeDAO viewTypeDAO;
    private Map<Integer, DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>>>
            clusters;
    private Map<String, ClusterStrategy> strategies;
    private JAXBContext context;


    public ClusterServiceImpl(ProfileDAO profileDAO,
                              ItemAssocService itemAssocService,
                              IDMappingDAO idMappingDAO,
                              TenantService tenantService,
                              ItemTypeDAO itemTypeDAO,
                              AssocTypeDAO assocTypeDAO,
                              SourceTypeDAO sourceTypeDAO,
                              ViewTypeDAO viewTypeDAO) {
        this.profileDAO = profileDAO;
        this.itemAssocService = itemAssocService;
        this.tenantService = tenantService;
        this.idMappingDAO = idMappingDAO;
        this.itemTypeDAO = itemTypeDAO;
        this.assocTypeDAO = assocTypeDAO;
        this.sourceTypeDAO = sourceTypeDAO;
        this.viewTypeDAO = viewTypeDAO;
        this.clusters =
                new HashMap<Integer, DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>>>();
        try {
            context = JAXBContext.newInstance(org.easyrec.model.core.ClusterVO.class);
        } catch (Exception e) {
            logger.error("Could not instantiate JAXB Context for ClusterService!", e);
        }
    }

    public void afterPropertiesSet() {

        // for each tenant
        //build tree from itemassoc
        List<TenantVO> tenants = tenantService.getAllTenants();
        for (TenantVO tenantVO : tenants) {
            initTenantForClusters(tenantVO);

            DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> tenantClusters =
                    clusters.get(tenantVO.getId());
            buildTree(tenantClusters, tenantClusters.getRoot());
            //clusters.put(tenantVO.getId(), tenantClusters);
        }
    }

    public void initTenantForClusters(TenantVO tenantVO) {
        // check if itemType CLUSTER exists, if not create
        int itemTypeId;
        try {
            itemTypeId = itemTypeDAO.getIdOfType(tenantVO.getId(), ClusterService.ITEMTYPE_CLUSTER);
            // if found, make sure it is invisible
            itemTypeDAO.insertOrUpdate(tenantVO.getId(), ClusterService.ITEMTYPE_CLUSTER, false);
        } catch (IllegalArgumentException iae) {
            itemTypeId =
                    tenantService.insertItemTypeForTenant(tenantVO.getId(), ClusterService.ITEMTYPE_CLUSTER, false);
        }

        // check if assocType "IS_PARENT_OF" exists, if not create
        try {
            assocTypeDAO.getIdOfType(tenantVO.getId(), ClusterService.ASSOCTYPE_CLUSTER);
            // if found, make sure it is invisible
            assocTypeDAO.insertOrUpdate(tenantVO.getId(), ClusterService.ASSOCTYPE_CLUSTER, false);
        } catch (IllegalArgumentException iae2) {
            tenantService.insertAssocTypeForTenant(tenantVO.getId(), ClusterService.ASSOCTYPE_CLUSTER, false);
        }

        // check if assocType "BELONGS_TO" exists, if not create
        try {
            assocTypeDAO.getIdOfType(tenantVO.getId(), ClusterService.ASSOCTYPE_BELONGSTO);
            // if found, make sure it is invisible
            assocTypeDAO.insertOrUpdate(tenantVO.getId(), ClusterService.ASSOCTYPE_BELONGSTO, false);
        } catch (IllegalArgumentException iae2) {
            tenantService.insertAssocTypeForTenant(tenantVO.getId(), ClusterService.ASSOCTYPE_BELONGSTO, false);
        }

        // check if root exists
        ClusterVO root = loadCluster(tenantVO.getId(), idMappingDAO.lookup(ClusterService.ROOT), itemTypeId);
        //if root does not exist, create it: every tenant needs a ROOT
        if (root == null) {
            logger.info("No ROOT cluster found for tenant " + tenantVO.getStringId() + "! Creating new ROOT.");
            root = new ClusterVO(tenantVO.getId(),
                    idMappingDAO.lookup(ClusterService.ROOT),
                    itemTypeId,
                    ClusterService.ROOT,
                    "The root object of every cluster hierarchy. Cannot be removed!");
            storeCluster(root);
        }

        DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> tenantClusters =
                new DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>>();
        tenantClusters.setRoot(root);
        clusters.put(tenantVO.getId(), tenantClusters);
    }

    public DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> getClustersForTenant(
            Integer tenantId) {
        return clusters.get(tenantId);
    }

    public void addCluster(Integer tenantId, String clusterName, String clusterDescription, String parent)
            throws ClusterException {

        ClusterVO parentCluster = loadCluster(tenantId, idMappingDAO.lookup(parent),
                itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER));
        if (parentCluster == null) throw new ClusterException("Cannot add cluster at this position! No parent cluster selected!");
        logger.info(parentCluster.getName());
        ClusterVO childCluster = new ClusterVO(tenantId,
                idMappingDAO.lookup(clusterName),
                itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER),
                clusterName,
                clusterDescription);
        addCluster(childCluster, parentCluster);
    }


    public void addCluster(ClusterVO cluster, ClusterVO parent) throws ClusterException {

        DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> tree =
                clusters.get(parent.getItem().getTenant());
        if (tree.containsVertex(cluster)) throw new ClusterException("Cannot add cluster! Cluster already exists!");
        if (tree.containsVertex(parent)) {
            storeCluster(cluster);
            ItemAssocVO<Integer,Integer> edge =
                    new ItemAssocVO<Integer,Integer>(
                            parent.getItem().getTenant(), // tenantId
                            parent.getItem(), //itemFrom
                            assocTypeDAO.getIdOfType(parent.getItem().getTenant(), ClusterService.ASSOCTYPE_CLUSTER),
                            //assocType
                            1.0, //assocValue
                            cluster.getItem(), //itemTo
                            sourceTypeDAO.getIdOfType(parent.getItem().getTenant(),
                                    SourceTypeDAO.SOURCETYPE_MANUALLY_CREATED), //sourceType
                            "", //comment
                            viewTypeDAO.getIdOfType(parent.getItem().getTenant(), ViewTypeDAO.VIEWTYPE_ADMIN),
                            true); //timeStamp
            itemAssocService.insertOrUpdateItemAssoc(edge);
            tree.addChild(edge, parent, cluster);
        } else {
            throw new ClusterException("Parent cluster could not be found!");
        }

        //check if profile with name already exists
        // if yes reject : cluster already exists
        // else check if parent exists, if not reject.
        // find parent;
        // insert child;
        // store itemassoc
    }

    public void removeCluster(Integer tenantId, String name) {

        //remove itemassoc (tree); if not leaf, remove whole subtree
        DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> tree =
                clusters.get(tenantId);
        ClusterVO cluster = loadCluster(tenantId, idMappingDAO.lookup(name),
                itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER));
        // disconnect subtree
        ItemAssocVO<Integer,Integer> parentEdge = tree.getParentEdge(cluster);
        itemAssocService.removeItemAssoc(parentEdge.getId());
        //now remove rest of subtree including all itemassocs (item->cluster)
        removeClustersFromDB(tree, cluster);
        tree.removeChild(cluster);
    }

    public void moveCluster(Integer tenantId, String clusterName, String newParent) {

        DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> tree =
                clusters.get(tenantId);
        ClusterVO cluster = loadCluster(tenantId, idMappingDAO.lookup(clusterName),
                itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER));
        // disconnect subtree
        ItemAssocVO<Integer,Integer> parentEdge = tree.getParentEdge(cluster);
        itemAssocService.removeItemAssoc(parentEdge.getId());
        tree.removeChild(cluster);

        // add cluster as child to new parent
        ClusterVO parent = loadCluster(tenantId, idMappingDAO.lookup(newParent),
                itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER));

        ItemAssocVO<Integer,Integer> edge =
                new ItemAssocVO<Integer,Integer>(
                        parent.getItem().getTenant(), // tenantId
                        parent.getItem(), //itemFrom
                        assocTypeDAO.getIdOfType(parent.getItem().getTenant(), ClusterService.ASSOCTYPE_CLUSTER),
                        //assocType
                        1.0, //assocValue
                        cluster.getItem(), //itemTo
                        sourceTypeDAO.getIdOfType(parent.getItem().getTenant(),
                                SourceTypeDAO.SOURCETYPE_MANUALLY_CREATED), //sourceType
                        "", //comment
                        viewTypeDAO.getIdOfType(parent.getItem().getTenant(), ViewTypeDAO.VIEWTYPE_ADMIN),
                        true); //timeStamp
        itemAssocService.insertOrUpdateItemAssoc(edge);
        tree.addChild(edge, parent, cluster);
        buildTree(tree, cluster);
    }

    public void renameCluster(Integer tenantId, String clusterName, String newName) throws ClusterException {
        ClusterVO cluster = loadCluster(tenantId, idMappingDAO.lookup(clusterName),
                itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER));
        DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> tree =
                clusters.get(tenantId);
        if (tree.isRoot(cluster)) throw new ClusterException("ROOT cluster cannot be renamed!");
        ClusterVO parent = tree.getParent(cluster);
        ClusterVO newCluster = new ClusterVO(tenantId, idMappingDAO.lookup(newName),
                itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER), newName, cluster.getDescription());
        // add the new cluster
        addCluster(newCluster, parent);
        // move all children to the new cluster
        for (ClusterVO child : tree.getChildren(cluster)) {
            moveCluster(tenantId, child.getName(), newName);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("removed all children: " + tree.isLeaf(cluster));
        }

        // move all items to the new cluster
        for (ItemVO<Integer, Integer> item : getItemsOfCluster(cluster)) {
            addItemToCluster(newCluster, item);
        }

        // delete the old cluster
        removeCluster(tenantId, clusterName);

    }


    public void updateClusterDescription(Integer tenantId, String clusterName, String newDescription)
            throws ClusterException {

        ClusterVO cluster = loadCluster(tenantId, idMappingDAO.lookup(clusterName),
                itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER));
        DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> tree =
                clusters.get(tenantId);
        if (tree.isRoot(cluster)) throw new ClusterException("ROOT cluster description cannot be changed!");

        cluster.setDescription(newDescription);
        storeCluster(cluster);

    }

    public ClusterVO getParent(Integer tenantId, String clusterName) {

        // to avoid a db access, we create a new cluster instead of using load. Only tenant, id, and type are necessary to identify a cluster
        ClusterVO cluster = new ClusterVO(tenantId, idMappingDAO.lookup(clusterName),
                itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER), "", "");
        return getParent(tenantId, cluster);
    }

    public ClusterVO getParent(Integer tenantId, ClusterVO cluster) {
        DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> tree =
                clusters.get(tenantId);
        // root has no parent
        if (tree.isRoot(cluster)) return null;
        return tree.getParent(cluster);
    }

    public List<ClusterVO> getSiblings(Integer tenantId, String clusterName) {

        // to avoid a db access, we create a new cluster instead of using load. Only tenant, id, and type are necessary to identify a cluster
        ClusterVO cluster = new ClusterVO(tenantId, idMappingDAO.lookup(clusterName),
                itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER), "", "");
        return getSiblings(tenantId, cluster);
    }

    public List<ClusterVO> getSiblings(Integer tenantId, ClusterVO cluster) {
        DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> tree =
                clusters.get(tenantId);
        if (tree.isRoot(cluster)) return null; //root has no siblings
        ClusterVO parent = tree.getParent(cluster);
        Collection<ClusterVO> sibl = tree.getChildren(parent);
        List<ClusterVO> siblings = new ArrayList<ClusterVO>();
        for (ClusterVO clusterVO : sibl) {
            if (!clusterVO.equals(cluster)) siblings.add(clusterVO);
        }
        return siblings;
    }

    public List<ItemVO<Integer, Integer>> getItemsOfCluster(ClusterVO cluster) {
        return getItemsOfCluster(cluster, null);
    }

    public List<ItemVO<Integer, Integer>> getItemsOfCluster(ClusterVO cluster, Integer itemType) {
        Preconditions.checkNotNull(cluster);
        Preconditions.checkNotNull(cluster.getItem());
        Preconditions.checkNotNull(cluster.getItem().getTenant());
        Preconditions.checkNotNull(cluster.getName());

        Integer tenantId = cluster.getItem().getTenant();
        Integer assocTypeBelongsTo = assocTypeDAO.getIdOfType(tenantId, ClusterService.ASSOCTYPE_BELONGSTO);

        IAConstraintVO<Integer, Integer> constraint = new IAConstraintVO<Integer, Integer>(
                null, null, null, null, tenantId, true, null);

        List<AssociatedItemVO<Integer, Integer>> itemAssocs =
                itemAssocService.getItemsFrom(itemType, assocTypeBelongsTo, cluster.getItem(), constraint);

        return Lists.transform(itemAssocs,
                new Function<AssociatedItemVO<Integer, Integer>,
                        ItemVO<Integer, Integer>>() {
                    public ItemVO<Integer, Integer> apply(
                            AssociatedItemVO<Integer, Integer> input) {
                        return input.getItem();
                    }
                });
    }

    public List<ItemVO<Integer, Integer>> getItemsOfCluster(ClusterVO cluster, String strategy,
                                                                     Boolean useFallback, int numberOfResults) {
        return getItemsOfCluster(cluster, strategy, useFallback, numberOfResults, null);
    }

    public List<ItemVO<Integer, Integer>> getItemsOfCluster(ClusterVO cluster, String strategy,
                                                                     Boolean useFallback, int numberOfResults,
                                                                     Integer itemType) {
        Preconditions.checkNotNull(cluster);
        Preconditions.checkNotNull(cluster.getItem());
        Preconditions.checkNotNull(cluster.getItem().getTenant());
        Preconditions.checkNotNull(cluster.getName());

        Integer tenantId = cluster.getItem().getTenant();

        ClusterStrategy clusterStrategy = strategy == null ? null : strategies.get(strategy);

        if (clusterStrategy == null) clusterStrategy = strategies.get(ClusterService.DEFAULT_STRATEGY);

        List<AssociatedItemVO<Integer, Integer>> itemAssocs;

        if (!useFallback) {
            itemAssocs = clusterStrategy.getItemsFromCluster(tenantId, cluster, numberOfResults, itemType);
        } else {
            itemAssocs = fallBack(tenantId, cluster, clusterStrategy, numberOfResults, itemType);
        }

        return Lists.transform(itemAssocs,
                new Function<AssociatedItemVO<Integer, Integer>, ItemVO<Integer, Integer>>() {
                    public ItemVO<Integer, Integer> apply(
                            AssociatedItemVO<Integer, Integer> input) {
                        return input.getItem();
                    }
                });
    }

    private List<AssociatedItemVO<Integer, Integer>> fallBack(Integer tenantId, ClusterVO cluster,
                                                                                ClusterStrategy strategy,
                                                                                int numberOfResults, Integer itemType) {
        int curNumberOfResults = 0;
        //use a set of ItemVO to remember which items have been added
        Set<ItemVO> itemsAlreadySeen = new HashSet<ItemVO>();

        List<AssociatedItemVO<Integer, Integer>> retList = new LinkedList<AssociatedItemVO<Integer, Integer>>();
        // get items for the cluster
        List<AssociatedItemVO<Integer, Integer>> clusterList =
                strategy.getItemsFromCluster(tenantId, cluster, numberOfResults, itemType);

        curNumberOfResults = addIfNewAndCountAdditions(itemsAlreadySeen, retList, clusterList, numberOfResults);

        // if not enough, get items from siblings
        if (curNumberOfResults < numberOfResults) {
            List<ClusterVO> siblings = getSiblings(tenantId, cluster);

            if (siblings != null)
                for (ClusterVO sibling : siblings) {
                    List<AssociatedItemVO<Integer, Integer>> sibList =
                            strategy.getItemsFromCluster(tenantId, sibling, numberOfResults - curNumberOfResults, itemType);

                    if (sibList.size() > 0) {
                        //add only unsee item assocs
                        curNumberOfResults += addIfNewAndCountAdditions(itemsAlreadySeen, retList, sibList, numberOfResults - curNumberOfResults);
                    }

                    logger.info("Found " + curNumberOfResults + " items for cluster " + sibling.getName());

                    if (curNumberOfResults >= numberOfResults) return retList;
                }
            //if still not enough, do the same for parent node.
            ClusterVO parent = clusters.get(tenantId).getParent(cluster);
            if (parent != null) {
                List<AssociatedItemVO<Integer, Integer>> parentList =
                        fallBack(tenantId, clusters.get(tenantId).getParent(cluster), strategy, numberOfResults - curNumberOfResults,
                                itemType);
                //add only unsee item assocs
                curNumberOfResults += addIfNewAndCountAdditions(itemsAlreadySeen, retList, parentList, numberOfResults - curNumberOfResults);
            }
        }
        return new ArrayList<AssociatedItemVO<Integer, Integer>>(retList);
    }

    private int addIfNewAndCountAdditions(
            Set<ItemVO> itemsAlreadySeen,
            List<AssociatedItemVO<Integer, Integer>> retList,
            List<AssociatedItemVO<Integer, Integer>> clusterList,
            int maxNum) {
        int addCount = 0;
        for (AssociatedItemVO<Integer, Integer> assocItem: clusterList){
            if (addCount >= maxNum) break;
            if (itemsAlreadySeen.contains(assocItem.getItem())) continue;
            itemsAlreadySeen.add(assocItem.getItem());
            retList.add(assocItem);
            addCount++;
        }
        return addCount;
    }

    public List<ItemVO<Integer, Integer>> getItemsOfCluster(Integer tenantId, String clusterName) {
        return getItemsOfCluster(tenantId, clusterName, null);
    }

    public List<ItemVO<Integer, Integer>> getItemsOfCluster(Integer tenantId, String clusterName,
                                                                     Integer itemType) {
        Preconditions.checkNotNull(tenantId);
        Preconditions.checkNotNull(clusterName);

        Integer itemTypeCluster = itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER);
        Integer clusterId = idMappingDAO.lookup(clusterName);

        ClusterVO cluster = new ClusterVO(tenantId, clusterId, itemTypeCluster, clusterName, null);

        return getItemsOfCluster(cluster, itemType);
    }

    public int addItemToCluster(ClusterVO cluster, ItemVO<Integer, Integer> item) throws ClusterException {
        Preconditions.checkNotNull(cluster);
        Preconditions.checkNotNull(cluster.getItem());
        Preconditions.checkNotNull(cluster.getItem().getTenant());
        Preconditions.checkNotNull(cluster.getName());
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(item.getItem());
        Preconditions.checkNotNull(item.getType());
        Preconditions.checkArgument(cluster.getItem().getTenant().equals(item.getTenant()),
                "cluster's tenantId (%s) must be the same as item's tenant id (%s)",
                cluster.getItem().getTenant(), item.getTenant());

        Integer tenantId = cluster.getItem().getTenant();
        Integer assocTypeBelongsTo = assocTypeDAO.getIdOfType(tenantId, ClusterService.ASSOCTYPE_BELONGSTO);
        Integer sourceTypeManuallyCreated = sourceTypeDAO.getIdOfType(tenantId,
                SourceTypeDAO.SOURCETYPE_MANUALLY_CREATED);
        Integer viewTypeAdmin = viewTypeDAO.getIdOfType(tenantId, ViewTypeDAO.VIEWTYPE_ADMIN);

        ItemAssocVO<Integer,Integer> itemAssoc =
                new ItemAssocVO<Integer,Integer>(tenantId,
                        item, assocTypeBelongsTo, 1.0, cluster.getItem(), sourceTypeManuallyCreated, null,
                        viewTypeAdmin,
                        true, new Date());

        try {
            return itemAssocService.insertOrUpdateItemAssoc(itemAssoc);
        } catch (DuplicateKeyException e) {
            throw new ClusterException(
                    "The item was not added to the cluster because the item already was in the cluster.");
        }
    }

    public int addItemToCluster(Integer tenantId, String clusterName, Integer itemId, Integer itemTypeId)
            throws ClusterException {
        Preconditions.checkNotNull(tenantId);
        Preconditions.checkNotNull(clusterName);
        Preconditions.checkNotNull(itemId);
        Preconditions.checkNotNull(itemTypeId);

        Integer itemTypeCluster = itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER);
        Integer clusterId = idMappingDAO.lookup(clusterName);

        ClusterVO cluster = new ClusterVO(tenantId, clusterId, itemTypeCluster, clusterName, null);
        ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(tenantId, itemId, itemTypeId);

        return addItemToCluster(cluster, item);
    }

    public int removeItemFromCluster(ClusterVO cluster, ItemVO<Integer, Integer> item)
            throws ClusterException {
        Preconditions.checkNotNull(cluster);
        Preconditions.checkNotNull(cluster.getItem());
        Preconditions.checkNotNull(cluster.getItem().getTenant());
        Preconditions.checkNotNull(cluster.getName());
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(item.getItem());
        Preconditions.checkNotNull(item.getType());
        Preconditions.checkArgument(cluster.getItem().getTenant().equals(item.getTenant()),
                "cluster's tenantId (%s) must be the same as item's tenant id (%s)",
                cluster.getItem().getTenant(), item.getTenant());

        Integer tenantId = cluster.getItem().getTenant();
        Integer assocTypeBelongsTo = assocTypeDAO.getIdOfType(tenantId, ClusterService.ASSOCTYPE_BELONGSTO);

        ItemAssocVO<Integer,Integer> itemAssoc =
                new ItemAssocVO<Integer,Integer>(tenantId, item,
                        assocTypeBelongsTo, null, cluster.getItem(), null, null, null, null, null);

        int rowsModified = itemAssocService.removeItemAssocQBE(itemAssoc);

        if (rowsModified == 0)
            throw new ClusterException(
                    "The item was not removed from the cluster, maybe the item was not in the cluster.");

        return rowsModified;
    }

    public int removeItemFromCluster(Integer tenantId, String clusterName, Integer itemId, Integer itemTypeId) throws
            ClusterException {
        Preconditions.checkNotNull(tenantId);
        Preconditions.checkNotNull(clusterName);
        Preconditions.checkNotNull(itemId);
        Preconditions.checkNotNull(itemTypeId);

        Integer itemTypeCluster = itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER);
        Integer clusterId = idMappingDAO.lookup(clusterName);

        ClusterVO cluster = new ClusterVO(tenantId, clusterId, itemTypeCluster, clusterName, null);
        ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(tenantId, itemId, itemTypeId);

        return removeItemFromCluster(cluster, item);
    }


    public ClusterVO loadCluster(Integer tenantId, String clusterName) {
        return loadCluster(tenantId, idMappingDAO.lookup(clusterName),
                itemTypeDAO.getIdOfType(tenantId, ClusterService.ITEMTYPE_CLUSTER));
    }

    public List<ClusterVO> getClustersForItem(ItemVO<Integer, Integer> item) {

        Integer assocTypeBelongsTo = assocTypeDAO.getIdOfType(item.getTenant(), ClusterService.ASSOCTYPE_BELONGSTO);

        IAConstraintVO<Integer, Integer> constraint = new IAConstraintVO<Integer, Integer>(
                null, null, null, null, item.getTenant(), true, null);

        List<AssociatedItemVO<Integer, Integer>> itemAssocs =
                itemAssocService.getItemsTo(item, assocTypeBelongsTo, itemTypeDAO.getIdOfType(item.getTenant(), ClusterService.ITEMTYPE_CLUSTER), constraint);

        return Lists.transform(itemAssocs,
                new Function<AssociatedItemVO<Integer, Integer>,ClusterVO>() {
                    public ClusterVO apply(
                            AssociatedItemVO<Integer, Integer> input) {
                        return loadCluster(input.getItem().getTenant(),input.getItem().getItem(),input.getItem().getType());
                    }
                });
    }


    /////////////// private methods ///////////////////////////////////////////

    private int storeCluster(ClusterVO cluster) {
        String xml;

        try {
            Marshaller m = context.createMarshaller();
            StringWriter sw = new StringWriter();
            m.marshal(cluster, sw);
            xml = sw.toString();
        } catch (Exception e) {
            System.out.println("An Error occurred marshaling the document" + e);
            return -1;
        }

        int i = profileDAO.storeProfile(cluster.getItem().getTenant(), cluster.getItem().getItem(),
                cluster.getItem().getType(),
                xml);

        profileDAO.activateProfile(cluster.getItem().getTenant(), cluster.getItem().getItem(), cluster.getItem().getType());

        return i;
    }

    private ClusterVO loadCluster(Integer tenantId, Integer itemId, Integer itemTypeId) {

        try {
            Unmarshaller um = context.createUnmarshaller();
//            logger.info("Trying to load Profile: tenant-" + tenantId + " item-" + itemId + " type-" + itemTypeId);
            // only return active profiles!
            String xml = profileDAO.getProfile(tenantId, itemId, itemTypeId, true);
            if (xml == null) {
//                logger.info("No profile String found!");
                return null;
            }
            ClusterVO ret = (ClusterVO) um.unmarshal(new StringReader(xml));
            ret.setItem(new ItemVO<Integer, Integer>(tenantId, itemId, itemTypeId));
            return ret;
        } catch (Exception e) {
            logger.error("An Error occurred marshaling the document" + e);
            return null;
        }
    }

    private void buildTree(
            DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> tree,
            ClusterVO vertex) {
        Integer assocTypeCluster =
                assocTypeDAO.getIdOfType(vertex.getItem().getTenant(), ClusterService.ASSOCTYPE_CLUSTER);

        List<ItemAssocVO<Integer,Integer>> edges = itemAssocService
                .getItemAssocs(vertex.getItem(), assocTypeCluster, null,
                        new IAConstraintVO<Integer, Integer>(null, vertex.getItem().getTenant()));

        for (ItemAssocVO<Integer,Integer> edge : edges) {
            ItemVO<Integer, Integer> child = edge.getItemTo();
            ClusterVO childVertex = loadCluster(child.getTenant(), child.getItem(), child.getType());
            if (childVertex != null) {
                tree.addChild(edge, vertex, childVertex);
                buildTree(tree, childVertex);
            }
        }
    }

    private void removeClustersFromDB(
            DelegateTree<ClusterVO, ItemAssocVO<Integer,Integer>> tree,
            ClusterVO vertex) {
        List<ItemAssocVO<Integer,Integer>> edges =
                itemAssocService.getItemAssocsForItem(vertex.getItem().getTenant(), vertex.getItem(), null);
        //remove all edges in db
        for (ItemAssocVO<Integer,Integer> edge : edges) {
            itemAssocService.removeItemAssoc(edge.getId());
        }
        profileDAO.deactivateProfile(vertex.getItem().getTenant(), vertex.getItem().getItem(),
                vertex.getItem().getType());
        // remove all items from cluster
        for (ItemVO<Integer, Integer> item : getItemsOfCluster(vertex)) {
            try {
                removeItemFromCluster(vertex, item);
            } catch (ClusterException ce) {
                logger.error(ce.getMessage());
            }
        }
        for (ClusterVO child : tree.getChildren(vertex)) {
            removeClustersFromDB(tree, child);
        }
    }

    ////////// getters & setters //////////////////////////////////////////////

    public Map<String, ClusterStrategy> getStrategies() {
        return strategies;
    }

    public void setStrategies(Map<String, ClusterStrategy> strategies) {
        this.strategies = strategies;
    }

}
