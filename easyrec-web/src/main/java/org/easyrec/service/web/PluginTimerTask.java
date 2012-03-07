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
package org.easyrec.service.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.web.Queue;
import org.easyrec.model.web.RemoteTenant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This Class adds a tenant to the plugin queue at its execution time
 * every 24 hours.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-04-04 13:26:26 +0200 (Mo, 04 Apr 2011) $<br/>
 * $Revision: 18100 $</p>
 *
 * @author phlavac
 * @version <CURRENT PROJECT VERSION>
 * @since <PROJECT VERSION ON FILE CREATION>
 */
public class PluginTimerTask {

    // TODO: move to vocabulary?
    private static final long FIXED_RATE = 1000 * 60 * 60 * 24; // schedule every 24hours
    private final Log logger = LogFactory.getLog(getClass());

    private Timer pluginTimer = null;
    private RemoteTenant remoteTenant;

    private class PluginInnerTimerTask extends TimerTask {

        private RemoteTenant remoteTenant;
        private Queue queue;
        private final Log logger = LogFactory.getLog(getClass());

        public PluginInnerTimerTask(RemoteTenant remoteTenant, Queue queue) {
            this.remoteTenant = remoteTenant;
            this.queue = queue;
        }

        public void run() {
            logger.info("Adding Tenant '" + remoteTenant.getOperatorId() + " - " + remoteTenant.getStringId() +
                    "' for plugin runs to Queue (" + new Date() + ")");

            queue.add(remoteTenant);
        }
    }

    ;

    public PluginTimerTask() {}

    public PluginTimerTask(RemoteTenant remoteTenant, Queue queue) {

        logger.debug(
                "Init PluginTimerTask for '" + remoteTenant.getOperatorId() + " - " + remoteTenant.getStringId() +
                        "' at " + remoteTenant.getSchedulerExecutionTime());

        this.remoteTenant = remoteTenant;

        pluginTimer = new Timer();
        pluginTimer.scheduleAtFixedRate(new PluginInnerTimerTask(remoteTenant, queue),
                getExecutionTime(remoteTenant.getSchedulerExecutionTime()), FIXED_RATE);
    }

    public String getTenantName() {
        return remoteTenant.getStringId();
    }

    /**
     * Cancels this task
     */
    public void destroy() {
        pluginTimer.cancel();
        pluginTimer = null;
        logger.debug(
                "cancel Timertask for '" + remoteTenant.getOperatorId() + " - " + remoteTenant.getStringId() + "'");
    }


    /**
     * This functions returns the time when the plugins have to be executed for the first time for a given time String.
     *
     * @param exeTime: e.g. 23:45
     * @return
     */
    private Date getExecutionTime(String exeTime) {
        try {
            Date now = new Date();
            DateFormat exeTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            DateFormat todayDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date execDate = (Date) exeTimeFormat.parse(todayDateFormat.format(now) + " " + exeTime);
            if (now.after(execDate)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(execDate);
                cal.add(Calendar.DATE, 1);
                execDate = cal.getTime();
            }
            return execDate;
        } catch (ParseException ex) {
            logger.info(ex.getMessage());
        }
        return null;
    }
}