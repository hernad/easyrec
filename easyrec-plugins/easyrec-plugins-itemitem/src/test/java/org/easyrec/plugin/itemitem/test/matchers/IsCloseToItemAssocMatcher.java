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

package org.easyrec.plugin.itemitem.test.matchers;

import org.easyrec.model.core.ItemAssocVO;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Tests if a {@link org.easyrec.model.core.ItemAssocVO} matches a supplied {@link org.easyrec.model.core.ItemAssocVO}
 * in all attributes but {@link org.easyrec.model.core.ItemAssocVO#assocValue}. {@link
 * org.easyrec.model.core.ItemAssocVO#assocValue} is only matched within a range specified by a delta parameter.
 * <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last
 * modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class IsCloseToItemAssocMatcher
        extends TypeSafeMatcher<ItemAssocVO<Integer,Integer>> {
    // ------------------------------ FIELDS ------------------------------

    private ItemAssocVO<Integer,Integer> itemAssoc;
    private double assocValue;
    private double delta;

    // -------------------------- STATIC METHODS --------------------------

    public static Matcher<ItemAssocVO<Integer,Integer>> closeToItemAssoc(
            ItemAssocVO<Integer,Integer> itemAssoc, double delta) {
        try {
            return new IsCloseToItemAssocMatcher(itemAssoc, delta);
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    // --------------------------- CONSTRUCTORS ---------------------------

    public IsCloseToItemAssocMatcher(final ItemAssocVO<Integer,Integer> itemAssoc,
                                     final double delta) throws CloneNotSupportedException {
        this.itemAssoc = itemAssoc.clone();
        this.delta = delta;
        this.assocValue = itemAssoc.getAssocValue();
        this.itemAssoc.setAssocValue(null);
    }

    // ------------------------ INTERFACE METHODS ------------------------


    // --------------------- Interface SelfDescribing ---------------------

    public void describeTo(final Description description) {
        description.appendValue(itemAssoc).appendText(" with all values equal and the assocValue within [");
        description.appendValue(assocValue - delta).appendText(", ").appendValue(assocValue + delta).appendText("]");
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    public boolean matchesSafely(final ItemAssocVO<Integer,Integer> other) {
        ItemAssocVO<Integer,Integer> clone;

        try {
            clone = other.clone();
        } catch (CloneNotSupportedException e) {
            return false;
        }

        double cloneValue = clone.getAssocValue();
        clone.setAssocValue(null);

        if (!clone.equals(itemAssoc)) return false;

        return actualDelta(cloneValue) <= 0.0;
    }

    private double actualDelta(double value) {
        return (Math.abs((value - assocValue)) - delta);
    }
}
