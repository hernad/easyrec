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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.test.framework.spi.container.TestContainerException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.easyrec.rest.api_v_1_0.AbstractApiTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

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
@RunWith(UnitilsJUnit4TestClassRunner.class)
@DataSet(AbstractApiTest.DATASET_BASE)
public abstract class ClustersTest extends AbstractApiTest {

    public static class JSON extends ClustersTest {
        public JSON() throws TestContainerException { super(METHOD_JSON); }
    }

    public static class XML extends ClustersTest {
        public XML() throws TestContainerException { super(METHOD_XML); }
    }

    public ClustersTest(String method) throws TestContainerException {
        super("clusters", method);
    }

    @Test
    public void clusters_noParameters_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("299"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Wrong APIKey/Tenant combination"));
    }

    @Test
    public void clusters_wrongTenant_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", "WRONG-TENANT");
        params.add("apikey", API_KEY);

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("299"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Wrong APIKey/Tenant combination"));
    }

    @Test
    public void clusters_wrongApiKey_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", "0123456789abcdefedcba98765432100");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("299"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Wrong APIKey/Tenant combination"));
    }

    private static class ClusterInfo {
        private String name;
        private String description;

        private ClusterInfo(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != getClass()) return false;
            ClusterInfo that = (ClusterInfo) obj;

            return Objects.equal(name, that.name) && Objects.equal(description, that.description);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, description);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("name", name)
                    .add("description", description)
                    .toString();
        }
    }

    @Test
    public void clusters_apiKeyTenant_shouldReturnAllClustersExceptInactive() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("tenantid"), is(TENANT_ID));

        JSONArray jsonClusters = getSafeJSONArray(json, "clusters", "cluster");

        List<ClusterInfo> clusters = Lists.newArrayList();
        for (int i = 0; i < jsonClusters.size(); i++) {
            JSONObject cluster = jsonClusters.getJSONObject(i);
            String description = cluster.getString("description");
            // conversion for xml -> json mapping
            if (description != null && description.equals("[]")) description = "";
            clusters.add(new ClusterInfo(cluster.getString("name"), description));
        }

        assertThat(clusters,
                hasItems(new ClusterInfo("CLUSTERS", "The root object of every cluster hierarchy. Cannot be removed!"),
                        new ClusterInfo("CLUSTER_A", ""), new ClusterInfo("CLUSTER_A_1", ""),
                        new ClusterInfo("CLUSTER_A_2", ""), new ClusterInfo("CLUSTER_B", "")));
        assertThat(clusters, not(hasItem(new ClusterInfo("CLUSTER_C_INVISIBLE", ""))));

        assertThat(clusters.size(), is(5));
    }
}
