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
package org.easyrec.plugin.container;

import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.TenantVO;
import org.easyrec.model.plugin.PluginVO;
import org.easyrec.plugin.Executable.ExecutionState;
import org.easyrec.plugin.Plugin.LifecyclePhase;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.plugin.support.GeneratorPluginSupport;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.store.dao.core.ItemAssocDAO;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.store.dao.plugin.NamedConfigurationDAO;
import org.easyrec.store.dao.plugin.PluginDAO;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author szavrel
 */
public class PluginRegistry implements ApplicationContextAware {

    public static final String DEFAULT_PLUGIN_CONFIG_FILE = "easyrec-plugin.xml";
    public static final String GENERATOR_PROP = "generator";
    public static final String PLUGINS_ENABLED_PROP = "plugins.enabled";


    private final Log logger = LogFactory.getLog(this.getClass());

    private ApplicationContext appContext;
    private Resource pluginFolder;
    private PluginDAO pluginDAO;
    private ItemAssocDAO itemAssocDAO;
    private TenantService tenantService;
    private TypeMappingService typeMappingService;
    private Properties properties;
    private Resource overrideFolder;
    private NamedConfigurationDAO namedConfigurationDAO;

    private Map<PluginId, Generator<GeneratorConfiguration, GeneratorStatistics>> generators;
    private Map<PluginId, ClassPathXmlApplicationContext> contexts = Maps.newHashMap();

    public PluginRegistry(Resource pluginFolder, PluginDAO pluginDAO, ItemAssocDAO itemAssocDAO,
                          TenantService tenantService, TypeMappingService typeMappingService,
                          Map<PluginId, Generator<GeneratorConfiguration, GeneratorStatistics>> generators) {
        this.pluginFolder = pluginFolder;
        this.pluginDAO = pluginDAO;
        this.itemAssocDAO = itemAssocDAO;
        this.tenantService = tenantService;
        this.typeMappingService = typeMappingService;
        this.generators = generators;
    }

    public void init() throws Exception {
        logger.info("Loading plugins ...");
        if (properties != null && "true".equals(properties.getProperty("easyrec.firstrun"))) {
            try {
                installDefaultOnFirstRun();
            } catch (Exception e) {
                logger.error("An error occured trying to install the default plugins! Try installing plugin manually!",
                        e);
            }
        } else {
            List<PluginVO> installedPlugins = this.pluginDAO.loadPluginInfos(LifecyclePhase.INITIALIZED.toString());
            if (installedPlugins != null && installedPlugins.size() > 0) {
                for (PluginVO plugin : installedPlugins) {
                    logger.info("Loading plugin " + plugin.getPluginId() + " - " + plugin.getPluginId().getVersion());
                    installPlugin(plugin.getPluginId().getUri(), plugin.getPluginId().getVersion());
                }
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    public void installPlugin(URI pluginId, Version version) {
        FileOutputStream fos = null;
        File tmpFile = null;

        try {
            PluginVO plugin = pluginDAO.loadPlugin(pluginId, version);
            if (plugin == null) throw new Exception("Plugin not found in DB!");
            // write file to plugin folder
            tmpFile = new File(pluginFolder.getFile(),
                    plugin.getId().toString() + "_" + plugin.getDisplayName() + ".jar");
            fos = new FileOutputStream(tmpFile);
            fos.write(plugin.getFile());
            fos.close();

            // install the plugin

            PluginClassLoader ucl = new PluginClassLoader(new URL[]{tmpFile.toURI().toURL()},
                    this.getClass().getClassLoader());

            if (ucl.findResource(DEFAULT_PLUGIN_CONFIG_FILE) == null) {
                logger.warn("no " + DEFAULT_PLUGIN_CONFIG_FILE + " found in plugin jar ");
                return;
            }

            ClassPathXmlApplicationContext cax =
                    new ClassPathXmlApplicationContext(new String[]{DEFAULT_PLUGIN_CONFIG_FILE}, false, appContext);
            cax.setClassLoader(ucl);
            cax.refresh();
//            cax.stop();
//            cax.start();

            // currently only GeneratorPluginSupport is used
            Map<String, GeneratorPluginSupport> beans = cax.getBeansOfType(GeneratorPluginSupport.class);

            if (beans.isEmpty()) {
                logger.warn("no GeneratorPluginSupport subclasses found in plugin jar");
                return;
            }

            Generator<GeneratorConfiguration, GeneratorStatistics> generator = beans.values().iterator().next();

            installGenerator(pluginId, version, plugin, cax, generator);
        } catch (Exception e) {
            logger.error("An Exception occurred while installing the plugin!", e);

            pluginDAO.updatePluginState(pluginId, version, LifecyclePhase.INSTALL_FAILED.toString());
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (Exception ignored) {
                    logger.warn("could not close file output stream", ignored);
                }

            /*
            if (tmpFile != null)
                try {
                    tmpFile.delete();
                } catch (Exception ignored) {
                    logger.warn("could not delete temporary plugin file", ignored);
                }
            */
        }
    }

    private void installGenerator(final URI pluginId, final Version version, final PluginVO plugin,
                                  final ClassPathXmlApplicationContext cax,
                                  final Generator<GeneratorConfiguration, GeneratorStatistics> generator) {
        cax.getAutowireCapableBeanFactory()
                .autowireBeanProperties(generator, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);

        if (generator.getConfiguration() == null) {
            GeneratorConfiguration generatorConfiguration = generator.newConfiguration();
            generator.setConfiguration(generatorConfiguration);
        }

        if (LifecyclePhase.NOT_INSTALLED.toString().equals(plugin.getState()))
            generator.install(true);
        else
            generator.install(false);

        pluginDAO.updatePluginState(pluginId, version, LifecyclePhase.INSTALLED.toString());

        generator.initialize();
        generators.put(generator.getId(), generator);
        contexts.put(generator.getId(), cax);
        logger.info("registered plugin " + generator.getSourceType());
        pluginDAO.updatePluginState(pluginId, version, LifecyclePhase.INITIALIZED.toString());
    }

    @SuppressWarnings({"unchecked"})
    public PluginVO checkPlugin(byte[] file) throws Exception {
        PluginVO plugin;
        FileOutputStream fos = null;
        URLClassLoader ucl;
        ClassPathXmlApplicationContext cax = null;
        File tmpFile = null;

        try {
            if (file == null) throw new IllegalArgumentException("Passed file must not be null!");

            tmpFile = File.createTempFile("plugin", null);
            tmpFile.deleteOnExit();

            fos = new FileOutputStream(tmpFile);
            fos.write(file);
            fos.close();

            // check if plugin is valid
            ucl = new URLClassLoader(new URL[]{tmpFile.toURI().toURL()}, this.getClass().getClassLoader());

            if (ucl.getResourceAsStream(DEFAULT_PLUGIN_CONFIG_FILE) != null) {
                cax = new ClassPathXmlApplicationContext(new String[]{DEFAULT_PLUGIN_CONFIG_FILE}, false, appContext);
                cax.setClassLoader(ucl);
                logger.info("Classloader: " + cax.getClassLoader());
                cax.refresh();

                Map<String, GeneratorPluginSupport> beans = cax.getBeansOfType(GeneratorPluginSupport.class);

                if (beans.isEmpty()) {
                    logger.debug("No class implementing a generator could be found. Plugin rejected!");
                    throw new Exception("No class implementing a generator could be found. Plugin rejected!");
                }

                Generator<GeneratorConfiguration, GeneratorStatistics> generator = beans.values().iterator().next();

                logger.info(String.format("Plugin successfully validated! class: %s name: %s, id: %s",
                        generator.getClass(), generator.getDisplayName(), generator.getId()));

                cax.getAutowireCapableBeanFactory()
                        .autowireBeanProperties(generator, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);

                plugin = new PluginVO(generator.getDisplayName(), generator.getId().getUri(),
                        generator.getId().getVersion(), LifecyclePhase.NOT_INSTALLED.toString(), file, null);

                if (tmpFile.delete())
                    logger.info("tmpFile deleted successfully");

                return plugin;
            } else { // config file not found
                logger.debug("No valid config file found in the supplied .jar file. Plugin rejected!");
                throw new Exception("No valid config file found in the supplied .jar file. Plugin rejected!");
            }
        } catch (Exception e) {
            logger.error("An Exception occurred while checking the plugin!", e);

            throw e;
        } finally {
            if (fos != null)
                fos.close();

            if ((cax != null) && (!cax.isActive()))
                cax.close();

            if (tmpFile != null)
                try {
                    if (!tmpFile.delete()) logger.warn("could not delete tmpFile");
                } catch (SecurityException se) {
                    logger.error("Could not delete temporary file! Please check permissions!" + se);
                }
        }
    }

    public void deactivatePlugin(URI pluginId, Version version) {
        PluginId key = new PluginId(pluginId, version);
        Generator<GeneratorConfiguration, GeneratorStatistics> generator = generators.get(key);

        if ((generator != null) && (LifecyclePhase.INITIALIZED.equals(generator.getLifecyclePhase()))) {
            String sourceType = generator.getSourceType();

            generator.cleanup();
            pluginDAO.updatePluginState(pluginId, version, LifecyclePhase.INSTALLED.toString());
            generators.remove(key);

            ClassPathXmlApplicationContext cax = contexts.get(key);

            generator.uninstall();
            pluginDAO.updatePluginState(pluginId, version, LifecyclePhase.NOT_INSTALLED.toString());

            if (cax != null) cax.close();

            contexts.remove(key);

            if (logger.isDebugEnabled()) logger.debug("Deactivating configurations for " + key.getUri() + "-" + key.getVersion() );

            int deactivates = namedConfigurationDAO.deactivateByPlugin(key);

            if (logger.isDebugEnabled()) logger.debug("Deactivated " + deactivates+ " plugins");

            List<TenantVO> tenants = tenantService.getAllTenants();

            for (TenantVO tenant : tenants) {
                Integer sourceTypeId;

                try {
                    sourceTypeId = typeMappingService.getIdOfSourceType(tenant.getId(), sourceType);
                } catch (IllegalArgumentException iae) {
                    logger.info(String.format("Source type %s not defined for tenant %d", sourceType, tenant.getId()));
                    continue;
                }

                int removedRows = itemAssocDAO.removeItemAssocByTenant(tenant.getId(), null, sourceTypeId, null);

                logger.info(String.format(
                        "Removed %d item assocs of source type %d for tenant %d because plugin is deactivating.",
                        removedRows, sourceTypeId, tenant.getId()));
            }
        }
    }

    public String getPluginDescription(URI pluginId, Version version) {
        PluginId key = new PluginId(pluginId, version);
        Generator<GeneratorConfiguration, GeneratorStatistics> generator = generators.get(key);

        if (generator != null)
            return generator.getPluginDescription();

        return "";
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void deletePlugin(URI pluginId, Version version) {
        PluginId key = new PluginId(pluginId, version);
        Generator<GeneratorConfiguration, GeneratorStatistics> generator = generators.get(key);

        if ((generator != null) && (LifecyclePhase.INITIALIZED.equals(generator.getLifecyclePhase())))
            deactivatePlugin(pluginId, version);

        pluginDAO.deletePlugin(pluginId, version);
    }

    public boolean isAllExecutablesStopped() {
        for (Generator<GeneratorConfiguration, GeneratorStatistics> executable : this.generators.values()) {
            ExecutionState state = executable.getExecutionState();

            if (state.isRunning() || state.isAbortRequested()) return false;
        }

        return true;
    }

    public void installDefaultOnFirstRun() throws Exception {
        if (properties != null && "true".equals(properties.getProperty("easyrec.firstrun"))) {
            properties.setProperty("easyrec.firstrun", "false");
            File of = new File(overrideFolder.getFile(), "easyrec.database.properties");
            properties.store(new FileOutputStream(of), "");

            logger.info("First run after install... installing/updating default plugins!");
            HashMap<URI, Version> installedPlugins = new HashMap<URI, Version>();
            for (PluginVO plugin : pluginDAO.loadPluginInfos()) {
                installedPlugins.put(plugin.getPluginId().getUri(), plugin.getPluginId().getVersion());
            }
            File[] files = new File[0];

            if (pluginFolder.exists())
                files = pluginFolder.getFile().listFiles(new FilenameFilter() {

                @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jar");
                    }
                });

            for (File file : files) {
                byte[] pluginContent = IOUtils.toByteArray(new FileInputStream(file));
                PluginVO defaultPlugin = checkPlugin(pluginContent);

                if (defaultPlugin != null) {
                    // if an older version of a default plugin exists, delete it
                    if (installedPlugins.containsKey(defaultPlugin.getPluginId().getUri())) {
                        if (installedPlugins.get(defaultPlugin.getPluginId().getUri())
                                .compareTo(defaultPlugin.getPluginId().getVersion()) < 0) {
                            pluginDAO.deletePlugin(defaultPlugin.getPluginId().getUri(),
                                    installedPlugins.get(defaultPlugin.getPluginId().getUri()));
                        }
                    }

                    pluginDAO.storePlugin(defaultPlugin);
                    installPlugin(defaultPlugin.getPluginId().getUri(), defaultPlugin.getPluginId().getVersion());
                }
            }
        }

        // check if assocType "IS_RELATED" exists for all tenants, if not add it
        List<TenantVO> tenants = tenantService.getAllTenants();
        for (TenantVO tenantVO : tenants) {
            tenantService.insertAssocTypeForTenant(tenantVO.getId(), AssocTypeDAO.ASSOCTYPE_IS_RELATED, true);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }

    public void setPluginFolder(Resource pluginFolder) { this.pluginFolder = pluginFolder; }

    public Map<PluginId, Generator<GeneratorConfiguration, GeneratorStatistics>> getGenerators() { return generators; }

    public void setProperties(Properties properties) { this.properties = properties; }

    public void setOverrideFolder(Resource overrideFolder) { this.overrideFolder = overrideFolder; }

    public NamedConfigurationDAO getNamedConfigurationDAO() {
        return namedConfigurationDAO;
    }

    public void setNamedConfigurationDAO(NamedConfigurationDAO namedConfigurationDAO) {
        this.namedConfigurationDAO = namedConfigurationDAO;
    }
}
