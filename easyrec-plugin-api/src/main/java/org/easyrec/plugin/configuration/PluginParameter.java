package org.easyrec.plugin.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PluginParameter {

    /**
     * The name to be displayed in the UI
     */
    String displayName();

    /**
     * The short description of the parameter, should be < 50 chars.
     */
    String shortDescription();

    /**
     * The long description of the parameter. Any length is ok.
     */
    String description();

    /**
     * If set, determines the display ordering of the parameter in the admin interface.
     * @return
     */
    int displayOrder() default -1;
    /**
     * @return
     */
    boolean optional() default false;

}
