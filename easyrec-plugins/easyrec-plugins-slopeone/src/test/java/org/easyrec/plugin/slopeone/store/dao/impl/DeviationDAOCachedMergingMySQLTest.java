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

package org.easyrec.plugin.slopeone.store.dao.impl;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.easyrec.plugin.slopeone.model.Deviation;
import org.easyrec.plugin.slopeone.model.TenantItem;
import org.easyrec.plugin.slopeone.store.dao.DeviationDAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.easyrec.plugin.slopeone.test.matchers.Matchers.equalToDeviationAndId;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link DeviationDAOCachedMergingMySQLTest}.
 * <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p>
 * <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "/spring/easyrecDataSource.xml",
        "/spring/utils/aop/Cache.xml",
        "/spring/utils/aop/DAO.xml",
        "/spring/plugins/slopeone/easyrecSlopeOneDataSource.xml",
        "/spring/plugins/slopeone/dao/DeviationDAO.xml"})
@DataSet("/dbunit/plugins/slopeone/so_deviation.xml")
public class DeviationDAOCachedMergingMySQLTest {
    @SpringBean("easyrecSlopeOneDataSource")
    protected DataSource dataSource;

    @SpringBean("easyrecSlopeOneSqlScriptService")
    protected SqlScriptService sqlScriptService;
    @SpringBean("cachedMergingMySqlDeviationDAO")
    private DeviationDAO cachedMergingMySqlDeviationDAO;

    private static boolean started;

    @Before
    public void before() {
        // hack, but we cant get the DAOs injected when using static members
        if (!started) {
            cachedMergingMySqlDeviationDAO.starting();
            started = true;
        }
    }

    @Test
    public void insertDeviations_canInsert2kDeviations() {
        final int TO_INSERT = 2000;

        List<Deviation> deviations = new ArrayList<Deviation>(TO_INSERT);

        for (int i = 0; i < TO_INSERT; i++)
            deviations.add(new Deviation(null, i, 1, i + 1, 1, i + 2, i * 1.5, i * 10));

        int noRows = cachedMergingMySqlDeviationDAO.insertDeviations(deviations);

        assertThat(noRows, is(TO_INSERT));
    }

    @Test
    public void getItemIds_shouldReturnAllItems() {
        TIntSet itemTypeIds = new TIntHashSet(new int[]{1});
        Set<TenantItem> items = cachedMergingMySqlDeviationDAO.getItemIds(1, itemTypeIds);

        assertThat(items.size(), is(3));
        assertThat(items, hasItem(new TenantItem(1, 1)));
        assertThat(items, hasItem(new TenantItem(2, 1)));
        assertThat(items, hasItem(new TenantItem(3, 1)));
    }

    @Test
    public void getDeviation_shouldReturnDeviationOrNull() {
        assertThat(cachedMergingMySqlDeviationDAO.getDeviation(1, 1, 1, 2, 1),
                equalToDeviationAndId(new Deviation(1, 1, 1, 1, 2, 1, 1.0, 2)));
        assertThat(cachedMergingMySqlDeviationDAO.getDeviation(1, 1, 1, 3, 3),
                equalToDeviationAndId(new Deviation(2, 1, 1, 1, 3, 3, 3.0, 4)));
        assertThat(cachedMergingMySqlDeviationDAO.getDeviation(1, 1, 1, 4, 5),
                equalToDeviationAndId(new Deviation(3, 1, 1, 1, 4, 5, 5.0, 6)));
        assertThat(cachedMergingMySqlDeviationDAO.getDeviation(1, 2, 1, 3, 7),
                equalToDeviationAndId(new Deviation(4, 1, 2, 1, 3, 7, 7.0, 8)));
        assertThat(cachedMergingMySqlDeviationDAO.getDeviation(1, 2, 1, 4, 9),
                equalToDeviationAndId(new Deviation(5, 1, 2, 1, 4, 9, 9.0, 10)));
        assertThat(cachedMergingMySqlDeviationDAO.getDeviation(1, 3, 1, 4, 11),
                equalToDeviationAndId(new Deviation(6, 1, 3, 1, 4, 11, 11.0, 12)));
        assertThat(cachedMergingMySqlDeviationDAO.getDeviation(2, 1, 1, 2, 13),
                equalToDeviationAndId(new Deviation(7, 2, 1, 1, 2, 13, 13.0, 14)));

        assertThat(cachedMergingMySqlDeviationDAO.getDeviation(1, 5, 1, 1, 2), is(nullValue())); // wrong item1
        assertThat(cachedMergingMySqlDeviationDAO.getDeviation(1, 1, 1, 5, 2), is(nullValue())); // wrong item2
        assertThat(cachedMergingMySqlDeviationDAO.getDeviation(1, 1, 2, 2, 2),
                is(nullValue())); // wrong itemtype
        assertThat(cachedMergingMySqlDeviationDAO.getDeviation(3, 1, 1, 2, 2),
                is(nullValue())); // wrong tenant
    }

    @Test
    public void getDeviationsOrdered_shouldHonorBothConstraints() {
        final int SIZE = 2;
        final long COUNT = 3L;

        List<Deviation> deviations = cachedMergingMySqlDeviationDAO.getDeviationsOrdered(1, 1, 1, COUNT, SIZE);

        // check size constraint
        assertThat(deviations.size(), is(lessThanOrEqualTo(SIZE)));

        // assume descending deviation ordering
        for (int i = 0; i < (deviations.size() - 1); i++) {
            Deviation a = deviations.get(i);
            Deviation b = deviations.get(i + 1);

            // check countr constraint
            assertThat(a.getDenominator(), is(greaterThanOrEqualTo(COUNT)));
            assertThat(b.getDenominator(), is(greaterThanOrEqualTo(COUNT)));

            // check order
            assertThat(a.getDeviation(), is(greaterThanOrEqualTo(b.getDeviation())));
        }
    }

    @Test
    public void getDeviationsOrdered_shouldWorkWithNoConstraints() {
        List<Deviation> deviations = cachedMergingMySqlDeviationDAO.getDeviationsOrdered(1, 1, 1, null, null);

        assertThat(deviations.size(), is(3));

        // assume descending deviation ordering
        for (int i = 0; i < (deviations.size() - 1); i++) {
            Deviation a = deviations.get(i);
            Deviation b = deviations.get(i + 1);

            // check order
            assertThat(a.getDeviation(), is(greaterThanOrEqualTo(b.getDeviation())));
        }
    }

    @Test
    public void getDeviationsOrdered_shouldHonorMaxNumberConstraint() {
        final int SIZE = 2;

        List<Deviation> deviations = cachedMergingMySqlDeviationDAO.getDeviationsOrdered(1, 1, 1, null, SIZE);

        // check size constraint
        assertThat(deviations.size(), is(lessThanOrEqualTo(SIZE)));

        // assume descending deviation ordering
        for (int i = 0; i < (deviations.size() - 1); i++) {
            Deviation a = deviations.get(i);
            Deviation b = deviations.get(i + 1);

            // check order
            assertThat(a.getDeviation(), is(greaterThanOrEqualTo(b.getDeviation())));
        }
    }

    @Test
    public void getDeviationsOrdered_shouldHonorMinCountConstraint() {
        final long COUNT = 3L;

        List<Deviation> deviations = cachedMergingMySqlDeviationDAO.getDeviationsOrdered(1, 1, 1, COUNT, null);

        // assume descending deviation ordering
        for (int i = 0; i < (deviations.size() - 1); i++) {
            Deviation a = deviations.get(i);
            Deviation b = deviations.get(i + 1);

            // check count constraint
            assertThat(a.getDenominator(), is(greaterThanOrEqualTo(COUNT)));
            assertThat(b.getDenominator(), is(greaterThanOrEqualTo(COUNT)));

            // check order
            assertThat(a.getDeviation(), is(greaterThanOrEqualTo(b.getDeviation())));
        }
    }
}
