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

import org.easyrec.store.dao.web.BackTrackingDAO;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This Controller manages backtracking. Backtracking is a mechanism that
 * records clicks on recommendations by users. This gives feedback if the
 * recommended items are accepted by the users.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: phlavac $<br/>
 * $Date: 2009-12-18 02:00:51 +0100 (Fr, 18 Dez 2009) $<br/>
 * $Revision: 15267 $</p>
 *
 * @author phlavac
 * @version 1.0
 * @since 1.0
 */
public class BackTrackingController extends AbstractController {

    // TODO: move to vocabulary?
    public static final int ASSOC_RECOMMENDATIONS_FOR_USER = 999;
    public static final int ASSOC_RANKINGS = 998;
    public static final int ASSOC_RATINGS = 997;
    public static final int ASSOC_CLUSTER = 996;
    public static final int ASSOC_HISTORY = 995;

    private BackTrackingDAO backTrackingDAO;

    public void setBackTrackingDAO(BackTrackingDAO backTrackingDAO) {
        this.backTrackingDAO = backTrackingDAO;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Integer tenantId;
        Integer itemFromId;
        Integer itemToId;
        Integer assocType;
        Integer userId;

        try {
            userId = Integer.parseInt(request.getParameter("r"));
        } catch (Exception e) {
            userId = 0;
        }

        try {
            tenantId = Integer.parseInt(request.getParameter("t"));
            itemFromId = Integer.parseInt(request.getParameter("f"));
            itemToId = Integer.parseInt(request.getParameter("i"));
            assocType = Integer.parseInt(request.getParameter("a"));

            backTrackingDAO.track(userId, tenantId, itemFromId, itemToId, assocType);
        } catch (Exception e) {
            logger.warn("error storing tracking information", e);
        }

        try {
            String redirectUrl = request.getParameter("u");
            RedirectView redirectView = new RedirectView(redirectUrl, false, true, false);
            ModelAndView mav = new ModelAndView(redirectView);
            return mav;
        } catch (Exception e) {
            logger.warn("unable to redirect", e);
        }

        return null;
    }
}