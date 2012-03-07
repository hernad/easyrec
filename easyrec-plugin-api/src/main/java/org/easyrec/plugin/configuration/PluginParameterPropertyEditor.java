package org.easyrec.plugin.configuration;

import java.beans.PropertyEditor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PluginParameterPropertyEditor {
    /**
     * The PropertyEditor class to be used for converting between the
     * parameter's string representation and its value. The default is
     */
    Class<? extends PropertyEditor> propertyEditorClass();
}
