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
package org.easyrec.utils.servlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Vector;

/**
 * <p>
 * Provides utilities for servlet Programming.
 * </p>
 * <p/>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2006
 * </p>
 * <p/>
 * <p>
 * <b>last modified:</b><br/> $Author: sat-rsa $<br/> $Date: 2008-06-27
 * 14:40:01 +0200 (Fr, 27 Jun 2008) $<br/> $Revision: 119 $
 * </p>
 *
 * @author Florian Kleedorfer
 */
public class ServletUtils {

    private ServletUtils() {}

    private static String[] proxyHeaders = {"FORWARDED", "HTTP_FORWARDED", "HTTP_X_FORWARDED", "HTTP_X_FORWARDED FOR"};

    /**
     * Gets a parameter with given key from the request, returning the given
     * default value if the parameter is <code>null</code> and decode it from
     * ISO-8859-1 to UTF-8. e.g. F%C3%B6rdert%C3%B6pfe --> Fördertöpfe
     *
     * @param request      HTTP request.
     * @param key          Parameter to get.
     * @param defaultValue Default value in case no value is stored in request.
     * @return the decoded Parameter or default if parameter could not be
     *         decoded.
     */
    public static String getSafeParameterDecoded(HttpServletRequest request, String key, String defaultValue) {
        try {
            return URLDecoder.decode(URLEncoder.encode(getSafeParameter(request, key, defaultValue), "ISO-8859-1"),
                    "UTF-8");
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gets a parameter with given key from the request, returning the given
     * default value if the parameter is <code>null</code>.
     *
     * @param request      ->
     *                     The HttpServletRequest
     * @param key          ->
     *                     Parameter Name
     * @param defaultValue ->
     *                     returns this value if the parameter value is <code>null</code>
     * @return The value of the Parameter
     */
    public static String getSafeParameter(HttpServletRequest request, String key, String defaultValue) {
        String ret = request.getParameter(key);

        if (ret == null) return defaultValue;

        return ret;
    }

    /**
     * Gets a parameter with given key from the request, returning the given
     * default value if the parameter is <code>null</code>.
     *
     * @param request      ->
     *                     The HttpServletRequest
     * @param key          ->
     *                     Parameter Name
     * @param defaultValue ->
     *                     returns this value if the parameter value is <code>null</code>
     * @return The value of the Parameter
     */
    public static int getSafeParameter(HttpServletRequest request, String key, int defaultValue) {
        int ret = defaultValue;
        String param = request.getParameter(key);
        if (param != null) {
            try {
                ret = Integer.parseInt(param);
            } catch (Exception ignored) {}
        }
        return ret;
    }

    /**
     * Gets a parameter with given key from the request, returning the given
     * default value if the parameter is <code>null</code>.
     *
     * @param request      ->
     *                     The HttpServletRequest
     * @param key          ->
     *                     Parameter Name
     * @param defaultValue ->
     *                     returns this value if the parameter value is <code>null</code>
     * @return The value of the Parameter
     */
    public static double getSafeParameter(HttpServletRequest request, String key, double defaultValue) {
        double ret = defaultValue;
        String param = request.getParameter(key);
        if (param != null) {
            try {
                ret = Double.parseDouble(param);
            } catch (Exception ignored) {}
        }
        return ret;
    }

    /**
     * This Function loads a cookie value from the given cookie name, returning
     * the given default value if the cookie value is <code>null</code>.
     *
     * @param request      ->
     *                     The HttpServletRequest
     * @param name         ->
     *                     Name of the Cookie
     * @param defaultValue ->
     *                     returns this value if the cookie value is <code>null</code>
     * @return The Value of the Cookie with the given Name
     */
    public static String getCookieValueByName(HttpServletRequest request, String name, String defaultValue) {
        Cookie returnCookie = getCookieByName(request, name, defaultValue);
        return returnCookie.getValue();
    }

    /**
     * This Function loads a cookie value from the given cookie name, returning
     * the given default value if the cookie value is <code>null</code>.
     *
     * @param request      ->
     *                     The HttpServletRequest
     * @param name         ->
     *                     Name of the Cookie
     * @param defaultValue ->
     *                     returns this value if the cookie value is <code>null</code>
     * @return The Value of the Cookie with the given Name
     */
    public static int getCookieValueByName(HttpServletRequest request, String name, int defaultValue) {
        Cookie returnCookie = getCookieByName(request, name, Integer.toString(defaultValue));
        return Integer.parseInt(returnCookie.getValue());
    }

    /**
     * This Function loads a Cookie with the given Name ,returning the given
     * default value if the cookie value is <code>null</code>.
     *
     * @param request      ->
     *                     The HttpServletRequest
     * @param name         ->
     *                     Name of the Cookie
     * @param defaultValue ->
     *                     returns this value if the cookie value is <code>null</code>
     * @return the Cookie with the given Name
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name, String defaultValue) {
        Cookie returnCookie = null;
        Cookie cookies[] = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    returnCookie = cookie;
                }
            }
        }

        if (returnCookie == null) returnCookie = new Cookie(name, defaultValue);

        return returnCookie;
    }

    /**
     * This Function returns a Vector of Strings with the Values of all cookies
     * starting with the given startString.
     *
     * @param request     ->
     *                    The HttpServletRequest
     * @param startString ->
     *                    The Prefix for the Cookies
     * @return a Vector of Strings with the Values of all cookies starting with
     *         the prefix
     */
    public static Vector<String> getAllCookiesStartWith(HttpServletRequest request, String startString) {
        Vector<String> returnVector = new Vector<String>();

        Cookie cookies[] = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getValue().startsWith(startString)) {
                returnVector.add(cookie.getValue());
            }
        }

        return returnVector;
    }

    /**
     * This function returns the number of cookies Of The User
     *
     * @param request The HttpServletRequest
     * @return number of cookies
     */
    public static int getCookieCount(HttpServletRequest request) {
        return request.getCookies().length;
    }

    /**
     * This Function replaces all special characters with HTML Code ... for
     * example: test< == test&lt;
     *
     * @param string --> the String to escape
     * @return a HTML STRING with all special characters Replaced
     */
    public static String stringToHTMLCode(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        // true if last char was blank
        boolean lastWasBlankChar = false;
        int len = string.length();
        char c;

        for (int i = 0; i < len; i++) {
            c = string.charAt(i);
            if (c == ' ') {
                // blank gets extra work, this solves the problem you get if you replace all blanks with &nbsp;, if you
                // do that you lose word breaking
                if (lastWasBlankChar) {
                    lastWasBlankChar = false;
                    sb.append("&nbsp;");
                } else {
                    lastWasBlankChar = true;
                    sb.append(' ');
                }
            } else {
                lastWasBlankChar = false;
                htmlEscapeCharacter(sb, c);
            }
        }

        return sb.toString();
    }

    private static void htmlEscapeCharacter(StringBuilder sb, char c) {//
        // HTML Special Chars
        // 2011-05-10 added more charactes to be escaped (http://wonko.com/post/html-escaping)
        switch (c) {
            case '"':
                sb.append("&quot;");
                break;
            case '\'':
                sb.append("&#x27;");
                break;
            case '&':
                sb.append("&amp;");
                break;
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '\n':
                // Handle Newline
                sb.append("&lt;br/&gt;");
                break;
            case '`':
                sb.append("&#96;");
                break;
            case '!':
                sb.append("&#33;");
                break;
            case '@':
                sb.append("&#64;");
                break;
            case '$':
                sb.append("&#36;");
                break;
            case '%':
                sb.append("&#37;");
                break;
            case '(':
                sb.append("&#40;");
                break;
            case ')':
                sb.append("&#41;");
                break;
            case '=':
                sb.append("&#61;");
                break;
            case '+':
                sb.append("&#43;");
                break;
            case '{':
                sb.append("&#123;");
                break;
            case '}':
                sb.append("&#125;");
                break;
            case '[':
                sb.append("&#91;");
                break;
            case ']':
                sb.append("&#93;");
                break;
            default:
                int ci = 0xffff & c;
                if (ci < 160)
                    // nothing special only 7 Bit
                    sb.append(c);
                else {
                    sb.append("&#");
                    sb.append(Integer.toString(ci));
                    sb.append(';');
                }
                break;
        }
    }

    /**
     * This Function gets the SessionId of an user
     *
     * @param request   ->
     *                  The HttpServletRequest
     * @param defaultId ->
     *                  returns this value if the sessionId is <code>null</code>
     * @return the Session Id of an user
     */
    public static String getSessionId(HttpServletRequest request, String defaultId) {
        String sessionId;

        try {
            sessionId = request.getSession().getId();
        } catch (IllegalStateException e) {
            sessionId = defaultId;
        }

        return sessionId;
    }

    /**
     * This Function gets the IP of an user or the IP of an Proxy Server. If The
     * Proxy Server is Transparent the IP of the User would look like :
     * <code>81.189.130.58 Proxy Server: 212.12.12.34</code>
     *
     * @param request ->
     *                The HttpServletRequest
     * @return the IP of an user
     */
    public static String getIP(HttpServletRequest request) {
        String ipAddress;
        String proxy = null;

        for (String proxyHeader : proxyHeaders) {
            proxy = request.getHeader(proxyHeader);

            if (!proxy.equals(""))
                break;
        }

        ipAddress = proxy != null ? proxy + " Proxy Server: " : "";
        ipAddress += request.getRemoteAddr();

        return ipAddress;
    }

}
