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

package org.easyrec.plugin.slopeone;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.TenantVO;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.slopeone.model.*;
import org.easyrec.plugin.slopeone.store.dao.ActionDAO;
import org.easyrec.plugin.slopeone.store.dao.DeviationDAO;
import org.easyrec.plugin.slopeone.store.dao.LogEntryDAO;
import org.easyrec.plugin.support.GeneratorPluginSupport;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.domain.TypeMappingService;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the Slope One algorithm [1] as an easyrec-Generator.
 * <p/>
 * Utilizes {@link org.easyrec.plugin.slopeone.SlopeOneService}.
 * <p/>
 * [1] Lemire and Maclachlan 2005. Slope One Predictors for Online Rating-Base Collaborative Filtering. In SIAM Data
 * Mining (SDM'05), Newport Beach, California, April 21-23, 2005.
 */
public class SlopeOneGenerator extends GeneratorPluginSupport<SlopeOneConfiguration, SlopeOneStats> {
    public static final String DISPLAY_NAME = "SlopeOne";
    public static final Version VERSION = new Version("0.97");
    public static final URI ID = URI.create("http://www.easyrec.org/plugins/slopeone");

    private static final Log logger = LogFactory.getLog(SlopeOneGenerator.class);

    private SlopeOneService slopeOneService;
    private TenantService tenantService;
    private ActionDAO actionDAO;
    private DeviationDAO deviationDAO;
    private LogEntryDAO logEntryDAO;
    private TypeMappingService typeMappingService;

    public SlopeOneGenerator() {
        super(DISPLAY_NAME, ID, VERSION, SlopeOneConfiguration.class, SlopeOneStats.class);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setDeviationDAO(final DeviationDAO deviationDAO) { this.deviationDAO = deviationDAO; }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setLogEntryDAO(final LogEntryDAO logEntryDAO) { this.logEntryDAO = logEntryDAO; }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setSlopeOneService(SlopeOneService slopeOneService) {
        this.slopeOneService = slopeOneService;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setTypeMappingService(final TypeMappingService typeMappingService) {
        this.typeMappingService = typeMappingService;
    }

    @Override
    public String getPluginDescription() {
        return "This plugin generates item relations based on the Slope One method. It analyzes item ratings and " +
                "tries to predict how yet unrated items would be rated by the community";
    }

    @Override
    public SlopeOneConfiguration newConfiguration() { return new SlopeOneConfiguration(); }

    @Override
    protected void doInstall() throws Exception {
        // tables are only created if they don't exist
        deviationDAO.createTable();
        actionDAO.createTable();
        logEntryDAO.createTable();
    }

    @Override
    protected void doUninstall() throws Exception {
        deviationDAO.dropTable();
        actionDAO.dropTable();
        logEntryDAO.dropTable();
    }

    @Override
    protected void doCleanup() throws Exception {}

    @Override
    protected void doExecute(final ExecutionControl control, SlopeOneStats stats) throws Exception {
        control.updateProgress(0, 4, "Started");

        SlopeOneConfiguration configuration = getConfiguration();
        TenantVO tenant = tenantService.getTenantById(configuration.getTenantId());

        LogEntry lastRun = logEntryDAO.getLatestLogEntry(tenant.getId());
        Date execution = new Date();
        Set<TenantItem> changedItemIds = Sets.newHashSet();

        stats.setStartDate(execution);

        int tenantId = tenant.getId();

        List<String> stringItemTypes = configuration.getItemTypes();

        if (stringItemTypes.isEmpty())
            stringItemTypes = Lists.newArrayList(typeMappingService.getItemTypes(tenantId, true));

        TIntSet itemTypes = new TIntHashSet(stringItemTypes.size());

        for (String stringItemType : stringItemTypes) {
            Integer objItemTypeId = typeMappingService.getIdOfItemType(tenantId, stringItemType);
            objItemTypeId = Preconditions.checkNotNull(objItemTypeId, "configuration value 'itemType'=%stats is " +
                    "invalid.", stringItemType);

            itemTypes.add(objItemTypeId);
        }

        Integer objActionTypeId = typeMappingService.getIdOfActionType(tenantId, configuration.getActionType());
        objActionTypeId = Preconditions.checkNotNull(objActionTypeId, "configuration value 'actionType'=%s is invalid.",
                configuration.getActionType());

        Integer objViewTypeId = typeMappingService.getIdOfViewType(tenantId, configuration.getViewType());
        objViewTypeId = Preconditions.checkNotNull(objViewTypeId, "configuration value 'viewType'=%s is invalid.",
                configuration.getViewType());

        Integer objAssocTypeId =
                typeMappingService.getIdOfAssocType(tenantId, configuration.getAssociationType());
        objAssocTypeId = Preconditions.checkNotNull(objAssocTypeId, "configuration value 'assocType'=%s is invalid.",
                configuration.getAssociationType());

        Integer objSourceTypeId = typeMappingService.getIdOfSourceType(tenantId, getId().toString());
        objSourceTypeId = Preconditions.checkNotNull(objSourceTypeId, "configuration value 'sourceType'=%s is invalid.",
                getId().toString());

        SlopeOneIntegerConfiguration integerConfiguration = new SlopeOneIntegerConfiguration(
                configuration.getMaxRecsPerItem(), configuration.getMinRatedCount(),
                configuration.getNonPersonalizedSourceInfo(), objActionTypeId, itemTypes, objViewTypeId,
                objAssocTypeId, objSourceTypeId, tenantId);

        LogEntry logEntry = new LogEntry(tenantId, execution, configuration, stats);

        try {
            if (control.isAbortRequested()) return;

            deviationDAO.starting();

            control.updateProgress(1, "Generating actions");

            slopeOneService.generateActions(integerConfiguration, lastRun, stats);

            if (logger.isInfoEnabled())
                logger.info(String.format("Generated actions in %dms", stats.getActionDuration()));

            if (control.isAbortRequested()) return;

            control.updateProgress(2, "Calculating deviations");

            slopeOneService.calculateDeviations(integerConfiguration, lastRun.getExecution(), stats, changedItemIds,
                    control);

            if (logger.isInfoEnabled()) {
                logger.info(String.format("Calculated deviations in %dms", stats.getDeviationDuration()));
                logger.info(String.format("  for %d new ratings", stats.getNumberOfActionsConsidered()));
                logger.info(String.format("  for %d new users", stats.getNoUsers()));
                logger.info(String.format("  added %d deviations", stats.getNoCreatedDeviations()));
                logger.info(String.format("  modified %d deviations", stats.getNoModifiedDeviations()));
            }

            if (control.isAbortRequested()) return;

            control.updateProgress(3, "Calculating non-personalized recommendations");

            // always generate all item assocs because we have to delete the old assoc values
            changedItemIds = deviationDAO.getItemIds(tenant.getId(), itemTypes);

            slopeOneService.nonPersonalizedRecommendations(integerConfiguration, stats, execution, changedItemIds,
                    control);

            if (logger.isInfoEnabled())
                logger.info(String.format("Calculated non-personalized recommendations in %dms",
                        stats.getNonPersonalizedDuration()));

            control.updateProgress(4, "Finishing ...");

            TIntIterator iterator = itemTypes.iterator();

            while (iterator.hasNext())
                deviationDAO.finished(tenantId, iterator.next());
        } catch (Exception e) {
            stats.setException(e);

            control.updateProgress(4, "Finishing with error ...");

            throw e;
        } finally {
            stats.setEndDateToNow();

            logEntryDAO.insertLogEntry(logEntry);
        }
    }

    @Override
    protected void doInitialize() throws Exception {
        tenantService = getTenantService();
        typeMappingService = (TypeMappingService) getTypeMappingService();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setSlopeOneActionDAO(final ActionDAO actionDAO) { this.actionDAO = actionDAO; }
}
