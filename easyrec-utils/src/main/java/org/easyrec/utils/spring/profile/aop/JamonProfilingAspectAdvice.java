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
package org.easyrec.utils.spring.profile.aop;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.easyrec.utils.spring.profile.annotation.Profiled;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.IOException;

/**
 * Provides profiling using the JAMON library via an aspect. It profiles the
 * specified methods and creates an output html file containing a report.
 * The settable properties of the aspect govern the generation of the report.
 * <p/>
 * Look into the file spring/spring.sat-util.aop-profiling.xml
 *
 * @author Florian Kleedorfer
 */

public class JamonProfilingAspectAdvice implements DisposableBean, InitializingBean {

    public static final String GROUPING_CLASS = "byClass";
    public static final String GROUPING_METHOD = "byMethod";
    public static final String GROUPING_DEFAULT = GROUPING_METHOD;

    public static final String DEFAULT_REPORT_OUTPUT_LOCATION = "jamonreport.html";
    private static final long DEFAULT_OUTPUT_INTERVAL = 3600000;

    private Log logger = LogFactory.getLog(getClass());

    // settable properties
    private String reportOutputLocation = DEFAULT_REPORT_OUTPUT_LOCATION;

    private long outputInterval = DEFAULT_OUTPUT_INTERVAL;

    private String grouping = GROUPING_DEFAULT;

    private File outfile;
    private long lastOutput = System.currentTimeMillis();

    public Object profileInvocationByAnnotation(ProceedingJoinPoint pjp, Profiled prof) throws Throwable {
        return profileInvocation(pjp);
    }

    public Object profileInvocation(ProceedingJoinPoint pjp) throws Throwable {
        if (logger.isDebugEnabled()) {
            logger.debug("profiling call for method" + pjp.toLongString());
        }
        Monitor mon = MonitorFactory.start(createMonitorName(pjp));
        try {
            return pjp.proceed();
        } finally {
            mon.stop();
            long now = System.currentTimeMillis();
            if (now - lastOutput > outputInterval) {
                lastOutput = now;
                try {
                    outputStats();
                } catch (IOException ioe) {
                    logger.warn("error writing to '" + getReportOutputLocation() + "'", ioe);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        createOutputFile();

        if (logger.isInfoEnabled()) {
            logger.info(this.getClass().getSimpleName() + " Aspect initialized!");
        }
    }

    private void createOutputFile() {
        if ((reportOutputLocation == null) || (reportOutputLocation.length() == 0) ||
                (reportOutputLocation.equals(""))) {
            if (logger.isDebugEnabled()) {
                logger.debug("No filename for report output file specified!");
            }
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("Checking if reportOutputLocation '" + reportOutputLocation + "'is writable...");
        }
        outfile = new File(this.reportOutputLocation);
        if (outfile.exists() && !outfile.canWrite()) {
            throw new IllegalArgumentException(
                    "Cannot write to specified output location '" + reportOutputLocation + "'!");
        }

        if (!outfile.getParentFile().exists()) {
            //build directory structure to outfile
            outfile.getParentFile().mkdirs();
        }

        if (!outfile.exists()) {
            try {
                outfile.createNewFile();
            } catch (IOException e) {
                throw new IllegalArgumentException(
                        "could not create file '" + reportOutputLocation + "':" + e.getMessage(), e);
            }

        }

        if (logger.isDebugEnabled()) {
            logger.debug("reportOutputLocation is writable.");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        outputStats();
    }

    /**
     * @throws IOException
     */
    private void outputStats() throws IOException {
        if (logger.isInfoEnabled()) {
            logger.info("producing html output and writing to '" + reportOutputLocation + "'");
        }
        if (outfile == null) throw new IllegalStateException("Outfile parameter must not be null!");
        String report = MonitorFactory.getRootMonitor().getReport();
        FileUtils.writeStringToFile(outfile, report);
        if (logger.isDebugEnabled()) {
            logger.debug("html output successfully written to '" + reportOutputLocation + "'");
        }
    }

    /**
     * @param pjp
     * @return
     */
    private String createMonitorName(ProceedingJoinPoint pjp) {
        if (GROUPING_CLASS.equals(grouping)) {
            return pjp.toLongString();
        } else if (GROUPING_METHOD.equals(grouping)) {
            return pjp.getSignature().toLongString();
        } else {
            throw new IllegalStateException(
                    "parameter 'grouping' must be one of [" + GROUPING_CLASS + "," + GROUPING_METHOD + "]");
        }
    }

    public String getReportOutputLocation() {
        return reportOutputLocation;
    }

    public void setReportOutputLocation(String reportOutputLocation) {
        this.reportOutputLocation = reportOutputLocation;
        createOutputFile();
    }

    public long getOutputInterval() {
        return outputInterval;
    }

    public void setOutputInterval(long outputInterval) {
        this.outputInterval = outputInterval;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }
}
