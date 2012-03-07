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

package org.easyrec.plugin.itemitem;

import org.easyrec.model.core.TenantVO;
import org.easyrec.plugin.Progress;
import org.easyrec.plugin.itemitem.model.ItemItemConfiguration;
import org.easyrec.plugin.itemitem.model.PredictionComputationType;
import org.easyrec.plugin.itemitem.model.SimilarityCalculationType;
import org.easyrec.plugin.itemitem.store.dao.ActionDAO;
import org.easyrec.plugin.itemitem.store.dao.UserAssocDAO;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.plugin.support.GeneratorPluginSupport;
import org.easyrec.service.core.ItemAssocService;
import org.easyrec.service.domain.TypeMappingService;

import java.net.URI;
import java.util.Date;
import java.util.Map;

/**
 * Implementation of the Item-Item algorithm [Sarwar et al, 2001] as an easyrec-Generator. <p/> Utilizes {@link
 * org.easyrec.plugin.itemitem.ItemItemService}. <p/> [Sarwar et al, 2001] Item-based collaborative filtering recommendation
 * algorithms. In SIAM Data Mining (WWW'01), New York, NY, USA, 2001. <p><b>Company:&nbsp;</b> SAT, Research Studios
 * Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/>
 * $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class ItemItemGenerator extends GeneratorPluginSupport<ItemItemConfiguration, GeneratorStatistics> {
    // ------------------------------ FIELDS ------------------------------

    public static final String DISPLAY_NAME = "Item-Item";
    public static final Version VERSION = new Version("0.97");
    public static final URI ID = URI.create("http://www.easyrec.org/plugins/itemitem");

    private Map<PredictionComputationType, PredictionComputationStrategy> predictionComputationTypes;
    private Map<SimilarityCalculationType, SimilarityCalculationStrategy> similarityCalculationTypes;
    private ItemItemService itemItemService;
    private ActionDAO actionDAO;
    private ItemAssocService itemAssocService;
    private UserAssocDAO userAssocDAO;

    // --------------------------- CONSTRUCTORS ---------------------------

    public ItemItemGenerator() {
        super(DISPLAY_NAME, ID, VERSION, ItemItemConfiguration.class, GeneratorStatistics.class);
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public void setItemAssocService(final ItemAssocService itemAssocService) {
        this.itemAssocService = itemAssocService;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setItemItemService(final ItemItemService itemItemService) { this.itemItemService = itemItemService; }

    @SuppressWarnings("UnusedDeclaration")
    public void setPredictionComputationTypes(
            final Map<PredictionComputationType, PredictionComputationStrategy> predictionComputationTypes) {
        this.predictionComputationTypes = predictionComputationTypes;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setSimilarityCalculationTypes(
            final Map<SimilarityCalculationType, SimilarityCalculationStrategy> similarityCalculationTypes) {
        this.similarityCalculationTypes = similarityCalculationTypes;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUserAssocDAO(final UserAssocDAO userAssocDAO) { this.userAssocDAO = userAssocDAO; }

    // ------------------------ INTERFACE METHODS ------------------------

    @Override
    public String getPluginDescription() {
        return "This generator creates item relations based on the Pearson method.";
    }

    // --------------------- Interface Configurable ---------------------

    @Override
    public ItemItemConfiguration newConfiguration() {
        return new ItemItemConfiguration();
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected void doInstall() throws Exception {
        actionDAO.createTable();
        userAssocDAO.createTable();
    }

    @Override
    protected void doUninstall() throws Exception {
        actionDAO.dropTable();
        userAssocDAO.dropTable();
    }

    @Override
    protected void doExecute(final ExecutionControl control, GeneratorStatistics stats) throws Exception {
        TypeMappingService typeMappingService = (TypeMappingService) getTypeMappingService();
        ItemItemConfiguration configuration = getConfiguration();

        final Integer tenantId = configuration.getTenantId();
        final Integer actionTypeId = typeMappingService.getIdOfActionType(tenantId, configuration.getActionType());
        final Integer itemTypeId = typeMappingService.getIdOfItemType(tenantId, configuration.getItemType());
        final Integer assocTypeId = typeMappingService.getIdOfAssocType(tenantId, configuration.getAssociationType());
        final Integer viewTypeId = typeMappingService.getIdOfViewType(tenantId, configuration.getViewType());
        final String sourceType = ID + "/" + VERSION;
        final Integer sourceTypeId = typeMappingService.getIdOfSourceType(tenantId, sourceType);
        final Date changeDate = new Date();

        stats.setStartDate(changeDate);

        TenantVO tenant = getTenantService().getTenantById(tenantId);

        SimilarityCalculationStrategy similarityCalculationStrategy = similarityCalculationTypes
                .get(configuration.getSimilarityType());
        similarityCalculationStrategy.setItemAssocService(itemAssocService);
        similarityCalculationStrategy.setActionDAO(actionDAO);

        PredictionComputationStrategy predictionComputationStrategy = predictionComputationTypes
                .get(configuration.getPredictionType());
        predictionComputationStrategy.setActionDAO(actionDAO);
        predictionComputationStrategy.setUserAssocDAO(userAssocDAO);

        itemItemService.setSimilarityCalculationStrategy(similarityCalculationStrategy);
        itemItemService.setPredictionComputationStrategy(predictionComputationStrategy);
        itemItemService.setConfiguration(configuration);

        // generate actions

        if (control.isAbortRequested()) return;

        control.updateProgress(new Progress(1, 4, "Generating actions"));

        int generatedActions = actionDAO.generateActions(tenantId, null);
        stats.setNumberOfActionsConsidered(generatedActions);

        // similarity calculation

        if (control.isAbortRequested()) return;

        control.updateProgress(new Progress(2, 4, "Calculating similarity"));

        itemItemService.calculateSimilarity(tenantId, actionTypeId, itemTypeId, assocTypeId, viewTypeId, sourceTypeId,
                changeDate, stats, control);

        // prediction generation

        if (control.isAbortRequested()) return;

        control.updateProgress(new Progress(3, 4, "Calculating predictions"));

        /* removed for now because user->item associations are not yet enabled

   itemItemService.predict(tenantId, actionTypeId, itemTypeId, assocTypeId, viewTypeId, sourceTypeId, changeDate,
       similarityCalculationStrategy.getSourceInfo(), tenant.getRatingRangeMin(), tenant.getRatingRangeMax(),
       control); */

        control.updateProgress(new Progress(4, 4, "Finished"));
        stats.setEndDateToNow();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setItemItemActionDAO(final ActionDAO actionDAO) { this.actionDAO = actionDAO; }
}
