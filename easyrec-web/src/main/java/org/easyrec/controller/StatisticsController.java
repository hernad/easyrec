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

import com.google.common.base.Strings;
import org.easyrec.model.web.flot.FlotDataSet;
import org.easyrec.model.web.flot.FlotSeries;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.store.dao.web.StatisticsDAO;
import org.easyrec.utils.Security;
import org.easyrec.utils.servlet.ServletUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;


/**
 * This Controller creates diagramms for the monthly action overview.
 *
 * @author Peter Hlavac
 */
public class StatisticsController extends AbstractController {

    private StatisticsDAO statisticsDAO;
    private TypeMappingService typeMappingService;


    public void setStatisticsDAO(StatisticsDAO statisticsDAO) {
        this.statisticsDAO = statisticsDAO;
    }

    public void setTypeMappingService(TypeMappingService typeMappingService) {
        this.typeMappingService = typeMappingService;
    }


    /* (non-Javadoc)
    * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        if (Security.isSignedIn(request)) {

            int tenant;
            int month;
            int year;
            boolean flot;

            String actionType = request.getParameter("actionType");
            try {
                tenant = Integer.parseInt(request.getParameter("tenant"));
                month = Integer.parseInt(request.getParameter("month"));
                year = Integer.parseInt(request.getParameter("year"));
                flot = Integer.parseInt(request.getParameter("flot")) == 0 ? false : true;
            } catch (Exception e) {
                return null;
            }

            ModelAndView mav = new ModelAndView();
            XYSeriesCollection dataset = new XYSeriesCollection();
            FlotDataSet flotDataSet = new FlotDataSet();

            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();

            from.set(year, month, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH), 0, 0, 0);
            to.set(year, month, from.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);

            Integer actionTypeId = null;
            Integer assocTypeId = null;

            if (!Strings.isNullOrEmpty(actionType)) {
                if ("CLICKS_ON_RECS".equals(actionType)) assocTypeId = 1001;
                else if ("CLICKS_ON_CHARTS".equals(actionType)) assocTypeId = 998;
                else actionTypeId = typeMappingService.getIdOfActionType(tenant, actionType);
            }

            HashMap<Integer, HashMap<Integer, Integer>> actionBundleMap = statisticsDAO
                    .getActionBundleMap(tenant, from.getTimeInMillis(), to.getTimeInMillis(), actionTypeId,
                            assocTypeId);

            Iterator<Integer> iterator = actionBundleMap.keySet().iterator();

            while (iterator.hasNext()) {
                actionTypeId = iterator.next();
                if (actionTypeId == 1001) actionType = "clicks on recommendations";
                else if (actionTypeId == 998) actionType = "clicks on rankings";
                else actionType = typeMappingService.getActionTypeById(tenant, actionTypeId).toLowerCase() + " actions";

                XYSeries xySeries = new XYSeries(actionType);
                FlotSeries flotSeries = new FlotSeries();
                flotSeries.setTitle(actionType);

                for (int i = 1; i <= 31; i++) {
                    Integer y = actionBundleMap.get(actionTypeId).get(i);
                    xySeries.add(i, y != null ? y : 0);
                    flotSeries.add(i, y != null ? y : 0);
                }
                //mav.addObject("data",flotDataSet.toString());

                dataset.addSeries(xySeries);
                flotDataSet.add(flotSeries);
            }


            // create datapoints that are rendered in the clients browser
            // return array or html side that renders array
            if (flot) {
                boolean onlyData = (ServletUtils.getSafeParameter(request, "onlyData", 0) == 0) ? false : true;
                if (onlyData) {
                    mav.setViewName("flot/dataOutput");
                } else {
                    mav.setViewName("flot/flotPlot");
                }
                mav.addObject("data", flotDataSet.toString());
                mav.addObject("flotDataSet", flotDataSet.getData());
                mav.addObject("noActions", flotDataSet.getData().size() == 0);
                return mav;

                // create a png
            } else {
                JFreeChart action_chart = ChartFactory
                        .createXYLineChart("", "actions", "days", dataset, PlotOrientation.VERTICAL, true,// show legend
                                true,   // show tooltips
                                false); // show urls

                XYPlot plot = action_chart.getXYPlot();

                ValueAxis axis = plot.getDomainAxis();
                axis.setRange(1, 31);
                plot.setDomainAxis(axis);

                BufferedImage bi = action_chart.createBufferedImage(300, 200);

                byte[] bytes = ChartUtilities.encodeAsPNG(bi);

                if (bytes != null & !flot) {
                    OutputStream os = response.getOutputStream();
                    response.setContentType("image/png");
                    response.setContentLength(bytes.length);
                    os.write(bytes);
                    os.close();
                }
            }
            return null;

        } else {
            return Security.redirectHome(request, response);
        }

    }
}