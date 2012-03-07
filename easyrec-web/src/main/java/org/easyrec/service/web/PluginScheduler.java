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
package org.easyrec.service.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.plugin.NamedConfiguration;
import org.easyrec.model.plugin.archive.ArchivePseudoConfiguration;
import org.easyrec.model.plugin.archive.ArchivePseudoGenerator;
import org.easyrec.model.web.EasyRecSettings;
import org.easyrec.model.web.Operator;
import org.easyrec.model.web.Queue;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.plugin.configuration.GeneratorContainer;
import org.easyrec.service.core.TenantService;
import org.easyrec.store.dao.plugin.LogEntryDAO;
import org.easyrec.store.dao.web.OperatorDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * This class schedules plugins for each tenant.
 * <p/>
 * TODO update javadoc
 * All tenants, that have an active plugin scheduler flag are added
 * to the plugin TaskList. The PluginTaskList is a HashMap that contains
 * the tenant id as key and a PluginTask as value.
 * <p/>
 * A PluginTask adds a tenant to the execution queue at its execution time.
 * <p/>
 * The queue holds a list of all tenants that are waiting to be scheduled.
 * After a tenant is processed by the plugins, he is removed from the queue.
 *
 * @author phlavac
 */
public class PluginScheduler implements InitializingBean, DisposableBean {

    // TODO: move to vocabulary?
    private final static int SCHEDULER_PAUSE = 30 * 1000;

    private final Log logger = LogFactory.getLog(getClass());

    private RemoteTenantDAO remoteTenantDAO;
    private OperatorDAO operatorDAO;
    private HashMap<Integer, PluginTimerTask> pluginTimerTasks;
    private LogEntryDAO logEntryDAO;
    private Queue queue;
    private TenantService tenantService;
    private RemoteTenantService remoteTenantService;
    private EasyRecSettings easyrecSettings;
    private GeneratorContainer generatorContainer;

    private Scheduler scheduler;

    public PluginScheduler() {
        queue = new Queue();
    }

    /**
     * Init PluginScheduler timertasks for all Tenants.
     *
     * @throws java.lang.Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        if (easyrecSettings.isGenerator()) {
            initTasks();
            logEntryDAO.endAllEntries();
            scheduler = new Scheduler(queue);
            scheduler.start();
            logger.info("Plugin Scheduler started.");
        }
    }

    /**
     * Shut down scheduler
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        Thread interruptThread = scheduler;
        scheduler = null;
        interruptThread.interrupt();

        for (PluginTimerTask task : pluginTimerTasks.values()) {
            task.destroy();
            //noinspection UnusedAssignment
            task = null;
        }
        pluginTimerTasks.clear();
        logger.info("PluginScheduler shut down.");
    }

    /**
     * Add a PluginTimerTask to a Tenant
     *
     * @param remoteTenant RemoteTenant
     */
    public void addTask(RemoteTenant remoteTenant) {

        if (pluginTimerTasks != null) {
            pluginTimerTasks.put(remoteTenant.getId(), new PluginTimerTask(remoteTenant, queue));
        }
    }

    /**
     * Update a tenant's PluginTimerTask
     *
     * @param remoteTenant RemoteTenant
     */
    public void updateTask(RemoteTenant remoteTenant) {

        boolean tenantInTaskList = false;

        if (pluginTimerTasks != null) {

            PluginTimerTask pluginTimerTask = pluginTimerTasks.get(remoteTenant.getId());

            if (pluginTimerTask != null) {
                tenantInTaskList = true;
                pluginTimerTask.destroy();
                pluginTimerTasks.remove(remoteTenant.getId());

            }
            if (remoteTenant.isSchedulerEnabled()) {
                pluginTimerTasks.put(remoteTenant.getId(), new PluginTimerTask(remoteTenant, queue));
                if (!tenantInTaskList) {
                    logger.info("'" + remoteTenant.getOperatorId() + " - " + remoteTenant.getStringId() +
                            "' added to PluginTask List");
                }
            } else {
                if (tenantInTaskList) {
                    logger.info("'" + remoteTenant.getOperatorId() + " - " + remoteTenant.getStringId() +
                            "' removed from PluginTask List");
                }
            }
        }
    }


    /**
     * Stops a tenant's PluginTimerTask
     *
     * @param remoteTenant RemoteTenant
     */
    public void stopTask(RemoteTenant remoteTenant) {

        if (pluginTimerTasks != null) {
            PluginTimerTask pluginTimerTask = pluginTimerTasks.get(remoteTenant.getId());

            if (pluginTimerTask != null) {
                pluginTimerTask.destroy();
                pluginTimerTasks.remove(remoteTenant.getId());
            }

            logger.info("'" + remoteTenant.getOperatorId() + " - " + remoteTenant.getStringId() +
                    "' removed from PluginTimerTask");

        }
    }

    public void initTasks() {

        pluginTimerTasks = new HashMap<Integer, PluginTimerTask>();

        List<Operator> operators = operatorDAO.getOperators(0, Integer.MAX_VALUE);
        for (Operator operator : operators) {

            List<RemoteTenant> tenants = remoteTenantDAO.getTenantsFromOperator(operator.getOperatorId());

            for (RemoteTenant r : tenants) {

                if (r.isSchedulerEnabled()) {
                    addTask(r);
                    logger.info("'" + r.getOperatorId() + " - " + r.getStringId() + "' added to PluginTask List");
                }
            }
        }
    }

    /**
     * Iterates through all tenants and adds or removes a tenant
     */
    private void updateTasks() {

        List<Operator> operators = operatorDAO.getOperators(0, Integer.MAX_VALUE);

        for (Operator operator : operators) {

            List<RemoteTenant> tenants = remoteTenantDAO.getTenantsFromOperator(operator.getOperatorId());
            for (RemoteTenant r : tenants) {
                updateTask(r);
            }
        }
    }

    private class Scheduler extends Thread {

        private final Log logger = LogFactory.getLog(getClass());

        Queue queue;
        RemoteTenant remoteTenant;

        Scheduler(Queue queue) {
            this.queue = queue;
        }

        // look if tenants are waiting in queue. If so process plugins for every tenant in queue.
        @Override
        public void run() {
            Thread thisThread = Thread.currentThread();

            while (!thisThread.isInterrupted() && scheduler == thisThread) {

                updateTasks();

                if (queue.isEmpty()) {
                    try {
                        sleep(SCHEDULER_PAUSE);
                        logger.debug("pausing plugin scheduler for " + SCHEDULER_PAUSE + "ms.");
                    } catch (InterruptedException ex) {
                        logger.debug("pausing plugin scheduler failed", ex);
                        Thread.currentThread().interrupt();
                    }
                } else {
                    remoteTenant = queue.poll();

                    final Properties tenantConfig = tenantService.getTenantConfig(remoteTenant.getId());

                    if (tenantConfig == null) {
                        logger.warn("could not get tenant configuration, aborting");

                        return;
                    }

                    if ("true".equals(tenantConfig.getProperty(RemoteTenant.AUTO_ARCHIVER_ENABLED))) {
                        String daysString = tenantConfig.getProperty(RemoteTenant.AUTO_ARCHIVER_TIME_RANGE);
                        final int days = Integer.parseInt(daysString);
                        ArchivePseudoConfiguration configuration = new ArchivePseudoConfiguration(days);
                        configuration.setAssociationType("ARCHIVE");
                        NamedConfiguration namedConfiguration = new NamedConfiguration(remoteTenant.getId(), 0,
                                ArchivePseudoGenerator.ID, "Archive", configuration, true);

                        logger.info("Archiving actions older than " + days + " day(s)");

                        generatorContainer.runGenerator(namedConfiguration);
                    }

                    logger.info("starting generator plugin for tenant: " + remoteTenant.getOperatorId() + ":" +
                            remoteTenant.getStringId());

                    generatorContainer.runGeneratorsForTenant(remoteTenant.getId());

                    ///////////////////////////////////////
                    // TODO: insert logic here to trigger plugin generators
                    // TODO: send call to REST-API to mostview ALL Time to get results cached
                    // Problem: how to get ContextPath the needs to present to build backtracking URL?

                    remoteTenantService.updateTenantStatistics(remoteTenant.getId());
                }
            }
            logger.debug("PluginScheduler stopped. ");
        }
    }

    public void setOperatorDAO(OperatorDAO operatorDAO) {
        this.operatorDAO = operatorDAO;
    }

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    public void setLogEntryDAO(LogEntryDAO logEntryDAO) {
        this.logEntryDAO = logEntryDAO;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public void setRemoteTenantService(RemoteTenantService remoteTenantService) {
        this.remoteTenantService = remoteTenantService;
    }

    public void setEasyrecSettings(EasyRecSettings easyrecSettings) {
        this.easyrecSettings = easyrecSettings;
    }

    public void setGeneratorContainer(GeneratorContainer generatorContainer) {
        this.generatorContainer = generatorContainer;
    }
}
