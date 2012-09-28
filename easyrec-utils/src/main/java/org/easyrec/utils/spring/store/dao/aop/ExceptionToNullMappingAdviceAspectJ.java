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
package org.easyrec.utils.spring.store.dao.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * Advice (Interceptor) that causes DAO methods to return null if certain
 * DataAccessExceptions are thrown.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2006</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Florian Kleedorfer
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ExceptionToNullMappingAdviceAspectJ implements InitializingBean {
    protected Log logger = LogFactory.getLog(getClass());

    public Object mapExceptionToNull(ProceedingJoinPoint pjp) throws Throwable {

        try {
            if (logger.isTraceEnabled()) {
                logger.trace("DAO Advice: entering dao method");
            }
            return pjp.proceed();
        } catch (IncorrectResultSizeDataAccessException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("caught IncorrectResultSizeDataAccessException", e);
            }
            //      if (e.getActualSize() == 0) {
            //        if (logger.isDebugEnabled()) {
            //          logger.debug("returning null instead");
            //        }
            //        return null;
            //      }
            //throw e;
            return null;
        } catch (ObjectRetrievalFailureException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("caught ObjectRetrievalFailureException", e);
            }
            return null;
        } //finally {
        //if (logger.isTraceEnabled()) {
        //    logger.trace("DAO Advice: leaving dao method");
        //}
        //}
    }

    public void afterPropertiesSet() throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info(this.getClass().getSimpleName() + " Aspect initialized!");
        }
    }

}
