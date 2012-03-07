package org.easyrec.plugin.configuration.testconfig;

import org.easyrec.plugin.configuration.Configuration;
import org.easyrec.plugin.configuration.PluginParameter;

public class TestConfiguration extends Configuration {

    @PluginParameter(description = "A string field. (Verbose description)", displayName = "The string parameter",
            shortDescription = "A string field")
    private String stringField;
    @PluginParameter(description = "A Double field. (Verbose description)", displayName = "The Double parameter",
            shortDescription = "A Double field")
    private Double doubleObjectField;
    @PluginParameter(description = "A double field. (Verbose description)", displayName = "The double parameter",
            shortDescription = "A double field")
    private double doublePrimitiveField;

    // not a parameter
    private int someInt;

    // not a parameter
    private String someString;

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public Double getDoubleObjectField() {
        return doubleObjectField;
    }

    public void setDoubleObjectField(Double doubleObjectField) {
        this.doubleObjectField = doubleObjectField;
    }

    public double getDoublePrimitiveField() {
        return doublePrimitiveField;
    }

    public void setDoublePrimitiveField(double doublePrimitiveField) {
        this.doublePrimitiveField = doublePrimitiveField;
    }

    public int getSomeInt() {
        return someInt;
    }

    public void setSomeInt(int someInt) {
        this.someInt = someInt;
    }

    public String getSomeString() {
        return someString;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

}
