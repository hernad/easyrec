/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easyrec.service.core.impl;

import com.google.common.collect.Lists;
import org.easyrec.model.core.AssociatedItemVO;
import org.easyrec.model.core.ClusterVO;
import org.easyrec.model.core.transfer.IAConstraintVO;
import org.easyrec.service.core.ClusterService;
import org.easyrec.service.core.ClusterStrategy;
import org.easyrec.service.core.ItemAssocService;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.utils.spring.cache.annotation.ShortCacheable;

import java.util.*;

/**
 * @author szavrel
 */
public class RandomClusterStrategyImpl implements ClusterStrategy {

    private ItemAssocService itemAssocService;
    private AssocTypeDAO assocTypeDAO;

    public List<AssociatedItemVO<Integer, Integer>> getItemsFromCluster(Integer tenantId,
                                                                                          ClusterVO cluster,
                                                                                          Integer numberOfResults,
                                                                                          Integer itemType) {



        if (numberOfResults <= 0) return Lists.newArrayList();

        List<AssociatedItemVO<Integer, Integer>> allItems =
                getAllItems(tenantId, cluster, numberOfResults * 2, itemType);
        int numberOfAvaliableItems = allItems.size();
        if (numberOfResults >= numberOfAvaliableItems) {
            List<AssociatedItemVO<Integer, Integer>> retList =  new ArrayList<AssociatedItemVO<Integer, Integer>>(numberOfAvaliableItems);
            retList.addAll(allItems);
            return retList;
        }
        //if we want more than half of all of the items, generate random index list for OMITTING items, else for SELECTING items
        Set<Integer> indices = new HashSet<Integer>(numberOfResults);
        boolean addItemsByIndices = numberOfResults < numberOfAvaliableItems / 2;
        int numberOfIndices = addItemsByIndices ? numberOfResults: numberOfAvaliableItems - numberOfResults;

        Random rnd = new Random(System.currentTimeMillis());
        while(indices.size() < numberOfIndices) {
            indices.add(rnd.nextInt(numberOfAvaliableItems));
        }
        //create the return list
        List<AssociatedItemVO<Integer, Integer>> retList =  new ArrayList<AssociatedItemVO<Integer, Integer>>(numberOfResults);
        //differentiate: are we selecting or omitting?
        if (addItemsByIndices){
            //we are selecting: walk over indices and copy individual item assocs to result
            for (Integer index: indices) {
                retList.add(allItems.get(index));
            }
        } else {
            //we are omitting: walk over available items and copy assocs to result unless the index has been selected
            for (int i = 0; i < allItems.size(); i++) {
                if (indices.contains(i)) continue;
                retList.add(allItems.get(i));
            }
        }
        return retList;
    }

    @ShortCacheable
    private List<AssociatedItemVO<Integer, Integer>> getAllItems(Integer tenantId, ClusterVO cluster,
                                                                                   Integer numberOfResults,
                                                                                   Integer itemType) {
        IAConstraintVO<Integer, Integer> constraint = new IAConstraintVO<Integer, Integer>(
                numberOfResults, null, null, null, tenantId, true, null);
        //TODO: modify SQL statement: SELECT RANDOM .... LIMIT 100;
        return itemAssocService.getItemsFrom(itemType,
                assocTypeDAO.getIdOfType(tenantId, ClusterService.ASSOCTYPE_BELONGSTO), cluster.getItem(), constraint);
    }

    public ItemAssocService getItemAssocService() {
        return itemAssocService;
    }

    public void setItemAssocService(ItemAssocService itemAssocService) {
        this.itemAssocService = itemAssocService;
    }

    public AssocTypeDAO getAssocTypeDAO() {
        return assocTypeDAO;
    }

    public void setAssocTypeDAO(AssocTypeDAO assocTypeDAO) {
        this.assocTypeDAO = assocTypeDAO;
    }


}
