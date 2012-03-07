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
package org.easyrec.service.web.impl;


import com.google.common.base.Strings;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.web.Item;
import org.easyrec.service.core.ItemAssocService;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.service.web.RemoteAssocService;
import org.easyrec.utils.spring.store.dao.IDMappingDAO;

/**
 * This Class is a Service for manipulating the item associations
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: phlavac $<br/>
 * $Date: 2010-04-05 21:45:48 +0200 (Mo, 05 Apr 2010) $<br/>
 * $Revision: 15919 $</p>
 *
 * @author Peter Hlavac
 */
public class RemoteAssocServiceImpl implements RemoteAssocService {


    private TypeMappingService typeMappingService;
    private ItemAssocService itemAssocService;
    private IDMappingDAO IDMappingDAO;

    public RemoteAssocServiceImpl() {}

    public void setTypeMappingService(TypeMappingService typeMappingService) {
        this.typeMappingService = typeMappingService;
    }

    public void setItemAssocService(ItemAssocService itemAssocService) {
        this.itemAssocService = itemAssocService;
    }

    public void setIDMappingDAO(IDMappingDAO IDMappingDAO) {
        this.IDMappingDAO = IDMappingDAO;
    }


    public void activate(Integer tenantId, String itemFromId, String itemFromTypeId, String itemToId, String itemToTypeId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deactivate(Integer tenantId, String itemFromId, String itemFromTypeId, String itemToId, String itemToTypeId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    /**
     * Adds a manually created rule to the itemassoc table.
     *
     * @param tenantId    Integer
     * @param itemFromId  String
     * @param itemToId    String
     * @param assocTypeId Integer
     * @param assocValue  Float
     */
    public void addRule(Integer tenantId, String itemFromId, String itemFromTypeId, String itemToId, String itemToTypeId, Integer assocTypeId, Float assocValue) {
        if(Strings.isNullOrEmpty(itemFromTypeId))
            itemFromTypeId = Item.DEFAULT_STRING_ITEM_TYPE;

        if(Strings.isNullOrEmpty(itemToTypeId))
            itemToTypeId = Item.DEFAULT_STRING_ITEM_TYPE;

        ItemVO<Integer, Integer> itemFrom = new ItemVO<Integer, Integer>(tenantId,
                IDMappingDAO.lookup(itemFromId),
                typeMappingService.getIdOfItemType(tenantId, itemFromTypeId));

        ItemVO<Integer, Integer> itemTo = new ItemVO<Integer, Integer>(tenantId,
                IDMappingDAO.lookup(itemToId),
                typeMappingService.getIdOfItemType(tenantId, itemToTypeId));

        ItemAssocVO<Integer,Integer> itemAssoc = new ItemAssocVO<Integer,Integer>(
                tenantId, itemFrom, assocTypeId, (double) assocValue, itemTo,
                typeMappingService.getIdOfSourceType(tenantId, "MANUALLY_CREATED"), "Imported Rule through REST API",
                typeMappingService.getIdOfViewType(tenantId, "ADMIN"), true); // active

        itemAssocService.insertOrUpdateItemAssoc(itemAssoc);
    }
}