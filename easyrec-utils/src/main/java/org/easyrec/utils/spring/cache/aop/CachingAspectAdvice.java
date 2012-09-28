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
package org.easyrec.utils.spring.cache.aop;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.Serializable;

/**
 * Aspect that caches method results.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Stephan Zavrel
 */

@Order(Ordered.HIGHEST_PRECEDENCE)
public class CachingAspectAdvice implements InitializingBean {
    private Log log = LogFactory.getLog(getClass());

    private Cache cache;

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    /**
     * Takes a method name and its arguments and stores the result in a cache.
     *
     * @param pjp the JoinPoint containing information about the intercepted method call
     * @return the result of the method call
     * @throws Throwable
     */
    public Object cacheMethodResult(ProceedingJoinPoint pjp) throws Throwable {
        String targetName = pjp.getTarget().getClass().getName();
        String methodName = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();
        Object result;

        Log logger = LogFactory.getLog(pjp.getTarget().getClass());

        if (logger.isDebugEnabled()) {
            logger.debug("looking for method " + methodName + " result in cache");
        }
        String cacheKey = getCacheKey(targetName, methodName, args);
        Element element = cache.get(cacheKey);
        if (element == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cache miss - calling intercepted method!");
            }
            result = pjp.proceed();
            if (result == null) return null;
            if (logger.isDebugEnabled()) {
                logger.debug("Caching new result!");
            }
            try {
                element = new Element(cacheKey, (Serializable) result);
            } catch (Exception e) {
                logger.debug("xxResult " + result + " for key: " + cacheKey + "..." + e.getMessage());
                e.printStackTrace();
            }

            cache.put(element);
        }

        assert element != null;

        return element.getValue();
    }

    /**
     * Generates a unique key from the given arguments for storing a method result in a cache.
     *
     * @param targetName name of the object (bean) on which a method is called
     * @param methodName name of the method called
     * @param arguments  the arguments passed to the method called
     * @return a unique key for the cache entry
     */
    private String getCacheKey(String targetName, String methodName, Object[] arguments) {

        StringBuilder sb = new StringBuilder();

        sb.append(targetName).append(".").append(methodName);
        for (Object object : arguments) {
            if (object != null) {
                sb.append(object.getClass().getName());
            }
            sb.append(object);
        }
        return sb.toString();
    }


    public void afterPropertiesSet() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Checking if Cache was set properly ...");
        }
        if (cache == null) {
            throw new IllegalArgumentException(
                    "A cache is required for the Caching Aspect to work! Use the 'cache' property to provide one!");
        }
        if (log.isDebugEnabled()) {
            log.debug("Cache found. Using cache " + cache.getName());
        }
        if (log.isInfoEnabled()) {
            log.info(this.getClass().getSimpleName() + " Aspect initialized!");
        }
    }
}
