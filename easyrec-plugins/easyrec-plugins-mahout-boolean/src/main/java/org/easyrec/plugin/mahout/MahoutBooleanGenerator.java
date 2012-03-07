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

package org.easyrec.plugin.mahout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.easyrec.mahout.model.EasyrecDataModel;
import org.easyrec.mahout.model.EasyrecInMemoryDataModel;
import org.easyrec.mahout.store.MahoutDataModelMappingDAO;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.support.GeneratorPluginSupport;
import org.easyrec.service.core.ItemAssocService;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.store.dao.core.types.ItemTypeDAO;
import org.easyrec.store.dao.web.ItemDAO;

import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * @author David MAnn
 */
public class MahoutBooleanGenerator extends GeneratorPluginSupport<MahoutBooleanGeneratorConfig, MahoutBooleanGeneratorStats> {
    // ------------------------------ FIELDS ------------------------------

    // the display name is the name of the generator that will show up in the admin tool when the plugin has been loaded.
    public static final String DISPLAY_NAME = "Mahout Boolean Generator";

    // version of the generator, should be ascending for each new release
    public static final Version VERSION = new Version("0.97");

    // The URI that uniquely identifies the plugin. While any valid URI is technically ok here, implementors
    // should choose their URIs wisely, ideally the URI should be 'cool'
    // (@see <a href="http://www.dfki.uni-kl.de/~sauermann/2006/11/cooluris/#cooluris">Cool URIs for the
    // Semantic Web</a>) If unsure, use an all-lowercase http URI pointing to a host/path that you control,
    // ending with '#[plugin-name]'.
    public static final URI ID = URI.create("http://www.easyrec.org/plugins/mahoutboolean");

    private static final Log logger = LogFactory.getLog(MahoutBooleanGenerator.class);

    private MahoutDataModelMappingDAO mahoutDataModelMappingDAO;
    private AssocTypeDAO assocTypeDAO;
    private ItemTypeDAO itemTypeDAO;
    private ItemDAO itemDAO;

    public void setAssocTypeDAO(AssocTypeDAO assocTypeDAO) {
        this.assocTypeDAO = assocTypeDAO;
    }

    public void setItemTypeDAO(ItemTypeDAO itemTypeDAO) {
        this.itemTypeDAO = itemTypeDAO;
    }

    public void setItemDAO(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    public void setMahoutDataModelMappingDAO(MahoutDataModelMappingDAO mahoutDataModelMappingDAO) {
        this.mahoutDataModelMappingDAO = mahoutDataModelMappingDAO;
    }
    // --------------------------- CONSTRUCTORS ---------------------------

    public MahoutBooleanGenerator() {
        // we need to call the constructor of GeneratorPluginSupport to provide the name, id and version
        //additionally, we have to pass the class objects of config and stats classes.
        super(DISPLAY_NAME, ID, VERSION, MahoutBooleanGeneratorConfig.class, MahoutBooleanGeneratorStats.class);
    }

    // ------------------------ INTERFACE METHODS ------------------------

    @Override
    public String getPluginDescription() {
        return "This is a plugin using algorihtms of the apache Mahout project using the CF code formerly known as the taste framework.";
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected void doCleanup() throws Exception {
        logger.info("The plugin is now being uninstalled.");
        // remove all tables/files/resources you created in {@link #doInitialize()}.
        // optional - you don't have to implement this method
    }

    @Override
    protected void doExecute(ExecutionControl executionControl, MahoutBooleanGeneratorStats stats) throws Exception {
        // when doExecute() is called, the generator has been initialized with the configuration we should use

        Date execution = new Date();

        MahoutBooleanGeneratorConfig config = getConfiguration();

        TypeMappingService typeMappingService = (TypeMappingService) super.getTypeMappingService();
        ItemAssocService itemAssocService = getItemAssocService();

        executionControl.updateProgress("initialize DataModel");
        DataModel easyrecDataModel = new EasyrecDataModel(config.getTenantId(), typeMappingService.getIdOfActionType(config.getTenantId(), config.getActionType()), false, mahoutDataModelMappingDAO);

        if (config.getCacheDataInMemory() == 1) {
            executionControl.updateProgress("initialize EasyrecInMemoryDataModel");
            easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);
        }

        /*TanimotoCoefficientSimilarity is intended for "binary" data sets  where a user either expresses a generic "yes" preference for an item or has no preference.*/
        UserSimilarity userSimilarity = null;

        switch (config.getUserSimilarityMethod()) {
            case 1:
                executionControl.updateProgress("using LogLikelihoodSimilarity as UserSimilarity");
                userSimilarity = new LogLikelihoodSimilarity(easyrecDataModel);
                break;
            case 2:
                executionControl.updateProgress("using TanimotoCoefficientSimilarity as UserSimilarity");
                userSimilarity = new TanimotoCoefficientSimilarity(easyrecDataModel);
                break;
            case 3:
                executionControl.updateProgress("using SpearmanCorrelationSimilarity as UserSimilarity");
                userSimilarity = new SpearmanCorrelationSimilarity(easyrecDataModel);
                break;
            case 4:
                executionControl.updateProgress("using CityBlockSimilarity as UserSimilarity");
                userSimilarity = new CityBlockSimilarity(easyrecDataModel);
                break;
        }

        /*ThresholdUserNeighborhood is preferred in situations where we go in for a  similarity measure between neighbors and not any number*/
        UserNeighborhood neighborhood = null;
        Double userNeighborhoodSamplingRate = config.getUserNeighborhoodSamplingRate();
        Double neighborhoodThreshold = config.getUserNeighborhoodThreshold();
        int neighborhoodSize = config.getUserNeighborhoodSize();
        double userNeighborhoodMinSimilarity = config.getUserNeighborhoodMinSimilarity();

        switch (config.getUserNeighborhoodMethod()) {
            case 1:
                executionControl.updateProgress("using ThresholdUserNeighborhood as UserNeighborhood");
                neighborhood = new ThresholdUserNeighborhood(neighborhoodThreshold, userSimilarity, easyrecDataModel, userNeighborhoodSamplingRate);
                break;
            case 2:
                executionControl.updateProgress("using NearestNUserNeighborhood as UserNeighborhood");
                neighborhood = new NearestNUserNeighborhood(neighborhoodSize, userNeighborhoodMinSimilarity, userSimilarity, easyrecDataModel, userNeighborhoodSamplingRate);
                break;
        }
        /*GenericBooleanPrefUserBasedRecommender is appropriate for use when no notion of preference value exists in the data. */
        executionControl.updateProgress("using GenericBooleanPrefUserBasedRecommender as Recommender");
        Recommender recommender = new GenericBooleanPrefUserBasedRecommender(easyrecDataModel, neighborhood, userSimilarity);

        itemTypeDAO.insertOrUpdate(config.getTenantId(), "USER", true);

        Integer assocType = typeMappingService.getIdOfAssocType(config.getTenantId(), config.getAssociationType());
        Integer userType = typeMappingService.getIdOfItemType(config.getTenantId(), "USER");
        Integer sourceType = typeMappingService.getIdOfSourceType(config.getTenantId(), getId().toString());
        Integer viewType = typeMappingService.getIdOfViewType(config.getTenantId(), config.getViewType());

        stats.setNumberOfItems(easyrecDataModel.getNumItems());


        int totalSteps = easyrecDataModel.getNumUsers();
        int currentStep = 1;
        for (LongPrimitiveIterator it = easyrecDataModel.getUserIDs(); it.hasNext() && !executionControl.isAbortRequested(); ) {
            executionControl.updateProgress(currentStep++, totalSteps, "Saving Recommendations...");
            long userId = it.nextLong();
            List<RecommendedItem> recommendations = recommender.recommend(userId, config.getNumberOfRecs());

            if (recommendations.isEmpty()) {
                logger.debug("User " + userId + " : no recommendations");
            }

            // print the list of recommendations for each
            for (RecommendedItem recommendedItem : recommendations) {
                logger.debug("User " + userId + " : " + recommendedItem);

                Integer itemToId = (int) recommendedItem.getItemID();
                Integer itemToType = itemDAO.getItemTypeIdOfItem(config.getTenantId(), itemToId);

                ItemVO<Integer, Integer> fromItem = new ItemVO<Integer, Integer>(config.getTenantId(), (int) userId, userType);
                Double recommendationStrength = (double) recommendedItem.getValue();
                ItemVO<Integer, Integer> toItem = new ItemVO<Integer, Integer>(config.getTenantId(), itemToId, itemToType);

                ItemAssocVO<Integer,Integer> itemAssoc = new ItemAssocVO<Integer,Integer>(
                        config.getTenantId(), fromItem, assocType, recommendationStrength, toItem, sourceType,
                        "Mahout Boolean Generator", viewType, null, execution);

                itemAssocService.insertOrUpdateItemAssoc(itemAssoc);
                stats.incNumberOfRulesCreated();
            }
        }

    }

}
