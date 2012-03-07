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

package org.easyrec.rest.api_v_1_0;

import org.easyrec.rest.api_v_1_0.action.ActionAPITestSuite;
import org.easyrec.rest.api_v_1_0.cluster.ClusterAPITestSuite;
import org.easyrec.rest.api_v_1_0.communityranking.CommunityRankingAPITestSuite;
import org.easyrec.rest.api_v_1_0.itemtype.ItemTypeAPITestSuite;
import org.easyrec.rest.api_v_1_0.recommendation.RecommendationAPITestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * ${DESCRIPTION}
 * <p/>
 * <p><b>Company:</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:</b>
 * (c) 2011</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: $<br/>
 * $Date: $<br/>
 * $Revision: $</p>
 *
 * @author patrick
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({RestAPITestSuite.XML.class, RestAPITestSuite.JSON.class})
public class RestAPITestSuite {
    @RunWith(Suite.class)
    @Suite.SuiteClasses(
            {ActionAPITestSuite.XML.class, RecommendationAPITestSuite.XML.class, ItemTypeAPITestSuite.XML.class,
                    ClusterAPITestSuite.XML.class, CommunityRankingAPITestSuite.XML.class})
    public static class XML {
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses(
            {ActionAPITestSuite.JSON.class, RecommendationAPITestSuite.JSON.class, ItemTypeAPITestSuite.JSON.class,
                    ClusterAPITestSuite.JSON.class, CommunityRankingAPITestSuite.JSON.class})
    public static class JSON {
    }

}
