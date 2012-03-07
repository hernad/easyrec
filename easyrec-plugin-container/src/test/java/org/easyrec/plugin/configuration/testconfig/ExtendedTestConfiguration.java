package org.easyrec.plugin.configuration.testconfig;

import org.easyrec.plugin.configuration.PluginParameter;

import java.util.Date;

public class ExtendedTestConfiguration extends TestConfiguration {
    @PluginParameter(displayName = "the Date Field", description = "a Date Field - holds a date",
            shortDescription = "a Date Field")
    private Date dateField;

    public Date getDateField() {
        return dateField;
    }

    public void setDateField(Date dateField) {
        this.dateField = dateField;
    }

}
