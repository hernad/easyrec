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


public class RuleMinerStatistic {

    private Integer itemsWithRules;
    private Integer itemsWithAssocValueGreaterThanMinAssocValue1;
    private Integer itemsWithAssocValueGreaterThanMinAssocValue2;
    private Integer itemsWithAssocValueGreaterThanMinAssocValue3;
    private Integer itemsWithAssocValueGreaterThanMinAssocValue4;

    public RuleMinerStatistic(Integer itemsWithRules, Integer itemsWithAssocValueGreaterThanMinAssocValue1,
                              Integer itemsWithAssocValueGreaterThanMinAssocValue2,
                              Integer itemsWithAssocValueGreaterThanMinAssocValue3,
                              Integer itemsWithAssocValueGreaterThanMinAssocValue4) {
        this.itemsWithRules = itemsWithRules;
        this.itemsWithAssocValueGreaterThanMinAssocValue1 = itemsWithAssocValueGreaterThanMinAssocValue1;
        this.itemsWithAssocValueGreaterThanMinAssocValue2 = itemsWithAssocValueGreaterThanMinAssocValue2;
        this.itemsWithAssocValueGreaterThanMinAssocValue3 = itemsWithAssocValueGreaterThanMinAssocValue3;
        this.itemsWithAssocValueGreaterThanMinAssocValue4 = itemsWithAssocValueGreaterThanMinAssocValue4;
    }


    public Integer getItemsWithAssocValueGreaterThanMinAssocValue1() {
        return itemsWithAssocValueGreaterThanMinAssocValue1;
    }

    public void setItemsWithAssocValueGreaterThanMinAssocValue1(Integer itemsWithAssocValueGreaterThanMinAssocValue1) {
        this.itemsWithAssocValueGreaterThanMinAssocValue1 = itemsWithAssocValueGreaterThanMinAssocValue1;
    }

    public Integer getItemsWithAssocValueGreaterThanMinAssocValue2() {
        return itemsWithAssocValueGreaterThanMinAssocValue2;
    }

    public void setItemsWithAssocValueGreaterThanMinAssocValue2(Integer itemsWithAssocValueGreaterThanMinAssocValue2) {
        this.itemsWithAssocValueGreaterThanMinAssocValue2 = itemsWithAssocValueGreaterThanMinAssocValue2;
    }

    public Integer getItemsWithAssocValueGreaterThanMinAssocValue3() {
        return itemsWithAssocValueGreaterThanMinAssocValue3;
    }

    public void setItemsWithAssocValueGreaterThanMinAssocValue3(Integer itemsWithAssocValueGreaterThanMinAssocValue3) {
        this.itemsWithAssocValueGreaterThanMinAssocValue3 = itemsWithAssocValueGreaterThanMinAssocValue3;
    }

    public Integer getItemsWithAssocValueGreaterThanMinAssocValue4() {
        return itemsWithAssocValueGreaterThanMinAssocValue4;
    }

    public void setItemsWithAssocValueGreaterThanMinAssocValue4(Integer itemsWithAssocValueGreaterThanMinAssocValue4) {
        this.itemsWithAssocValueGreaterThanMinAssocValue4 = itemsWithAssocValueGreaterThanMinAssocValue4;
    }

    public Integer getItemsWithRules() {
        return itemsWithRules;
    }

    public void setItemsWithRules(Integer itemsWithRules) {
        this.itemsWithRules = itemsWithRules;
    }


    public Float getPercentageGroup5() {
        return (itemsWithAssocValueGreaterThanMinAssocValue4 / new Float(itemsWithRules)) * 100;
    }

    public Float getPercentageGroup4() {
        return ((itemsWithAssocValueGreaterThanMinAssocValue3 - itemsWithAssocValueGreaterThanMinAssocValue4) /
                        new Float(itemsWithRules)) * 100;
    }

    public Float getPercentageGroup3() {
        return ((itemsWithAssocValueGreaterThanMinAssocValue2 - itemsWithAssocValueGreaterThanMinAssocValue3) /
                        new Float(itemsWithRules)) * 100;
    }

    public Float getPercentageGroup2() {
        return ((itemsWithAssocValueGreaterThanMinAssocValue1 - itemsWithAssocValueGreaterThanMinAssocValue2) /
                        new Float(itemsWithRules)) * 100;
    }

    public Float getPercentageGroup1() {
        return ((itemsWithRules - itemsWithAssocValueGreaterThanMinAssocValue1) / new Float(itemsWithRules)) * 100;
    }

    public Integer getGroup5() {
        return itemsWithAssocValueGreaterThanMinAssocValue4;
    }

    public Integer getGroup4() {
        return itemsWithAssocValueGreaterThanMinAssocValue3 - itemsWithAssocValueGreaterThanMinAssocValue4;
    }

    public Integer getGroup3() {
        return itemsWithAssocValueGreaterThanMinAssocValue2 - itemsWithAssocValueGreaterThanMinAssocValue3;
    }

    public Integer getGroup2() {
        return itemsWithAssocValueGreaterThanMinAssocValue1 - itemsWithAssocValueGreaterThanMinAssocValue2;
    }

    public Integer getGroup1() {
        return itemsWithRules - itemsWithAssocValueGreaterThanMinAssocValue1;
    }


}
