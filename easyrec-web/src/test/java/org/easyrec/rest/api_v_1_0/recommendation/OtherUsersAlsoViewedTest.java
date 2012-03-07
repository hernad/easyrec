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
public abstract class OtherUsersAlsoViewedTest extends AbstractApiTest {

    public static class JSON extends OtherUsersAlsoViewedTest {
        public JSON() throws TestContainerException { super(METHOD_JSON); }
    }

    public static class XML extends OtherUsersAlsoViewedTest {
        public XML() throws TestContainerException { super(METHOD_XML); }
    }

    public OtherUsersAlsoViewedTest(String method) throws TestContainerException {
        super("otherusersalsoviewed", method);
    }

    @Test
    public void otherUsersAlsoViewed_noParameters_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("299"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Wrong APIKey/Tenant combination"));
    }

    @Test
    public void otherUsersAlsoViewed_wrongTenant_shouldReturnError() {
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
    public void otherUsersAlsoViewed_wrongApiKey_shouldReturnError() {
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
    public void otherUsersAlsoViewed_onlyApiKeyTenant_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("300"));
        assertThat(json.getJSONObject("error").getString("@message"), containsString("Item does not exist"));
    }

    @Test
    public void otherUsersAlsoViewed_noFilter_shouldReturnRecommendationsOfAllItemTypes() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("itemid", "ITEM_A");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("action"), is("otherusersalsoviewed"));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getJSONObject("baseitem").getString("creationDate"), is("2011-06-22 12:01:00.0"));
        assertThat(json.getJSONObject("baseitem").getString("description"), is("Item A"));
        assertThat(json.getJSONObject("baseitem").getString("imageUrl"),
                is("http://testtenant.com/test/item/a/img.png"));
        assertThat(json.getJSONObject("baseitem").getString("id"), is("ITEM_A"));
        assertThat(json.getJSONObject("baseitem").getString("itemType"), is("ITEM"));
        assertThat(json.getJSONObject("baseitem").getString("url"), is("http://testtenant.com/test/item/a"));

        JSONArray items = getSafeJSONArray(json, "recommendeditems", "item");

        assertThat(items.size(), is(2));

        JSONObject item = items.getJSONObject(0);

        assertThat(item.getString("creationDate"), is("2011-06-22 12:04:00.0"));
        assertThat(item.getString("description"), is("Other A"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/other/a/img.png"));
        assertThat(item.getString("id"), is("OTHER_A"));
        assertThat(item.getString("itemType"), is("OTHER_ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/other/a"));
        assertThat(item.getString("value"), is("0.09"));

        item = items.getJSONObject(1);

        assertThat(item.getString("creationDate"), is("2011-06-22 12:02:00.0"));
        assertThat(item.getString("description"), is("Item B"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/item/b/img.png"));
        assertThat(item.getString("id"), is("ITEM_B"));
        assertThat(item.getString("itemType"), is("ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/item/b"));
        assertThat(item.getString("value"), is("0.06"));
    }

    @Test
    public void otherUsersAlsoViewed_filterItemType_shouldReturnRecommendationsOfSpecificItemType() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("itemid", "ITEM_A");
        params.add("requesteditemtype", "ITEM");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("action"), is("otherusersalsoviewed"));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getJSONObject("baseitem").getString("creationDate"), is("2011-06-22 12:01:00.0"));
        assertThat(json.getJSONObject("baseitem").getString("description"), is("Item A"));
        assertThat(json.getJSONObject("baseitem").getString("imageUrl"),
                is("http://testtenant.com/test/item/a/img.png"));
        assertThat(json.getJSONObject("baseitem").getString("id"), is("ITEM_A"));
        assertThat(json.getJSONObject("baseitem").getString("itemType"), is("ITEM"));
        assertThat(json.getJSONObject("baseitem").getString("url"), is("http://testtenant.com/test/item/a"));

        JSONArray items = getSafeJSONArray(json, "recommendeditems", "item");
        JSONObject item = items.getJSONObject(0);

        assertThat(item.getString("creationDate"), is("2011-06-22 12:02:00.0"));
        assertThat(item.getString("description"), is("Item B"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/item/b/img.png"));
        assertThat(item.getString("id"), is("ITEM_B"));
        assertThat(item.getString("itemType"), is("ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/item/b"));
        assertThat(item.getString("value"), is("0.06"));
    }

    @Test
    public void otherUsersAlsoViewed_filterUser_shouldFilterRecommendationsWhereUserTookAction() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("itemid", "ITEM_A");
        params.add("userid", "USER_2");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("action"), is("otherusersalsoviewed"));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getJSONObject("baseitem").getString("creationDate"), is("2011-06-22 12:01:00.0"));
        assertThat(json.getJSONObject("baseitem").getString("description"), is("Item A"));
        assertThat(json.getJSONObject("baseitem").getString("imageUrl"),
                is("http://testtenant.com/test/item/a/img.png"));
        assertThat(json.getJSONObject("baseitem").getString("id"), is("ITEM_A"));
        assertThat(json.getJSONObject("baseitem").getString("itemType"), is("ITEM"));
        assertThat(json.getJSONObject("baseitem").getString("url"), is("http://testtenant.com/test/item/a"));

        JSONArray items = getSafeJSONArray(json, "recommendeditems", "item");

        assertThat(items.size(), is(1));
        JSONObject item = items.getJSONObject(0);

        assertThat(item.getString("creationDate"), is("2011-06-22 12:04:00.0"));
        assertThat(item.getString("description"), is("Other A"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/other/a/img.png"));
        assertThat(item.getString("id"), is("OTHER_A"));
        assertThat(item.getString("itemType"), is("OTHER_ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/other/a"));
        assertThat(item.getString("value"), is("0.09"));
    }

    @Test
    public void otherUsersAlsoViewed_numberOfResults_shouldTruncateReults() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("itemid", "ITEM_A");
        params.add("numberOfResults", "1");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("action"), is("otherusersalsoviewed"));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getJSONObject("baseitem").getString("creationDate"), is("2011-06-22 12:01:00.0"));
        assertThat(json.getJSONObject("baseitem").getString("description"), is("Item A"));
        assertThat(json.getJSONObject("baseitem").getString("imageUrl"),
                is("http://testtenant.com/test/item/a/img.png"));
        assertThat(json.getJSONObject("baseitem").getString("id"), is("ITEM_A"));
        assertThat(json.getJSONObject("baseitem").getString("itemType"), is("ITEM"));
        assertThat(json.getJSONObject("baseitem").getString("url"), is("http://testtenant.com/test/item/a"));

        JSONArray items = getSafeJSONArray(json, "recommendeditems", "item");

        assertThat(items.size(), is(1));

        JSONObject item = items.getJSONObject(0);

        assertThat(item.getString("creationDate"), is("2011-06-22 12:04:00.0"));
        assertThat(item.getString("description"), is("Other A"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/other/a/img.png"));
        assertThat(item.getString("id"), is("OTHER_A"));
        assertThat(item.getString("itemType"), is("OTHER_ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/other/a"));
        assertThat(item.getString("value"), is("0.09"));
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void otherUsersAlsoViewed_wrongItemType_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("itemid", "ITEM_A");
        params.add("itemtype", "WRONG_ITEMTYPE");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("912"));
        assertThat(json.getJSONObject("error").getString("@message"),
                allOf(containsString("Operation failed"), containsString("itemType"), containsString("WRONG_ITEMTYPE"),
                        containsString("not found")));
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void otherUsersAlsoViewed_wrongRequestedItemType_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("itemid", "ITEM_A");
        params.add("itemtype", "ITEM");
        params.add("requesteditemtype", "WRONG_ITEMTYPE");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("912"));
        assertThat(json.getJSONObject("error").getString("@message"),
                allOf(containsString("Operation failed"), containsString("itemType"), containsString("WRONG_ITEMTYPE"),
                        containsString("not found")));
    }

    @Test
    public void otherUsersAlsoViewed_wrongItem_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("itemid", "WRONG_ITEM");
        params.add("itemtype", "ITEM");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("300"));
        assertThat(json.getJSONObject("error").getString("@message"), containsString(
                "does not exist"));
    }

    @Test
    public void otherUsersAlsoViewed_deactivatedItem_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("itemid", "ITEM_C");
        params.add("itemtype", "ITEM");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("306"));
        assertThat(json.getJSONObject("error").getString("@message"), containsString("inactive"));
    }
}
