/**Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

import org.easyrec.utils.spring.exception.annotation.MapThrowableToException;
import org.easyrec.utils.spring.log.annotation.IOLog;

/**
 * A dummy class for the purpose of testing the ThrowableToException and the IOLog aspect.
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
@MapThrowableToException(exceptionClazz = TestException.class)
public class ThrowableToExceptionAnnotationDummy {
    /**
     * Throws a new Exception to test if the mapping Aspect works correctly.
     *
     * @param message a message printed to the log.
     * @throws Exception
     */
    public void throwEx(String message) throws Exception {
        throw new Exception(message);
    }


    /**
     * A method that takes two arguments and returns a String. The method has no other
     * functionality and performs no action on the given parameters. Its only purpose is
     * to test if the IOLog aspect writes the arguments correctly to the log.
     *
     * @param testInt    an Integer value
     * @param testString a String value
     * @return the constant String 'This is the result!
     * @throws Exception
     */
    @IOLog("info")
    public String testIOLog(Integer testInt, String testString) throws Exception {
        return "This is the result!";
    }

}
