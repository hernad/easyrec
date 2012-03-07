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
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

/**
 * DOCUMENT ME! <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p>
 * <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class ActionDAOMemoryImplTest extends AbstractActionDAOTest {
    private ActionDAO actionDAO;

    @Before
    public void doSetUp() throws Exception {
        List<RatingVO<Integer, Integer>> ratings = new ArrayList<RatingVO<Integer, Integer>>(
                7);
        ratings.add(
                new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 1, 1), 1.0,
                        null, makeDate("2007-04-15 12:11:00"), 1, null));
        ratings.add(
                new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 2, 1), 2.0,
                        null, makeDate("2007-04-15 12:12:00"), 1, null));
        ratings.add(
                new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 3, 1), 3.0,
                        null, makeDate("2007-04-15 12:13:00"), 1, null));
        ratings.add(
                new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 4, 1), 4.0,
                        null, makeDate("2007-04-15 12:14:00"), 1, null));
        ratings.add(
                new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(2, 5, 2), 5.0,
                        null, makeDate("2007-04-15 12:15:00"), 1, null));
        ratings.add(
                new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(2, 6, 1), 6.0,
                        null, makeDate("2007-04-15 12:16:00"), 2, null));
        ratings.add(
                new RatingVO<Integer, Integer>(new ItemVO<Integer, Integer>(1, 1, 1), 6.0,
                        null, makeDate("2007-04-15 12:17:00"), 2, null));

        actionDAO = new ActionDAOMemoryImpl(ratings, 1);
    }

    @Override
    protected ActionDAO getActionDAO() {
        return actionDAO;
    }
}
