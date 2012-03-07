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
package org.easyrec.utils.spring.exception.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.easyrec.utils.spring.exception.annotation.MapThrowableToException;
import org.easyrec.utils.spring.log.LoggerUtils;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Constructor;

/**
 * <DESCRIPTION>
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
public class ThrowableToExceptionAspectAdvice implements InitializingBean {
    private Log log = LogFactory.getLog(getClass());

    public Object mapToException(ProceedingJoinPoint pjp, MapThrowableToException mtte) throws Exception {

        try {
            return pjp.proceed();
        } catch (Throwable ta) {
            Log logger = LogFactory.getLog(pjp.getTarget().getClass());
            Constructor<? extends Exception> cons;
            Exception ex = null;
            LoggerUtils.log(logger, mtte.logLevel(), "Aspect caught Exception", ta);
            try {
                cons = mtte.exceptionClazz().getConstructor(String.class);
                ex = cons.newInstance((ta.getMessage()));
            } catch (NoSuchMethodException nsme) {
                logger.error("The exception passed to the aspect does not provide a constructor(String message)!",
                        nsme);
            } catch (Exception e) {
                logger.error("Error instantiating aspect Exception, throwing original instead", e);
                throw (Exception) ta;
            }
            throw ex;
        }
    }

    public void afterPropertiesSet() throws Exception {
        if (log.isInfoEnabled()) {
            log.info(this.getClass().getSimpleName() + " Aspect initialized!");
        }
    }
}
