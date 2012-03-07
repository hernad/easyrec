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

package org.easyrec;

import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.AfterClass;

/**
 * Do not restart app context after each test but only after each test class.
 * See <a href="http://java.net/jira/browse/JERSEY-705"/>
 * <p/>
 * <p><b>Company:</b>SAT, Research Studios Austria</p>
 * <p><b>Copyright:</b> (c) 2011</p>
 * <p><b>last modified:</b><br/>
 * $Author: $<br/>
 * $Date: $<br/>
 * $Revision: $</p>
 *
 * @author patrick
 */
public class FastJerseyTest extends JerseyTest {

    private static FastJerseyTest self = null;

    public FastJerseyTest(WebAppDescriptor build) {
        super(build);
    }

    @Override
    public void setUp() throws Exception {
        if (self == null) {
            self = this;
            super.setUp();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        self.stopContainer();
                    } catch (Exception e) {
                        throw new RuntimeException("failed to stop container", e);
                    } finally {
                        self = null;
                    }
                }
            });
        }
    }

    @Override
    public void tearDown() throws Exception {}

    @AfterClass
    public static void stop() throws Exception {
        /*self.stopContainer();
        self = null;*/
    }

    private void stopContainer() throws Exception {
        super.tearDown();
    }
}
