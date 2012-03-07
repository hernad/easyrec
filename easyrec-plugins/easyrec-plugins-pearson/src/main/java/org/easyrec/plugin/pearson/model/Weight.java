/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.pearson.model;

public class Weight {
    private Integer id;
    private User user1;
    private User user2;
    private Double weight;

    public Weight(final Integer id, final User user1, final User user2, final Double weight) {
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
        this.weight = weight;
    }

    public Weight(final User user1, final User user2, final Double weight) {
        this(null, user1, user2, weight);
    }

    public Integer getId() {
        return id;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public Double getWeight() {
        return weight;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setUser1(final User user1) {
        this.user1 = user1;
    }

    public void setUser2(final User user2) {
        this.user2 = user2;
    }

    public void setWeight(final Double weight) {
        this.weight = weight;
    }

}
