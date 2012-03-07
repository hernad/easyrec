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

package org.easyrec.service.web.impl;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.plugin.NamedConfiguration;
import org.easyrec.model.web.EasyRecSettings;
import org.easyrec.model.web.Item;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.model.web.Session;
import org.easyrec.plugin.configuration.GeneratorContainer;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.service.web.NamedConfigurationService;
import org.easyrec.service.web.RemoteTenantService;
import org.easyrec.service.web.nodomain.ShopRecommenderService;
import org.easyrec.store.dao.core.types.SourceTypeDAO;
import org.easyrec.store.dao.plugin.NamedConfigurationDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * @author patrick
 */
public class NamedConfigurationServiceImpl implements NamedConfigurationService {

    private static Log logger = LogFactory.getLog(NamedConfigurationServiceImpl.class);

    private TypeMappingService typeMappingService;
    private SourceTypeDAO sourceTypeDAO;
    private PluginRegistry pluginRegistry;
    private NamedConfigurationDAO namedConfigurationDAO;
    private EasyRecSettings easyrecSettings;
    private RemoteTenantDAO remoteTenantDAO;
    private RemoteTenantService remoteTenantService;
    private ShopRecommenderService shopRecommenderService;
    private GeneratorContainer generatorContainer;

    public NamedConfigurationServiceImpl(TypeMappingService typeMappingService, SourceTypeDAO sourceTypeDAO,
                                         PluginRegistry pluginRegistry, NamedConfigurationDAO namedConfigurationDAO,
                                         EasyRecSettings easyrecSettings, RemoteTenantDAO remoteTenantDAO,
                                         RemoteTenantService remoteTenantService,
                                         ShopRecommenderService shopRecommenderService,
                                         GeneratorContainer generatorContainer) {
        this.typeMappingService = typeMappingService;
        this.sourceTypeDAO = sourceTypeDAO;
        this.pluginRegistry = pluginRegistry;
        this.namedConfigurationDAO = namedConfigurationDAO;
        this.easyrecSettings = easyrecSettings;
        this.remoteTenantDAO = remoteTenantDAO;
        this.remoteTenantService = remoteTenantService;
        this.shopRecommenderService = shopRecommenderService;
        this.generatorContainer = generatorContainer;
    }

    private void makeARMConfiguration(Generator<GeneratorConfiguration, GeneratorStatistics> generator, int tenantId,
                                      String assocType, int assocTypeId, String actionType) {
        GeneratorConfiguration generatorConfiguration = generator.newConfiguration();
        generatorConfiguration.setAssociationType(assocType);

        try {
            Field actionTypeField = generatorConfiguration.getClass().getDeclaredField("actionType");
            actionTypeField.setAccessible(true);
            actionTypeField.set(generatorConfiguration, actionType);
        } catch (Exception e) {
            logger.warn("Failed to set action type on ARM config", e);
        }

        NamedConfiguration namedConfiguration = new NamedConfiguration(tenantId, assocTypeId,
                new PluginId("http://www.easyrec.org/plugins/ARM", easyrecSettings.getVersion()),
                "Default Configuration", generatorConfiguration, true);

        namedConfigurationDAO.createConfiguration(namedConfiguration);
    }


    public void setupDefaultTenant(int tenantId, String ip) {
        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);

        shopRecommenderService.viewItem(remoteTenant, "A", "42", Item.DEFAULT_STRING_ITEM_TYPE,
                "Fatboy Slim - The Rockafeller Skank", "/item/fatboyslim", "/img/covers/fatboyslim.jpg", new Date(),
                new Session("init", ip));
        shopRecommenderService.viewItem(remoteTenant, "B", "42", Item.DEFAULT_STRING_ITEM_TYPE,
                "Fatboy Slim - The Rockafeller Skank", "/item/fatboyslim", "/img/covers/fatboyslim.jpg", new Date(),
                new Session("init", ip));
        shopRecommenderService.viewItem(remoteTenant, "A", "43", Item.DEFAULT_STRING_ITEM_TYPE,
                "Beastie Boys - Intergalactic", "/item/beastieboyz", "/img/covers/beastieboys.jpg", new Date(),
                new Session("init", ip));
        shopRecommenderService.viewItem(remoteTenant, "B", "43", Item.DEFAULT_STRING_ITEM_TYPE,
                "Beastie Boys - Intergalactic", "/item/beastieboyz", "/img/covers/beastieboys.jpg", new Date(),
                new Session("init", ip));
        shopRecommenderService.viewItem(remoteTenant, "A", "44", Item.DEFAULT_STRING_ITEM_TYPE,
                "Gorillaz - Clint Eastwood", "/item/gorillaz", "/img/covers/gorillaz.jpg", new Date(),
                new Session("init", ip));
        shopRecommenderService.viewItem(remoteTenant, "B", "44", Item.DEFAULT_STRING_ITEM_TYPE,
                "Gorillaz - Clint Eastwood", "/item/gorillaz", "/img/covers/gorillaz.jpg", new Date(),
                new Session("init", ip));

        setupDefaultConfiguration(tenantId);
    }

    public void setupDefaultConfiguration(int tenantId) {
        PluginId armPluginId = new PluginId("http://www.easyrec.org/plugins/ARM", easyrecSettings.getVersion());
        Generator<GeneratorConfiguration, GeneratorStatistics> generator =
                pluginRegistry.getGenerators().get(armPluginId);

        sourceTypeDAO.insertOrUpdate(tenantId, armPluginId.toString());

        makeARMConfiguration(generator, tenantId, "VIEWED_TOGETHER", 1, "VIEW");
        makeARMConfiguration(generator, tenantId, "GOOD_RATED_TOGETHER", 2, "RATE");
        makeARMConfiguration(generator, tenantId, "BOUGHT_TOGETHER", 3, "BUY");

        PluginId slopeOnePluginId =
                new PluginId("http://www.easyrec.org/plugins/slopeone", easyrecSettings.getVersion());
        sourceTypeDAO.insertOrUpdate(tenantId, slopeOnePluginId.toString());
        int isRelatedAssocTypeId = typeMappingService.getIdOfAssocType(tenantId, "IS_RELATED");

        createDefaultConfiguration(slopeOnePluginId, tenantId, isRelatedAssocTypeId);

        generatorContainer.runGeneratorsForTenant(tenantId);

        remoteTenantService.updateTenantStatistics(tenantId);
    }

    public NamedConfiguration createDefaultConfiguration(PluginId pluginId, int tenantId, int assocTypeId) {
        Preconditions.checkNotNull(pluginId);

        Generator<GeneratorConfiguration, GeneratorStatistics> generator = pluginRegistry.getGenerators().get(pluginId);
        String assocType = typeMappingService.getAssocTypeById(tenantId, assocTypeId);

        if (generator == null)
            throw new IllegalArgumentException(String.format("Could not find generator with id \"%s\"", pluginId));

        if (assocType == null)
            throw new IllegalArgumentException(
                    String.format("Could not find association type for tenant %d with id %d", tenantId, assocTypeId));

        Integer sourceType = sourceTypeDAO.getIdOfType(tenantId, pluginId.toString());

        if (sourceType == null) {
            int rowsModified = sourceTypeDAO.insertOrUpdate(tenantId, pluginId.toString());

            if (rowsModified == 0)
                throw new RuntimeException(
                        String.format("Could not generate sourceType \"%s\" for tenant %d.", pluginId.toString(),
                                tenantId));
        }

        GeneratorConfiguration defaultConfiguration = generator.newConfiguration();
        defaultConfiguration.setAssociationType(assocType);

        // when there is no active configuration for <tenant, assocType> then set the newly created
        // configuration as the active one
        boolean notExistsActive = namedConfigurationDAO.readActiveConfiguration(tenantId, assocTypeId) == null;

        NamedConfiguration namedConfiguration = new NamedConfiguration(tenantId, assocTypeId, generator.getId(),
                defaultConfiguration.getConfigurationName(), defaultConfiguration, notExistsActive);


        int rowsModified = namedConfigurationDAO.createConfiguration(namedConfiguration);

        if (rowsModified == 0) {
            logger.error("could not store named configuration");
            return null;
        }

        return namedConfiguration;
    }
}
