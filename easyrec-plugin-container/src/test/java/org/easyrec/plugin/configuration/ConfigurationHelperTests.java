package org.easyrec.plugin.configuration;

import org.easyrec.plugin.configuration.testconfig.*;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class ConfigurationHelperTests {

    @Test
    public void testGetParameterNames() {
        TestConfiguration configuration = new TestConfiguration();
        ConfigurationHelper configHelper = new ConfigurationHelper(configuration);
        Set<String> names = configHelper.getParameterNames();
        assertEquals(4, names.size());
        assertTrue("stringField not recognized as parameter name", names.contains("stringField"));
        assertTrue("doubleObjectField not recognized as parameter name", names.contains("doubleObjectField"));
        assertTrue("doublePrimitiveField not recognized as parameter name", names.contains("doublePrimitiveField"));
    }

    @Test
    public void testGetParameterDescriptions() {
        TestConfiguration configuration = new TestConfiguration();
        ConfigurationHelper configHelper = new ConfigurationHelper(configuration);
        assertEquals("A string field. (Verbose description)", configHelper.getParameterDescription("stringField"));
        assertEquals("A double field. (Verbose description)",
                configHelper.getParameterDescription("doublePrimitiveField"));
    }

    @Test
    public void testGetParameterDescriptionsExtended() {
        TestConfiguration configuration = new ExtendedTestConfiguration();
        ConfigurationHelper configHelper = new ConfigurationHelper(configuration);
        assertEquals("a Date Field - holds a date", configHelper.getParameterDescription("dateField"));
        assertEquals("A string field. (Verbose description)", configHelper.getParameterDescription("stringField"));
    }

    // we don't need to test for the other strings as they work identically...

    @Test
    public void testGetParameterNamesOfSubClass() {
        ExtendedTestConfiguration extConfiguration = new ExtendedTestConfiguration();
        ConfigurationHelper extConfigHelper = new ConfigurationHelper(extConfiguration);

        Set<String> names = extConfigHelper.getParameterNames();
        assertEquals(5, names.size());
        assertTrue("stringField not recognized as parameter name", names.contains("stringField"));
        assertTrue("doubleObjectField not recognized as parameter name", names.contains("doubleObjectField"));
        assertTrue("doublePrimitiveField not recognized as parameter name", names.contains("doublePrimitiveField"));
        assertTrue("dateField not recognized as parameter name", names.contains("dateField"));
    }

    @Test
    public void testSetParameterValues() {
        MutablePropertyValues values = new MutablePropertyValues();
        values.addPropertyValue("stringField", "the quick brown fox");
        values.addPropertyValue("doubleObjectField", "57.5434d");
        values.addPropertyValue("doublePrimitiveField", "58.5434d");
        TestConfiguration configuration = new TestConfiguration();
        ConfigurationHelper configHelper = new ConfigurationHelper(configuration);
        configHelper.setValues(values);
        assertEquals("the quick brown fox", configuration.getStringField());
        assertEquals(57.5434d, configuration.getDoubleObjectField().doubleValue(), 0.0d);
        assertEquals(58.5434d, configuration.getDoublePrimitiveField(), 0.0d);
    }

    @Test
    public void testSetParameterValuesNull() {
        MutablePropertyValues values = new MutablePropertyValues();
        values.addPropertyValue("stringField", null);
        values.addPropertyValue("doubleObjectField", null);
        values.addPropertyValue("doublePrimitiveField", null);
        TestConfiguration configuration = new TestConfiguration();
        ConfigurationHelper configHelper = new ConfigurationHelper(configuration);
        BindingResult result = configHelper.setValues(values);
        assertEquals(1, result.getFieldErrorCount("doublePrimitiveField"));
    }

    @Test
    public void testCustomPropertyEditor() {
        MutablePropertyValues values = new MutablePropertyValues();
        values.addPropertyValue("flavour", "SWEET");
        CustomFieldTestConfiguration customFieldConfig = new CustomFieldTestConfiguration();
        ConfigurationHelper customConfigHelper = new ConfigurationHelper(customFieldConfig);
        BindingResult result = customConfigHelper.setValues(values);
        assertEquals(Flavour.SWEET, customFieldConfig.getFlavour());
    }

    @Test
    public void testCustomPropertyEditorGetStringValue() {
        CustomFieldTestConfiguration config = new CustomFieldTestConfiguration();
        config.setFlavour(Flavour.SALTY);
        ConfigurationHelper helper = new ConfigurationHelper(config);
        // difference: check the returned string value
        assertEquals("SALTY", helper.getParameterStringValue("flavour"));
    }

    @Test
    public void testCustomValidator() {
        MutablePropertyValues values = new MutablePropertyValues();
        values.addPropertyValue("stringField", "the quick brown fox");
        values.addPropertyValue("doubleObjectField", "57.5434d");
        values.addPropertyValue("doublePrimitiveField", "58.5434d");
        Configuration config = new SameValuesTestConfiguration();
        ConfigurationHelper helper = new ConfigurationHelper(config);
        BindingResult result = helper.setValues(values);
        assertEquals(1, result.getGlobalErrorCount());
        assertEquals("error.sameValue", ((ObjectError) result.getGlobalErrors().get(0)).getCode());
        // now set different values:
        values = new MutablePropertyValues();
        values.addPropertyValue("stringField", "the quick brown fox");
        values.addPropertyValue("doubleObjectField", null);
        values.addPropertyValue("doublePrimitiveField", "58.5434d");
        helper.reset();
        result = helper.setValues(values);
        assertEquals(1, result.getFieldErrorCount());
        assertEquals("error.sameValue", ((ObjectError) result.getFieldErrors("doublePrimitiveField").get(0)).getCode());
        values = new MutablePropertyValues();
        values.addPropertyValue("stringField", "the quick brown fox");
        values.addPropertyValue("doubleObjectField", "58.5434d");
        values.addPropertyValue("doublePrimitiveField", "58.5434d");
        helper.reset();
        result = helper.setValues(values);
        assertEquals(0, result.getErrorCount());
    }

    @Test
    public void testGetValuesAsProperties() {
        TestConfiguration config = new TestConfiguration();
        ConfigurationHelper helper = new ConfigurationHelper(config);
        config.setDoubleObjectField(9.81d);
        config.setDoublePrimitiveField(3.141d);
        config.setStringField("my string");
        config.setSomeString("some unipmortant string)");
        config.setSomeInt(42);
        Properties props = helper.getValuesAsProperties();
        assertEquals(4, props.size());
        assertEquals("my string", props.getProperty("stringField"));
        assertEquals("9.81", props.getProperty("doubleObjectField"));
        assertEquals("3.141", props.getProperty("doublePrimitiveField"));
        assertEquals("Default Configuration", props.getProperty("configurationName"));
    }

    @Test
    public void testGetValuesAsPropertiesEmptyString() {
        TestConfiguration config = new TestConfiguration();
        ConfigurationHelper helper = new ConfigurationHelper(config);
        config.setDoubleObjectField(9.81d);
        config.setDoublePrimitiveField(3.141d);
        config.setStringField("");
        config.setSomeString("some unipmortant string)");
        config.setSomeInt(42);
        Properties props = helper.getValuesAsProperties();
        assertEquals(4, props.size());
        assertEquals("", props.getProperty("stringField"));
        assertEquals("9.81", props.getProperty("doubleObjectField"));
        assertEquals("3.141", props.getProperty("doublePrimitiveField"));
        assertEquals("Default Configuration", props.getProperty("configurationName"));
    }

    @Test
    public void testGetValuesAsPropertiesNullString() {
        TestConfiguration config = new TestConfiguration();
        ConfigurationHelper helper = new ConfigurationHelper(config);
        config.setDoubleObjectField(9.81d);
        config.setDoublePrimitiveField(3.141d);
        config.setStringField(null);
        config.setSomeString("some unipmortant string)");
        config.setSomeInt(42);
        Properties props = helper.getValuesAsProperties();
        assertEquals(3, props.size());
        assertNull(props.getProperty("stringField"));
        assertEquals("9.81", props.getProperty("doubleObjectField"));
        assertEquals("3.141", props.getProperty("doublePrimitiveField"));
        assertEquals("Default Configuration", props.getProperty("configurationName"));
    }

    @Test
    public void testSetValuesFromProperties() {
        Properties values = new Properties();
        values.setProperty("stringField", "the quick brown fox");
        values.setProperty("doubleObjectField", "9.81d");
        values.setProperty("doublePrimitiveField", "3.141d");
        TestConfiguration config = new TestConfiguration();
        ConfigurationHelper helper = new ConfigurationHelper(config);
        helper.setValues(values, null);
        assertEquals("the quick brown fox", config.getStringField());
        assertEquals(9.81d, config.getDoubleObjectField(), 0.0);
        assertEquals(3.141d, config.getDoublePrimitiveField(), 0.0);
    }

    @Test
    public void testSetValuesFromPropertiesWithPrefix() {
        Properties values = new Properties();
        values.setProperty("a.b.c.d.stringField", "the quick brown fox");
        values.setProperty("a.b.c.d.doubleObjectField", "9.81d");
        values.setProperty("a.b.c.d.doublePrimitiveField", "3.141d");
        TestConfiguration config = new TestConfiguration();
        ConfigurationHelper helper = new ConfigurationHelper(config);
        helper.setValues(values, "a.b.c.d");
        assertEquals("the quick brown fox", config.getStringField());
        assertEquals(9.81d, config.getDoubleObjectField(), 0.0);
        assertEquals(3.141d, config.getDoublePrimitiveField(), 0.0);
    }

    @Test
    public void testSetValuesFromIncompleteProperties() {
        //One of the properties is not set. In this case, 
        Properties values = new Properties();
        values.setProperty("stringField", "the quick brown fox");
        //values.setProperty("doubleObjectField", "9.81d");
        values.setProperty("doublePrimitiveField", "3.141d");
        TestConfiguration config = new TestConfiguration();
        config.setDoubleObjectField(1000d);
        ConfigurationHelper helper = new ConfigurationHelper(config);
        helper.setValues(values, null);
        assertEquals("the quick brown fox", config.getStringField());
        assertNull(null, config.getDoubleObjectField());
        assertEquals(3.141d, config.getDoublePrimitiveField(), 0.0);
    }

}
