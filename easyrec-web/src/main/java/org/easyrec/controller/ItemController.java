/**Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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
package org.easyrec.controller;


import org.easyrec.model.core.ClusterVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.web.Item;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.service.core.ClusterService;
import org.easyrec.service.web.IDMappingService;
import org.easyrec.service.web.ItemService;
import org.easyrec.store.dao.web.ItemDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.MessageBlock;
import org.easyrec.utils.Security;
import org.easyrec.utils.Web;
import org.easyrec.utils.servlet.ServletUtils;
import org.easyrec.vocabulary.MSG;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * This Controller manages all item operations.
 * <p/>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: dmann $<br/>
 * $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/>
 * $Revision: 18685 $</p>
 *
 * @author phlavac
 * @version <CURRENT PROJECT VERSION>
 * @since <PROJECT VERSION ON FILE CREATION>
 */
public class ItemController extends MultiActionController {

    private RemoteTenantDAO remoteTenantDAO;
    private ItemDAO itemDAO;
    private ItemService itemService;
    private ClusterService clusterService;
    private IDMappingService idMappingService;

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    public void setItemDAO(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    public void setIdMappingService(IDMappingService idMappingService) {
        this.idMappingService = idMappingService;
    }

    /**
     * @param request
     * @param action
     * @return
     */
    private ModelAndView security(HttpServletRequest request, String action) {

        ModelAndView mav = new ModelAndView();

        if (!Security.isSignedIn(request)) {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, action, MSG.ERROR);
        }

        RemoteTenant r = remoteTenantDAO.get(request);
        if (r == null) {
            return MessageBlock.createSingle(mav, MSG.TENANT_NOT_EXISTS, action, MSG.ERROR);
        }
        return mav;
    }

    /**
     * Activates an item.
     * call:
     * http://localhost:8080/item/activate?tenantId=EASYREC_DEMO&itemId=42
     *
     * @param request
     * @param response
     * @return
     */
    public ModelAndView activate(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();

        try {
            itemService.activate(request);
            return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL, "deactivate", MSG.SUCCESS);
        } catch (Exception e) {
            return MessageBlock.createSingle(mav, MSG.ITEM_NOT_EXISTS, "deactivate", MSG.ERROR);
        }

    }

    /**
     * Dectivates an item.
     * call:
     * //http://localhost:8080/item/deactivate?tenantId=EASYREC_DEMO&itemId=42
     *
     * @param request
     * @param response
     * @return
     */
    public ModelAndView deactivate(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();

        try {
            itemService.deactivate(request);
            return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL, "deactivate", MSG.SUCCESS);
        } catch (Exception e) {
            return MessageBlock.createSingle(mav, MSG.ITEM_NOT_EXISTS, "deactivate", MSG.ERROR);
        }
    }

    /**
     * Return a mav with items to a given itemdescription
     *
     * @param request
     * @param response
     * @return
     */
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = security(request, "viewItems");

        if (Web.validated(mav)) {
            mav.setViewName("easyrec/viewitems");

            mav.addObject("items", itemDAO.getItems(remoteTenantDAO.get(request),
                    ServletUtils.getSafeParameter(request, "itemDescription", ""), 0, 10));

            mav.addObject("tenantId", ServletUtils.getSafeParameter(request, "tenantId", ""));

            return mav;
        } else return mav;
    }

    /**
     * Return a mav with itemdetails to a given itemid
     *
     * @param request
     * @param response
     * @return
     */
    public ModelAndView viewitemdetails(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = security(request, "viewItemDetails");

        if (Web.validated(mav)) {
            mav.setViewName("item/itemdetails");
            RemoteTenant r = remoteTenantDAO.get(request);
            try {
                Item item = itemService.get(request);
                // create relative urls
                Item itemDetailed = new Item(item.getId(), item.getTenantId(), item.getItemId(), item.getItemType(),
                        item.getDescription(), item.getUrl(), item.getImageUrl(), item.getValue(), item.isActive(),
                        item.getCreationDate());

                itemDetailed.setRelativeImageUrl(r.getUrl());
                itemDetailed.setRelativeUrl(r.getUrl());

                ItemVO<Integer, Integer> itemVO = idMappingService.convertItem(item);
                List<ClusterVO> clusters = clusterService.getClustersForItem(itemVO);

                // this string is used to have a unique id for the generated detail box. its used by
                // the javascript functions to generate the tabs and load the content to the specific DIVs.
                String detailBoxUniqueId = ServletUtils.getSafeParameter(request, "detailBoxUniqueId", "");
                boolean editEnabled =
                        ServletUtils.getSafeParameter(request, "editEnabled", "false").equalsIgnoreCase("true");

                mav.addObject("detailBoxUniqueId", detailBoxUniqueId);
                mav.addObject("editEnabled", editEnabled);
                mav.addObject("item", itemDetailed);
                mav.addObject("operatorId", Security.getOperatorId(request));
                mav.addObject("tenant", r);
                mav.addObject("tenantId", ServletUtils.getSafeParameter(request, "tenantId", ""));
                mav.addObject("clusters", clusters);
            } catch (Exception ex) {
                logger.debug("viewitemdetails: " + ex.getMessage());
            }

            return mav;
        } else return mav;
    }

    /**
     * Return a mav with itemstatistic to a given itemid
     * (first action, last action,
     * nr of Users acted on item, number of actions on item)
     *
     * @param request
     * @param response
     * @return
     */
    public ModelAndView viewitemstatistics(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = security(request, "viewItemStatistics");

        if (!Web.validated(mav)) return mav;

        mav.setViewName("item/itemstatistics");
        RemoteTenant r = remoteTenantDAO.get(request);

        try {
            Item item = itemService.get(request);
            mav.addObject("item", item);
            mav.addObject("itemDetails", itemDAO.getItemDetails(r.getId(), item.getItemId(), item.getItemType()));
        } catch (Exception ex) {
            logger.debug("viewitemstatistics", ex);
        }

        return mav;
    }

    /**
     * Removes items for a given tenant
     *
     * @param request
     * @param response
     * @return
     */
    public ModelAndView remove(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = security(request, "removeItems");

        if (Web.validated(mav)) {

            itemDAO.removeItems(remoteTenantDAO.get(request).getId());
            return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL, "removeItems", MSG.SUCCESS);
        } else return mav;
    }

    /**
     * Removes items for a given tenant
     *
     * @param request
     * @param response
     * @return
     */
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) {

        String action = "editItem";
        ModelAndView mav = security(request, action);

        String description = ServletUtils.getSafeParameter(request, "description", "");
        String url = ServletUtils.getSafeParameter(request, "url", "");
        String imageUrl = ServletUtils.getSafeParameter(request, "imageUrl", "");

        RemoteTenant r = remoteTenantDAO.get(request);
        try {
            Item item = itemService.get(request);
            itemDAO.insertOrUpdate(r.getId(), item.getItemId(), item.getItemType(), description, url, imageUrl);

            return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL, action, MSG.SUCCESS);
        } catch (Exception ex) {
            return MessageBlock.createSingle(mav, MSG.ITEM_UPDATE_FAILED, action, MSG.SUCCESS);
        }
    }
}