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

package org.easyrec.plugin.cli;

import com.google.common.collect.ObjectArrays;
import org.apache.commons.cli.*;
import org.easyrec.model.core.TenantVO;
import org.easyrec.plugin.Executable;
import org.easyrec.plugin.Plugin;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.plugin.util.Observer;
import org.easyrec.service.core.TenantService;
import org.easyrec.utils.spring.cli.AbstractDependencyInjectionSpringCLI;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract base class for implementing CLI utilities for {@link org.easyrec.plugin.generator.Generator}s.
 * <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last
 * modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public abstract class AbstractGeneratorCLI<GC extends GeneratorConfiguration, GS extends GeneratorStatistics>
        extends AbstractDependencyInjectionSpringCLI {
    // ------------------------------ FIELDS ------------------------------

    private TenantService tenantService;

    // --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * Spring injected tenant service.
     *
     * @param tenantService The tenant service.
     */
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected String[] getConfigLocations() {
        String[] generatorConfigurations = getConfigurations();

        return ObjectArrays.concat(generatorConfigurations,
                new String[]{"classpath:spring/core/spring.easyrec-core.PropertyPlaceholderConfigurerDB.xml",
                        "classpath:spring/core/easyrecDataSource.xml", "classpath:spring/core/ItemAssocDAO.xml",
                        "classpath:spring/core/AuthenticationDAO.xml", "classpath:spring/core/TenantConfig_DEFAULT.xml",
                        "classpath:spring/core/TenantDAO.xml",
                        "classpath:spring/core/spring.easyrec-core.SQLScriptServiceSatRecommenderDS.xml",
                        "classpath:spring/core/TenantService.xml", "classpath:spring/core/ActionDAO.xml",
                        "classpath:spring/core/ItemAssocDAO.xml", "easyrec-plugin.xml"}, String.class);
    }

    public abstract String[] getConfigurations();

    private final Options options;

    protected AbstractGeneratorCLI() {
        options = new Options();

        OptionBuilder optionBuilder = OptionBuilder.withArgName("tenants");
        optionBuilder.withLongOpt("tenant");
        optionBuilder.isRequired(false);
        optionBuilder.hasArg(true);
        optionBuilder.withDescription(
                "Specifiy a tenant to generate rules for. Ranges can be specified e.g. 1 - 3 will generate rules for tenants 1 to 3. Alternatively you can specify a list of tenants like 1,2,4. If omitted rules for all tenants are generated.");

        Option tenantOption = optionBuilder.create('t');

        optionBuilder = OptionBuilder.withLongOpt("uninstall");
        optionBuilder.isRequired(false);
        optionBuilder.hasArg(false);
        optionBuilder.withDescription("When true the generator is uninstalled when execution finished");

        Option uninstallOption = optionBuilder.create('u');

        options.addOption(tenantOption);
        options.addOption(uninstallOption);
    }

    @Override
    protected int processCommandLineCall(final String[] args) {
        final Generator<GC, GS> generator = getGenerator();
        super.context.getAutowireCapableBeanFactory()
                .autowireBeanProperties(generator, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);

        List<GC> configurations = new LinkedList<GC>();

        CommandLineParser parser = new PosixParser();
        CommandLine commandLine;

        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();

            usage();
            return -1;
        }

        boolean doUninstall = commandLine.hasOption('u');

        if (!commandLine.hasOption('t')) {
            List<TenantVO> tenants = tenantService.getAllTenants();

            for (TenantVO tenant : tenants) {
                GC configuration = generator.newConfiguration();
                configuration.setTenantId(tenant.getId());
                configuration.setConfigurationName(String.format("Configuration for tenant %d", tenant.getId()));

                configurations.add(configuration);
            }
        } else {
            String strTenants = commandLine.getOptionValue('t');

            if (strTenants.contains("-")) {
                String[] argParts = strTenants.split("-");

                if (argParts.length != 2) {
                    usage();
                    return -1;
                }

                int lowerBound = Integer.parseInt(argParts[0]);
                int upperBound = Integer.parseInt(argParts[1]);

                if (lowerBound > upperBound) {
                    int tmp = lowerBound;
                    lowerBound = upperBound;
                    upperBound = tmp;
                }

                for (int i = lowerBound; i <= upperBound; i++) {
                    GC configuration = generator.newConfiguration();
                    configuration.setTenantId(i);
                    configuration.setConfigurationName(String.format("Configuration for tenant %d", i));

                    configurations.add(configuration);
                }
            } else if (strTenants.contains(",")) {
                String[] argParts = strTenants.split(",");

                for (String argPart : argParts) {
                    int tenant = Integer.parseInt(argPart);

                    GC configuration = generator.newConfiguration();
                    configuration.setTenantId(tenant);
                    configuration.setConfigurationName(String.format("Configuration for tenant %d", tenant));

                    configurations.add(configuration);
                }
            } else {
                GC configuration = generator.newConfiguration();
                configuration.setTenantId(Integer.parseInt(strTenants));
                configuration.setConfigurationName(
                        String.format("Configuration for tenant %d", configuration.getTenantId()));

                configurations.add(configuration);
            }
        }

        final ExecutableObserver executableObserver = new ExecutableObserver();
        final PluginObserver pluginObserver = new PluginObserver();

        generator.getExecutableObserverRegistry().addObserver(executableObserver);
        generator.getPluginObserverRegistry().addObserver(pluginObserver);

        generator.install(true);

        for (GC configuration : configurations) {
            generator.initialize();

            generator.setConfiguration(configuration);

            System.out.println("##################################################");
            System.out.println(
                    String.format("[%s] Starting generator with configuration: \"%s\"", new Date().toString(),
                            configuration.getConfigurationName()));
            System.out.println("##################################################");

            try {
                generator.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // need to sleep 1s to avoid duplicate keys
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            generator.cleanup();
        }

        if (doUninstall) generator.uninstall();

        return 0;
    }

    public abstract Generator<GC, GS> getGenerator();

    @Override
    protected void usage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(String.format("java -jar %s", getClass().getCanonicalName()), options);
    }

    // -------------------------- INNER CLASSES --------------------------

    private static class ExecutableObserver implements Observer<Executable> {
        // ------------------------------ FIELDS ------------------------------

        private Date start = new Date();

        // ------------------------ INTERFACE METHODS ------------------------


        // --------------------- Interface Observer ---------------------

        public void stateChanged(final Executable target) {
            Date end = new Date();
            double durationInSeconds = end.getTime() - start.getTime();
            durationInSeconds /= 1000.0;

            float progressPercentage = 0.0f;

            if (target.getProgress().getTotalSteps() != 0.0f) progressPercentage =
                    (target.getProgress().getCurrentSteps() / target.getProgress().getTotalSteps()) * 100.0f;

            System.out.println(String.format("\ttook %.2fs", durationInSeconds));
            System.out.println(
                    String.format("[%s] %s: %s - %.2f%% (%d/%d)", end.toString(), target.getExecutionState().toString(),
                            target.getProgress().getMessage(), progressPercentage,
                            target.getProgress().getCurrentSteps() + 1, target.getProgress().getTotalSteps()));

            start = new Date();
        }
    }

    private static class PluginObserver implements Observer<Plugin> {
        // ------------------------ INTERFACE METHODS ------------------------


        // --------------------- Interface Observer ---------------------

        public void stateChanged(final Plugin target) {
            System.out.println("Lifecycle-Phase changed to " + target.getLifecyclePhase().toString());
        }
    }
}
