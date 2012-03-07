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


import org.easyrec.model.web.Operator;
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
 * $Author: phlavac $<br/>
 * $Date: 2010-02-26 16:54:38 +0100 (Fr, 26 Feb 2010) $<br/>
 * $Revision: 15673 $</p>
 *
 * @author dmann
 * @version 1.0
 * @since 1.0
 */
public class DemoController extends MultiActionController {

    public ModelAndView initMav(HttpServletRequest request, String title, String itemId, String description,
                                String image) {

        ModelAndView mav = new ModelAndView("page");
        Operator operator = Security.signedInOperator(request);

        mav.addObject("title", title);
        mav.addObject("apikey", (operator == null) ? Operator.DEFAULT_API_KEY : operator.getApiKey());
        mav.addObject("page", "demo");
        mav.addObject("itemId", itemId);
        mav.addObject("description", description);
        mav.addObject("image", image);
        return mav;
    }

    public ModelAndView fatboyslim(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = initMav(request, "easyrec :: item :: fatboy slim :: the rockafeller skank", "42",
                "fatboy slim :: the rockafeller skank", "fatboyslim.jpg");

        return mav;
    }

    public ModelAndView gorillaz(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = initMav(request, "easyrec :: item :: gorillaz :: clint eastwood", "44",
                "gorillaz :: clint eastwood", "gorillaz.jpg");

        return mav;
    }

    public ModelAndView beastieboyz(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = initMav(request, "easyrec :: item :: beastie boys :: intergalactic", "43",
                "beastie boys :: intergalactic", "beastieboys.jpg");

        return mav;
    }
}
