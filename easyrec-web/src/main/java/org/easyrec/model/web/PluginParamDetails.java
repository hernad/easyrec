/**Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.model.web;

/**
 * @author szavrel
 */
public class PluginParamDetails {

    private String name;
    private String displayName;
    private String description;
    private String shortDescription;
    private Object value;
    private String stringValue;
    private boolean optional;

    public PluginParamDetails(String name, String displayName, String description, String shortDescription,
                              Object value, String stringValue, boolean optional) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.shortDescription = shortDescription;
        this.value = value;
        this.stringValue = stringValue;
        this.optional = optional;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }

    public boolean isOptional() {
        return optional;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getStringValue() {
        return stringValue;
    }

    public Object getValue() {
        return value;
    }


}
