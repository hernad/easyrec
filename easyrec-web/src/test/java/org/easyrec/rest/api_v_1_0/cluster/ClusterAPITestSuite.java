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

package org.easyrec.rest.api_v_1_0.cluster;

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
@Suite.SuiteClasses({ClusterAPITestSuite.XML.class, ClusterAPITestSuite.JSON.class})
public class ClusterAPITestSuite {
    @RunWith(Suite.class)
    @Suite.SuiteClasses({ClustersTest.XML.class, ItemsOfClusterTest.XML.class})
    public static class XML {
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses({ClustersTest.JSON.class, ItemsOfClusterTest.JSON.class})
    public static class JSON {
    }
}
