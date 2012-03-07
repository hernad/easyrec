package org.easyrec.controller.dev;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.easyrec.model.plugin.LogEntry;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.store.dao.plugin.LogEntryDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.MessageBlock;
import org.easyrec.utils.PageStringGenerator;
import org.easyrec.utils.Security;
import org.easyrec.utils.servlet.ServletUtils;
import org.easyrec.vocabulary.MSG;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

/**
 * @author dmann
 */
public class PluginLogController extends MultiActionController {

    private RemoteTenantDAO remoteTenantDAO;
    private TypeMappingService typeMappingService;
    private LogEntryDAO logEntryDAO;

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    public void setLogEntryDAO(LogEntryDAO logEntryDAO) {
        this.logEntryDAO = logEntryDAO;
    }

    public void setTypeMappingService(TypeMappingService typeMappingService) {
        this.typeMappingService = typeMappingService;
    }

    private static final String VIEW_PLUGIN_LOGS = "viewPluginLogs";
    private static final String EMPTY_PLUGIN_LOGS = "emptypluginlogs";

    public static class LogEntryExtended extends LogEntry {

        private String assocType;
        private String tenant;
        private String operator;

        public LogEntryExtended(LogEntry logEntry, String assocType, String tenant, String operator) {
            super(logEntry.getId(), logEntry.getTenantId(), logEntry.getPluginId(), logEntry.getStartDate(),
                    logEntry.getEndDate(), logEntry.getAssocTypeId(), logEntry.getConfiguration(),
                    logEntry.getStatistics());

            this.assocType = assocType;
            this.tenant = tenant;
            this.operator = operator;
        }

        public String getAssocType() {
            return assocType;
        }

        public String getTenant() {
            return tenant;
        }

        public String getOperator() {
            return operator;
        }
    }

    public ModelAndView viewpluginlogs(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        int siteNumber = ServletUtils.getSafeParameter(request, "siteNumber", 0);

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            mav.setViewName("dev/page");
            mav.addObject("page", "viewpluginlogs");
            String type = ServletUtils.getSafeParameter(request, "type", "");
            mav.addObject("type", type);
            RemoteTenant remoteTenant = remoteTenantDAO.get(operatorId, tenantId);

            mav.addObject("tenant", remoteTenant != null ? remoteTenant.getStringId() : tenantId);

            PageStringGenerator psg = new PageStringGenerator(
                    request.getRequestURL() + "?" + request.getQueryString());
            int logCount;

            if ((remoteTenant == null) || ("all".equals(type)))
                logCount = logEntryDAO.getNumberOfLogEntries();
            else
                logCount = logEntryDAO.getNumberOfLogEntriesForTenant(remoteTenant.getId());

            List<LogEntry> logEntries;

            if ((remoteTenant == null) || ("all".equals(type)))
                logEntries = logEntryDAO.getLogEntries(siteNumber * psg.getNumberOfItemsPerPage(),
                        psg.getNumberOfItemsPerPage());
            else
                logEntries = logEntryDAO.getLogEntriesForTenant(remoteTenant.getId(),
                        siteNumber * psg.getNumberOfItemsPerPage(),
                        psg.getNumberOfItemsPerPage());


            Collection<LogEntryExtended> extendedLogEntries = Collections2.transform(logEntries, new Function<LogEntry,
                    LogEntryExtended>() {
                public LogEntryExtended apply(LogEntry input) {
                    RemoteTenant remoteTenant = remoteTenantDAO.get(input.getTenantId());

                    String assocType = "";
                    if (input.getAssocTypeId() == 0) { // special handling for assocType "Archive"
                        assocType = "ARCHIVE";
                    } else {
                        assocType = typeMappingService.getAssocTypeById(input.getTenantId(), input.getAssocTypeId());
                    }
                    String tenant = remoteTenant.getStringId();
                    String operator = remoteTenant.getOperatorId();

                    return new LogEntryExtended(input, assocType, tenant, operator);
                }
            });

            mav.addObject("pageMenuString", psg.getPageMenuString(logCount, siteNumber));
            mav.addObject("logCount", logCount);
            mav.addObject("logEntries", extendedLogEntries);
            return mav;
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, VIEW_PLUGIN_LOGS, MSG.ERROR);
        }
    }

    public ModelAndView emptypluginlogs(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            logEntryDAO.deleteLogEntries();
            return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL, EMPTY_PLUGIN_LOGS, MSG.SUCCESS);
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, EMPTY_PLUGIN_LOGS, MSG.ERROR);
        }
    }
}
