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

package org.easyrec.plugin.slopeone.store.dao.impl;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.easyrec.model.core.ActionVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.slopeone.store.dao.ActionDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for {@link org.easyrec.plugin.slopeone.store.dao.impl.ActionDAOMysqlImpl}.<p><b>Company:&nbsp;</b> SAT, Research
 * Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author: dmann $<br/> $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/>
 * $Revision: 18685 $</p>
 *
 * @author Patrick Marschik
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "/spring/easyrecDataSource.xml",
        "/spring/utils/aop/DAO.xml",
        "/spring/core/dao/AuthenticationDAO.xml",
        "/spring/core/dao/types/ViewTypeDAO.xml",
        "/spring/core/dao/types/SourceTypeDAO.xml",
        "/spring/core/dao/types/ItemTypeDAO.xml",
        "/spring/core/dao/types/AssocTypeDAO.xml",
        "/spring/core/dao/types/AggregateTypeDAO.xml",
        "/spring/core/dao/types/ActionTypeDAO.xml",
        "/spring/core/dao/TenantDAO.xml",
        "/spring/core/dao/ActionDAO.xml",
        "/spring/core/dao/ProfileDAO.xml",
        "/spring/core/dao/ItemAssocDAO.xml",
        "/spring/core/idMapping.xml",
        "/spring/core/service/ItemAssocService.xml",
        "/spring/core/service/ClusterStrategies.xml",
        "/spring/core/service/TenantService.xml",
        "/spring/core/service/ClusterService.xml",
        "/spring/core/TenantConfig_DEFAULT.xml",
        "/spring/plugins/slopeone/easyrecSlopeOneDataSource.xml",
        "/spring/plugins/slopeone/dao/ActionDAO.xml"})
@DataSet("/dbunit/plugins/slopeone/so_action.xml")
public class ActionDAOMysqlImplTest {

    @SpringBean("slopeOneActionDAO")
    private ActionDAO actionDAO;

    @Test
    public void getRatings_shouldReturnAllRatingsOfUser() {
        TIntSet itemTypeIds = new TIntHashSet(new int[]{1});
        List<RatingVO<Integer, Integer>> ratings = actionDAO.getRatings(1, itemTypeIds, 1);

        assertThat(ratings.size(), is(4));
        assertThat(ratings, hasItem(equalTo(createRating(1, 1, 1, "2007-04-15 12:11:00.0"))));
        assertThat(ratings, hasItem(equalTo(createRating(2, 1, 2, "2007-04-15 12:12:00.0"))));
        assertThat(ratings, hasItem(equalTo(createRating(3, 1, 3, "2007-04-15 12:13:00.0"))));
        assertThat(ratings, hasItem(equalTo(createRating(4, 1, 4, "2007-04-15 12:14:00.0"))));

        ratings = actionDAO.getRatings(1, itemTypeIds, 2);

        assertThat(ratings.size(), is(1));
        assertThat(ratings, hasItem(equalTo(createRating(7, 2, 7, "2007-04-15 12:17:00.0"))));
    }

    @Test
    public void getRatings_shouldReturnAllRatingsWithMultipleItemtypes() {
        TIntSet itemTypeIds = new TIntHashSet(new int[]{1, 2});
        List<RatingVO<Integer, Integer>> ratings = actionDAO.getRatings(1, itemTypeIds, 1);

        assertThat(ratings.size(), is(5));
        assertThat(ratings, hasItem(equalTo(createRating(1, 1, 1, "2007-04-15 12:11:00.0"))));
        assertThat(ratings, hasItem(equalTo(createRating(2, 1, 2, "2007-04-15 12:12:00.0"))));
        assertThat(ratings, hasItem(equalTo(createRating(3, 1, 3, "2007-04-15 12:13:00.0"))));
        assertThat(ratings, hasItem(equalTo(createRating(4, 1, 4, "2007-04-15 12:14:00.0"))));
        RatingVO<Integer, Integer> ratingOfOtherItemType = createRating(1, 1, 1,
                "2007-04-15 12:14:30.0");
        ratingOfOtherItemType.getItem().setType(2);
        assertThat(ratings, hasItem(equalTo(ratingOfOtherItemType)));
    }

    private RatingVO<Integer, Integer> createRating(int itemId, int userId, int ratingValue,
                                                                      String actionTime) {
        return new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, itemId, 1),
                (double) ratingValue, 0, createDate(actionTime), userId);
    }

    private Date createDate(String strDate) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Date date = new Date();

        try {
            date = f.parse(strDate);
        } catch (ParseException ex) {
            fail(ex.toString());
        }

        return date;
    }

    @Test
    public void getUsers_shouldReturnUsersSinceDate() {
        TIntSet itemTypeIds = new TIntHashSet(new int[]{1});
        List<Integer> users = actionDAO.getUsers(1, itemTypeIds, createDate("2007-04-15 12:10:00.0"));

        assertThat(users.size(), is(2));
        assertThat(users, hasItem(equalTo(1)));
        assertThat(users, hasItem(equalTo(2)));

        users = actionDAO.getUsers(1, itemTypeIds, createDate("2007-04-15 12:15:00.0"));

        assertThat(users.size(), is(1));
        assertThat(users, not(hasItem(equalTo(1))));
        assertThat(users, hasItem(equalTo(2)));
    }

    @Test
    public void insertAction_shouldStoreActionOnce() {
        final int ID = 100;
        final int TENANT = 1;
        final int ITEM = 10;
        final int ITEMTYPE = 1;
        final int USER = 10;
        final int RATINGVALUE = 20;
        TIntSet itemTypeIds = new TIntHashSet(new int[]{ITEMTYPE});

        ActionVO<Integer, Integer> action =
                new ActionVO<Integer, Integer>(
                        ID, TENANT, USER, null, null, new ItemVO<Integer, Integer>(TENANT, ITEM, ITEMTYPE), 2,
                        RATINGVALUE, true, 10, null, createDate("2007-04-15 13:01:00.0"));

        int rowsModified = actionDAO.insertAction(action);

        assertThat(rowsModified, is(1));

        action.setActionTime(new Date());
        action.setRatingValue(100);
        rowsModified = actionDAO.insertAction(action);

        assertThat(rowsModified, is(0));

        List<RatingVO<Integer, Integer>> ratings = actionDAO.getRatings(TENANT, itemTypeIds, USER);

        assertThat(ratings.size(), is(1));
        assertThat(ratings, hasItem(equalTo(createRating(ITEM, USER, RATINGVALUE, "2007-04-15 13:01:00.0"))));
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void insertActions_canStoreMoreThan100() {
        // 100 is the bulk size used in the mysqlimpl
        final int ID = 100;
        final int TENANT = 1;
        final int ITEM = 10;
        final int ITEMTYPE = 1;
        final int USER = 10;
        final int RATINGVALUE = 20;

        final int NR_OF_ITEMS = 202;
        TIntSet itemTypeIds = new TIntHashSet(new int[]{ITEMTYPE});

        List<ActionVO<Integer, Integer>> actions =
                new ArrayList<ActionVO<Integer, Integer>>(
                        202);
        List<RatingVO<Integer, Integer>> expectedRatingsList =
                new ArrayList<RatingVO<Integer, Integer>>(
                        NR_OF_ITEMS);

        for (int i = 0; i < NR_OF_ITEMS; i++) {
            String strTime = "2007-04-15 13:" + (((i % 60) < 10) ? "0" : "") + (i % 60) + ":00.0";
            Date time = createDate(strTime);
            ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(TENANT, ITEM + i, ITEMTYPE);

            actions.add(
                    new ActionVO<Integer, Integer>(ID + i, TENANT, USER, null, null, item, 2,
                            RATINGVALUE + i, true, 10, null, time));
            expectedRatingsList
                    .add(new RatingVO<Integer, Integer>(item, (double) RATINGVALUE + i, 0, time, USER,
                            null));
        }

        RatingVO<Integer, Integer>[] expectedRatings = new RatingVO[NR_OF_ITEMS];
        expectedRatings = expectedRatingsList.toArray(expectedRatings);

        int rowsModified = actionDAO.insertActions(actions);

        assertThat(rowsModified, is(NR_OF_ITEMS));

        List<RatingVO<Integer, Integer>> ratings = actionDAO.getRatings(TENANT, itemTypeIds, USER);

        assertThat(ratings.size(), is(NR_OF_ITEMS));
        assertThat(ratings, hasItems(expectedRatings));
    }

    @Test
    @SuppressWarnings({"unchecked"})
    public void insertActions_shouldStoreActionsOnce() {
        final int ID = 100;
        final int TENANT = 1;
        final int ITEM = 10;
        final int ITEMTYPE = 1;
        final int USER = 10;
        final int RATINGVALUE = 20;
        TIntSet itemTypeIds = new TIntHashSet(new int[]{1});

        ActionVO<Integer, Integer> action1 =
                new ActionVO<Integer, Integer>(
                        ID + 1, TENANT, USER, null, null,
                        new ItemVO<Integer, Integer>(TENANT, ITEM + 1, ITEMTYPE), 2,
                        RATINGVALUE + 1, true, 10, null, createDate("2007-04-15 13:01:00.0"));
        ActionVO<Integer, Integer> action2 =
                new ActionVO<Integer, Integer>(
                        ID + 2, TENANT, USER, null, null,
                        new ItemVO<Integer, Integer>(TENANT, ITEM + 2, ITEMTYPE), 2,
                        RATINGVALUE + 2, true, 10, null, createDate("2007-04-15 13:02:00.0"));
        ActionVO<Integer, Integer> action3 =
                new ActionVO<Integer, Integer>(
                        ID + 3, TENANT, USER, null, null,
                        new ItemVO<Integer, Integer>(TENANT, ITEM + 1, ITEMTYPE), 2,
                        RATINGVALUE + 3, true, 10, null, createDate("2007-04-15 13:03:00.0"));
        List<ActionVO<Integer, Integer>> actions = Arrays.asList(action1, action2, action3);

        int rowsModified = actionDAO.insertActions(actions);

        assertThat(rowsModified, is(2));

        List<RatingVO<Integer, Integer>> ratings = actionDAO.getRatings(TENANT, itemTypeIds, USER);

        assertThat(ratings.size(), is(2));
        assertThat(ratings, hasItem(equalTo(createRating(ITEM + 1, USER, RATINGVALUE + 1, "2007-04-15 13:01:00.0"))));
        assertThat(ratings, hasItem(equalTo(createRating(ITEM + 2, USER, RATINGVALUE + 2, "2007-04-15 13:02:00.0"))));
        assertThat(ratings,
                not(hasItem(equalTo(createRating(ITEM + 1, USER, RATINGVALUE + 3, "2007-04-15 13:03:00.0")))));
    }
}
