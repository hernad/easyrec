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
package org.easyrec.controller.aop;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.easyrec.model.web.EasyRecSettings;
import org.easyrec.utils.MyUtils;
import org.easyrec.utils.Security;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Set;

/**
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-06-14 15:02:22 +0200 (Di, 14 Jun 2011) $<br/>
 * $Revision: 18435 $</p>
 *
 * @author David Mann
 */
@Aspect
public class LoggedInCheckAspect {
    private String localName;
    private String webappPath;
    private String extendedWebAppPath;
    private EasyRecSettings easyrecSettings;
    /**
     * This String array conatains a list of public available sites. All pages in this array are accessible
     * without logging into the system.
     */
    private static Set<String> PUBLIC_SITES =
            Sets.newHashSet("home", "login", "RSSBlog", "UpdateCheck", "operator/signin", "operator/signout",
                    "api-js/easyrec.js", "/api-js/easyrec.js", "t", "about");

    public void setEasyrecSettings(EasyRecSettings easyrecSettings) {
        this.easyrecSettings = easyrecSettings;
    }

    private final Log logger = LogFactory.getLog(getClass());

    @Pointcut("execution(* org.springframework.web.servlet.mvc.Controller+.handleRequest(..))")
    public void allControllers() {
    }

    private void initPath(HttpServletRequest request) {
        localName = request.getLocalName();
        localName = localName.equals("0.0.0.0") ? "localhost" : localName;
        this.webappPath = request.getContextPath();
        this.extendedWebAppPath = request.getScheme() + "://" + localName + ":" + request.getLocalPort() + webappPath;

        PUBLIC_SITES = Sets.newHashSet(Collections2.transform(PUBLIC_SITES, new Function<String, String>() {
            public String apply(@Nullable String input) {
                StringBuilder result = new StringBuilder(webappPath);
                result.append('/');
                result.append(input);

                return result.toString();
            }
        }));
    }

    @Around("allControllers()")
    public Object LoggedInAspect(ProceedingJoinPoint pjp) throws Throwable {

        HttpServletRequest request = (HttpServletRequest) pjp.getArgs()[0];
        HttpSession session = request.getSession(false);

        ModelAndView originalModelAndView = (ModelAndView) pjp.proceed();

        if (extendedWebAppPath == null)
            initPath(request);

        boolean signedIn = false;

        try {
            if (session != null && session.getAttribute("signedInOperatorId") != null)
                signedIn = true;

            if (originalModelAndView != null) {
                originalModelAndView.addObject("signedIn", signedIn);
                originalModelAndView.addObject("isDeveloper", Security.isDeveloper(request));
                originalModelAndView.addObject("securityToken", Security.getSecurityToken(request));
                originalModelAndView.addObject("webappPath", webappPath);
                originalModelAndView.addObject("extendedWebappPath", extendedWebAppPath);
                originalModelAndView.addObject("isGenerator", easyrecSettings.isGenerator());
                originalModelAndView.addObject("operationMode", easyrecSettings.getOperationMode());

                originalModelAndView.addObject("currentMonth", MyUtils.getCurrentMonth());
                originalModelAndView.addObject("currentMonthName", MyUtils.getCurrentMonthName());
                originalModelAndView.addObject("currentYear", MyUtils.getCurrentYear());

                if (session != null) {
                    originalModelAndView.addObject("signedInOperator", session.getAttribute("signedInOperator"));
                    originalModelAndView.addObject("signedInOperatorId", session.getAttribute("signedInOperatorId"));
                }

                originalModelAndView.addObject("easyrecName", easyrecSettings.getName());
                originalModelAndView.addObject("easyrecVersion", easyrecSettings.getVersion());
                originalModelAndView.addObject("easyrecBiz", easyrecSettings.getBiz());
                originalModelAndView.addObject("easyrecReleases", easyrecSettings.getReleases());
                originalModelAndView.addObject("checkUpdateUrl", easyrecSettings.getUpdateURL());
                originalModelAndView.addObject("updateUrl", easyrecSettings.getReleases());
            }
        } catch (IllegalStateException e) {
            logger.debug("User Logged Out and can not get Session atributes.", e);
        }

        String currentPage = request.getRequestURI();
        boolean isPublicSite = PUBLIC_SITES.contains(currentPage);

        if (!signedIn && !isPublicSite)
            return new ModelAndView("redirect:" + webappPath + "/home");

        return originalModelAndView;
    }
}