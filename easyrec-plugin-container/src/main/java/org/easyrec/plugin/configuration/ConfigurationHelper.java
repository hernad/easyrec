/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.easyrec.plugin.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.beans.PropertyEditor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Provides utilities for setting and querying configuration parameters and their associated metadata as defined in the
 * {@link PluginParameter} annotations.
 * <p/>
 * Getting, setting and validation of parameter values is done by spring's {@link BeanWrapper} and {@link DataBinder}
 * classes. As the <code>DataBinder</code> maintains state over multiple calls to setValues(), we provide a reset()
 * method that re-initializes the <code>ConfigurationHelper</code>'s resources.
 *
 * @author fkleedorfer
 */
public class ConfigurationHelper {
    protected final Log logger = LogFactory.getLog(getClass());

    protected Validator validator;

    protected Configuration configuration;

    protected BeanWrapper configurationWrapper;

    protected Map<String, Field> parameterFields = new HashMap<String, Field>();
    private DataBinder dataBinder;

    public ConfigurationHelper(Configuration configuration) {
        this.configuration = configuration;
        init();
    }

    /**
     * Re-initializes the internally used resources of this configuration helper. If this method is not called between
     * multiple calls of setValues(), the <code>BindingResult</code> accumulates errors.
     */
    public void reset() {
        init();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.easyrec.plugin.container.ConfigurationHelper#getParameterDescription (java.lang.String)
     */
    public String getParameterDescription(String parameterName) {
        PluginParameter parameterAnnotation = getParameterAnnotation(parameterName);
        return parameterAnnotation.description();
    }

    public String getParameterDisplayName(String parameterName) {
        PluginParameter parameterAnnotation = getParameterAnnotation(parameterName);
        return parameterAnnotation.displayName();
    }

    public Set<String> getParameterNames() {
        return Collections.unmodifiableSet(this.parameterFields.keySet());
    }

    public boolean getParameterOptional(String parameterName) {
        PluginParameter parameterAnnotation = getParameterAnnotation(parameterName);
        return parameterAnnotation.optional();
    }

    public String getParameterShortDescription(String parameterName) {
        PluginParameter parameterAnnotation = getParameterAnnotation(parameterName);
        return parameterAnnotation.shortDescription();
    }

    public int getParameterDisplayOrder(String parameterName) {
        PluginParameter parameterAnnotation = getParameterAnnotation(parameterName);
        return parameterAnnotation.displayOrder();
    }

    /**
     * Converts the parameter value to a string. If a custom {@link PropertyEditor} is configured for the parameter (via
     * the {@link PluginParameterPropertyEditor} Annotation), it is used for conversion, otherwise the value's
     * <code>toString()</code> method is called. If the value is <code>null</code>, <code>null</code> is returned.
     *
     * @param parameterName
     * @return
     * @throws IllegalArgumentException if no parameter of given name exists.
     */
    public String getParameterStringValue(String parameterName) {
        Field field = getParameterField(parameterName);
        PropertyEditor editor = this.configurationWrapper.findCustomEditor(field.getType(), field.getName());
        Object value = getParameterValue(parameterName);
        if (editor != null) {
            editor.setValue(value);
            return editor.getAsText();
        } else {
            if (value == null) {
                return null;
            }
            return value.toString();
        }
    }

    /**
     * Returns the value for the specified parameter name.
     *
     * @param parameterName
     * @return
     */
    public Object getParameterValue(String parameterName) {
        return this.configurationWrapper.getPropertyValue(parameterName);
    }

    /**
     * Returns all values as a {@link PropertyValues} object, using no property key prefix.
     *
     * @return
     */
    public PropertyValues getValues() {
        return getValues(null);
    }

    /**
     * Same as getValues(), but property keys are prefixed with the specified prefix.
     *
     * @param propertyKeyPrefix
     * @return
     */
    public PropertyValues getValues(String propertyKeyPrefix) {
        if (propertyKeyPrefix == null) {
            propertyKeyPrefix = "";
        } else {
            propertyKeyPrefix += ".";
        }
        MutablePropertyValues props = new MutablePropertyValues();
        for (String propertyName : getParameterNames()) {
            String strVal = getParameterStringValue(propertyName);
            if (strVal != null) {
                props.addPropertyValue(propertyKeyPrefix + propertyName, strVal);
            }
        }
        return props;
    }

    /**
     * Delegates the call to <code>getValuesAsProperties(null,null)</code>.
     *
     * @return
     */
    public Properties getValuesAsProperties() {
        return getValuesAsProperties(null, null);
    }

    /**
     * Delegates the call to <code>getValuesAsProperties(props,null)</code>
     *
     * @param props
     * @return
     */
    public Properties getValuesAsProperties(Properties props) {
        return getValuesAsProperties(props, null);
    }

    /**
     * Populate the specified properties object with the configuration values, using the specified prefix for the
     * property keys. If a parameter value is null, no property is created for it.
     *
     * @param props             properties to populate. New Properties are created if null.
     * @param propertyKeyPrefix prefix for property keys. Use null to omit prefix.
     * @return
     */
    public Properties getValuesAsProperties(Properties props, String propertyKeyPrefix) {
        if (props == null) {
            props = new Properties();
        }
        if (propertyKeyPrefix == null) {
            propertyKeyPrefix = "";
        } else {
            propertyKeyPrefix += ".";
        }
        for (String propertyName : getParameterNames()) {
            String strVal = getParameterStringValue(propertyName);
            if (strVal != null) {
                props.setProperty(propertyKeyPrefix + propertyName, strVal);
            }
        }
        return props;
    }

    /**
     * Delegates the call to <code>getValuesAsProperties(null,propertyKeyPrefix)</code>
     *
     * @param propertyKeyPrefix
     * @return
     */
    public Properties getValuesAsProperties(String propertyKeyPrefix) {
        return getValuesAsProperties(null, propertyKeyPrefix);
    }

    /**
     * Sets the parameter values from the specified PropertyValues object. A parameter value is set to <code>null</code>
     * if the respective property is not present.
     *
     * @param values
     * @return
     */
    public BindingResult setValues(PropertyValues values) {
        this.dataBinder.bind(values);

        try {
            this.dataBinder.validate();
        } catch (RuntimeException ex) {
            logger.warn("Plugin configuration validator threw an exception!", ex);
        }

        return this.dataBinder.getBindingResult();
    }

    /**
     * Sets the parameter values from the specified Properties object. A parameter value is set to <code>null</code> if
     * the respective property is not present.
     *
     * @param properties
     * @param propertyKeyPrefix
     * @return
     */
    public BindingResult setValues(Properties properties, String propertyKeyPrefix) {
        if (propertyKeyPrefix == null) {
            propertyKeyPrefix = "";
        } else {
            propertyKeyPrefix += ".";
        }
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        for (String propertyName : getParameterNames()) {
            propertyValues.addPropertyValue(propertyName, properties.getProperty(propertyKeyPrefix + propertyName));
        }
        return setValues(propertyValues);
    }

    /**
     * Returns the PluginParameter annotation for the specified name.
     *
     * @param parameterName
     * @return
     */
    protected PluginParameter getParameterAnnotation(String parameterName) {
        Field field = getParameterField(parameterName);
        return field.getAnnotation(PluginParameter.class);
    }

    /**
     * Retrieves the {@link Field} with the given name. If no Field is found in the <code>Configuration</code> class of
     * this <code>ConfigurationWrapper</code> that is annotated with the {@link PluginParameter} annotation, an
     * {@link IllegalArgumentException} is raised.
     *
     * @param parameterName
     * @return
     * @throws IllegalArgumentException if no parameter of the specified name is found.
     */
    protected Field getParameterField(String parameterName) {
        Field field = this.parameterFields.get(parameterName);
        if (field == null) {
            throw new IllegalArgumentException("No field named '" + parameterName +
                    "' with the field-level annotation 'PluginParameter' in class '" +
                    this.configuration.getClass().getName() + "'");
        }
        return field;
    }

    protected void init() {
        this.configurationWrapper = new BeanWrapperImpl(this.configuration);
        this.parameterFields = getParameterFields(this.configuration);
        this.dataBinder = new DataBinder(this.configuration, "Configuration");
        allowFieldsForDataBinding();
        registerPropertyEditors();
        checkForValidator();
    }

    private void allowFieldsForDataBinding() {
        Set<String> propertyNames = getParameterNames();
        this.dataBinder.setAllowedFields(propertyNames.toArray(new String[propertyNames.size()]));
    }

    private void checkForValidator() {
        PluginConfigurationValidator validatorAnnotation = this.configuration.getClass()
                .getAnnotation(PluginConfigurationValidator.class);
        if (validatorAnnotation != null) {
            Validator validator;
            try {
                validator = validatorAnnotation.validatorClass().getConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("could not create validator for configuration", e);
            }
            this.dataBinder.setValidator(validator);
            this.validator = validator;
        }
    }

    private Set<Field> collectFields(Class<?> clazz) {
        return collectFields(clazz, null);
    }

    private Set<Field> collectFields(Class<?> clazz, Set<Field> fields) {
        if (fields == null) {
            fields = new HashSet<Field>();
        }
        Field[] curFields = clazz.getDeclaredFields();
        for (int i = 0; i < curFields.length; i++) {
            fields.add(curFields[i]);
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            return collectFields(superClass, fields);
        }
        return fields;
    }

    private Map<String, Field> getParameterFields(Configuration config) {
        Set<Field> fields = collectFields(config.getClass());
        Map<String, Field> parameterFields = new HashMap<String, Field>();
        for (Field field : fields) {
            Annotation pluginParameterAnnotation = field.getAnnotation(PluginParameter.class);
            if (pluginParameterAnnotation != null) {
                parameterFields.put(field.getName(), field);
            }
        }
        return parameterFields;
    }

    private void registerPropertyEditors() {
        for (String fieldName : this.parameterFields.keySet()) {
            Field field = this.parameterFields.get(fieldName);
            PluginParameterPropertyEditor propertyEditorAnnotation = field
                    .getAnnotation(PluginParameterPropertyEditor.class);
            if (propertyEditorAnnotation != null) {
                PropertyEditor editor;
                try {
                    editor = propertyEditorAnnotation.propertyEditorClass().getConstructor().newInstance();
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "could not create property editor for field '" + field.getName() + "'", e);
                }
                this.dataBinder.registerCustomEditor(field.getType(), field.getName(), editor);
                this.configurationWrapper.registerCustomEditor(field.getType(), field.getName(), editor);
            }
        }
    }

}
