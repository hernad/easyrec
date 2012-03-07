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
package org.easyrec.plugin.slopeone.store.dao;

import gnu.trove.set.TIntSet;
import org.easyrec.model.core.ActionVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.utils.spring.store.dao.TableCreatingDroppingDAO;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;


/**
 * Slope One's own action dao. <p/> Doesn't store actionType and other unneeded fields of the vanilla action DAO.
 * Contains only the first rating of a user. <p/> <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p/>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p/> <p><b>last modified:</b><br/> $Author: dmann $<br/> $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/> $Revision: 18685 $</p>
 *
 * @author Patrick Marschik
 */
public interface ActionDAO extends TableCreatingDroppingDAO {
    static final String TABLE_NAME = "so_action";
    static final String COLUMN_ID = "id";
    static final String COLUMN_TENANTID = "tenantId";
    static final String COLUMN_USERID = "userId";
    static final String COLUMN_ITEMID = "itemId";
    static final String COLUMN_ITEMTYPEID = "itemTypeId";
    static final String COLUMN_RATINGVALUE = "ratingValue";
    static final String COLUMN_ACTIONTIME = "actionTime";

    /**
     * This inserts actions from the action table to the so_action table. Also this is only temporary until the
     * generator interface is finished. Since then actions should be sent to the generator by easyrec and only the
     * insertAction(s) method will be called.
     *
     * @param tenantId     The tenant.
     * @param itemTypeIds  The item type(s).
     * @param actionTypeId The action type.
     * @param since        Time range since.
     * @return Number of actions generated.
     */
    int generateActions(int tenantId, TIntSet itemTypeIds, int actionTypeId, @Nullable Date since);

    /**
     * Get all ratings a user did.
     *
     * @param tenantId    Tenant to get ratings for.
     * @param itemTypeIds Item type(s) to get ratings for.
     * @param userId      User to get ratings fro.
     * @return Ratings from tenant with itemTypeId by userId.
     */
    List<RatingVO<Integer, Integer>> getRatings(int tenantId, TIntSet itemTypeIds, int userId);

    /**
     * Get all users that did an action after the {@code since} parameter.
     *
     * @param tenantId    Tenant.
     * @param itemTypeIds Item type(s).
     * @param since       Date since when actions count, i.e. all users who only did actions before that date are
     *                    ignored.
     * @return List of users ids.
     */
    List<Integer> getUsers(int tenantId, TIntSet itemTypeIds, @Nullable Date since);

    /**
     * Inserts an action or ignores the insert if the action was already present.
     *
     * @param action Action.
     * @return {@code 1} if the action was inserted. {@code 0} if the insertion was skipped.
     */
    int insertAction(ActionVO<Integer, Integer> action);

    /**
     * Inserts multiple actions or ignores the insert if the actions were already present.
     *
     * @param actions Actions.
     * @return Number of actions inserted.
     */
    int insertActions(List<ActionVO<Integer, Integer>> actions);
}
