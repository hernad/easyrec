/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.plugin.waiting;

import org.easyrec.plugin.configuration.PluginConfigurationValidator;
import org.easyrec.plugin.configuration.PluginParameter;
import org.easyrec.plugin.generator.GeneratorConfiguration;

/**
 * @author fkleedorfer
 */
@PluginConfigurationValidator(validatorClass = WaitingGeneratorConfigurationValidator.class)
public class WaitingGeneratorConfiguration extends GeneratorConfiguration {
    @PluginParameter(
            description = "Timeout (milliseconds) to wait during each phase",
            displayName = "Timeout",
            shortDescription = "Timeout in milliseconds",
            optional = true)
    private long timeout = 1000;

    @PluginParameter(
            description = "Number of phases",
            displayName = "Number of phases",
            shortDescription = "Number of phases. In each phase the plugin sleeps for 'timeout' milliseconds.",
            optional = true)
    private int numberOfPhases = 3;

    public long getTimeout() {
        return timeout;
    }

    public int getNumberOfPhases() {
        return numberOfPhases;
    }

    public void setNumberOfPhases(int numberOfPhases) {
        this.numberOfPhases = numberOfPhases;
    }


    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }


}
