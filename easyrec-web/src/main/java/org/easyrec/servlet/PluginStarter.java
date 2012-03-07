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

package org.easyrec.servlet;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.plugin.LogEntry;
import org.easyrec.model.plugin.NamedConfiguration;
import org.easyrec.model.plugin.archive.ArchivePseudoConfiguration;
import org.easyrec.model.plugin.archive.ArchivePseudoGenerator;
import org.easyrec.model.plugin.archive.ArchivePseudoStatistics;
import org.easyrec.model.web.*;
import org.easyrec.plugin.configuration.GeneratorContainer;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.plugin.stats.StatisticsConstants;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.web.RemoteTenantService;
import org.easyrec.service.web.nodomain.ShopRecommenderService;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.Security;
import org.easyrec.vocabulary.MSG;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Servlet implementation class for Servlet: PluginStarter
 */
public class PluginStarter extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

    @SuppressWarnings({"UnusedDeclaration"})
    static final long serialVersionUID = 1L;
    private TenantService tenantService;
    private RemoteTenantDAO remoteTenantDAO;
    private RemoteTenantService remoteTenantService;
    private ShopRecommenderService shopRecommenderService;
    private EasyRecSettings easyrecSettings;
    private PluginRegistry pluginRegistry;
    private GeneratorContainer generatorContainer;
    private boolean initialized = false;
    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    /* (non-Java-doc)
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public PluginStarter() {
        super();
    }

    private void initialize() {
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        this.tenantService = context.getBean("tenantService", TenantService.class);
        this.remoteTenantDAO = context.getBean("remoteTenantDAO", RemoteTenantDAO.class);
        this.remoteTenantService = context.getBean("remoteTenantService", RemoteTenantService.class);
        this.shopRecommenderService = context.getBean("shopRecommenderService", ShopRecommenderService.class);
        this.easyrecSettings = context.getBean("easyrecSettings", EasyRecSettings.class);
        this.pluginRegistry = context.getBean("pluginRegistry", PluginRegistry.class);
        this.generatorContainer = context.getBean("generatorContainer", GeneratorContainer.class);

        initialized = true;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @XmlRootElement(name = "generators")
    private static class GeneratorsResponse {
        @XmlElement(name = "generator")
        private List<GeneratorResponse> generatorResponses;

        private GeneratorsResponse() {}

        public GeneratorsResponse(List<GeneratorResponse> generatorResponses) {
            this.generatorResponses = generatorResponses;
        }

        public List<GeneratorResponse> getGeneratorResponses() {
            return generatorResponses;
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @XmlRootElement(name = "generator")
    private static class GeneratorResponse {
        @XmlElement(name = "success", required = false)
        private SuccessMessage successMessage;
        @XmlElement(name = "error", required = false)
        private ErrorMessage errorMessage;
        @XmlElement(name = "action")
        private String action;
        @XmlAttribute(name = "id")
        @XmlJavaTypeAdapter(PluginId.URIAdapter.class)
        private PluginId pluginId;

        private GeneratorResponse() {}

        public GeneratorResponse(Message message, String action, PluginId pluginId) {
            if (message.getClass().equals(ErrorMessage.class))
                errorMessage = (ErrorMessage) message;
            else
                successMessage = (SuccessMessage) message;
            this.action = action;
            this.pluginId = pluginId;
        }

        public Message getMessage() {
            return errorMessage != null ? errorMessage : successMessage;
        }

        public String getAction() {
            return action;
        }

        public PluginId getPluginId() {
            return pluginId;
        }
    }

    private void marshalResponseXml(GeneratorsResponse generatorsResponse, OutputStream output) {
        try {
            Set<Class<?>> classes = Sets.newHashSet(GeneratorsResponse.class, GeneratorResponse.class,
                    ErrorMessage.class, SuccessMessage.class);

            for(GeneratorResponse generatorResponse : generatorsResponse.getGeneratorResponses()) {
                Object content = generatorResponse.getMessage().getContent();

                // TODO workaround classes that are not @XmlRootElement annotated
                if(content != null)
                    classes.add(content.getClass());
            }

            Class[] classesArray = classes.toArray(new Class[classes.size()]);

            JAXBContext jaxbContext = JAXBContext.newInstance(classesArray);
            Marshaller marshaller = jaxbContext.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(generatorsResponse, output);
        } catch (JAXBException e) {
            logger.error("failed to marshal response!", e);
        }
    }

    private void marshalResponseXml(GeneratorResponse generatorResponse, OutputStream output) {
        try {
            Set<Class<?>> classes = Sets.newHashSet(GeneratorsResponse.class, GeneratorResponse.class,
                    ErrorMessage.class, SuccessMessage.class);

            Object content = generatorResponse.getMessage().getContent();

            // TODO workaround classes that are not @XmlRootElement annotated
            if(content != null)
                classes.add(content.getClass());

            Class[] classesArray = classes.toArray(new Class[classes.size()]);

            JAXBContext jaxbContext = JAXBContext.newInstance(classesArray);
            Marshaller marshaller = jaxbContext.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(generatorResponse, output);
        } catch (JAXBException e) {
            logger.error("failed to marshal response!", e);
        }
    }

    /* (non-Java-doc)
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/xml; charset=utf-8");

        if (!initialized) {
            initialize();
        }

        if (!easyrecSettings.isGenerator()) {
            marshalResponseXml(new GeneratorResponse(MSG.GENERATOR_ALREADY_EXECUTING.replace(
                    "Cannot start the generator! easyrec is running in frontend mode!"), "startPlugin",
                    new PluginId("http://www.easyrec.org/internal/starter", easyrecSettings.getVersion())),
                    response.getOutputStream());

            return;
        }

        if (!pluginRegistry.isAllExecutablesStopped()) { // already running
            marshalResponseXml(new GeneratorResponse(MSG.GENERATOR_ALREADY_EXECUTING
                    .replace("Cannot start the calculation! Another calculation is currently running!"), "startPlugin",
                    new PluginId("http://www.easyrec.org/internal/starter", easyrecSettings.getVersion())),
                    response.getOutputStream());

            return;
        }

        String tenantId = request.getParameter("tenantId");
        String operatorId = request.getParameter("operatorId");
        RemoteTenant remoteTenant = remoteTenantDAO.get(operatorId, tenantId);

        if (remoteTenant == null || !Security.isSignedIn(request)) {
            logger.info("No tenant specified. Start mining for all tenants.");

            marshalResponseXml(new GeneratorResponse(MSG.GENERATOR_ALREADY_EXECUTING
                    .replace("No tenant specified!"), "startPlugin",
                    new PluginId("http://www.easyrec.org/internal/starter", easyrecSettings.getVersion())),
                    response.getOutputStream());

            return;
        }

        logger.info("Starting generator for tenant: " + operatorId + ":" + tenantId);

        final Properties tenantConfig = tenantService.getTenantConfig(remoteTenant.getId());

        if (tenantConfig == null) {
            logger.warn("could not get tenant configuration, aborting");

            marshalResponseXml(new GeneratorResponse(MSG.GENERATOR_ALREADY_EXECUTING
                    .replace("Could not get tenant configuration!"), "startPlugin",
                    new PluginId("http://www.easyrec.org/internal/starter", easyrecSettings.getVersion())),
                    response.getOutputStream());

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

            generatorContainer.runGenerator(namedConfiguration,
                    // create a log entry only for archiver runs where actions were actually archived
                    // --> remove log entries where the number of archived actions is 0
                    new Predicate<GeneratorStatistics>() {
                        public boolean apply(GeneratorStatistics input) {
                            ArchivePseudoStatistics archivePseudoStatistics = (ArchivePseudoStatistics) input;

                            return archivePseudoStatistics.getNumberOfArchivedActions() > 0;
                        }
                    }, true);
        }

        List<LogEntry> generatorRuns = generatorContainer.runGeneratorsForTenant(remoteTenant.getId());

        List<GeneratorResponse> responses = Lists.transform(generatorRuns, new Function<LogEntry, GeneratorResponse>() {
            public GeneratorResponse apply(LogEntry input) {
                Message message =
                        input.getStatistics().getClass().equals(StatisticsConstants.ExecutionFailedStatistics.class)
                        ? MSG.GENERATOR_FINISHED_FAIL : MSG.GENERATOR_FINISHED_SUCCESS;
                message = message.content(input.getStatistics());

                return new GeneratorResponse(message, "startPlugin", input.getPluginId());
            }
        });

        GeneratorsResponse generatorsResponse = new GeneratorsResponse(responses);


        // TODO send call to shoprecommendersystem to cache most viewed item of all time
        /*
        // send a call to MOST VIEWED ITEM of ALL TIME to be cached
            shopRecommenderService.mostViewedItems(r.getId(), Item.DEFAULT_STRING_ITEM_TYPE, 50, "ALL", null,
                    new Session(null, request.getRemoteAddr()));
         */

        remoteTenantService.updateTenantStatistics(remoteTenant.getId());

        marshalResponseXml(generatorsResponse, response.getOutputStream());
    }

    /* (non-Java-doc)
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}
