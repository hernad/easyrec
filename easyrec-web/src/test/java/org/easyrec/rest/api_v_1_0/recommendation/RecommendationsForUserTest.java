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

package org.easyrec.rest.api_v_1_0.recommendation;

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
public abstract class RecommendationsForUserTest extends AbstractApiTest {

    public static class JSON extends RecommendationsForUserTest {
        public JSON() throws TestContainerException { super(METHOD_JSON); }
    }

    public static class XML extends RecommendationsForUserTest {
        public XML() throws TestContainerException { super(METHOD_XML); }
    }

    public RecommendationsForUserTest(String method) throws TestContainerException {
        super("recommendationsforuser", method);
    }

    @Test
    public void recommendationsForUser_noParameters_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("299"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Wrong APIKey/Tenant combination"));
    }

    @Test
    public void recommendationsForUser_wrongTenant_shouldReturnError() {
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
    public void recommendationsForUser_wrongApiKey_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", "0123456789abcdefedcba98765432100");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("299"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Wrong APIKey/Tenant combination"));
    }

    @Test
    public void recommendationsForUser_onlyApiKeyTenant_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("402"));
        assertThat(json.getJSONObject("error").getString("@message"), containsString("No User Id given"));
    }

    @Test
    public void recommendationsForUser_noFilter_shouldReturnRecommendationsOfAllItemTypes() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("userid", "USER_2");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("action"), is("recommendationsforuser"));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getString("userid"), is("USER_2"));

        JSONArray items = getSafeJSONArray(json, "recommendeditems", "item");

        assertThat(items.size(), is(2));

        JSONObject item = items.getJSONObject(0);

        assertThat(item.getString("creationDate"), is("2011-06-22 12:01:00.0"));
        assertThat(item.getString("description"), is("Item A"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/item/a/img.png"));
        assertThat(item.getString("id"), is("ITEM_A"));
        assertThat(item.getString("itemType"), is("ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/item/a"));
        assertThat(item.getString("value"), is("0.11"));

        item = items.getJSONObject(1);

        assertThat(item.getString("creationDate"), is("2011-06-22 12:03:30.0"));
        assertThat(item.getString("description"), is("Other D"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/other/d/img.png"));
        assertThat(item.getString("id"), is("OTHER_D"));
        assertThat(item.getString("itemType"), is("OTHER_ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/other/d"));
        assertThat(item.getString("value"), is("0.12"));
    }

    @Test
    public void recommendationsForUser_filterItemType_shouldReturnRecommendationsOfSpecificItemType() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("userid", "USER_2");
        params.add("requesteditemtype", "ITEM");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("action"), is("recommendationsforuser"));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getString("userid"), is("USER_2"));

        JSONArray items = getSafeJSONArray(json, "recommendeditems", "item");
        JSONObject item = items.getJSONObject(0);

        assertThat(item.getString("creationDate"), is("2011-06-22 12:01:00.0"));
        assertThat(item.getString("description"), is("Item A"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/item/a/img.png"));
        assertThat(item.getString("id"), is("ITEM_A"));
        assertThat(item.getString("itemType"), is("ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/item/a"));
        assertThat(item.getString("value"), is("0.11"));
    }

    @Test
    public void recommendationsForUser_numberOfResults_shouldTruncateReults() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("userid", "USER_2");
        params.add("numberOfResults", "1");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("action"), is("recommendationsforuser"));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getString("userid"), is("USER_2"));

        JSONArray items = getSafeJSONArray(json, "recommendeditems", "item");

        assertThat(items.size(), is(1));

        JSONObject item = items.getJSONObject(0);

        assertThat(item.getString("creationDate"), is("2011-06-22 12:01:00.0"));
        assertThat(item.getString("description"), is("Item A"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/item/a/img.png"));
        assertThat(item.getString("id"), is("ITEM_A"));
        assertThat(item.getString("itemType"), is("ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/item/a"));
        assertThat(item.getString("value"), is("0.11"));
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void recommendationsForUser_wrongRequestedItemType_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("userid", "USER_2");
        params.add("requesteditemtype", "WRONG_ITEMTYPE");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("912"));
        assertThat(json.getJSONObject("error").getString("@message"),
                allOf(containsString("Operation failed"), containsString("itemType"), containsString("WRONG_ITEMTYPE"),
                        containsString("not found")));
    }

    @Test
    public void recommendationsForUser_wrongUser_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("userid", "WRONG_USER");
        params.add("itemtype", "ITEM");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("action"), is("recommendationsforuser"));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getString("userid"), is("WRONG_USER"));

        JSONArray items = getSafeJSONArray(json, "recommendeditems", "item");

        assertThat(items.size(), is(0));

        // TODO in the future this test case should return an error (ie when users are explicitly modelled)
//        assertThat(json, not(is(nullValue())));
//        assertThat(json.getJSONObject("error").getString("@code"), is("402"));
//        assertThat(json.getJSONObject("error").getString("@message"), containsString("User id not valid"));
    }
}
