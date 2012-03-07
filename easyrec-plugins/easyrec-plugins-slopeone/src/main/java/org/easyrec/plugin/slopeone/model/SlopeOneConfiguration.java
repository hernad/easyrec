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

package org.easyrec.plugin.slopeone.model;

import com.google.common.base.Objects;
import org.easyrec.plugin.configuration.PluginConfigurationValidator;
import org.easyrec.plugin.configuration.PluginParameter;
import org.easyrec.plugin.configuration.PluginParameterPropertyEditor;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import java.util.Collections;
import java.util.List;

/**
 * Configuration for Slope One. <p/> <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p/>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p/> <p><b>last modified:</b><br/> $Author: pmarschik $<br/> $Date: 2011-06-14 15:02:31 +0200 (Di, 14 Jun 2011) $<br/> $Revision: 18436 $</p>
 *
 * @author Patrick Marschik
 */
@SuppressWarnings({"UnusedDeclaration"})
@PluginConfigurationValidator(validatorClass = SlopeOneConfigurationValidator.class)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SlopeOneConfiguration extends GeneratorConfiguration {
    public static class NullableIntegerPropertyEditor extends CustomNumberEditor {
        public NullableIntegerPropertyEditor() throws IllegalArgumentException {
            super(Integer.class, true);
        }
    }

    public static class NullableLongPropertyEditor extends CustomNumberEditor {
        public NullableLongPropertyEditor() throws IllegalArgumentException {
            super(Long.class, true);
        }
    }

    public static String DEFAULT_NAME = "Default Configuration";
    public static List<String> DEFAULT_ITEMTYPES = Collections.emptyList();
    public static String DEFAULT_ACTIONTYPE = "RATE";
    public static String DEFAULT_NONPERSONALIZEDSOURCEINFO = "slopeone-nonpersonalized";
    public static Long DEFAULT_MINRATEDCOUNT = null;
    public static Integer DEFAULT_MAXRECSPERITEM = 10;
    public static String DEFAULT_VIEWTYPE = "COMMUNITY";

    @PluginParameter(
            displayName = "maxRecsPerItem",
            shortDescription = "Maximum number of deviations to be generated.",
            description = "When generating the non-personalized recommendations no more than maxRecsPerItem item associations will be generated.",
            optional = true)
    @PluginParameterPropertyEditor(propertyEditorClass = NullableIntegerPropertyEditor.class)
    @Nullable
    @XmlElement(name = "maxRecsPerItem", nillable = true)
    private Integer maxRecsPerItem;

    @PluginParameter(
            displayName = "minRatedCount",
            shortDescription = "Minimum number of occurences of the deviation.",
            description = "When generating the non-personalized recommendations all deviations with a count less than minRatedCount will be ignored.",
            optional = true)
    @PluginParameterPropertyEditor(propertyEditorClass = NullableLongPropertyEditor.class)
    @Nullable
    @XmlElement(name = "minRatedCount", nillable = true)
    private Long minRatedCount;

    //    @PluginParameter(
    //        displayName = "sourceInfo",
    //        shortDescription = "The source information string to set.",
    //        description = "The source information string to set for non-personalized item associations.",
    //        optional = false)
    @XmlElement(name = "nonPersonalizedSourceInfo")
    private String nonPersonalizedSourceInfo;

    // Changing this parameter in the scope of easyrec makes no sense -> annotation removed
    //    @PluginParameter(
    //        displayName = "actionType",
    //        shortDescription = "The actions to consider for calculating deviations.",
    //        description = "The actions to consider for calculating deviations.",
    //        optional = false)
    @XmlElement(name = "actionType")
    private String actionType;

    // Changing this parameter in the scope of easyrec makes no sense -> annotation removed
    //    @PluginParameter(
    //        displayName = "itemTypes",
    //        shortDescription = "The types of items to consider for calculating deviations.",
    //        description = "The types of items to consider for calculating deviations.",
    //        optional = false)
    @XmlElementWrapper(name = "itemTypes")
    @XmlElement(name = "itemType")
    private List<String> itemTypes;

    //    @PluginParameter(
    //        displayName = "viewType",
    //        shortDescription = "The view type to consider for calculating deviations.",
    //        description = "The view type to consider for calculating deviations.",
    //        optional = false)
    @XmlElement(name = "viewType")
    private String viewType;

    // --------------------------- CONSTRUCTORS ---------------------------

    public SlopeOneConfiguration() {
        this(DEFAULT_NAME, DEFAULT_ITEMTYPES, DEFAULT_ACTIONTYPE, DEFAULT_MINRATEDCOUNT, DEFAULT_MAXRECSPERITEM,
                DEFAULT_NONPERSONALIZEDSOURCEINFO, DEFAULT_VIEWTYPE);
    }

    public SlopeOneConfiguration(String configurationName, List<String> itemTypes, String actionType,
                                 @Nullable Long minRatedCount, @Nullable Integer maxRecsPerItem,
                                 String nonPersonalizedSourceInfo, String viewType) {
        super.setConfigurationName(configurationName);
        this.itemTypes = itemTypes;
        this.actionType = actionType;
        this.minRatedCount = minRatedCount;
        this.maxRecsPerItem = maxRecsPerItem;
        this.nonPersonalizedSourceInfo = nonPersonalizedSourceInfo;
        this.viewType = viewType;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * Action type used when querying the action table.
     *
     * @return Action type used when querying the action table.
     */
    public String getActionType() { return actionType; }

    public void setActionType(final String actionType) {
        this.actionType = actionType;
    }

    /**
     * Item types used when querying the action table and storing to the so_action and so_deviation tables.
     *
     * @return Item types used when querying the action table and storing to the so_action and so_deviation tables.
     */
    public List<String> getItemTypes() { return itemTypes; }

    public void setItemTypes(final List<String> itemTypes) {
        this.itemTypes = itemTypes;
    }

    /**
     * Maximum number of deviations queried when using non-personalized recommendations. i.e. when generating
     * recommandations for an item you can tell the generator to only store the 10 best associations with this
     * parameter.
     *
     * @return Maximum number of deviations queried when using non-personalized recommendations.
     */
    @Nullable
    public Integer getMaxRecsPerItem() { return maxRecsPerItem; }

    public void setMaxRecsPerItem(final @Nullable Integer maxRecsPerItem) {
        this.maxRecsPerItem = maxRecsPerItem;
    }

    /**
     * Minimum count (denominator) needed for a deviation when used for non-personalized recommendations. The higher the
     * count the more users rated that item tuple, therefore the confidence in the recommendation is higher. However if
     * this parameter is set to a value too high fewer recommendations might be generated (esepcially for items not
     * rated often). This parameter should be set in relation to the number users.
     *
     * @return Minimum denominator value.
     */
    @Nullable
    public Long getMinRatedCount() { return minRatedCount; }

    public void setMinRatedCount(final @Nullable Long minRatedCount) {
        this.minRatedCount = minRatedCount;
    }

    /**
     * Source info used when writing to the itemassoc table.
     *
     * @return Source info stored when writing to the itemassoc table.
     */
    public String getNonPersonalizedSourceInfo() { return nonPersonalizedSourceInfo; }

    public void setNonPersonalizedSourceInfo(final String nonPersonalizedSourceInfo) {
        this.nonPersonalizedSourceInfo = nonPersonalizedSourceInfo;
    }

    /**
     * View type used when writing to the itemassoc table.
     *
     * @return View type used when writing to the itemassoc table.
     */
    public String getViewType() { return viewType; }

    public void setViewType(final String viewType) {
        this.viewType = viewType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SlopeOneConfiguration that = (SlopeOneConfiguration) o;

        return Objects.equal(actionType, that.actionType) &&
                Objects.equal(itemTypes, that.itemTypes) &&
                Objects.equal(maxRecsPerItem, that.maxRecsPerItem) &&
                Objects.equal(minRatedCount, that.minRatedCount) &&
                Objects.equal(nonPersonalizedSourceInfo, that.nonPersonalizedSourceInfo) &&
                Objects.equal(viewType, that.viewType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(actionType, itemTypes, maxRecsPerItem, minRatedCount, nonPersonalizedSourceInfo,
                viewType);
    }
}
