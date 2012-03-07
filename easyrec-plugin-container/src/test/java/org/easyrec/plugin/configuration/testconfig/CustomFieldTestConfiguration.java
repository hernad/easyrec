package org.easyrec.plugin.configuration.testconfig;

import org.easyrec.plugin.configuration.Configuration;
import org.easyrec.plugin.configuration.PluginParameter;
import org.easyrec.plugin.configuration.PluginParameterPropertyEditor;

public class CustomFieldTestConfiguration extends Configuration {

    @PluginParameter(description = "the configured flavour", shortDescription = "the flavour", displayName = "flavour")
    @PluginParameterPropertyEditor(propertyEditorClass = FlavourPropertyEditor.class)
    private Flavour flavour;

    public Flavour getFlavour() {
        return flavour;
    }

    public void setFlavour(Flavour flavour) {
        this.flavour = flavour;
    }

}
