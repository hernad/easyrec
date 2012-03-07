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
package org.easyrec.plugin.arm.impl;

import gnu.trove.map.hash.TObjectIntHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.plugin.arm.model.ARMConfiguration;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.domain.TypeMappingService;

import java.util.*;
import org.easyrec.plugin.arm.ARMGenerator;
import org.easyrec.plugin.arm.AssocRuleMiningService;
import org.easyrec.plugin.arm.TupleCounter;
import org.easyrec.plugin.arm.model.ARMConfigurationInt;
import org.easyrec.plugin.arm.model.ARMStatistics;
import org.easyrec.plugin.arm.model.TupleVO;
import org.easyrec.plugin.arm.store.dao.RuleminingActionDAO;
import org.easyrec.plugin.arm.store.dao.RuleminingItemAssocDAO;
import org.easyrec.store.dao.core.ArchiveDAO;

/**
 * <DESCRIPTION>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-11 18:35:47 +0100 (Fr, 11 Feb 2011) $<br/>
 * $Revision: 17681 $</p>
 *
 * @author Stephan Zavrel
 */
public class AssocRuleMiningServiceImpl implements AssocRuleMiningService {
    private TypeMappingService typeMappingService;
    private TenantService tenantService;
    private ArchiveDAO archiveDAO;
    private RuleminingActionDAO ruleminingActionDAO;
    private RuleminingItemAssocDAO itemAssocDAO;

    private Integer currentRunningTenantId = null;

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    public AssocRuleMiningServiceImpl(TypeMappingService typeMappingService, TenantService tenantService,
                                      RuleminingActionDAO ruleminingActionDAO, RuleminingItemAssocDAO itemAssocDAO) {
        this.typeMappingService = typeMappingService;
        this.tenantService = tenantService;
        this.ruleminingActionDAO = ruleminingActionDAO;
        this.itemAssocDAO = itemAssocDAO;

    }

    /**
     * Move actions to archive table if older than given days.
     *
     * @param tenantId
     * @return
     */
    public void archive(Integer tenantId, Integer days) throws Exception {

//
//        currentRunningTenantId = tenantId;
//        try {
//
//            String actualArchiveTableName = archiveDAO.getActualArchiveTableName();
//
//            Date refDate = new Date(System.currentTimeMillis() - (days * 86400000l)); //convert days to millis
//
//            if (ruleMinerLogDAO != null) {
//                ruleMinerLogDAO
//                        .start(tenantId, AssocRuleMiningService.GENERATOR_ID, AssocRuleMiningService.GENERATOR_VERSION,
//                                "archiving", "Cutoff date=" + refDate + "<br/>");
//            }
//
//            logger.info("Cutoff date: " + refDate);
//
//            Integer numberOfActionsToArchive = archiveDAO.getNumberOfActionsToArchive(tenantId, refDate);
//
//            logger.info("Number of actions to archive:" + numberOfActionsToArchive);
//
//            if (numberOfActionsToArchive > 0) {
//                if (archiveDAO.isArchiveFull(actualArchiveTableName, numberOfActionsToArchive)) {
//                    //generate new archive
//                    actualArchiveTableName = archiveDAO.generateNewArchive(actualArchiveTableName);
//                }
//                // move actions to archive
//                archiveDAO.moveActions(actualArchiveTableName, tenantId, refDate);
//            }
//            if (ruleMinerLogDAO != null) {
//                ruleMinerLogDAO
//                        .finish(tenantId, AssocRuleMiningService.GENERATOR_ID, AssocRuleMiningService.GENERATOR_VERSION,
//                                "archiving", "<actionsArchived>" + numberOfActionsToArchive + "</actionsArchived>");
//            }
//            currentRunningTenantId = null;
//        } catch (Exception e) {
//            currentRunningTenantId = null;
//            if (ruleMinerLogDAO != null) {
//                ruleMinerLogDAO
//                        .finish(tenantId, AssocRuleMiningService.GENERATOR_ID, AssocRuleMiningService.GENERATOR_VERSION,
//                                "archiving", "Archiving rules aborted!");
//            }
//            throw e;
//        }
    }

    public Integer getNumberOfBaskets(ARMConfigurationInt configuration) {

        if (configuration.getExcludeSingleItemBaskests()) {
            return ruleminingActionDAO.getNumberOfBasketsESIB(configuration.getTenantId(), configuration.getActionType(), configuration.getRatingNeutral(), configuration.getItemTypes());
        } else {
            return ruleminingActionDAO.getNumberOfBaskets(configuration.getTenantId(), configuration.getActionType(), configuration.getRatingNeutral(), configuration.getItemTypes());
       }
    }


    public Integer getNumberOfProducts(ARMConfigurationInt configuration) {

        return ruleminingActionDAO.getNumberOfProducts(configuration.getTenantId(), configuration.getActionType(), configuration.getRatingNeutral(), configuration.getItemTypes());
    }

    public Integer getNumberOfActions(ARMConfigurationInt configuration) {

        return ruleminingActionDAO.getNumberOfActions(configuration.getTenantId(), configuration.getActionType());
    }

    public TObjectIntHashMap<ItemVO<Integer, Integer>> defineL1(ARMConfigurationInt configuration) {


        return ruleminingActionDAO.defineL1(configuration);
    }

    public List<TupleVO> defineL2(TObjectIntHashMap<ItemVO<Integer, Integer>> L1, TupleCounter tupleCounter, ARMConfigurationInt configuration, ARMStatistics stats) {
            
        return ruleminingActionDAO.defineL2(L1, tupleCounter, configuration, stats);
    }


    public ARMConfigurationInt mapTypesToConfiguration(ARMConfiguration configuration) throws Exception {

        ARMConfigurationInt ret;

        ret = new ARMConfigurationInt(configuration.getTenantId(),
                                      null,
                                      configuration.getSupport(),
                                      new ArrayList<Integer>(),
                                      null,
                                      configuration.getSupportPrcnt(),
                                      configuration.getSupportMinAbs(),
                                      configuration.getConfidencePrcnt(),
                                      configuration.getMaxRulesPerItem(),
                                      configuration.getRatingNeutral(),
                                      configuration.getMetricType(),
                                      configuration.getMaxSizeL1(),
                                      configuration.getDoDeltaUpdate());

        Integer actionId = typeMappingService.getIdOfActionType(configuration.getTenantId(), configuration.getActionType());
        if (actionId == null) {
            StringBuilder sb = new StringBuilder("Action '").append(configuration.getActionType())
                                                            .append("' not valid for Tenant '")
                                                            .append(configuration.getTenantId())
                                                            .append("'! Action will not be considered in Rulemining!");
            logger.info(sb.toString());
            throw new Exception(sb.toString());
        }
        ret.setActionType(actionId);

        for (String type : configuration.getItemTypes()) {
            Integer itemTypeId = typeMappingService.getIdOfItemType(configuration.getTenantId(), type);
            if (itemTypeId != null) {
                ret.getItemTypes().add(itemTypeId);
            } else {
                logger.info("ItemType '" + type + "' not valid for Tenant '" + configuration.getTenantId() +
                        "'! ItemType will not be considered in rulemining!");
            }
        }
        if (ret.getItemTypes() == null) {
                            StringBuilder sb = new StringBuilder("No valid ItemTypes defined for Tenant '" )
                                                                  .append(configuration.getTenantId())
                                                                  .append("'! Skipping this rulemining configuration!");
            logger.info(sb.toString());
            throw new Exception(sb.toString());
        }

        Integer assocTypeId = typeMappingService.getIdOfAssocType(configuration.getTenantId(), configuration.getAssociationType());
        if (assocTypeId == null) {
            StringBuilder sb = new StringBuilder("AssocType '")
                                          .append(configuration.getAssociationType())
                                          .append("' not valid for Tenant '")
                                          .append(configuration.getTenantId())
                                          .append("'! Skipping analysis!");
            logger.info(sb.toString());
            throw new Exception(sb.toString());
        }
        ret.setAssocType(assocTypeId);

        if (configuration.getAssociationType().equals("GOOD_RATED_TOGETHER")) {
            ret.setRatingNeutral(configuration.getRatingNeutral());
        } else {
            ret.setRatingNeutral(null);
        }

        return ret;

    }


    /**
     * @param tuples        Vector
     * @param L1            HashMap
     * @param configuration      int
     * @param minConfidence minConfidence
     * @return Vector
     */
    public List<ItemAssocVO<Integer,Integer>> createRules(List<TupleVO> tuples,
                                                                                                TObjectIntHashMap<ItemVO<Integer, Integer>> L1,
                                                                                                ARMConfigurationInt configuration,
                                                                                                ARMStatistics stats,
                                                                                                Double minConfidence) {

        // Integer h1, h2;
        Double dh1, dh2;

        Integer sup1, sup2;
        Double dsup1, dsup2, assocValue1, assocValue2;

        Double baskets = new Double(stats.getNrBaskets());
        stats.setMetricType(configuration.getMetricType());
        Vector<ItemAssocVO<Integer,Integer>> ret = new Vector<ItemAssocVO<Integer,Integer>>();

        for (TupleVO tuple : tuples) {
            sup1 = L1.get(tuple.getItem1());
            dsup1 = new Double(sup1);
            sup2 = L1.get(tuple.getItem2());
            dsup2 = new Double(sup2);
            if (sup1 == null || sup2 == null) {
                continue;
            }
            // confidence
            //          h1 = (tuple.getSupport() * 100) / sup1;
            //          h2 = (tuple.getSupport() * 100) / sup2;

            // confidence
            dh1 = (tuple.getSupport() * 100) / dsup1;
            dh2 = (tuple.getSupport() * 100) / dsup2;

            // lift
            Double lift = tuple.getSupport() / (dsup1 * dsup2);

            // conviction
            Double conviction1 = (1 - (dsup2 / baskets)) / (100 - dh1);
            Double conviction2 = (1 - (dsup1 / baskets)) / (100 - dh2);

            // ltc
            Double ltc1 = dsup1 * Math.log10(dsup1 / dsup2);
            Double ltc2 = dsup2 * Math.log10(dsup2 / dsup1);

            switch (configuration.getMetricType()) {
                case CONFIDENCE:
                    assocValue1 = dh1;
                    assocValue2 = dh2;
                    break;
                case CONVICTION:
                    assocValue1 = conviction1;
                    assocValue2 = conviction2;
                    break;
                case LIFT:
                    assocValue1 = lift;
                    assocValue2 = lift;
                    break;
                case LONGTAIL:
                    assocValue1 = ltc1;
                    assocValue2 = ltc2;
                    break;
                default:
                    assocValue1 = dh1;
                    assocValue2 = dh2;
                    break;
            }

            //          public ItemAssocVO(T tenant, ItemVO<T, I, IT> itemFrom, AT assocType,
            //                          Double assocValue, ItemVO<T, I, IT> itemTo, ST sourceType,
            //                          String sourceInfo, VT viewType, Boolean active)
            // TODO: confidence always used as quality reference! maybe better use assocValue
            if (dh1 >= (minConfidence)) {

                String comment1 = new StringBuilder("conf=").append(String.format("%04f", dh1)).append(" lift=")
                        .append(String.format("%04f", lift)).append(" convic=")
                        .append(String.format("%04f", conviction1)).append(" ltc=").append(String.format("%04f", ltc1))
                        .append(" sup1=").append(String.format("%04f", dsup1)).append(" sup2=")
                        .append(String.format("%04f", dsup2)).append(" tsup=").append(tuple.getSupport()).toString();

                ItemAssocVO<Integer,Integer> rule = new ItemAssocVO<Integer,Integer>(
                        configuration.getTenantId(), tuple.getItem1(), configuration.getAssocType(), assocValue1/*new Double(h1)*/,
                        tuple.getItem2(),
                        typeMappingService.getIdOfSourceType(configuration.getTenantId(), ARMGenerator.ID.toString() + "/" + ARMGenerator.VERSION),
                        comment1, typeMappingService
                        .getIdOfViewType(configuration.getTenantId(), TypeMappingService.VIEW_TYPE_COMMUNITY), true,
                        stats.getStartDate());
                ret.add(rule);
            }

            if (dh2 >= (minConfidence)) {

                String comment2 = new StringBuilder("conf=").append(String.format("%04f", dh2)).append(" lift=")
                        .append(String.format("%04f", lift)).append(" convic=")
                        .append(String.format("%04f", conviction2)).append(" ltc=").append(String.format("%04f", ltc2))
                        .append(" sup2=").append(String.format("%04f", dsup2)).append(" sup1=")
                        .append(String.format("%04f", dsup1)).append(" tsup=").append(tuple.getSupport()).toString();

                ItemAssocVO<Integer,Integer> rule = new ItemAssocVO<Integer,Integer>(
                        configuration.getTenantId(), tuple.getItem2(), configuration.getAssocType(), assocValue2/*new Double(h2)*/,
                        tuple.getItem1(),
                        typeMappingService.getIdOfSourceType(configuration.getTenantId(), ARMGenerator.ID.toString() + "/" + ARMGenerator.VERSION),
                        comment2, typeMappingService
                        .getIdOfViewType(configuration.getTenantId(), TypeMappingService.VIEW_TYPE_COMMUNITY), true,
                        stats.getStartDate());
                ret.add(rule);
            }
        }
        return ret;
    }

    public Collection<SortedSet<ItemAssocVO<Integer,Integer>>> createBestRules(
            List<TupleVO> tuples, 
            TObjectIntHashMap<ItemVO<Integer, Integer>> L1,
            ARMConfigurationInt configuration,
            ARMStatistics stats,
            Double minConfidence) {
        // Integer h1, h2;
        Double dh1, dh2;

        Integer sup1, sup2;
        Double dsup1, dsup2, assocValue1, assocValue2;

        Double baskets = new Double(stats.getNrBaskets());
        stats.setMetricType(configuration.getMetricType());
        //Vector<ItemAssocVO<Integer,Integer>> ret = new Vector<ItemAssocVO<Integer,Integer>>();
        Map<ItemVO<Integer, Integer>, SortedSet<ItemAssocVO<Integer,Integer>>> ret = new HashMap<ItemVO<Integer, Integer>, SortedSet<ItemAssocVO<Integer,Integer>>>();
        for (TupleVO tuple : tuples) {
            sup1 = L1.get(tuple.getItem1());
            dsup1 = new Double(sup1);
            sup2 = L1.get(tuple.getItem2());
            dsup2 = new Double(sup2);
            if (sup1 == null || sup2 == null) {
                continue;
            }
            // confidence
            //          h1 = (tuple.getSupport() * 100) / sup1;
            //          h2 = (tuple.getSupport() * 100) / sup2;

            // confidence
            dh1 = (tuple.getSupport() * 100) / dsup1;
            dh2 = (tuple.getSupport() * 100) / dsup2;

            // lift
            Double lift = tuple.getSupport() / (dsup1 * dsup2);

            // conviction
            Double conviction1 = (1 - (dsup2 / baskets)) / (100 - dh1);
            Double conviction2 = (1 - (dsup1 / baskets)) / (100 - dh2);

            // ltc
            Double ltc1 = dsup1 * Math.log10(dsup1 / dsup2);
            Double ltc2 = dsup2 * Math.log10(dsup2 / dsup1);

            switch (configuration.getMetricType()) {
                case CONFIDENCE:
                    assocValue1 = dh1;
                    assocValue2 = dh2;
                    break;
                case CONVICTION:
                    assocValue1 = conviction1;
                    assocValue2 = conviction2;
                    break;
                case LIFT:
                    assocValue1 = lift;
                    assocValue2 = lift;
                    break;
                case LONGTAIL:
                    assocValue1 = ltc1;
                    assocValue2 = ltc2;
                    break;
                default:
                    assocValue1 = dh1;
                    assocValue2 = dh2;
                    break;
            }

            //          public ItemAssocVO(T tenant, ItemVO<T, I, IT> itemFrom, AT assocType,
            //                          Double assocValue, ItemVO<T, I, IT> itemTo, ST sourceType,
            //                          String sourceInfo, VT viewType, Boolean active)

            if (dh1 >= (minConfidence)) {

                SortedSet<ItemAssocVO<Integer,Integer>> bestRules = ret
                        .get(tuple.getItem1());
                if (bestRules == null) {
                    bestRules = new TreeSet<ItemAssocVO<Integer,Integer>>();
                }
                if ((bestRules.size() < configuration.getMaxRulesPerItem()) || (assocValue1 > bestRules.first()
                        .getAssocValue())) { // no need to create objects if limit already reached and rule shows worse quality
                    String comment1 = new StringBuilder("conf=").append(String.format("%04f", dh1)).append(" lift=")
                            .append(String.format("%04f", lift)).append(" convic=")
                            .append(String.format("%04f", conviction1)).append(" ltc=")
                            .append(String.format("%04f", ltc1)).append(" sup1=").append(String.format("%04f", dsup1))
                            .append(" sup2=").append(String.format("%04f", dsup2)).append(" tsup=")
                            .append(tuple.getSupport()).toString();

                    ItemAssocVO<Integer,Integer> rule = new ItemAssocVO<Integer,Integer>(
                            configuration.getTenantId(), tuple.getItem1(), configuration.getAssocType(), assocValue1
                            /*new Double(h1)*/, tuple.getItem2(), typeMappingService
                            .getIdOfSourceType(configuration.getTenantId(), ARMGenerator.ID.toString() + "/" + ARMGenerator.VERSION), comment1,
                            typeMappingService
                                    .getIdOfViewType(configuration.getTenantId(), TypeMappingService.VIEW_TYPE_COMMUNITY),
                            true, stats.getStartDate());

                    bestRules.add(rule);
                    if (bestRules.size() > configuration.getMaxRulesPerItem()) {
                        bestRules.remove(bestRules.first());
                    }
                    ret.put(tuple.getItem1(), bestRules);
                }
            }

            if (dh2 >= (minConfidence)) {

                SortedSet<ItemAssocVO<Integer,Integer>> bestRules = ret
                        .get(tuple.getItem2());
                if (bestRules == null) {
                    bestRules = new TreeSet<ItemAssocVO<Integer,Integer>>();
                }
                if ((bestRules.size() < configuration.getMaxRulesPerItem()) || (assocValue2 > bestRules.first()
                        .getAssocValue())) { // no need to create objects if limit already reached and rule shows worse quality

                    String comment2 = new StringBuilder("conf=").append(String.format("%04f", dh2)).append(" lift=")
                            .append(String.format("%04f", lift)).append(" convic=")
                            .append(String.format("%04f", conviction2)).append(" ltc=")
                            .append(String.format("%04f", ltc2)).append(" sup2=").append(String.format("%04f", dsup2))
                            .append(" sup1=").append(String.format("%04f", dsup1)).append(" tsup=")
                            .append(tuple.getSupport()).toString();

                    ItemAssocVO<Integer,Integer> rule = new ItemAssocVO<Integer,Integer>(
                            configuration.getTenantId(), tuple.getItem2(), configuration.getAssocType(), assocValue2
                            /*new Double(h2)*/, tuple.getItem1(), typeMappingService
                            .getIdOfSourceType(configuration.getTenantId(), ARMGenerator.ID.toString() + "/" + ARMGenerator.VERSION), comment2,
                            typeMappingService
                                    .getIdOfViewType(configuration.getTenantId(), TypeMappingService.VIEW_TYPE_COMMUNITY),
                            true, stats.getStartDate());
                    bestRules.add(rule);
                    if (bestRules.size() > configuration.getMaxRulesPerItem()) {
                        bestRules.remove(bestRules.first());
                    }
                    ret.put(tuple.getItem2(), bestRules);
                }
            }
        }
        return ret.values();
    }

    public void removeOldRules(ARMConfigurationInt configuration,
                               ARMStatistics stats) {

        itemAssocDAO.removeItemAssocByTenant(configuration.getTenantId(), configuration.getAssocType(),
                typeMappingService.getIdOfSourceType(configuration.getTenantId(), ARMGenerator.ID.toString() + "/" + ARMGenerator.VERSION),
                stats.getStartDate());
    }

    // getters and setters
    public TypeMappingService getTypeMappingService() {
        return typeMappingService;
    }

    public void setTypeMappingService(TypeMappingService typeMappingService) {
        this.typeMappingService = typeMappingService;
    }

    public TenantService getTenantService() {
        return tenantService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public RuleminingItemAssocDAO getItemAssocDAO() {
        return itemAssocDAO;
    }

    public void setItemAssocDAO(RuleminingItemAssocDAO itemAssocDAO) {
        this.itemAssocDAO = itemAssocDAO;
    }

    public RuleminingActionDAO getRuleminingActionDAO() {
        return ruleminingActionDAO;
    }

    public void setRuleminingActionDAO(RuleminingActionDAO ruleminingActionDAO) {
        this.ruleminingActionDAO = ruleminingActionDAO;
    }

    public void setArchiveDAO(ArchiveDAO archiveDAO) {
        this.archiveDAO = archiveDAO;
    }

    /**
     * Return true if ruleminer is running.
     *
     * @return
     */
    public boolean isRunning() {
        return (currentRunningTenantId != null);
    }

    /**
     * Return the integer id of the current running tenantid.
     *
     * @return
     */
    public Integer getRunningTenantId() {
        return currentRunningTenantId;
    }

}
