package org.easyrec.plugin.configuration;


/**
 * Base class for Configuration implementations.
 *
 * @author fkleedorfer
 */
public abstract class Configuration {

    public Configuration() {

    }

    /**
     * The name of the <code>Configuration</code> instance. Naming a
     * configuration supports readability of larger sets of
     * <code>Configuration</code>s eg. in the administration interface. May be
     * null.
     */
    @PluginParameter(description = "The name of the configuration, used in overview lists", displayName = "Config name",
            shortDescription = "The name of the configuration")
    private String configurationName = "Default Configuration";

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

}
