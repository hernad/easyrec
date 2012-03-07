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
package org.easyrec.controller;


import org.easyrec.store.dao.web.OperatorDAO;
import org.easyrec.utils.Security;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This Controller displays static content of the homepage.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2009</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: dmann $<br/>
 * $Date: 2011-10-12 17:12:55 +0200 (Mi, 12 Okt 2011) $<br/>
 * $Revision: 18618 $</p>
 *
 * @author dmann
 * @version 1.0
 * @since 1.0
 */
public class HomeController extends MultiActionController {

    private OperatorDAO operatorDAO;

    public void setOperatorDAO(OperatorDAO operatorDAO) {
        this.operatorDAO = operatorDAO;
    }
    public ModelAndView robots(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        ModelAndView mav = new ModelAndView("robots");
        return mav;
    }

    public ModelAndView home(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = new ModelAndView("page");
        mav.addObject("title", "easyrec :: home ");
        mav.addObject("page", "home");
        mav.addObject("selectedMenu", "home");
        mav.addObject("signedInOperator", Security.signedInOperator(request));
        mav.addObject("updateToken", Math.random()) ;

        return mav;
    }

    public ModelAndView API(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = new ModelAndView("page");
        mav.addObject("title", "easyrec :: api");
        mav.addObject("page", "api");
        mav.addObject("selectedMenu", "api");
        return mav;
    }

    public ModelAndView contact(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = new ModelAndView("page");
        mav.addObject("title", "easyrec :: contact us");
        mav.addObject("page", "contact");
        return mav;
    }

    public ModelAndView about(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = new ModelAndView("page");
        mav.addObject("title", "easyrec :: about");
        mav.addObject("page", "about");
        return mav;
    }
}
