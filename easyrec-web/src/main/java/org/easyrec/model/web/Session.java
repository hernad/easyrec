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
package org.easyrec.model.web;

import javax.servlet.http.HttpServletRequest;


/**
 * <DESCRIPTION>
 * The Session Model contains information about the user, session & ip address
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-22 14:35:41 +0100 (Di, 22 Feb 2011) $<br/>
 * $Revision: 17734 $</p>
 *
 * @author phlavac
 * @version <CURRENT PROJECT VERSION>
 * @since <PROJECT VERSION ON FILE CREATION>
 */
public class Session {

    private int userId;
    private String userName;
    private String sessionId;
    private String ip;
    private HttpServletRequest request;

    // TODO: move to vocabulary?
    private final static String ANONYMOUS_USER = "ANONYMOUS";

    public Session(String sessionId, HttpServletRequest request) {
        this.sessionId = sessionId;
        this.ip = request.getRemoteAddr();
        this.request = request;
    }

    public Session(String sessionId, String ip) {
        this.sessionId = sessionId;
        this.ip = ip;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserName() {

        if (userId > 0) {
            return userName;
        } else {
            return ANONYMOUS_USER;
        }

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
