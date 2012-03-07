package org.easyrec.plugin;

import org.easyrec.plugin.exception.PluginException;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.util.ObserverRegistry;

public interface Plugin {

    /**
     * The URI that uniquely identifies the plugin. While any valid URI is
     * technically ok here, implementors should choose their URIs wisely,
     * ideally the URI should be 'cool' (@see <a
     * href="http://www.dfki.uni-kl.de/~sauermann/2006/11/cooluris/#cooluris"
     * >Cool URIs for the Semantic Web</a>) If unsure, use an all-lowercase http
     * URI pointing to a host/path that you control, ending with
     * '#[plugin-name]'. E.g.,
     * http://www.example.com/mypath/easyrec-plugins#myplugin
     *
     * @return the URI
     */
    public PluginId getId();

    /**
     * Returns the plugin's name as it should be displayed in UIs etc.
     *
     * @return
     */
    public String getDisplayName();

    /**
     * Every plugin should provide a short textual description of what the plugin does and what the
     * relation of the generated rules actually means.
     *
     * @return a String containing the description.
     */
    public String getPluginDescription();

    /**
     * Fetch the delegate used to (un)register observers that monitor the
     * plugin's lifecycle state.
     *
     * @return
     */
    public ObserverRegistry<Plugin> getPluginObserverRegistry();

    /**
     * Install method - Called when a <code>Plugin</code> is installed.
     *
     * @param executeInstall set to <code>false</code> if you only want to change the state of the plugin
     *                       without executing the actual install code.
     * @throws PluginException
     */
    public void install(boolean executeInstall) throws PluginException;

    /**
     * Uninstall method - Called when a <code>Plugin</code> is uninstalled.
     */
    public void uninstall() throws PluginException;

    /**
     * Initialization method - Called when the <code>Plugin</code> instance is
     * created.
     */
    public void initialize() throws PluginException;

    /**
     * Cleanup method - Called when the <code>Plugin</code> is no longer needed.
     */
    public void cleanup() throws PluginException;

    /**
     * Retrieves the plugin lifecycle phase.
     *
     * @return
     */
    public LifecyclePhase getLifecyclePhase();


    /**
     * The lifecycle phases of a plugin and their transitions:
     * <p/>
     * <pre>
     *      NOT_INSTALLED
     *          |
     *          v
     *      INSTALLING  <-> INSTALL_FAILED
     *          |
     *          v
     *      INSTALLED
     *          |
     *          v
     *      INITIALIZING  <-> INIT_FAILED
     *          |
     *          v
     *      INITIALIZED
     *          |
     *          v
     *       CLEANING_UP     <-> CLEANUP_FAILED
     *          |
     *          v
     *      INSTALLED
     *          |
     *          v
     *      UNINSTALLING    <-> UNINSTALL_FAILED
     *          |
     *          v
     *      NOT_INSTALLED
     * <p/>
     * </pre>
     *
     * @author fkleedorfer
     */
    public enum LifecyclePhase {
        NOT_INSTALLED,
        INSTALLING,
        INSTALLED,
        INSTALL_FAILED,
        UNINSTALLING,
        UNINSTALL_FAILED,
        INITIALIZING,
        INITIALIZED,
        INIT_FAILED,
        CLEANING_UP,
        CLEANUP_FAILED;

        public boolean isNotInstalled() {
            return this == NOT_INSTALLED;
        }

        public boolean isInstalled() {
            return this == INSTALLED;
        }

        public boolean isInstalling() {
            return this == INSTALLING;
        }

        public boolean isInstallFailed() {
            return this == INSTALL_FAILED;
        }

        public boolean isUnInstalling() {
            return this == UNINSTALLING;
        }

        public boolean isUnInstallFailed() {
            return this == UNINSTALL_FAILED;
        }

        public boolean isInitializing() {
            return this == INITIALIZING;
        }

        public boolean isInitialized() {
            return this == INITIALIZED;
        }

        public boolean isInitFailed() {
            return this == INIT_FAILED;
        }

        public boolean isCleaningUp() {
            return this == CLEANING_UP;
        }

        public boolean isCleanupFailed() {
            return this == CLEANUP_FAILED;
        }

        public boolean isInstallAllowed() {
            return (this.isNotInstalled() || this.isInstallFailed() || this.isUnInstallFailed());
        }

        public boolean isInitializeAllowed() {
            return (this.isInstalled() || this.isInitFailed() || this.isCleanupFailed());
        }

        public boolean isCleanupAllowed() {
            return (this.isInitialized() || this.isInitFailed() || this.isCleanupFailed());
        }

        public boolean isUninstallAllowed() {
            return (this.isInstalled() || this.isInstallFailed() || this.isUnInstallFailed());
        }

    }

}
