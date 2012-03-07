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

package org.easyrec.rest.api_v_1_0.action;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.test.framework.spi.container.TestContainerException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.easyrec.rest.api_v_1_0.AbstractApiTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;

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
public abstract class RateTest extends AbstractApiTest {

    public static class JSON extends RateTest {
        public JSON() throws TestContainerException { super(METHOD_JSON); }
    }

    public static class XML extends RateTest {
        public XML() throws TestContainerException { super(METHOD_XML); }
    }

    public RateTest(String method) throws TestContainerException {
        super("rate", method);
    }

    @Test
    public void rate_noParameters_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));

        JSONArray errors = getSafeJSONArray(json, "error", "error");

        JSONObject error = errors.getJSONObject(0);
        assertThat(error.getString("@code"), is("299"));
        assertThat(error.getString("@message"),
                containsString("Wrong APIKey/Tenant combination"));

        error = errors.getJSONObject(1);
        assertThat(error.getString("@code"), is("301"));
        assertThat(error.getString("@message"), containsString("id required"));

        error = errors.getJSONObject(2);
        assertThat(error.getString("@code"), is("303"));
        assertThat(error.getString("@message"), containsString("requires a description"));

        error = errors.getJSONObject(3);
        assertThat(error.getString("@code"), is("304"));
        assertThat(error.getString("@message"), containsString("requires a URL"));

        error = errors.getJSONObject(4);
        assertThat(error.getString("@code"), is("401"));
        assertThat(error.getString("@message"), containsString("session id required"));

        error = errors.getJSONObject(5);
        assertThat(error.getString("@code"), is("305"));
        assertThat(error.getString("@message"), containsString("Rating Value must be a valid Integer"));
    }

    @Test
    public void rate_wrongTenant_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", "WRONG-TENANT");
        params.add("apikey", API_KEY);
        params.add("sessionid", "TEST_SESSION");
        params.add("itemid", "TEST_A");
        params.add("itemdescription", "Test A");
        params.add("itemurl", "http://testtenant.com/test/test/a");
        params.add("ratingvalue", "5");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("299"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Wrong APIKey/Tenant combination"));
    }

    @Test
    public void rate_wrongApiKey_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", "0123456789abcdefedcba98765432100");
        params.add("sessionid", "TEST_SESSION");
        params.add("itemid", "TEST_A");
        params.add("itemdescription", "Test A");
        params.add("itemurl", "http://testtenant.com/test/test/a");
        params.add("ratingvalue", "5");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("299"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Wrong APIKey/Tenant combination"));
    }

    @Test
    public void rate_noSessionId_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("itemid", "TEST_A");
        params.add("itemdescription", "Test A");
        params.add("itemurl", "http://testtenant.com/test/test/a");
        params.add("ratingvalue", "5");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("401"));
        assertThat(json.getJSONObject("error").getString("@message"), containsString("session id required"));
    }

    @Test
    public void rate_noItemId_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("sessionid", "TEST_SESSION");
        params.add("itemdescription", "Test A");
        params.add("itemurl", "http://testtenant.com/test/test/a");
        params.add("ratingvalue", "5");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("301"));
        assertThat(json.getJSONObject("error").getString("@message"), containsString("id required"));
    }

    @Test
    public void rate_noItemDescription_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("sessionid", "TEST_SESSION");
        params.add("itemid", "TEST_A");
        params.add("itemurl", "http://testtenant.com/test/test/a");
        params.add("ratingvalue", "5");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("303"));
        assertThat(json.getJSONObject("error").getString("@message"), containsString("requires a description"));
    }

    @Test
    public void rate_noItemUrl_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("sessionid", "TEST_SESSION");
        params.add("itemid", "TEST_A");
        params.add("itemdescription", "Test A");
        params.add("ratingvalue", "5");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("304"));
        assertThat(json.getJSONObject("error").getString("@message"), containsString("requires a URL"));
    }

    @Test
    public void rate_noRatingValue_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("sessionid", "TEST_SESSION");
        params.add("itemid", "TEST_A");
        params.add("itemdescription", "Test A");
        params.add("itemurl", "http://testtenant.com/test/test/a");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("305"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Rating Value must be a valid Integer"));
    }

    @Test
    public void rate_stringRatingValue_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("sessionid", "TEST_SESSION");
        params.add("itemid", "TEST_A");
        params.add("itemdescription", "Test A");
        params.add("itemurl", "http://testtenant.com/test/test/a");
        params.add("ratingvalue", "five");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("305"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Rating Value must be a valid Integer"));
    }

    @Test
    public void rate_doubleRatingValue_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("sessionid", "TEST_SESSION");
        params.add("itemid", "TEST_A");
        params.add("itemdescription", "Test A");
        params.add("itemurl", "http://testtenant.com/test/test/a");
        params.add("ratingvalue", "5.0");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("305"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Rating Value must be a valid Integer"));
    }

    @Test
    public void rate_tooLittleRatingValue_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("sessionid", "TEST_SESSION");
        params.add("itemid", "TEST_A");
        params.add("itemdescription", "Test A");
        params.add("itemurl", "http://testtenant.com/test/test/a");
        params.add("ratingvalue", "-1");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("305"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Rating Value must be a valid Integer"));
    }

    @Test
    public void rate_tooBigRatingValue_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("sessionid", "TEST_SESSION");
        params.add("itemid", "TEST_A");
        params.add("itemdescription", "Test A");
        params.add("itemurl", "http://testtenant.com/test/test/a");
        params.add("ratingvalue", "11");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("305"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Rating Value must be a valid Integer"));
    }

    @Test
    @ExpectedDataSet("/dbunit/web/rest/expected_rate_1.xml")
    public void rate_withRequiredParameters_shouldCreateViewActionAndItem() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("sessionid", "TEST_SESSION");
        params.add("itemid", "TEST_A");
        params.add("itemdescription", "Test A");
        params.add("itemurl", "http://testtenant.com/test/test/a");
        params.add("ratingvalue", "5");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("action"), is("rate"));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getString("sessionid"), is("TEST_SESSION"));
        assertThat(json.getString("ratingValue"), is("5"));
        assertThat(json.getJSONObject("item").getString("id"), is("TEST_A"));
        assertThat(json.getJSONObject("item").getString("itemType"), is("ITEM"));
        assertThat(json.getJSONObject("item").getString("description"), is("Test A"));
        assertThat(json.getJSONObject("item").getString("url"), is("http://testtenant.com/test/test/a"));
    }

    @Test
    @ExpectedDataSet("/dbunit/web/rest/expected_rate_2.xml")
    public void rate_withUserIdAndOtherItemTypeAndExtraInfo_shouldCreateViewActionAndItem() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("sessionid", "TEST_SESSION");
        params.add("itemid", "TEST_OTHER_A");
        params.add("itemtype", "OTHER_ITEM");
        params.add("itemdescription", "Test Other A");
        params.add("itemurl", "http://testtenant.com/test/test_other/a");
        params.add("ratingvalue", "5");
        params.add("userid", "USER_1");
        params.add("itemimageurl", "http://testtenant.com/test/test_other/a/img.png");
        params.add("actiontime", "01_01_2011_23_59_59");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("action"), is("rate"));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getString("userid"), is("USER_1"));
        assertThat(json.getString("sessionid"), is("TEST_SESSION"));
        assertThat(json.getString("ratingValue"), is("5"));
        assertThat(json.getJSONObject("item").getString("id"), is("TEST_OTHER_A"));
        assertThat(json.getJSONObject("item").getString("itemType"), is("OTHER_ITEM"));
        assertThat(json.getJSONObject("item").getString("description"), is("Test Other A"));
        assertThat(json.getJSONObject("item").getString("url"), is("http://testtenant.com/test/test_other/a"));
    }
}
