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
package org.easyrec.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.utils.spring.profile.aop.JamonProfilingAspectAdvice;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;

/**
 * Utility class that sets the correct path for the JamonProfilingAspect
 * output (within the current webapp).
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-11 11:04:49 +0100 (Fr, 11 Feb 2011) $<br/>
 * $Revision: 17656 $</p>
 *
 * @author Roman Cerny
 */
public class WebAppPathHolder implements InitializingBean {
    private Log logger = LogFactory.getLog(this.getClass());

    private JamonProfilingAspectAdvice jamonProfilingAspectAdvice;

    private String reportOutputLocation;

    public void afterPropertiesSet() throws Exception {
        String currentPathToClazz = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String currentWebAppPath = currentPathToClazz.substring(0, currentPathToClazz.indexOf("WEB-INF") - 1);

        String outputLocation = reportOutputLocation;
        if (!outputLocation.startsWith(File.separator)) {
            outputLocation = File.separator + outputLocation;
        }
        jamonProfilingAspectAdvice.setReportOutputLocation(currentWebAppPath + outputLocation);

        if (logger.isInfoEnabled()) {
            logger.info(this.getClass().getSimpleName() +
                    " succesfully changed report output location of JamonProfiling aspect to '" +
                    jamonProfilingAspectAdvice.getReportOutputLocation() + "'");
        }
    }

    public JamonProfilingAspectAdvice getJamonProfilingAspectAdvice() {
        return jamonProfilingAspectAdvice;
    }

    public void setJamonProfilingAspectAdvice(JamonProfilingAspectAdvice jamonProfilingAspectAdvice) {
        this.jamonProfilingAspectAdvice = jamonProfilingAspectAdvice;
    }

    public String getReportOutputLocation() {
        return reportOutputLocation;
    }

    public void setReportOutputLocation(String reportOutputLocation) {
        this.reportOutputLocation = reportOutputLocation;
    }


}
