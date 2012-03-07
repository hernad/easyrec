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

package org.easyrec.plugin.itemitem.impl;

import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.plugin.itemitem.ItemItemService;
import org.easyrec.plugin.itemitem.store.dao.impl.ActionDAOMemoryImpl;
import org.easyrec.plugin.itemitem.test.helpers.ItemAssocServiceStub;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.easyrec.plugin.itemitem.test.matchers.IsCloseToItemAssocMatcher.closeToItemAssoc;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link AdjustedCosineSimilarityCalculationStrategy}. <p><b>Company:&nbsp;</b> SAT, Research Studios
 * Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/>
 * $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class AdjustedCosineSimilarityCalculationStrategyTest extends AbstractySimilarityCalculationStrategyTest {
    // ------------------------------ FIELDS ------------------------------

    private AdjustedCosineSimilarityCalculationStrategy strategy;

    // -------------------------- OTHER METHODS --------------------------

    @Before
    public void before() {
        actionDAO = new ActionDAOMemoryImpl(lemireRatings, 1);
        itemAssocService = new ItemAssocServiceStub();

        strategy = new AdjustedCosineSimilarityCalculationStrategy(actionDAO, itemAssocService);
    }

    @Test
    @Ignore
    public void calculateSimilarity_isCorrect() {
        Date now = new Date();

        strategy.calculateSimilarity(1, 1, 1, ASSOC_TYPE, SOURCE_TYPE, VIEW_TYPE, now, null);

        List<ItemAssocVO<Integer,Integer>> result = itemAssocService
                .getItemAssocs();

        double delta = 1.0e-6;

        assertThat(result.size(), is(16));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(1, 2, -0.91557180812291200000, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(2, 1, -0.91557180812291200000, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(1, 3, -0.42262153619390100000, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(3, 1, -0.42262153619390100000, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(1, 4, -0.98788278773686600000, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(4, 1, -0.98788278773686600000, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(1, 5, 1.0, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(5, 1, 1.0, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(2, 3, -1.0, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(3, 2, -1.0, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(2, 4, 0.90508621498992000000, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(4, 2, 0.90508621498992000000, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(3, 4, 0.40354863494078700000, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(4, 3, 0.40354863494078700000, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(4, 5, -1.0, now), delta)));
        assertThat(result, hasItem(closeToItemAssoc(makeAssoc(5, 4, -1.0, now), delta)));
    }

    protected String getSourceInfo() { return ItemItemService.SOURCE_INFO_COSINE_ADJUSTED; }

    @Test
    public void getSourceInfo_isCorrect() {
        assertThat(strategy.getSourceInfo(), is(ItemItemService.SOURCE_INFO_COSINE_ADJUSTED));
    }
}
