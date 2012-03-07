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
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.easyrec.mahout.model.EasyrecDataModel;
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
 * Sample generator plugin that demonstrates how to use the easyrec plugin API. <p/> <p><b>Company:&nbsp;</b> SAT,
 * Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/>
 * $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class MahoutSlopeoneGenerator extends GeneratorPluginSupport<MahoutSlopeoneGeneratorConfig, MahoutSlopeoneGeneratorStats> {
    // ------------------------------ FIELDS ------------------------------

    // the display name is the name of the generator that will show up in the admin tool when the plugin has been loaded.
    public static final String DISPLAY_NAME = "Mahout Slopeone Generator";

    // version of the generator, should be ascending for each new release
    public static final Version VERSION = new Version("0.97");

    // The URI that uniquely identifies the plugin. While any valid URI is technically ok here, implementors
    // should choose their URIs wisely, ideally the URI should be 'cool'
    // (@see <a href="http://www.dfki.uni-kl.de/~sauermann/2006/11/cooluris/#cooluris">Cool URIs for the
    // Semantic Web</a>) If unsure, use an all-lowercase http URI pointing to a host/path that you control,
    // ending with '#[plugin-name]'.
    public static final URI ID = URI.create("http://www.easyrec.org/plugins/mahoutslopeone");

    private static final Log logger = LogFactory.getLog(MahoutSlopeoneGenerator.class);

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

    public MahoutSlopeoneGenerator() {
        // we need to call the constructor of GeneratorPluginSupport to provide the name, id and version
        //additionally, we have to pass the class objects of config and stats classes.
        super(DISPLAY_NAME, ID, VERSION, MahoutSlopeoneGeneratorConfig.class, MahoutSlopeoneGeneratorStats.class);
    }

    // ------------------------ INTERFACE METHODS ------------------------

    @Override
    public String getPluginDescription() {
        return "This is a sample generator that crates random recommendations for each item found. It just takes one item and creates a random list of recommendations." +
                "The number of recommendations can be defined using the easyrec admin tool.";
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected void doCleanup() throws Exception {
        logger.info("The plugin is now being uninstalled.");
        // remove all tables/files/resources you created in {@link #doInitialize()}.
        // optional - you don't have to implement this method
    }

    @Override
    protected void doExecute(ExecutionControl executionControl, MahoutSlopeoneGeneratorStats stats) throws Exception {
        // when doExecute() is called, the generator has been initialized with the configuration we should use

        Date execution = new Date();

        MahoutSlopeoneGeneratorConfig config = getConfiguration();

        TypeMappingService typeMappingService = (TypeMappingService) super.getTypeMappingService();
        ItemAssocService itemAssocService = getItemAssocService();

        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(config.getTenantId(), typeMappingService.getIdOfActionType(config.getTenantId(), config.getActionType()), true, mahoutDataModelMappingDAO);

        Recommender recommender = new SlopeOneRecommender(easyrecDataModel);

        itemTypeDAO.insertOrUpdate(config.getTenantId(), "USER", false);

        Integer assocType = typeMappingService.getIdOfAssocType(config.getTenantId(), config.getAssociationType());
        Integer userType = typeMappingService.getIdOfItemType(config.getTenantId(), "USER");
        Integer sourceType = typeMappingService.getIdOfSourceType(config.getTenantId(), getSourceType());
        Integer viewType = typeMappingService.getIdOfViewType(config.getTenantId(), config.getViewType());

        stats.setNumberOfItems(easyrecDataModel.getNumItems());

        for (LongPrimitiveIterator it = easyrecDataModel.getUserIDs(); it.hasNext(); ) {
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
                        "Mahout Slopeone Generator", viewType, null, execution);

                itemAssocService.insertOrUpdateItemAssoc(itemAssoc);
                stats.incNumberOfRulesCreated();
            }
        }

    }

}
