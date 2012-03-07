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
package org.easyrec.service.web.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.model.web.statistics.*;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.service.web.RemoteTenantService;
import org.easyrec.store.dao.plugin.LogEntryDAO;
import org.easyrec.store.dao.web.ItemDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.store.dao.web.StatisticsDAO;

import java.util.HashMap;

/**
 * This Class is a Service for adding extra statistical Information
 * to Tenants. e.g. Userstatistics, number of items, number of actions,...
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
public class RemoteTenantServiceImpl implements RemoteTenantService {

    private TenantService tenantService;
    private RemoteTenantDAO remoteTenantDAO;
    private StatisticsDAO statisticsDAO;
    private TypeMappingService typeMappingService;
    private LogEntryDAO logEntryDAO;
    private ItemDAO itemDAO;

    private final Log logger = LogFactory.getLog(this.getClass());

    public RemoteTenantServiceImpl() {}

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public void setStatisticsDAO(StatisticsDAO statisticsDAO) {
        this.statisticsDAO = statisticsDAO;
    }

    public void setTypeMappingService(TypeMappingService typeMappingService) {
        this.typeMappingService = typeMappingService;
    }

    public void setLogEntryDAO(LogEntryDAO logEntryDAO) {
        this.logEntryDAO = logEntryDAO;
    }

    public void setItemDAO(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }


    /**
     * Updates the TenantStatistics for a given tenant.
     * This method is called after ruleminer finished execution.
     *
     * @param operatorId
     * @param tenantId
     */
    public void updateTenantStatistics(Integer tenantId) {

        long start = System.currentTimeMillis();
        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);
        // TODO statistic calcuation + DAO
        /*ruleMinerLogDAO.start(tenantId, "easyrec native", "0.95", "computing statistics",
                remoteTenant.getTenantStatisticProperties().toString());*/
        logger.info(
                "Updating statistics for tenant '" + remoteTenant.getOperatorId() + " - " + remoteTenant.getStringId() +
                        "'");

        remoteTenant.setTenantStatistic(statisticsDAO.getTenantStatistics(remoteTenant));
        storeAndUpdate(remoteTenant);
        logger.debug(
                "TenantStatistics for tenant '" + remoteTenant.getOperatorId() + " - " + remoteTenant.getStringId() +
                        "' updated.");

        Integer buyActionTypeId = null;
        try {
            buyActionTypeId = typeMappingService.getIdOfActionType(tenantId, "BUY");
        } catch (Exception e) {
            logger.debug("no buy actionType: " + e);
        }

        ConversionStatistic c = statisticsDAO.getConversionStatistics(tenantId, buyActionTypeId);

        remoteTenant.setConversionStatistic(c);
        storeAndUpdate(remoteTenant);
        logger.debug("Conversion Statistics for tenant '" + remoteTenant.getOperatorId() + " - " +
                remoteTenant.getStringId() + "' updated.");

        RuleMinerStatistic ruleStat = statisticsDAO
                .getRuleMinerStatistics(tenantId, 20,         // those values might be passed as parameters
                        40, 60, 80);


        remoteTenant.setRuleMinerStatistic(ruleStat);
        storeAndUpdate(remoteTenant);
        logger.debug("RuleMiner Statistics for tenant '" + remoteTenant.getOperatorId() + " - " +
                remoteTenant.getStringId() + "' updated.");


        // initializing statAssoc e.g.
        // key          value
        // "view"   AssocStatistic
        // "bought" AssocStatistic
        // "rated"  AssocStatistic
        HashMap<String, Integer> assocMapping = typeMappingService.getAssocTypeMapping(tenantId);
        HashMap<String, AssocStatistic> statAssoc = new HashMap<String, AssocStatistic>();
        if (assocMapping != null) {
            for (Integer assocId : assocMapping.values()) {
                statAssoc.put(typeMappingService.getAssocTypeById(tenantId, assocId),
                        statisticsDAO.getAssocStatistics(tenantId, assocId, assocId));
            }
        }

        remoteTenant.setAssocStatistic(statAssoc);
        remoteTenant.setMonthlyActions(statisticsDAO.getMonthlyActions(tenantId).toString());
        storeAndUpdate(remoteTenant);
        logger.debug(
                "Assoc Statistics for tenant '" + remoteTenant.getOperatorId() + " - " + remoteTenant.getStringId() +
                        "' updated.");

        // !!! >10M actions --> about 1 hour to execute!!!
        UserStatistic u = statisticsDAO.getUserStatistics(tenantId);
        // alternative compute useractions from last X days or use Profile System
        // in upcoming version.
        // UserStatistic   u = statisticsDAO.getUserStatistics(tenantId, 31);

        remoteTenant.setUserStatistic(u);
        storeAndUpdate(remoteTenant);
        logger.debug(
                "User Statistics for tenant '" + remoteTenant.getOperatorId() + " - " + remoteTenant.getStringId() +
                        "' updated.");
        logger.info(
                "Updating Statistics for tenant '" + remoteTenant.getOperatorId() + " - " + remoteTenant.getStringId() +
                        "' finished.");
        //        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //        remoteTenant.getTenantStatisticProperties().storeToXML(baos, "");
        //        ruleMinerLogDAO.finish(tenantId, "easyrec native", "computing statistics", "{duration[ms]:" + (System.currentTimeMillis()- start) + "," + baos.toString());
        // TODO statistic calculation + DAO
        /* ruleMinerLogDAO.finish(tenantId, "easyrec native", "0.95", "computing statistics",
                "{duration[ms]:" + (System.currentTimeMillis() - start) + "," +
                        remoteTenant.getTenantStatisticProperties().toString().substring(1));*/
    }

    /**
     * Because of long queries always store the statistics after each step
     *
     * @param remoteTenant
     */
    private void storeAndUpdate(RemoteTenant remoteTenant) {
        tenantService.storeTenantStatistic(remoteTenant.getId(), remoteTenant.getTenantStatisticProperties());

        remoteTenantDAO.updateTenantInCache(remoteTenant);
    }


    /**
     * Returns Tenant specific Statistics for the given Tenant
     *
     * @param tenantId
     * @return
     */
    public TenantStatistic getTenantStatistics(Integer tenantId) {

        RemoteTenant r = remoteTenantDAO.get(tenantId);
        return (r != null ? r.getTenantStatistic() : null);

    }

    /**
     * Returns User specific Statistics for the given Tenant
     *
     * @param tenantId
     * @return
     */
    public UserStatistic getUserStatistics(Integer tenantId) {
        RemoteTenant r = remoteTenantDAO.get(tenantId);
        return (r != null ? r.getUserStatistic() : null);
    }

    /**
     * Returns Conversion specific Statistics for the given Tenant
     *
     * @param tenantId
     * @return
     */
    public ConversionStatistic getConversionStatistics(Integer tenantId) {
        RemoteTenant r = remoteTenantDAO.get(tenantId);
        return (r != null ? r.getConversionStatistic() : null);
    }


    /**
     * Returns RuleMiner specific Statistics for the given Tenant
     *
     * @param tenantId
     * @return
     */
    public RuleMinerStatistic getRuleMinerStatistics(Integer tenantId) {
        RemoteTenant r = remoteTenantDAO.get(tenantId);
        return (r != null ? r.getRuleMinerStatistic() : null);
    }

    /**
     * Get Assoc specficic Statistics for the given Tenant
     *
     * @param tenantId
     * @return
     */
    public HashMap<String, AssocStatistic> getAssocStatistic(Integer tenantId) {
        RemoteTenant r = remoteTenantDAO.get(tenantId);
        return (r != null ? r.getAssocStatistic() : null);

    }

    /**
     * Clears the statistic properties of the given tenant
     *
     * @param tenantId
     */
    public void resetTenant(Integer tenantId) {
        RemoteTenant r = remoteTenantDAO.get(tenantId);
        if (r != null) {
            itemDAO.removeItems(r.getId());
        }

        remoteTenantDAO.reset(tenantId);
        updateTenantStatistics(tenantId);
    }

    /**
     * Removes a tenant with all its dependencies (items, actions, ...)
     *
     * @param tenantId
     */
    public void removeTenant(Integer tenantId) {
        RemoteTenant r = remoteTenantDAO.get(tenantId);
        if (r != null) {
            itemDAO.removeItems(r.getId());
            remoteTenantDAO.remove(r.getOperatorId(), r.getStringId());
        }

    }


}