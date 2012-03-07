package org.easyrec.plugin.mahout;

import org.easyrec.plugin.configuration.PluginParameter;
import org.easyrec.plugin.generator.GeneratorConfiguration;

/**
 * Configuration object for the demo plugin. <p/> This class contains all parameters that can be configured and are
 * needed for the plugin to work correctly. <p/> <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class MahoutSlopeoneGeneratorConfig extends GeneratorConfiguration {
    // ------------------------------ FIELDS ------------------------------

    // each configuration value needs to be annotated with @PluginParameter.
    // displayName      - is the string that will be displayed for the value in the administration tool.
    // shortDescription - will be the first paragraph of the description when the help button is pressed in the admin tool.
    // description      - is the second paragraph displayed in the admin tool.
    //
    // each config value should be initialized with a default value. when a new configuration object is created
    // all config values are initialized with the default values and the configuration is named "Default Configuration" in
    // the superclass (GeneratorConfiguration.)
    @PluginParameter(description = "The number of item associations calculated for each item.",
            displayName = "numberOfRecs",
            shortDescription = "Number of recommendations per item")
    private int numberOfRecs = 10; // DEFAULT VALUE

    private String actionType = "RATE";

    private String viewType = "SYSTEM";

    // --------------------- GETTER / SETTER METHODS ---------------------

    public int getNumberOfRecs() {
        return numberOfRecs;
    }

    public void setNumberOfRecs(int numberOfRecs) {
        this.numberOfRecs = numberOfRecs;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(final String viewType) {
        this.viewType = viewType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
