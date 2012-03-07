package org.easyrec.controller.dev;

import com.jamonapi.MonitorFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easyrec.utils.MessageBlock;
import org.easyrec.utils.Security;
import org.easyrec.utils.servlet.ServletUtils;
import org.easyrec.vocabulary.MSG;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * This Controller provides the end user a tool to test his server response times
 * over the rest API. The user has to call the page then send some REST calls to the website
 * and then he has to call the refresh the site to display request times in MS.
 *
 * @author dmann
 */
public class BenchmarkController  extends MultiActionController {

    private static final String JAMON_REPORT = "jamonreport";
    private static final String JAMON_RESET = "jamonreset";

    /*
     * this function returns a mav Object which displays the java monitor report.
     */
    public ModelAndView jamonreport(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            mav.setViewName("dev/page");
            mav.addObject("page", "output");


            String[] header = MonitorFactory.getRootMonitor().getBasicHeader();
            Object[][] data = MonitorFactory.getRootMonitor().getBasicData();

            if (data != null) {
                StringBuilder jamonReport = new StringBuilder("<table width='100%'><tr>");
                String v;

                // create header
                for (int l = 0; l < 8; l++) {
                    jamonReport.append("<td><b>");
                    jamonReport.append(header[l]);
                    if (l > 1) {
                        jamonReport.append("[ms]");
                    }
                    jamonReport.append("</b></td>");
                }

                // create fields
                jamonReport.append("</tr>");
                for (int i = 0; i < data.length; i++) {

                    if (i % 2 == 0) {
                        jamonReport.append("<tr style='background-color:#eeeeee'>");
                    } else {
                        jamonReport.append("<tr>");
                    }

                    for (int l = 0; l < 8; l++) {
                        v = data[i][l].toString();
                        jamonReport.append("<td>");
                        jamonReport.append((l > 0) ? v.substring(0, v.indexOf(".")) : v);
                        jamonReport.append("</td>");
                    }
                    jamonReport.append("</tr>");
                }
                jamonReport.append("</table>");

                mav.addObject("outstr",
                        "<a href='javascript:jamonreset()'>reset</a><br/><br/>" + jamonReport.toString());
            } else {
                StringBuilder emptyReport = new StringBuilder();
                emptyReport.append("Please do some REST API calls (e.g.view) ");
                emptyReport.append("to see results here.<br/><br/>");
                emptyReport.append("<a href='");
                emptyReport.append(request.getContextPath());
                emptyReport.append("/dev/jamonreport?tenantId=").append(tenantId).append("&operatorId=").append(operatorId).append("'>refresh</a>.");


                mav.addObject("outstr", emptyReport.toString());
            }


            return mav;
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, JAMON_REPORT, MSG.ERROR);
        }
    }

    /*
     * this function returns a mav object which resets the Java Monitor Factory, so the user can start over.
     */
    public ModelAndView jamonreset(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
                mav.setViewName("dev/page");
                mav.addObject("page", "output");
                mav.addObject("outstr", "report reset");
                MonitorFactory.reset();
                return mav;
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, JAMON_RESET, MSG.ERROR);
        }
    }

}
