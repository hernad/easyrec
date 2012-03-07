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

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerException;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.FastJerseyTest;
import org.springframework.web.context.ContextLoaderListener;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

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
public abstract class AbstractApiTest extends FastJerseyTest {

    public static final String DATASET_BASE = "/dbunit/web/rest/base.xml";

    public static final String TENANT_ID = "REST-Test";
    public static final String API_KEY = "deadbeef00feedface00cafe00cafe00";

    private static WebAppDescriptor webAppDescriptor = new WebAppDescriptor.Builder()
            .contextPath("/")
            .contextParam("contextConfigLocation", "classpath:/spring/web/restTestContext.xml")
            .servletClass(SpringServlet.class)
            .contextListenerClass(ContextLoaderListener.class)
            .build();

    private Log log = LogFactory.getLog(AbstractApiTest.class);

    private final String method;
    private final String apiFunction;

    public final static String METHOD_JSON = "json/";
    public final static String METHOD_XML = "";

    public AbstractApiTest(String apiFunction) throws TestContainerException {
        this(apiFunction, "");
    }

    public AbstractApiTest(String apiFunction, String method) throws TestContainerException {
        super(webAppDescriptor);

        this.apiFunction = apiFunction;
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public String getApiFunction() {
        return apiFunction;
    }

    public WebResource.Builder makeAPIResource() {
        return makeAPIResource(null);
    }

    public WebResource.Builder makeAPIResource(@Nullable MultivaluedMap<String, String> params) {
        return makeAPIResource(apiFunction, params);
    }

    public WebResource.Builder makeAPIResource(String apiFunction, @Nullable MultivaluedMap<String, String> params) {
        WebResource resource = resource();
        String requestUrl = "1.0/" + method + apiFunction;

        return params != null ? resource.path(requestUrl).queryParams(params).accept(MediaType.APPLICATION_XML_TYPE)
                              : resource.path(requestUrl).accept(MediaType.APPLICATION_XML_TYPE);
    }

    public JSONObject makeAPIRequest(@Nullable MultivaluedMap<String, String> params) {
        return makeAPIRequest(apiFunction, params);
    }

    public JSONObject makeAPIRequest(String apiFunction, @Nullable MultivaluedMap<String, String> params) {
        ClientResponse clientResponse = makeAPIResource(apiFunction, params).get(ClientResponse.class);

        log.info(clientResponse.getLocation());

        String response = clientResponse.getEntity(String.class);

        System.out.println(response);
        log.info("response: " + response);

        if (!method.equals("json/")) {
            XMLSerializer xmlSerializer = new XMLSerializer();
            JSON json = xmlSerializer.read(response);

            System.out.println(json);

            return JSONObject.fromObject(json);
        } else
            return JSONObject.fromObject(response);
    }

    public JSONArray getSafeJSONArray(JSONObject jsonObject, String key, String itemKey) {
        JSON json = (JSON) jsonObject.get(key);

        if (json.isArray()) return (JSONArray) json;

        if (json instanceof JSONNull)
            return new JSONArray();

        json = (JSON) ((JSONObject) json).get(itemKey);

        if (json.isArray()) return (JSONArray) json;

        if (json instanceof JSONNull)
            return new JSONArray();

        JSONArray jsonArray = new JSONArray();
        jsonArray.add(0, json);

        return jsonArray;
    }
}
