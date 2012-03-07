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

package org.easyrec.controller.clusterManager;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.model.web.Item;
import org.easyrec.model.web.Operator;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.store.dao.core.types.ItemTypeDAO;
import org.easyrec.store.dao.web.ItemDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.Security;
import org.easyrec.utils.servlet.ServletUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * This Controller is used to search specific items of a tenant.
 * It handles searching and displaying the result to the user.
 *
 * @author dmann
 */
public class ItemSearchController extends MultiActionController {

    private ItemDAO itemDAO;
    private RemoteTenantDAO remoteTenantDAO;
    private SimpleDateFormat dateFormat;
    private ItemTypeDAO itemTypeDAO;
    private AssocTypeDAO assocTypeDAO;

    static final Splitter COMMA_SPLITTER = Splitter.on(',').omitEmptyStrings().trimResults();
    private static final int PAGE_SIZE = 25;// Items per page for search results

    public ItemSearchController(ItemDAO itemDAO, String dateFormat, RemoteTenantDAO remoteTenantDAO,
                                ItemTypeDAO itemTypeDAO, AssocTypeDAO assocTypeDAO) {
        this.itemDAO = itemDAO;
        this.dateFormat = new SimpleDateFormat(dateFormat);
        this.remoteTenantDAO = remoteTenantDAO;
        this.itemTypeDAO = itemTypeDAO;
        this.assocTypeDAO = assocTypeDAO;
    }

    public ModelAndView searchresult(HttpServletRequest request, HttpServletResponse httpServletResponse)
            throws ParseException {
        ModelAndView mav = new ModelAndView("clustermanager/ajax/searchresult");
        return generateSearchResult(request, mav);
    }

    public ModelAndView items(HttpServletRequest request, HttpServletResponse httpServletResponse)
            throws ParseException {
        ModelAndView mav = new ModelAndView("page");

        Security.setAttribute(request, "menu", "items");

        mav.addObject("title", "easyrec :: view items");
        mav.addObject("selectedMenu", "myEasyrec");
        mav.addObject("menubar", "viewItems");
        mav.addObject("page", "easyrec/viewitems");

        return generateSearchResult(request, mav);
    }

    private ModelAndView generateSearchResult(HttpServletRequest request, ModelAndView mav) throws ParseException {
        RemoteTenant tenant = initializeView(request, mav);

        // set to empty values so in case an error is returned the displayTag:table does not throw an exception
        mav.addObject("itemSearchResult", null);
        mav.addObject("totalCount", 0);
        mav.addObject("pageSize", PAGE_SIZE);

        if (tenant == null) {
            logger.warn("no tenantId supplied");
            return mav;
        }

        String itemId = ServletUtils.getSafeParameter(request, "itemId", "");
        String description = ServletUtils.getSafeParameter(request, "description", "");
        String creationDateFrom = ServletUtils.getSafeParameter(request, "fromCreationDate", "");
        String creationDateTo = ServletUtils.getSafeParameter(request, "toCreationDate", "");
        String[] itemTypes = request.getParameterValues("itemTypes");
        boolean hasRules = ServletUtils.getSafeParameter(request, "hasRules", null) != null;
        String activatedString = ServletUtils.getSafeParameter(request, "isActivated", "null");
        String rulesOfType = ServletUtils.getSafeParameter(request, "rulesOfType", "");

        Boolean isActivated;
        if (activatedString.equalsIgnoreCase("null"))
            isActivated = null;
        else isActivated = activatedString.equalsIgnoreCase("true");

        mav.addObject("itemId", itemId);
        mav.addObject("description", description);
        mav.addObject("fromCreationDate", creationDateFrom);
        mav.addObject("toCreationDate", creationDateTo);
        mav.addObject("itemTypes", itemTypes);
        mav.addObject("hasRules", hasRules);
        mav.addObject("rulesOfType", rulesOfType);
        mav.addObject("isActivated", isActivated);

        ItemDAO.SortColumn sortColumn =
                tableSortParameterToDatabaseColumnName(ServletUtils.getSafeParameter(request, "d-16544-s", 0));
        Integer page = ServletUtils.getSafeParameter(request, "d-16544-p", 1);
        boolean sortDescending = ServletUtils.getSafeParameter(request, "d-16544-o", "0").equalsIgnoreCase("1");

        Date from = null;
        Date to = null;

        if (!Strings.isNullOrEmpty(creationDateFrom))
            from = dateFormat.parse(creationDateFrom);

        if (!Strings.isNullOrEmpty(creationDateTo))
            to = dateFormat.parse(creationDateTo);

        if (Strings.isNullOrEmpty(itemId))
            itemId = null;

        if (Strings.isNullOrEmpty(description))
            description = null;

        TimeConstraintVO creationDateConstraint = from != null || to != null ? new TimeConstraintVO(from, to) : null;
        Iterable<String> itemTypesIterable = itemTypes != null ? Arrays.asList(itemTypes) : null;

        List<Item> items =
                itemDAO.searchItems(tenant.getId(), itemId, itemTypesIterable, description, null, null, isActivated,
                        creationDateConstraint, hasRules, rulesOfType, sortColumn, sortDescending,
                        PAGE_SIZE * (page - 1), PAGE_SIZE);

        int totalCount =
                itemDAO.searchItemsTotalCount(tenant.getId(), itemId, itemTypesIterable, description, null, null,
                        isActivated, creationDateConstraint, hasRules, rulesOfType);

        mav.addObject("itemSearchResult", items);
        mav.addObject("totalCount", totalCount);

        return mav;
    }


    private ItemDAO.SortColumn tableSortParameterToDatabaseColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return ItemDAO.SortColumn.ITEM_ID;
            case 1:
                return ItemDAO.SortColumn.DESCRIPTION;
            case 2:
                return ItemDAO.SortColumn.ITEM_TYPE;
            default:
                return ItemDAO.SortColumn.NONE;
        }
    }


    /*
    todo(pm): move function away from this controller
    */
    private RemoteTenant initializeView(HttpServletRequest request, ModelAndView mav) {
        RemoteTenant remoteTenant = null;

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");

        String signedInOperatorId = Security.signedInOperatorId(request);
        Operator signedInOperator = Security.signedInOperator(request);

        if (signedInOperator != null) {
            if (Security.isDeveloper(request) && !Strings.isNullOrEmpty(operatorId))
                remoteTenant = remoteTenantDAO.get(operatorId, tenantId);
            else
                remoteTenant = remoteTenantDAO.get(signedInOperator.getOperatorId(), tenantId);

            if (remoteTenant != null)
                mav.addObject("remoteTenant", remoteTenant);
        }

        if (Security.isDeveloper(request) && !Strings.isNullOrEmpty(operatorId))
            mav.addObject("operatorId", operatorId);
        else
            mav.addObject("operatorId", signedInOperatorId);

        mav.addObject("tenantId", tenantId);
        mav.addObject("tenants", remoteTenantDAO.getTenantsFromOperator(operatorId));

        String apiKey = Operator.DEFAULT_API_KEY;

        if (signedInOperator != null) {
            apiKey = signedInOperator.getApiKey();
        }

        if (remoteTenant != null) {
            Set<String> itemTypes = itemTypeDAO.getTypes(remoteTenant.getId(), true);
            mav.addObject("availableItemTypes", itemTypes);

            Set<String> assocTypes = assocTypeDAO.getTypes(remoteTenant.getId(), true);
            assocTypes.add("");
            mav.addObject("availableAssocTypes", assocTypes);
        }

        mav.addObject("apiKey", apiKey);
        mav.addObject("signedIn", Security.isSignedIn(request));

        return remoteTenant;
    }
}
