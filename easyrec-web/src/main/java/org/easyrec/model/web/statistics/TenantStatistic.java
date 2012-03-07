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
public class TenantStatistic implements Serializable {

    private static final long serialVersionUID = 416734168653581394L;

    private Integer actions;
    private Integer backtracks;
    private Integer items;
    private Integer users;
    private Float averageActionsPerUser;
    private Float recommendationCoverage;

    public TenantStatistic(Integer actions, Integer backtracks, Integer items, Integer users,
                           Float averageActionsPerUser, Float recommendationCoverage) {
        this.actions = actions;
        this.backtracks = backtracks;
        this.items = items;
        this.users = users;
        this.averageActionsPerUser = averageActionsPerUser;
        this.recommendationCoverage = recommendationCoverage;
    }

    public Integer getActions() {
        return actions;
    }

    public void setActions(Integer actions) {
        this.actions = actions;
    }

    public Integer getBacktracks() {
        return backtracks;
    }

    public void setBacktracks(Integer backtracks) {
        this.backtracks = backtracks;
    }


    public Float getAverageActionsPerUser() {
        return averageActionsPerUser;
    }

    public void setAverageActionsPerUser(Float averageActionsPerUser) {
        this.averageActionsPerUser = averageActionsPerUser;
    }

    public Integer getItems() {
        return items;
    }

    public void setItems(Integer items) {
        this.items = items;
    }

    public Integer getUsers() {
        return users;
    }

    public void setUsers(Integer users) {
        this.users = users;
    }

    public Float getRecommendationCoverage() {
        return recommendationCoverage;
    }

    public void setRecommendationCoverage(Float recommendationCoverage) {
        this.recommendationCoverage = recommendationCoverage;
    }


}
