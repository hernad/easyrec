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
package org.easyrec.utils.spring.exception;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Unit test for ThrowableToException Aspect and IOLog Aspect.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fr, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Stephan Zavrel
 */
public class MapThrowableToExceptionTest {
    private ThrowableToExceptionAnnotationDummy dummy;


    @Before
    public void setup() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "/spring/utils/aop/MapThrowableToExceptionTest.xml");
        dummy = (ThrowableToExceptionAnnotationDummy) context.getBean("exceptionTestDummy");
    }

    // setter for dependency injection
    public void setThrowableToExceptionAnnotationDummy(ThrowableToExceptionAnnotationDummy dummy) {
        this.dummy = dummy;
    }

    @Test
    public void TestAnnotation() {
        try {
            dummy.throwEx("test message");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestIOLog() {
        try {
            dummy.testIOLog(23, "Jordan");
        } catch (Exception e) {

        }
    }

}
