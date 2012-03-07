package org.easyrec.plugin.configuration.testconfig;

import java.beans.PropertyEditorSupport;

public class FlavourPropertyEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Flavour flavour = Flavour.valueOf(text);
        setValue(flavour);
    }

    @Override
    public String getAsText() {
        Flavour flavour = (Flavour) getValue();
        return flavour == null ? "[null]" : flavour.toString();
    }
}
