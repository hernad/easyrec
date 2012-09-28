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
package org.easyrec.utils.spring.cache.aop;

import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Aspect that resets all the caches managed by the given CacheManager.
 * USE WITH CAUTION: LEADS TO TEMPORARY PERFORMANCE DECREASE!!!!!!!!!!
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
public class CacheResetAspectAdvice implements InitializingBean {

    private Log log = LogFactory.getLog(getClass());

    private CacheManager cacheManager;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void clearCaches() {
        if (log.isDebugEnabled()) {
            log.debug("!!!!!!!!!!!!! CLEARING ALL CACHES !!!!!!!!!!!!!!!!!");
        }
        cacheManager.clearAll();
    }

    public void afterPropertiesSet() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Checking if CacheManager was set properly ...");
        }
        if (cacheManager == null) {
            throw new IllegalArgumentException(
                    "A CacheManager is required for the CacheReset Aspect to work! Use the 'cacheManager' property to provide one!");
        }
        if (log.isInfoEnabled()) {
            log.info(this.getClass().getSimpleName() + " Aspect initialized!");
        }
    }

}
