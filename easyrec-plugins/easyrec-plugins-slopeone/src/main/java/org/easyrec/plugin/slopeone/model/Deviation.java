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
package org.easyrec.plugin.slopeone.model;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.easyrec.model.core.ItemVO;

import javax.annotation.Nullable;
import java.io.Serializable;


/**
 * Stores Slope One's average deviations.<p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author: dmann $<br/> $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/> $Revision: 18685 $</p>
 *
 * @author Patrick Marschik
 */
public class Deviation implements Serializable {
    private static final long serialVersionUID = -6064201804889054767L;

    @Nullable
    private Integer id;
    private double numerator;
    private int item1Id;
    private int item2Id;
    private int item1TypeId;
    private int item2TypeId;
    private int tenantId;
    private long denominator;

    /**
     * A hash code using only the {@code item1Id} and {@code item2Id} fields.
     *
     * @param item1Id     Id of item 1.
     * @param item1TypeId Type of item 1.
     * @param item2Id     Id of item 2.
     * @param item2TypeId Type of item 2.
     * @return Hash code.
     */
    public static long hashCodeOfItems(int item1Id, int item1TypeId, int item2Id, int item2TypeId) {
        return Objects.hashCode(item1Id, item1TypeId, item2Id, item2TypeId);
    }

    public Deviation(ItemVO<Integer, Integer> item1, ItemVO<Integer, Integer> item2, double numerator,
                     long denominator) {
        this(null, item1, item2, numerator, denominator);
    }

    public Deviation(@Nullable Integer id, ItemVO<Integer, Integer> item1,
                     ItemVO<Integer, Integer> item2, double numerator, long denominator) {
        validateItems(item1, item2);

        this.item1Id = item1.getItem();
        this.item2Id = item2.getItem();
        this.tenantId = item1.getTenant();
        this.item1TypeId = item1.getType();
        this.item2TypeId = item2.getType();

        this.id = id;
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * Checks if item1 and item2 have the same tenant and item type.
     *
     * @param item1 Item 1.
     * @param item2 Item 2.
     */
    private void validateItems(ItemVO<Integer, Integer> item1, ItemVO<Integer, Integer> item2) {
        Preconditions.checkNotNull(item1, "item1 can't be null.");
        Preconditions.checkNotNull(item2, "item2 can't be null.");
        Preconditions.checkArgument(item1.getTenant().intValue() == item2.getTenant().intValue(),
                "Tenant of item1 and item2 doesn't match.");
    }

    public Deviation(int tenantId, int item1Id, int item1TypeId, int item2Id, int item2TypeId, double numerator,
                     long denominator) {
        this(null, tenantId, item1Id, item1TypeId, item2Id, item2TypeId, numerator, denominator);
    }

    public Deviation(@Nullable Integer id, int tenantId, int item1Id, int item1TypeId, int item2Id, int item2TypeId,
                     double numerator, long denominator) {
        this.id = id;
        this.item1Id = item1Id;
        this.item2Id = item2Id;
        this.item1TypeId = item1TypeId;
        this.item2TypeId = item2TypeId;
        this.tenantId = tenantId;
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public long getDenominator() { return denominator; }

    public void setDenominator(long denominator) { this.denominator = denominator; }

    @Nullable
    public Integer getId() { return id; }

    public void setId(@Nullable Integer id) { this.id = id; }

    public int getItem1Id() { return item1Id; }

    public void setItem1Id(int item1Id) { this.item1Id = item1Id; }

    public int getItem2Id() { return item2Id; }

    public void setItem2Id(int item2Id) { this.item2Id = item2Id; }

    public int getItem1TypeId() { return item1TypeId; }

    public void setItem1TypeId(int item1TypeId) { this.item1TypeId = item1TypeId; }

    public int getItem2TypeId() { return item2TypeId; }

    public void setItem2TypeId(int item2TypeId) { this.item2TypeId = item2TypeId; }

    public double getNumerator() { return numerator; }

    public void setNumerator(double numerator) { this.numerator = numerator; }

    public int getTenantId() { return tenantId; }

    public void setTenantId(int tenantId) { this.tenantId = tenantId; }

    /**
     * The hash code ignores the id, denominator and numerator fields.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(item1Id, item2Id, item1TypeId, item2TypeId, tenantId);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("item1Id", item1Id)
                .add("item1TypeId", item1TypeId)
                .add("item2Id", item2Id)
                .add("item2TypeId", item2TypeId)
                .add("tenantId", tenantId)
                .add("numerator", numerator)
                .add("denominator", denominator)
                .toString();
    }

    /**
     * Adds checking for equality of id, numerator and denominator to standard equality.
     *
     * @param other Object to test equality.
     * @return {@code true} if equal, {@code false} otherwise.
     */
    public boolean equalsWithDeviationAndId(Deviation other) {
        return this.equalsWithDeviation(other) && Objects.equal(getId(), other.getId());
    }

    /**
     * Adds checking for equality of numerator and denominator to standard equality.
     *
     * @param other Object to test equality.
     * @return {@code true} if equal, {@code false} otherwise.
     */
    public boolean equalsWithDeviation(Deviation other) {
        return this.equals(other) && getNumerator() == other.getNumerator() &&
                getDenominator() == other.getDenominator();
    }

    /**
     * Equality ignores the id, numerator and denominator fields.
     *
     * @param obj Object to test equality.
     * @return {@code true} if equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        final Deviation that = (Deviation) obj;

        return Objects.equal(item1Id, that.item1Id) && Objects.equal(item2Id, that.item2Id) &&
                Objects.equal(item1TypeId, that.item1TypeId) && Objects.equal(tenantId, that.tenantId) &&
                Objects.equal(item2TypeId, this.item2TypeId);
    }

    public double getDeviation() { return denominator != 0.0 ? numerator / denominator : 0.0; }

    public ItemVO<Integer, Integer> getItem1() {
        return new ItemVO<Integer, Integer>(tenantId, item1Id, item1TypeId);
    }

    public ItemVO<Integer, Integer> getItem2() {
        return new ItemVO<Integer, Integer>(tenantId, item2Id, item2TypeId);
    }

    /**
     * A hash code using only the {@code item1Id} and {@code item2Id} fields.
     *
     * @return Hash code.
     */
    public long hashCodeOfItems() { return hashCodeOfItems(item1Id, item1TypeId, item2Id, item2TypeId); }

    public void setItem1(ItemVO<Integer, Integer> item1) {
        validateItem(item1);

        item1Id = item1.getItem();
        item1TypeId = item1.getType();
    }

    /**
     * Checks if the item has the same tenant and item type are the same as the currently used values.
     *
     * @param item Item to check.
     */
    private void validateItem(ItemVO<Integer, Integer> item) {
        Preconditions.checkNotNull(item, "item can't be null.");
        Preconditions.checkArgument(item.getTenant() == tenantId, "Tenant of item doesn't match.");
    }

    public void setItem2(ItemVO<Integer, Integer> item2) {
        validateItem(item2);

        item2Id = item2.getItem();
        item2TypeId = item2.getType();
    }

    public void setItems(ItemVO<Integer, Integer> item1, ItemVO<Integer, Integer> item2) {
        validateItems(item1, item2);

        this.item1Id = item1.getItem();
        this.item2Id = item2.getItem();
        this.tenantId = item1.getTenant();
        this.item1TypeId = item1.getType();
        this.item2TypeId = item2.getType();
    }
}
