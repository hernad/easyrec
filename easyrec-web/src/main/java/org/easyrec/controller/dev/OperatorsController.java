/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.controller.dev;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easyrec.model.web.Operator;
import org.easyrec.store.dao.web.OperatorDAO;
import org.easyrec.utils.MessageBlock;
import org.easyrec.utils.PageStringGenerator;
import org.easyrec.utils.Security;
import org.easyrec.utils.servlet.ServletUtils;
import org.easyrec.vocabulary.MSG;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author dmann
 */
public class OperatorsController  extends MultiActionController {
    private OperatorDAO operatorDAO;

    public void setOperatorDAO(OperatorDAO operatorDAO) {
        this.operatorDAO = operatorDAO;
    }

    private static final String VIEW_OPERATORS = "viewOperators";


    public ModelAndView viewoperators(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        PageStringGenerator psg = new PageStringGenerator(
                request.getRequestURL() + "?" + request.getQueryString());

        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");


        int siteNumber = ServletUtils.getSafeParameter(request, "siteNumber", 0);

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            int operatorsTotal = operatorDAO.count();
            mav.addObject("operatorsTotal", operatorsTotal);
            mav.addObject("pageMenuString", psg.getPageMenuString(operatorsTotal, siteNumber));

            List<Operator> operators = operatorDAO.getOperators(siteNumber * psg.getNumberOfItemsPerPage(), psg.getNumberOfItemsPerPage());

            mav.setViewName("dev/page");
            mav.addObject("page", "viewoperators");
            mav.addObject("operators", operators);
            mav.addObject("dbname", operatorDAO.getDbName());

            return mav;
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, VIEW_OPERATORS, MSG.ERROR);
        }
    }

}
