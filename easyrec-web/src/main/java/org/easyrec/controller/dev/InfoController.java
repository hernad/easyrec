/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.controller.dev;

import com.eaio.util.text.HumanTime;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.store.dao.plugin.LogEntryDAO;
import org.easyrec.store.dao.web.OperatorDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.MessageBlock;
import org.easyrec.utils.Security;
import org.easyrec.utils.servlet.ServletUtils;
import org.easyrec.vocabulary.MSG;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * @author dmann
 */
public class InfoController extends MultiActionController {

    private final Log logger = LogFactory.getLog(this.getClass());

    private RemoteTenantDAO remoteTenantDAO;
    private OperatorDAO operatorDAO;
    private LogEntryDAO logEntryDAO;


    public void setOperatorDAO(OperatorDAO operatorDAO) {
        this.operatorDAO = operatorDAO;
    }

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    public void setLogEntryDAO(LogEntryDAO logEntryDAO) {
        this.logEntryDAO = logEntryDAO;
    }

    private static final String HOME = "home";
    private static final String PULL_USED_MEM = "pullusedmem";


    public ModelAndView home(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            mav.setViewName("dev/page");
            mav.addObject("page", "home");
            mav.addObject("operatorCount", operatorDAO.count());
            mav.addObject("tenantCount", remoteTenantDAO.count());
            mav.addObject("dbName", operatorDAO.getDbName());
            mav.addObject("dbUserName", operatorDAO.getDbUserName());
            mav.addObject("signedinOperatorId", Security.signedInOperatorId(request));


            mav.addObject("heapsize",
                    ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / (1000000));
            mav.addObject("usedmemory",
                    ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / (1000000));

            int computationTimeInMilliseconds = logEntryDAO.getComputationDurationForDate() * 1000;
            String computationTime = HumanTime.approximately(computationTimeInMilliseconds);
            computationTime = computationTime.replaceAll("ms", "@1");
            computationTime = computationTime.replaceAll("s", "@2");
            computationTime = computationTime.replaceAll("m", "@3");
            computationTime = computationTime.replaceAll("h", "@4");
            computationTime = computationTime.replaceAll("d", "@5");
            computationTime = computationTime.replaceAll("@1", "milliseconds");
            computationTime = computationTime.replaceAll("@2", "seconds");
            computationTime = computationTime.replaceAll("@3", "minutes");
            computationTime = computationTime.replaceAll("@4", "hours");
            computationTime = computationTime.replaceAll("@5", "days");
            mav.addObject("dailyComputationTime", computationTime);

            long freeSpace = 0L;

            try {
                freeSpace = FileSystemUtils.freeSpaceKb(System.getProperties().getProperty("user.dir"));
            } catch (IOException e) {
                logger.error("Could not check free Free Disc Space :" + e.getMessage());
            }

            mav.addObject("freespace", freeSpace / 1048576);

            return mav;
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, HOME, MSG.ERROR);
        }
    }

    public ModelAndView pullusedmem(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL,
                    Long.toString(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / (1000000)),
                    MSG.SUCCESS);
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, PULL_USED_MEM, MSG.ERROR);
        }
    }

}
