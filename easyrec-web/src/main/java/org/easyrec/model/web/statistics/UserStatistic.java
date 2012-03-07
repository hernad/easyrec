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
public class UserStatistic implements Serializable {

    private static final long serialVersionUID = 3577077328786770416L;

    private Integer users_with_1_action;
    private Integer users_with_2_actions;
    private Integer users_with_3_10_actions;
    private Integer users_with_11_100_actions;
    private Integer users_with_101_and_more_actions;

    public UserStatistic(Integer users_with_1_action, Integer users_with_2_actions, Integer users_with_3_10_actions,
                         Integer users_with_11_100_actions, Integer users_with_101_and_more_actions) {


        this.users_with_1_action = users_with_1_action;
        this.users_with_2_actions = users_with_2_actions;
        this.users_with_3_10_actions = users_with_3_10_actions;
        this.users_with_11_100_actions = users_with_11_100_actions;
        this.users_with_101_and_more_actions = users_with_101_and_more_actions;
    }


    public Integer getUsers_with_101_and_more_actions() {
        return users_with_101_and_more_actions;
    }

    public void setUsers_with_101_and_more_actions(Integer users_with_101_and_more_actions) {
        this.users_with_101_and_more_actions = users_with_101_and_more_actions;
    }

    public Integer getUsers_with_11_100_actions() {
        return users_with_11_100_actions;
    }

    public void setUsers_with_11_100_actions(Integer users_with_11_100_actions) {
        this.users_with_11_100_actions = users_with_11_100_actions;
    }

    public Integer getUsers_with_1_action() {
        return users_with_1_action;
    }

    public void setUsers_with_1_action(Integer users_with_1_action) {
        this.users_with_1_action = users_with_1_action;
    }

    public Integer getUsers_with_2_actions() {
        return users_with_2_actions;
    }

    public void setUsers_with_2_actions(Integer users_with_2_actions) {
        this.users_with_2_actions = users_with_2_actions;
    }


    public Integer getUsers_with_3_10_actions() {
        return users_with_3_10_actions;
    }

    public void setUsers_with_3_10_actions(Integer users_with_3_10_actions) {
        this.users_with_3_10_actions = users_with_3_10_actions;
    }
}