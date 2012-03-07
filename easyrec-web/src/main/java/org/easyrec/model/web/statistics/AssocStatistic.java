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

package org.easyrec.model.web.statistics;

import java.io.Serializable;

/**
 * @author phlavac
 */
public class AssocStatistic implements Serializable {

    private static final long serialVersionUID = 7516780695016785452L;

    private Integer actions;
    private Integer rules;
    private Integer itemsWithRules;
    private Integer averageNumberOfRulesPerItem;
    private Integer stdNumberOfRulesPerItem;

    public AssocStatistic(Integer actions, Integer rules, Integer itemsWithRules, Integer averageNumberOfRulesPerItem,
                          Integer stdNumberOfRulesPerItem) {
        this.actions = actions;
        this.rules = rules;
        this.itemsWithRules = itemsWithRules;
        this.averageNumberOfRulesPerItem = averageNumberOfRulesPerItem;
        this.stdNumberOfRulesPerItem = stdNumberOfRulesPerItem;
    }

    /**
     * Get number of actions for that assocType
     *
     * @return
     */
    public Integer getActions() {
        return actions;
    }

    public void setActions(Integer actions) {
        this.actions = actions;
    }

    /**
     * Get average Number of Rules per Item for that assoc Type
     *
     * @return
     */
    public Integer getAverageNumberOfRulesPerItem() {
        return averageNumberOfRulesPerItem;
    }

    public void setAverageNumberOfRulesPerItem(Integer averageNumberOfRulesPerItem) {
        this.averageNumberOfRulesPerItem = averageNumberOfRulesPerItem;
    }

    /**
     * Get number of Items for that assoc Type
     *
     * @return
     */
    public Integer getItemsWithRules() {
        return itemsWithRules;
    }

    public void setItemsWithRules(Integer itemsWithRules) {
        this.itemsWithRules = itemsWithRules;
    }

    /**
     * Get Number of total Rules for that assoc Type
     *
     * @return
     */
    public Integer getRules() {
        return rules;
    }

    public void setRules(Integer rules) {
        this.rules = rules;
    }

    /**
     * Get standard deviation of rules for an item
     *
     * @return
     */
    public Integer getStdNumberOfRulesPerItem() {
        return stdNumberOfRulesPerItem;
    }

    public void setStdNumberOfRulesPerItem(Integer stdNumberOfRulesPerItem) {
        this.stdNumberOfRulesPerItem = stdNumberOfRulesPerItem;
    }


}
