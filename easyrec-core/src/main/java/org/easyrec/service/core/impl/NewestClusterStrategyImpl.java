/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easyrec.service.core.impl;

import org.easyrec.model.core.AssociatedItemVO;
import org.easyrec.model.core.ClusterVO;
import org.easyrec.model.core.transfer.IAConstraintVO;
import org.easyrec.service.core.ClusterService;
import org.easyrec.service.core.ClusterStrategy;
import org.easyrec.service.core.ItemAssocService;
import org.easyrec.store.dao.core.ItemAssocDAO;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.utils.spring.cache.annotation.ShortCacheable;

import java.util.List;

/**
 * @author szavrel
 */
public class NewestClusterStrategyImpl implements ClusterStrategy {

    private ItemAssocService itemAssocService;
    private AssocTypeDAO assocTypeDAO;

    @ShortCacheable
    public List<AssociatedItemVO<Integer, Integer>> getItemsFromCluster(Integer tenantId,
                                                                                          ClusterVO cluster,
                                                                                          Integer numberOfResults,
                                                                                          Integer itemType) {
        IAConstraintVO<Integer, Integer> constraint = new IAConstraintVO<Integer, Integer>(
                numberOfResults, null, null, null, tenantId, true, false, ItemAssocDAO.DEFAULT_CHANGE_DATE_COLUMN_NAME);

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
