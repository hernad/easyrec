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

package org.easyrec.plugin.itemitem.store.dao.impl;

import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.itemitem.store.dao.ActionDAO;
import org.junit.Ignore;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * DOCUMENT ME! <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p>
 * <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public abstract class AbstractActionDAOTest {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected abstract ActionDAO getActionDAO();

    @Test
    @Ignore
    public void didUserRateItem_returnsTrueOrFalse() {
        assertThat(getActionDAO().didUserRateItem(1, new ItemVO<Integer, Integer>(1, 1, 1), 1), is(true));
        assertThat(getActionDAO().didUserRateItem(1, new ItemVO<Integer, Integer>(1, 5, 1), 1), is(false));
        assertThat(getActionDAO().didUserRateItem(1, new ItemVO<Integer, Integer>(1, 1, 1), 2), is(false));
        assertThat(getActionDAO().didUserRateItem(1, new ItemVO<Integer, Integer>(1, 1, 2), 1), is(false));
        assertThat(getActionDAO().didUserRateItem(1, new ItemVO<Integer, Integer>(3, 1, 1), 1), is(false));
        assertThat(getActionDAO().didUserRateItem(2, new ItemVO<Integer, Integer>(1, 2, 1), 1), is(false));
    }

    @Test
    @Ignore
    public void getAvailableItemsForTenant_getsAllItems() {
        List<ItemVO<Integer, Integer>> result = getActionDAO().getAvailableItemsForTenant(1, 1);

        assertThat(result.size(), is(4));

        assertThat(result, hasItem(new ItemVO<Integer, Integer>(1, 1, 1)));
        assertThat(result, hasItem(new ItemVO<Integer, Integer>(1, 2, 1)));
        assertThat(result, hasItem(new ItemVO<Integer, Integer>(1, 3, 1)));
        assertThat(result, hasItem(new ItemVO<Integer, Integer>(1, 4, 1)));

        result = getActionDAO().getAvailableItemsForTenant(2, 1);

        assertThat(result.size(), is(1));
        assertThat(result, hasItem(new ItemVO<Integer, Integer>(2, 6, 1)));

        result = getActionDAO().getAvailableItemsForTenant(2, 2);

        assertThat(result.size(), is(1));
        assertThat(result, hasItem(new ItemVO<Integer, Integer>(2, 5, 2)));
    }

    @Test
    @Ignore
    public void getAverageRatingsForItem_getsAverage() {
        List<RatingVO<Integer,Integer>> result = getActionDAO().getAverageRatingsForItem(1, 1);

        assertThat(result.size(), is(4));

        assertThat(result,
                hasItem(new RatingVO<Integer,Integer>(new ItemVO<Integer, Integer>(1, 1, 1),
                        3.5, 2, null, null, null)));
        assertThat(result,
                hasItem(new RatingVO<Integer,Integer>(new ItemVO<Integer, Integer>(1, 2, 1),
                        2.0, 1, null, null, null)));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 3, 1),
                        3.0, 1, null, null, null)));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 4, 1),
                        4.0, 1, null, null, null)));

        result = getActionDAO().getAverageRatingsForItem(2, 1);

        assertThat(result.size(), is(1));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(2, 6, 1),
                        6.0, 1, null, null, null)));

        result = getActionDAO().getAverageRatingsForItem(2, 2);

        assertThat(result.size(), is(1));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(2, 5, 2),
                        5.0, 1, null, null, null)));

        result = getActionDAO().getAverageRatingsForItem(1, 2);
        assertThat(result.size(), is(0));

        result = getActionDAO().getAverageRatingsForItem(3, 1);
        assertThat(result.size(), is(0));
    }

    @Test
    @Ignore
    public void getAverageRatingsForUser_getsAverage() {
        List<RatingVO<Integer, Integer>> result = getActionDAO().getAverageRatingsForUser(1, 1);

        assertThat(result.size(), is(2));

        assertThat(result, hasItem(new RatingVO<Integer, Integer>(null, 2.5, 4, null, 1, null)));
        assertThat(result, hasItem(new RatingVO<Integer, Integer>(null, 6.0, 1, null, 2, null)));

        result = getActionDAO().getAverageRatingsForUser(2, 1);
        assertThat(result.size(), is(1));
        assertThat(result, hasItem(new RatingVO<Integer, Integer>(null, 6.0, 1, null, 2, null)));

        result = getActionDAO().getAverageRatingsForUser(2, 2);
        assertThat(result.size(), is(1));
        assertThat(result, hasItem(new RatingVO<Integer, Integer>(null, 5.0, 1, null, 1, null)));

        result = getActionDAO().getAverageRatingsForUser(1, 2);
        assertThat(result.size(), is(0));

        result = getActionDAO().getAverageRatingsForUser(3, 1);
        assertThat(result.size(), is(0));
    }

    @Test
    @Ignore
    public void getItemsRatedTogether_getsAllCombinations() {
        List<ActionDAO.RatedTogether<Integer, Integer>> result = getActionDAO()
                .getItemsRatedTogether(1, 1, 1, 2, 1);

        assertThat(result.size(), is(1));
        assertThat(result, hasItem(new ActionDAO.RatedTogether<Integer, Integer>(
                new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 1, 1), 1.0,
                        null, null, 1, null),
                new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 2, 1), 2.0,
                        null, null, 1, null))));

        result = getActionDAO().getItemsRatedTogether(1, 1, 2, 1, 1);

        assertThat(result.size(), is(1));
        assertThat(result, hasItem(new ActionDAO.RatedTogether<Integer, Integer>(
                new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 2, 1), 2.0,
                        null, null, 1, null),
                new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 1, 1), 1.0,
                        null, null, 1, null))));

        result = getActionDAO().getItemsRatedTogether(2, 1, 1, 2, 1);
        assertThat(result.size(), is(0));

        result = getActionDAO().getItemsRatedTogether(1, 2, 1, 2, 1);
        assertThat(result.size(), is(0));

        result = getActionDAO().getItemsRatedTogether(1, 1, 1, 2, 2);
        assertThat(result.size(), is(0));

        result = getActionDAO().getItemsRatedTogether(1, 1, 1, 7, 1);
        assertThat(result.size(), is(0));

        result = getActionDAO().getItemsRatedTogether(1, 1, 7, 1, 1);
        assertThat(result.size(), is(0));
    }

    @Test
    @Ignore
    public void getLatestRatingsForTenant_returnsAllRatings() {
        // no filters
        List<RatingVO<Integer, Integer>> result = getActionDAO()
                .getLatestRatingsForTenant(1, 1, null, null, null);
        assertThat(result.size(), is(5));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 1, 1),
                        1.0, null, makeDate("2007-04-15 12:11:00"), 1, null)));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 2, 1),
                        2.0, null, makeDate("2007-04-15 12:12:00"), 1, null)));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 3, 1),
                        3.0, null, makeDate("2007-04-15 12:13:00"), 1, null)));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 4, 1),
                        4.0, null, makeDate("2007-04-15 12:14:00"), 1, null)));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 1, 1),
                        6.0, null, makeDate("2007-04-15 12:17:00"), 2, null)));

        // filter by itemid
        result = getActionDAO().getLatestRatingsForTenant(1, 1, 1, null, null);
        assertThat(result.size(), is(2));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 1, 1),
                        1.0, null, makeDate("2007-04-15 12:11:00"), 1, null)));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 1, 1),
                        6.0, null, makeDate("2007-04-15 12:17:00"), 2, null)));

        // filter by user
        result = getActionDAO().getLatestRatingsForTenant(1, 1, null, 1, null);

        assertThat(result.size(), is(4));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 1, 1),
                        1.0, null, makeDate("2007-04-15 12:11:00"), 1, null)));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 2, 1),
                        2.0, null, makeDate("2007-04-15 12:12:00"), 1, null)));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 3, 1),
                        3.0, null, makeDate("2007-04-15 12:13:00"), 1, null)));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 4, 1),
                        4.0, null, makeDate("2007-04-15 12:14:00"), 1, null)));

        // filter by time
        result = getActionDAO().getLatestRatingsForTenant(1, 1, null, null, makeDate("2007-04-15 12:13:00.0"));

        assertThat(result.size(), is(2));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 4, 1),
                        4.0, null, makeDate("2007-04-15 12:14:00"), 1, null)));
        assertThat(result,
                hasItem(new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 1, 1),
                        6.0, null, makeDate("2007-04-15 12:17:00"), 2, null)));
    }

    protected static Date makeDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }
}
