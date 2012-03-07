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

public class Assoc {
    Item itemFrom;
    Item itemTo;
    String assoc;
    Double value;
    String sourceType;
    String sourceInfo;
    String viewType;

    public Assoc(Item itemFrom, Item itemTo, String assoc, Double value, String sourceType, String viewType, String sourceInfo) {
        this.itemFrom = itemFrom;
        this.itemTo = itemTo;
        this.assoc = assoc;
        this.value = value;
        this.sourceType = sourceType;
        this.viewType = viewType;
        this.sourceInfo = sourceInfo;
    }

    public String getAssoc() {
        return assoc;
    }

    public void setAssoc(String assoc) {
        this.assoc = assoc;
    }

    public Item getItemFrom() {
        return itemFrom;
    }

    public void setItemFrom(Item itemFrom) {
        this.itemFrom = itemFrom;
    }

    public Item getItemTo() {
        return itemTo;
    }

    public void setItemTo(Item itemTo) {
        this.itemTo = itemTo;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }


}
