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

package org.easyrec.plugin.slopeone.model;

import org.easyrec.plugin.stats.GeneratorStatistics;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;


/**
 * Collected statistics for a run of Slope One.<p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author: pmarschik $<br/> $Date: 2011-06-14 15:02:31 +0200 (Di, 14 Jun 2011) $<br/> $Revision: 18436 $</p>
 *
 * @author Patrick Marschik
 */
@XmlRootElement
public class SlopeOneStats extends GeneratorStatistics implements Serializable {
    private static final long serialVersionUID = 1981154200893808863L;

    private int noUsers = 0;
    private long actionDuration = 0;
    private long deviationDuration = 0;
    private long noCreatedDeviations = 0;
    private long noModifiedDeviations = 0;
    private long nonPersonalizedDuration = 0;
    private String exception = null;

    public long getActionDuration() { return actionDuration; }

    public void setActionDuration(long actionDuration) { this.actionDuration = actionDuration; }

    public long getDeviationDuration() { return deviationDuration; }

    public void setDeviationDuration(long differenceDuration) { this.deviationDuration = differenceDuration; }

    public String getException() {
        return exception;
    }

    public void setException(final String exception) {
        this.exception = exception;
    }

    public void setException(final Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        throwable.printStackTrace(printWriter);

        setException(stringWriter.toString());
    }

    public long getNoCreatedDeviations() {
        return noCreatedDeviations;
    }

    public void setNoCreatedDeviations(final long noCreatedDeviations) {
        this.noCreatedDeviations = noCreatedDeviations;
    }

    public long getNoModifiedDeviations() {
        return noModifiedDeviations;
    }

    public void setNoModifiedDeviations(final long noModifiedDeviations) {
        this.noModifiedDeviations = noModifiedDeviations;
    }

    public int getNoUsers() { return noUsers; }

    public void setNoUsers(int noUsers) { this.noUsers = noUsers; }

    public long getNonPersonalizedDuration() { return nonPersonalizedDuration; }

    public void setNonPersonalizedDuration(long nonPersonalizedDuration) {
        this.nonPersonalizedDuration = nonPersonalizedDuration;
    }

    /* Disabled for now since personalized recommendations won't be generated

       private long personalizedDuration;
       public void setPersonalizedDuration(long personalizedDuration) { this.personalizedDuration = personalizedDuration; }

       public long getPersonalizedDuration() { return personalizedDuration; }
     */
}
