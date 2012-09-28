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
package org.easyrec.utils.spring.log;

import org.apache.commons.logging.Log;

/**
 * A simple util class for writing log messages to specific log levels.
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
public class LoggerUtils {

    /**
     * @param logger
     * @param logLevel
     * @return
     */
    public static boolean isLogLevelEnabled(Log logger, String logLevel) {
        if (logLevel.equalsIgnoreCase("info")) {
            if (logger.isInfoEnabled()) {
                return true;
            }
        } else if (logLevel.equalsIgnoreCase("debug")) {
            if (logger.isDebugEnabled()) {
                return true;
            }
        } else if (logLevel.equalsIgnoreCase("error")) {
            if (logger.isErrorEnabled()) {
                return true;
            }
        } else if (logLevel.equalsIgnoreCase("trace")) {
            if (logger.isTraceEnabled()) {
                return true;
            }
        } else if (logLevel.equalsIgnoreCase("warn")) {
            if (logger.isWarnEnabled()) {
                return true;
            }
        } else if (logLevel.equalsIgnoreCase("fatal")) {
            if (logger.isFatalEnabled()) {
                return true;
            }
        } else {
            logger.warn("Passed unknown log level '" + logLevel + "' to Aspect - returning false!");
            return false;
        }
        logger.warn("log level '" + logLevel + "' not enabled - returning false!");
        return false;
    }

    /**
     * Writes the given 'message' to the Log 'logger' with level 'logLevel'.
     *
     * @param logger   the Log to which the message is written
     * @param logLevel the level to which the message is written
     * @param message  the message to be written
     */
    public static void log(Log logger, String logLevel, String message) {
        if (logLevel.equalsIgnoreCase("info")) {
            if (logger.isInfoEnabled()) {
                logger.info(message);
            }
        } else if (logLevel.equalsIgnoreCase("debug")) {
            if (logger.isDebugEnabled()) {
                logger.debug(message);
            }
        } else if (logLevel.equalsIgnoreCase("error")) {
            if (logger.isErrorEnabled()) {
                logger.error(message);
            }
        } else if (logLevel.equalsIgnoreCase("trace")) {
            if (logger.isTraceEnabled()) {
                logger.trace(message);
            }
        } else if (logLevel.equalsIgnoreCase("warn")) {
            if (logger.isWarnEnabled()) {
                logger.warn(message);
            }
        } else if (logLevel.equalsIgnoreCase("fatal")) {
            if (logger.isFatalEnabled()) {
                logger.fatal(message);
            }
        } else {
            logger.error("Passed unknown log level " + logLevel + " to Aspect - logging to error instead!");
            logger.error(message);
        }
    }

    /**
     * Writes the given 'message' to the Log 'logger' with level 'logLevel'.
     *
     * @param logger   the Log to which the message is written
     * @param logLevel the level to which the message is written
     * @param message  the message to be written
     * @param ta       a Throwable passed on to the Log
     */
    public static void log(Log logger, String logLevel, String message, Throwable ta) {
        if (logLevel.equalsIgnoreCase("info")) {
            if (logger.isInfoEnabled()) {
                logger.info(message, ta);
            }
        } else if (logLevel.equalsIgnoreCase("debug")) {
            if (logger.isDebugEnabled()) {
                logger.debug(message, ta);
            }
        } else if (logLevel.equalsIgnoreCase("error")) {
            if (logger.isErrorEnabled()) {
                logger.error(message, ta);
            }
        } else if (logLevel.equalsIgnoreCase("trace")) {
            if (logger.isTraceEnabled()) {
                logger.trace(message, ta);
            }
        } else if (logLevel.equalsIgnoreCase("warn")) {
            if (logger.isWarnEnabled()) {
                logger.warn(message, ta);
            }
        } else if (logLevel.equalsIgnoreCase("fatal")) {
            if (logger.isFatalEnabled()) {
                logger.fatal(message, ta);
            }
        } else {
            logger.error("Passed unknown log level " + logLevel + " to Aspect - logging to error instead!");
            logger.error(message, ta);
        }
    }
}
