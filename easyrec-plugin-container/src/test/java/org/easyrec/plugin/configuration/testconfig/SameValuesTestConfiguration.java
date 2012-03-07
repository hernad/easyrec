package org.easyrec.plugin.configuration.testconfig;

import org.easyrec.plugin.configuration.PluginConfigurationValidator;

@PluginConfigurationValidator(validatorClass = SameValuesValidator.class)
public class SameValuesTestConfiguration extends TestConfiguration {
}
