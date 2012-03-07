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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easyrec.plugin.slopeone.model;

import org.easyrec.model.core.ItemVO;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;


/**
 * Tests for {@link org.easyrec.plugin.slopeone.model.Deviation}.<p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author: dmann $<br/> $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/> $Revision: 18685 $</p>
 *
 * @author Patrick Marschik
 */
public class DeviationTest {
    // ------------------------------ FIELDS ------------------------------

    //~ Instance fields ////////////////////////////////////////////////////////////////////////////////////////////////
    private ItemVO<Integer, Integer> item1;
    private ItemVO<Integer, Integer> item2;
    private ItemVO<Integer, Integer> item3;
    private Deviation deviation;

    // -------------------------- OTHER METHODS --------------------------

    @Test
    public void getters_shouldReturnConstructorValue() {
        assertThat("denominator", deviation.getDenominator(), equalTo(2L));
        assertThat("deviation", deviation.getDeviation(), equalTo(5.0));
        assertThat("id", deviation.getId(), equalTo(null));
        assertThat("item1", deviation.getItem1(), equalTo(item1));
        assertThat("item2", deviation.getItem2(), equalTo(item2));
        assertThat("numerator", deviation.getNumerator(), equalTo(10.0));
        assertThat("tenant", deviation.getTenantId(), equalTo(1));
        assertThat("itemType", deviation.getItem1TypeId(), equalTo(1));
    }

    //~ Methods ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void setItem1_shouldThrowOnInvalidTenant() {
        deviation.setItem1(item3);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings({"NullableProblems"})
    public void setItem1_shouldThrowOnNull() {
        deviation.setItem2(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setItem2_shouldThrowOnInvalidTenant() {
        deviation.setItem2(item3);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings({"NullableProblems"})
    public void setItem2_shouldThrowOnNull() {
        deviation.setItem2(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setItems_shouldThrowOnInvalidTenantItem1() {
        deviation.setItems(item3, item2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setItems_shouldThrowOnInvalidTenantItem2() {
        deviation.setItems(item1, item3);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings({"NullableProblems"})
    public void setItems_shouldThrowOnNullItem1() {
        deviation.setItems(null, item2);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings({"NullableProblems"})
    public void setItems_shouldThrowOnNullItem2() {
        deviation.setItems(item1, null);
    }

    @Before
    public void setUp() {
        item1 = new ItemVO<Integer, Integer>(1, 1, 1);
        item2 = new ItemVO<Integer, Integer>(1, 2, 1);
        item3 = new ItemVO<Integer, Integer>(2, 3, 1);

        deviation = new Deviation(item1, item2, 10.0, 2);
    }

    @Test
    public void setters_shouldSet() {
        deviation.setDenominator(3L);
        deviation.setId(1);
        deviation.setItem1(item2);
        deviation.setItem2(item1);
        deviation.setNumerator(15.0);

        assertThat("denominator", deviation.getDenominator(), equalTo(3L));
        assertThat("deviation", deviation.getDeviation(), equalTo(5.0));
        assertThat("id", deviation.getId(), equalTo(1));
        assertThat("item1", deviation.getItem1(), equalTo(item2));
        assertThat("item2", deviation.getItem2(), equalTo(item1));
        assertThat("numerator", deviation.getNumerator(), equalTo(15.0));
        assertThat("tenant", deviation.getTenantId(), equalTo(1));
        assertThat("itemType", deviation.getItem1TypeId(), equalTo(1));

        deviation.setItems(item1, item2);

        assertThat("item1", deviation.getItem1(), equalTo(item1));
        assertThat("item2", deviation.getItem2(), equalTo(item2));
        assertThat("tenant", deviation.getTenantId(), equalTo(1));
        assertThat("itemType", deviation.getItem1TypeId(), equalTo(1));
    }

    @Test
    public void testEquals() throws CloneNotSupportedException {
        Deviation a = new Deviation(1, new ItemVO<Integer, Integer>(1, 1, 1),
                new ItemVO<Integer, Integer>(1, 2, 1), 10.0, 2);
        Deviation b = new Deviation(2, new ItemVO<Integer, Integer>(2, 1, 1),
                new ItemVO<Integer, Integer>(2, 2, 1), 10.0, 2);

        assertThat("unequal", a, not(equalTo(b)));

        Deviation a2 = new Deviation(3, new ItemVO<Integer, Integer>(1, 1, 1),
                new ItemVO<Integer, Integer>(1, 2, 1), 11.0, 3);

        assertThat("equal", a, equalTo(a2));
    }

    @Test
    public void testHashCode() throws CloneNotSupportedException {
        Deviation a = new Deviation(1, new ItemVO<Integer, Integer>(1, 1, 1),
                new ItemVO<Integer, Integer>(1, 2, 1), 10.0, 2);
        Deviation b = new Deviation(2, new ItemVO<Integer, Integer>(2, 1, 1),
                new ItemVO<Integer, Integer>(2, 2, 1), 10.0, 2);

        assertThat("unequal", a.hashCode(), is(not(b.hashCode())));

        Deviation a2 = new Deviation(3, new ItemVO<Integer, Integer>(1, 1, 1),
                new ItemVO<Integer, Integer>(1, 2, 1), 10.0, 2);

        assertThat("equal", a.hashCode(), is(a2.hashCode()));
    }
}
