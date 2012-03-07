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
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.itemitem.store.dao.impl.ActionDAOMemoryImpl;
import org.easyrec.plugin.itemitem.test.helpers.ItemAssocServiceStub;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Abstract test for {@link org.easyrec.plugin.itemitem.SimilarityCalculationStrategy}s. <p><b>Company:&nbsp;</b> SAT, Research
 * Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/>
 * $Revision$</p>
 *
 * @author Patrick Marschik
 */
public abstract class AbstractySimilarityCalculationStrategyTest {
    // ------------------------------ FIELDS ------------------------------

    protected static List<RatingVO<Integer, Integer>> lemireRatings;
    protected static final int ASSOC_TYPE = 99;
    protected static final int SOURCE_TYPE = 98;
    protected static final int VIEW_TYPE = 97;
    protected ActionDAOMemoryImpl actionDAO;
    protected ItemAssocServiceStub itemAssocService;

    // -------------------------- STATIC METHODS --------------------------

    @BeforeClass
    public static void beforeClass() {
        lemireRatings = new ArrayList<RatingVO<Integer, Integer>>();

        lemireRatings.add(createRating(1, 10.0, 1));
        lemireRatings.add(createRating(2, 5.0, 1));
        lemireRatings.add(createRating(4, 1.0, 1));

        lemireRatings.add(createRating(1, 10.0, 2));
        lemireRatings.add(createRating(3, 5.0, 2));
        lemireRatings.add(createRating(4, 2.0, 2));

        lemireRatings.add(createRating(1, 9.0, 3));
        lemireRatings.add(createRating(2, 4.0, 3));
        lemireRatings.add(createRating(3, 5.0, 3));
        lemireRatings.add(createRating(4, 1.0, 3));

        lemireRatings.add(createRating(1, 1.0, 4));
        lemireRatings.add(createRating(4, 10.0, 4));
        lemireRatings.add(createRating(5, 4.0, 4));
    }

    protected static RatingVO<Integer, Integer> createRating(int itemId, double ratingValue,
                                                                               int userId) {
        return new RatingVO<Integer, Integer>(createItem(itemId), ratingValue, 1, new Date(), userId,
                null);
    }

    protected static ItemVO<Integer, Integer> createItem(int itemId) {
        return new ItemVO<Integer, Integer>(1, itemId, 1);
    }

    // -------------------------- OTHER METHODS --------------------------

    protected ItemAssocVO<Integer,Integer> makeAssoc(int item1Id, int item2Id,
                                                                                          double assocValue,
                                                                                          Date date) {
        return new ItemAssocVO<Integer,Integer>(1,
                new ItemVO<Integer, Integer>(1, item1Id, 1), ASSOC_TYPE, assocValue,
                new ItemVO<Integer, Integer>(1, item2Id, 1), SOURCE_TYPE, getSourceInfo(), VIEW_TYPE, null,
                date);
    }

    protected abstract String getSourceInfo();
}
