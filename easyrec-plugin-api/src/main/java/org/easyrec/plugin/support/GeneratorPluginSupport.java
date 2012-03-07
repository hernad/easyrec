package org.easyrec.plugin.support;

import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.service.core.ItemAssocService;
import org.easyrec.service.core.TenantService;
import org.easyrec.store.dao.core.ItemAssocDAO;

import javax.sql.DataSource;
import java.net.URI;

public abstract class GeneratorPluginSupport<C extends GeneratorConfiguration, S extends GeneratorStatistics>
        extends ExecutablePluginSupport<S> implements Generator<C, S> {

    private C configuration;

    private DataSource satRecommenderDS;
    private ItemAssocDAO itemAssocDAO;
    private TenantService tenantService;
    private Object typeMappingService;
    private ItemAssocService itemAssocService;
    private Class<C> configClass;

    public GeneratorPluginSupport(String displayName, URI id, Version version, Class<C> configClass,
                                  Class<S> statsClass) {
        super(displayName, id, version, statsClass);
        this.configClass = configClass;
    }

    /**
     * @deprecated  use #getId().toString() instead
     * @return
     */
    @Deprecated
    public String getSourceType() {
        return getId().toString();
    }

    public C getConfiguration() {
        return configuration;
    }

    public Class<C> getConfigurationClass() {
        return this.configClass;
    }


    public C newConfiguration() {
        try {
            return this.configClass.newInstance();
        } catch (Exception e) {
            logger.warn("could not instantiate instance of " + this.configClass.getName());
            throw new IllegalStateException(e);
        }
    }

    public void setConfiguration(C configuration) {
        this.configuration = configuration;
    }

    public ItemAssocDAO getItemAssocDAO() {
        return itemAssocDAO;
    }

    public void setItemAssocDAO(ItemAssocDAO itemAssocDAO) {
        this.itemAssocDAO = itemAssocDAO;
    }

    public DataSource getSatRecommenderDS() {
        return satRecommenderDS;
    }

    public void setSatRecommenderDS(DataSource satRecommenderDS) {
        this.satRecommenderDS = satRecommenderDS;
    }

    public TenantService getTenantService() {
        return tenantService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public Object getTypeMappingService() {
        return typeMappingService;
    }

    public void setTypeMappingService(final Object typeMappingService) {
        this.typeMappingService = typeMappingService;
    }

    public ItemAssocService getItemAssocService() {
        return itemAssocService;
    }

    public void setItemAssocService(final ItemAssocService itemAssocService) {
        this.itemAssocService = itemAssocService;
    }


}
