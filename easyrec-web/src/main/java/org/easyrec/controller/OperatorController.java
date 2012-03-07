/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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
import org.easyrec.model.core.TenantVO;
import org.easyrec.model.web.Message;
import org.easyrec.model.web.Operator;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.web.NamedConfigurationService;
import org.easyrec.service.web.PluginScheduler;
import org.easyrec.service.web.RemoteTenantService;
import org.easyrec.store.dao.web.OperatorDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.MessageBlock;
import org.easyrec.utils.Security;
import org.easyrec.utils.Web;
import org.easyrec.utils.io.Text;
import org.easyrec.utils.servlet.ServletUtils;
import org.easyrec.vocabulary.MSG;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * This Controller manages all Operator operations.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2009</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-04-01 19:23:19 +0200 (Fr, 01 Apr 2011) $<br/>
 * $Revision: 18099 $</p>
 *
 * @author dmann
 * @version 1.0
 * @since 1.0
 */
public class OperatorController extends MultiActionController {
    private OperatorDAO operatorDAO;
    private RemoteTenantDAO remoteTenantDAO;
    private TenantService tenantService;
    private PluginScheduler pluginScheduler;
    private RemoteTenantService remoteTenantService;
    private NamedConfigurationService namedConfigurationService;

    public void setNamedConfigurationService(NamedConfigurationService namedConfigurationService) {
        this.namedConfigurationService = namedConfigurationService;
    }

    public void setPluginScheduler(PluginScheduler pluginScheduler) {
        this.pluginScheduler = pluginScheduler;
    }

    public void setOperatorDAO(OperatorDAO operatorDAO) {
        this.operatorDAO = operatorDAO;
    }

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public void setRemoteTenantService(RemoteTenantService remoteTenantService) {
        this.remoteTenantService = remoteTenantService;
    }

    private ModelAndView security(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("page");
        mav.addObject("title", "operator");
        mav.addObject("operator", operatorDAO.get(Security.signedInOperatorId(request)));
        mav.addObject("operatorId", ServletUtils.getSafeParameter(request, "operatorId", ""));

        return mav;
    }

    public ModelAndView updateform(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = security(request);

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        if (operatorId.length() == 0) {
            TenantVO tenantVO =
                    tenantService.getTenantByStringId(ServletUtils.getSafeParameter(request, "tenantId", ""));
            RemoteTenant remoteTenant = remoteTenantDAO.get(tenantVO.getId());
            operatorId = remoteTenant.getOperatorId();


        }

        Operator operator = operatorDAO.get(operatorId);
        mav.addObject("operator", operator);

        mav.addObject("title", "easyrec :: update account");
        mav.addObject("page", "operator/update");
        mav.addObject("selectedMenu", "");
        return mav;
    }

    // http://localhost:8080/operator/signin?operatorId=easyrec&password=easyrec
    public ModelAndView signin(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = security(request);

        Operator operator = operatorDAO.signIn(ServletUtils.getSafeParameter(request, "operatorId", ""),
                ServletUtils.getSafeParameter(request, "password", ""));

        if (operator != null) {

            if (operator.isActive()) {

                String token = Security.signIn(request, operator);
                Security.setAttribute(request, "tenantId", RemoteTenant.DEFAULT_TENANT_ID);

                operatorDAO.setTokenForOperator(token, operator.getOperatorId());
                if (operator.getAccessLevel() == Operator.ACCESS_LEVEL_ADMINISTRATOR) {
                    return MessageBlock
                            .createSingle(mav, MSG.ADMINISTRATOR_SIGNED_IN, "signin", MSG.SUCCESS, token);
                }
                return MessageBlock.createSingle(mav, MSG.OPERATOR_SIGNED_IN, "signin", MSG.SUCCESS, token);


            } else {
                return MessageBlock.createSingle(mav, MSG.OPERATOR_NOT_ACTIVATED, "signin", MSG.ERROR);
            }
        } else {
            return MessageBlock.createSingle(mav, MSG.OPERATOR_SIGNED_IN_FAILED, "signin", MSG.ERROR);
        }

    }

    // http://localhost:8080/operator/signout
    public ModelAndView signout(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        operatorDAO.removeTokenFromOperator((String) Security.getAttribute(request, "signedInOperatorId"));

        request.getSession().invalidate();

        return MessageBlock.createSingle(security(request), MSG.OPERATOR_SIGNED_OUT, "signout", MSG.SUCCESS);
    }

    // http://localhost:8080/operator/update?operatorId=easyrec&firstName=pez&lastName=huzi&email=p@muzifuzi.com
    public ModelAndView update(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String firstName = ServletUtils.getSafeParameter(request, "firstName", "");
        String lastName = ServletUtils.getSafeParameter(request, "lastName", "");

        firstName = Text.capitalize(firstName);
        lastName = Text.capitalize(lastName);

        String email = ServletUtils.getSafeParameter(request, "email", "");
        String phone = ServletUtils.getSafeParameter(request, "phone", "");
        String company = ServletUtils.getSafeParameter(request, "company", "");
        String address = ServletUtils.getSafeParameter(request, "address", "");
        String apiKey = ServletUtils.getSafeParameter(request, "apiKey", "");
        String ip = request.getRemoteAddr();

        List<Message> messages = new ArrayList<Message>();
        ModelAndView mav = security(request);

        Operator operator = operatorDAO.get(operatorId);

        if (operator != null) {

            apiKey = Text.generateHash(operatorId);
            if (Strings.isNullOrEmpty(operatorId)) {
                messages.add(MSG.OPERATOR_EMPTY);
            }

            if (messages.size() > 0) {
                return MessageBlock.create(mav, messages, "update", MSG.ERROR);
            } else {
                operatorDAO.update(operatorId, firstName, lastName, email, phone, company, address, apiKey, ip);

                // Refresh signedIn Operator Data
                Security.signIn(request, operatorDAO.get(operatorId));

                messages.add(MSG.OPERATOR_UPDATED.append(" (" + operatorId + ")"));

                return MessageBlock.create(mav, messages, "update", MSG.SUCCESS);
            }
        } else {
            return MessageBlock.createSingle(mav, MSG.OPERATOR_DOES_NOT_EXISTS, "update", MSG.ERROR);
        }
    }

    // http://localhost:8080/operator/remove?operatorId=easyrec
    // Delete an operator with all Tenants. The default operator 'easyrec'
    // can not be deleted.
    public ModelAndView remove(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = security(request);
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        if (Security.isDeveloper(request)) {
            Operator operator = operatorDAO.get(operatorId);
            if (operator != null) {
                if (!operator.getOperatorId().equals(Operator.DEFAULT_OPERATORID)) {
                    List<RemoteTenant> tenants = remoteTenantDAO.getTenantsFromOperator(operatorId);
                    for (RemoteTenant remoteTenant : tenants) {
                        remoteTenantService.removeTenant(remoteTenant.getId());
                    }
                    operatorDAO.remove(operatorId);

                    return MessageBlock.createSingle(mav, MSG.OPERATOR_REMOVED, "removeOperator", MSG.SUCCESS);
                }
            }
        }
        return MessageBlock.createSingle(mav, MSG.OPERATOR_REMOVE_FAILED, "removeOperator", MSG.ERROR);

    }

    public ModelAndView activate(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = security(request);

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String apiKey = ServletUtils.getSafeParameter(request, "apiKey", "");

        boolean success = operatorDAO.activate(operatorId, apiKey);

        logger.info("Activating operator: " + operatorId);

        if (success) {

            mav.addObject("selectedMenu", "");

            // sign in operator
            Security.signIn(request, operatorDAO.get(operatorId));

            // and create a demo easyRec if not already exists
            String demoTenantId = RemoteTenant.DEFAULT_TENANT_ID;
            TenantVO tenantVO = new TenantVO(demoTenantId, RemoteTenant.DEFAULT_TENANT_DESCRIPITON);

            if (!remoteTenantDAO.exists(operatorId, demoTenantId)) {

                int iTenantId;
                synchronized (this) {
                    try {
                        iTenantId = tenantService.insertTenantWithTypes(tenantVO, null);
                        remoteTenantDAO.update(operatorId, iTenantId, Web.getExtendedWebappPath(request),
                                RemoteTenant.DEFAULT_TENANT_DESCRIPITON);

                        // enable auto archive function
                        // by default actions older than 5 years are moved
                        // to the archive
                        tenantService.updateConfigProperty(iTenantId, RemoteTenant.AUTO_ARCHIVER_ENABLED, "true");

                        tenantService.updateConfigProperty(iTenantId, RemoteTenant.AUTO_ARCHIVER_TIME_RANGE,
                                RemoteTenant.AUTO_ARCHIVER_DEFAULT_TIME_RANGE);

                        // enable backtracking by default
                        tenantService.updateConfigProperty(iTenantId, RemoteTenant.BACKTRACKING, "true");

                        // enable auto rule mining by default
                        tenantService.updateConfigProperty(iTenantId, RemoteTenant.SCHEDULER_ENABLED, "true");
                        tenantService.updateConfigProperty(iTenantId, RemoteTenant.SCHEDULER_EXECUTION_TIME,
                                RemoteTenant.SCHEDULER_DEFAULT_EXECUTION_TIME);
                        pluginScheduler.addTask(remoteTenantDAO.get(iTenantId));

                        pluginScheduler.addTask(remoteTenantDAO.get(iTenantId));

                        namedConfigurationService.setupDefaultTenant(iTenantId, request.getRemoteAddr());

                        remoteTenantService.updateTenantStatistics(iTenantId);
                    } catch (Exception e) {
                        logger.info("Creating Demo Tenant '" + demoTenantId + "' for operator '" + operatorId +
                                "' failed! " + e.getMessage());
                        success = false;
                    }
                }
                logger.info(
                        "Demo Tenant '" + demoTenantId + "' for operator '" + operatorId + "' created successfully! ");
            }
        }

        mav.setViewName("page");
        mav.addObject("title", "easyrec :: activation");
        mav.addObject("page", "operator/activate");
        mav.addObject("success", success);
        mav.addObject("selectedMenu", "");
        mav.addObject("operator", operatorDAO.get(operatorId));
        return mav;
    }

    // http://localhost:8080/operator/changePassword
    public ModelAndView changePassword(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = new ModelAndView("page");
        mav.addObject("title", "easyrec :: update password");
        mav.addObject("page", "operator/changepassword");
        mav.addObject("operatorId", Security.signedInOperatorId(request));

        return mav;

    }

    // http://localhost:8080/operator/updatepassword?operatorId=easyrec&oldPassword=x&newPassword=y&confirmPassword=y
    public ModelAndView updatePassword(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = new ModelAndView("page");

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String oldPassword = ServletUtils.getSafeParameter(request, "oldPassword", "");
        String newPassword = ServletUtils.getSafeParameter(request, "newPassword", "");
        String confirmPassword = ServletUtils.getSafeParameter(request, "confirmPassword", "");

        if (Strings.isNullOrEmpty(newPassword) || newPassword.length() < Operator.MIN_PASSWORD_LENGTH) {
            return MessageBlock.createSingle(mav, MSG.OPERATOR_PASSWORD_TO_SHORT, "updatePassword", MSG.ERROR);
        }
        if (!Strings.isNullOrEmpty(operatorId)) {

            Operator operator = operatorDAO.get(operatorId);
            if (operator != null) {

                if (operatorDAO.correctPassword(operatorId, oldPassword)) {
                    if (newPassword.equals(confirmPassword)) {
                        operatorDAO.updatePassword(operatorId, newPassword);

                        return MessageBlock.createSingle(mav, MSG.OPERATOR_PASSWORD_UPDATED, "updatePassword",
                                MSG.SUCCESS);
                    } else return MessageBlock
                            .createSingle(mav, MSG.OPERATOR_PASSWORD_MATCH, "updatePassword", MSG.ERROR);
                } else return MessageBlock
                        .createSingle(mav, MSG.OPERATOR_WRONG_PASSWORD, "updatePassword", MSG.ERROR);
            }
        }
        return MessageBlock
                .createSingle(mav, MSG.OPERATOR_PASSWORD_UPDATED_FAILED, "updatePassword", MSG.ERROR);
    }
}
