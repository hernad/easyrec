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

import com.google.common.base.Strings;
import org.easyrec.model.web.Operator;
import org.easyrec.utils.io.Text;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class checks if a Operator or Administrator is signed in.
 *
 * @author phlavac
 */
public class Security {

    // TODO: move to vocabulary?   i would say remove this pathetic class :)
    public static final Integer ACCESS_LEVEL_DEVELOPER = 1;

    // The User can view this sites without a login:
    private static String[] WHITELIST_DOMAIN = {"localhost"};

    /**
     * This function signs in an operator and returns a security token
     * the a valid for the current session.
     *
     * @param request
     * @param operator
     */
    public static String signIn(HttpServletRequest request, Operator operator) {
        String token = null;
        if (operator != null) {
            request.getSession(true).setAttribute("signedInOperatorId", operator.getOperatorId());
            request.getSession(true).setAttribute("signedInOperator", operator);
            token = Text.generateHash(Long.toString(System.currentTimeMillis()) + operator.getOperatorId());
            Security.setAttribute(request, "token", token);
        }
        return token;
    }


    /**
     * This function checks if an operator is signed in
     *
     * @param request
     * @return
     */
    public static boolean isSignedIn(HttpServletRequest request) {
        return request.getSession().getAttribute("signedInOperatorId") != null;
    }

    /**
     * This function checks if an operator is signed in as a developer
     * Developer can edit/remove core-, remote-tenants and operators
     *
     * @param request
     * @return
     */
    public static boolean isDeveloper(HttpServletRequest request) {
        if (request.getSession(false) != null) {
            Operator o = (Operator) request.getSession().getAttribute("signedInOperator");
            if (o != null) {
                return (ACCESS_LEVEL_DEVELOPER.equals(o.getAccessLevel()));
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the operator Id of the signed in operator, "" otherwise.
     *
     * @param request
     * @return
     */
    public static String signedInOperatorId(HttpServletRequest request) {
        String signedInOperatorId = "";
        try {
            signedInOperatorId = request.getSession().getAttribute("signedInOperatorId").toString();
        } catch (Exception e) {
        }
        return (Strings.isNullOrEmpty(signedInOperatorId)) ? "" : signedInOperatorId;
    }

    /**
     * Returns the operator Object of the signed in operator, "" otherwise.
     *
     * @param request
     * @return
     */
    public static Operator signedInOperator(HttpServletRequest request) {
        Operator operator;
        try {
            operator = (Operator) request.getSession(true).getAttribute("signedInOperator");
            return operator;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * This function returns an empty mav object and tries to redirect the user
     * to the homepage (e.g. if not logged in)
     *
     * @param request
     * @param response
     * @return
     */
    public static ModelAndView redirectHome(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (IOException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Returns the operatorId from the Parameter "operatorId" in the request object,
     * if signed in as administrator or the operatorId of the signed in Operator.
     * If not signed in, null is returned.
     *
     * @param request
     * @return
     */
    public static String getOperatorId(HttpServletRequest request) {

        String operatorId = null;

        // a developer account is allowed to read and write item objects from
        // any operator
        if (Security.isDeveloper(request)) {
            operatorId = request.getParameter("operatorId");
        }
        if (operatorId == null) {
            Operator o = Security.signedInOperator(request);
            if (o != null) {
                operatorId = o.getOperatorId();
            }

        }
        return operatorId;
    }

    /**
     * Returns a security token valid for this session. A secrity token
     * is used to call specific REST-API calls to manipulate Data.
     * If not signed in, null is returned.
     *
     * @param request
     * @return
     */
    public static String getSecurityToken(HttpServletRequest request) {

        String token = "xxxxx";
        if (Security.isSignedIn(request)) {
            if (nullAttribute(request, "token")) {
                setAttribute(request, "token",
                        Text.generateHash(Long.toString(System.currentTimeMillis()) + Security.getOperatorId(request)));
            } else {
                return (String) getAttribute(request, "token");
            }
        }
        return token;
    }

    /**
     * This function returns true if url
     * contains a domain that is in white list.
     *
     * @param url
     * @return
     */
    public static boolean inWhiteListDomain(String url) {

        if (!Strings.isNullOrEmpty(url)) {

            for (String whiteDomain : WHITELIST_DOMAIN) {
                if (url.contains(whiteDomain)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This function returns a new randomized 8-digit password.
     *
     * @return
     */
    public static String getNewPassword() {
        String password = "";
        Random r = new Random();
        String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        for (int i = 1; i < 8; i++) {
            password = password + validChars.charAt(r.nextInt(validChars.length()));
        }
        return password;
    }

    /**
     * Sets a given attribute for a session
     *
     * @param request
     * @param name
     * @param value
     */
    public static void setAttribute(HttpServletRequest request, String name, Object value) {
        HttpSession session = request.getSession(false);
        if (session != null && name != null) {
            session.setAttribute(name, value);
        }
    }

    /**
     * return a given attribute (if available) for a session
     *
     * @param request
     * @param name
     * @return
     */
    public static Object getAttribute(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(false);
        if (session != null && name != null) {
            return session.getAttribute(name);
        }
        return null;
    }

    /**
     * returns true if a given attribute is null
     *
     * @param request
     * @param name
     * @return
     */
    public static Boolean nullAttribute(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(false);
        if (session != null && name != null) {
            return (session.getAttribute(name) == null);
        }
        return true;
    }

}
