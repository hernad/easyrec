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
package org.easyrec.utils.io.autoimport.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.utils.io.autoimport.AutoImportCommand;
import org.easyrec.utils.io.autoimport.AutoImportService;
import org.easyrec.utils.io.autoimport.AutoImportTimerTask;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;

/**
 * Implementation of the { @link at.researchstudio.sat.utils.io.autoimport.AutoImportService } interface.
 * Provides activation/deactivation of the automatic import, as well as changing parameters (timeout, directory).
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
 * @author Roman Cerny
 */
public class AutoImportServiceImpl implements AutoImportService {
    ///////////////////////////////////////////////////////////////////////////    
    // members

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    private boolean active;
    private File directory;
    private long timeout;
    private String defaultCommandKeyword;
    private HashMap<String, AutoImportCommand> typeToCommandMap;

    private Timer autoImportTimer = null;
    private AutoImportTimerTask autoImportTask = null;

    public AutoImportServiceImpl(boolean active, String directoryPath, long timeout, String defaultCommandKeyword,
                                 HashMap<String, AutoImportCommand> typeToCommandMap) {
        this.active = active;
        setDirectory(directoryPath);
        this.timeout = timeout;
        this.defaultCommandKeyword = defaultCommandKeyword;
        this.typeToCommandMap = typeToCommandMap;

        if (logger.isInfoEnabled()) {
            printServiceLogInfo();
        }

        // schedule the AutoImportTimerTask for the 'AutoImporter'
        if (active) {
            if (logger.isInfoEnabled()) {
                logger.info("'AutoImport' is ACTIVATED");
                logger.info("'AutoImport' will poll directory: '" + directory.getAbsoluteFile() + "'");
                logger.info("'AutoImport' will be started every '" + timeout + "' ms");
            }
            // start TimerTask anyway (even if deactivated in spring bean config xml
            // file, maybe it will be changed during runtime)
            autoImportTimer = new Timer();
            autoImportTask = new AutoImportTimerTask(directory, typeToCommandMap, defaultCommandKeyword);
            autoImportTask.deleteCurrentRunningFiles();
            autoImportTimer.scheduleAtFixedRate(autoImportTask, timeout, timeout);
            if (active && logger.isInfoEnabled()) {
                logger.info("scheduled 'AutoImport' every " + timeout + " millis");
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("'AutoImport' is DEACTIVATED");
            }
        }
    }

    // TODO: attach to a spring destroy event? (if necessary)
    public void destroy() {
        if (autoImportTimer != null) {
            autoImportTimer.cancel();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        if (active == false) {
            active = true;
            if (autoImportTimer == null) {
                autoImportTimer = new Timer();
            }
            autoImportTask = new AutoImportTimerTask(directory, typeToCommandMap, defaultCommandKeyword);
            autoImportTimer.scheduleAtFixedRate(autoImportTask, timeout, timeout);
            if (logger.isInfoEnabled()) {
                logger.info("scheduled 'AutoImport' every " + timeout + " millis");
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("'AutoImport' already running every " + timeout + " millis, activation not necessary");
            }
        }
    }

    public void deactivate() {
        if (active == true) {
            active = false;
            if (autoImportTask != null) {
                autoImportTask.cancel();
                if (logger.isInfoEnabled()) {
                    logger.info("stopping 'AutoImport'...");
                }
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("'AutoImport' is currently not running, deactivation not necessary");
            }
        }
    }

    public String getDirectory() {
        return directory.getAbsolutePath();
    }

    public long getTimeout() {
        return timeout;
    }

    /**
     * sets the path to the directory for the automatically import (either absolute path, or relative from classpath)
     */
    public void setDirectory(String directory) {
        String directoryString = ((directory.length() == 0) ? null : directory);
        if (directoryString != null) {
            this.directory = new File(directoryString);
        }
    }

    public void setTimeout(long timeout) {
        // check if timeout has changed
        if (this.timeout != timeout) {
            // validate timeout range
            if ((timeout >= MIN_TIMEOUT) && (timeout <= MAX_TIMEOUT)) {
                this.timeout = timeout;
            } else {
                throw new IllegalArgumentException(
                        "timeout out of range! Value = " + timeout + ", Range = [" + MIN_TIMEOUT + " .. " +
                                MAX_TIMEOUT + "]");
            }
            if (active) {
                deactivate();
                activate();
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////// 
    // private methods
    private void printServiceLogInfo() {
        logger.info("number of registered AutoImportCommands: " + typeToCommandMap.size());
        for (String key : typeToCommandMap.keySet()) {
            StringBuilder builder = new StringBuilder("\tkey: ");
            builder.append(key);
            builder.append(" class: ");
            builder.append(typeToCommandMap.get(key).getClass().getName());
            logger.info(builder.toString());
        }

        if (defaultCommandKeyword != null) {
            logger.info("registered '" + defaultCommandKeyword +
                    "' as default service, unknown file types will be processed by this service");
        } else {
            logger.info("no service registered as default service, unknown file types will be omitted");
            if (logger.isDebugEnabled()) {
                logger.debug("you can define a 'defaultService' in the spring bean config xml file");
            }
        }
    }
}
