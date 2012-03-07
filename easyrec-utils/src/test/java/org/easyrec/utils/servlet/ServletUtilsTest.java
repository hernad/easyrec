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
package org.easyrec.utils.servlet;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.internal.NamedSequence;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests for the {@link org.easyrec.utils.servlet.ServletUtils} class.
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2007
 * </p>
 * <p>
 * <b>last modified:</b><br/> $Author: sat-rsa $<br/> $Date: 2011-08-12 18:40:54 +0200 (Fr, 12 Aug 2011) $<br/> $Revision: 119 $
 * </p>
 *
 * @author Florian Kleedorfer
 */
@RunWith(JMock.class)
public class ServletUtilsTest {

    private Map<String, String> stringParams = new HashMap<String, String>();
    private Map<String, String> intParams = new HashMap<String, String>();
    private Mockery context = new JUnit4Mockery();

    @Before
    public void setUp() throws Exception {
        stringParams.put("param1", "value1");
        stringParams.put("param2", "value2");
        stringParams.put("param3", null);
        intParams.put("param1", "1");
        intParams.put("param2", "2");
        intParams.put("param3", null);
    }

    @Test
    public void testGetSafeParameterString() {
        final HttpServletRequest request = context.mock(HttpServletRequest.class);

        context.checking(new Expectations() {

            {
                Sequence getParams = new NamedSequence("getParams");

                for (Map.Entry<String, String> entry : stringParams.entrySet()) {
                    oneOf(request).getParameter(entry.getKey());
                    inSequence(getParams);
                    will(returnValue(entry.getValue()));
                }
            }
        });

        String value = ServletUtils.getSafeParameter(request, "param1", "default");
        assertThat("value1", equalTo(value));
        value = ServletUtils.getSafeParameter(request, "param2", "default");
        assertThat("value2", equalTo(value));
        value = ServletUtils.getSafeParameter(request, "param3", "default");
        assertThat("default", equalTo(value));
    }

    @Test
    public void testGetSafeParameterInt() {
        final HttpServletRequest request = context.mock(HttpServletRequest.class);

        context.checking(new Expectations() {

            {
                Sequence getParams = new NamedSequence("getParams");

                for (Map.Entry<String, String> entry : intParams.entrySet()) {
                    oneOf(request).getParameter(entry.getKey());
                    inSequence(getParams);
                    will(returnValue(entry.getValue()));
                }
            }
        });

        int value = ServletUtils.getSafeParameter(request, "param1", 0);
        assertEquals(1, value);
        value = ServletUtils.getSafeParameter(request, "param2", 0);
        assertEquals(2, value);
        value = ServletUtils.getSafeParameter(request, "param3", 0);
        assertEquals(0, value);
    }
}
