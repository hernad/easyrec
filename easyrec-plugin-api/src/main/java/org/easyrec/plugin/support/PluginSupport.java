package org.easyrec.plugin.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.plugin.Plugin;
import org.easyrec.plugin.exception.PluginException;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.util.ObserverRegistry;
import org.easyrec.plugin.util.ObserverRegistryImpl;

import java.net.URI;

public abstract class PluginSupport implements Plugin {
    private String displayName;
    private PluginId id;

    private LifecyclePhase lifecyclePhase = LifecyclePhase.NOT_INSTALLED;
    protected final Log logger = LogFactory.getLog(getClass());
    private ObserverRegistryImpl<Plugin> pluginObserverRegistry = new ObserverRegistryImpl<Plugin>(this);

    protected PluginSupport(String displayName, URI id, Version version) {
        this.displayName = displayName;
        this.id = new PluginId(id, version);
    }

    /**
     * Implementations place code here that needs to be executed one time before a
     * plugin is able to be used (i.e set up db tables). Any Exception is wrapped
     * in a PluginException and thrown on.
     *
     * @throws Exception
     */
    protected void doInstall() throws Exception {}

    /**
     * Implementations place code here that needs to be executed to properly
     * uninstall a plugin (i.e. drop db tables).
     * Any Exception is wrapped in a PluginException and thrown on.
     *
     * @throws Exception
     */
    protected void doUninstall() throws Exception {}

    /**
     * Implementations place their initialization code here. Any Exception is
     * wrapped in a PluginException and thrown on.
     *
     * @throws Exception
     */
    protected void doInitialize() throws Exception {}

    /**
     * Implementations place their cleanup code here. Any Exception is wrapped
     * in a PluginException and thrown on.
     *
     * @throws Exception
     */
    protected void doCleanup() throws Exception {}

    public String getDisplayName() {
        return displayName;
    }

    public PluginId getId() {
        return id;
    }

    public final void cleanup() {
        // valid call states: INITIALIZED, INIT_FAILED, CLEANUP_FAILED
        if (!this.lifecyclePhase.isCleanupAllowed()) {
            throw new IllegalStateException("Cleanup not allowed from plugin state " + this.lifecyclePhase);
        }
        try {
            changeLifecyclePhaseTo(LifecyclePhase.CLEANING_UP);
            doCleanup();
            changeLifecyclePhaseTo(LifecyclePhase.INSTALLED);
        } catch (Throwable t) {
            changeLifecyclePhaseTo(LifecyclePhase.CLEANUP_FAILED);
            throw new PluginException(this, "Caught error during doCleanup", t);
        }
    }

    public final void initialize() {
        // valid call states: INSTALLED, INIT_FAILED, CLEANUP_FAILED
        if (!this.lifecyclePhase.isInitializeAllowed()) {
            throw new IllegalStateException("Initialize not allowed from plugin state " + this.lifecyclePhase);
        }
        try {
            changeLifecyclePhaseTo(LifecyclePhase.INITIALIZING);
            doInitialize();
            this.lifecyclePhase = LifecyclePhase.INITIALIZED;
        } catch (Throwable t) {
            changeLifecyclePhaseTo(LifecyclePhase.INIT_FAILED);
            throw new PluginException(this, "Caught error during doInitialize", t);
        }
    }

    public final void install(boolean executeInstall) throws PluginException {
        // valid call states: NOT_INSTALLED, INSTALL_FAILED, UNINSTALL_FAILED
        if (!this.lifecyclePhase.isInstallAllowed()) {
            throw new IllegalStateException("Install not allowed from plugin state " + this.lifecyclePhase);
        }
        try {
            changeLifecyclePhaseTo(LifecyclePhase.INSTALLING);
            if (executeInstall) doInstall();
            this.lifecyclePhase = LifecyclePhase.INSTALLED;
        } catch (Throwable t) {
            changeLifecyclePhaseTo(LifecyclePhase.INSTALL_FAILED);
            throw new PluginException(this, "Caught error during doInstall", t);
        }
    }

    public final void uninstall() throws PluginException {
        // valid call states: INSTALLED, INSTALL_FAILED, UNINSTALL_FAILED
        if (!this.lifecyclePhase.isUninstallAllowed()) {
            throw new IllegalStateException("Uninstall not allowed from plugin state " + this.lifecyclePhase);
        }
        try {
            changeLifecyclePhaseTo(LifecyclePhase.UNINSTALLING);
            doUninstall();
            this.lifecyclePhase = LifecyclePhase.NOT_INSTALLED;
        } catch (Throwable t) {
            changeLifecyclePhaseTo(LifecyclePhase.UNINSTALL_FAILED);
            throw new PluginException(this, "Caught error during doUninstall", t);
        }
    }

    public ObserverRegistry<Plugin> getPluginObserverRegistry() {
        return pluginObserverRegistry;
    }

    public LifecyclePhase getLifecyclePhase() {
        return this.lifecyclePhase;
    }

    private void changeLifecyclePhaseTo(LifecyclePhase newPhase) {
        this.lifecyclePhase = newPhase;
        this.pluginObserverRegistry.notifyObservers();
    }

}
