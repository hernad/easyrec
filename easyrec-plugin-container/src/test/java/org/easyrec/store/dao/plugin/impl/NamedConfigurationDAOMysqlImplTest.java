/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.store.dao.plugin.impl;

import org.easyrec.model.plugin.NamedConfiguration;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.generator.GeneratorConfigurationConstants;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.plugin.waiting.WaitingGenerator;
import org.easyrec.plugin.waiting.WaitingGeneratorConfiguration;
import org.easyrec.store.dao.plugin.NamedConfigurationDAO;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DuplicateKeyException;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author pmarschik
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({"/spring/easyrecDataSource.xml", "/spring/pluginContainer/Plugins_AllInOne.xml"})
@DataSet("/dbunit/pluginContainer/plugin_configuration.xml")
public class NamedConfigurationDAOMysqlImplTest {
    @SpringBeanByName
    private NamedConfigurationDAO namedConfigurationDAO;

    @SpringBeanByName
    private PluginRegistry pluginRegistry;

    private WaitingGenerator waitingGenerator;

    private static WaitingGeneratorConfiguration CONFIG_NULL_NAME;
    // naming scheme: x_y_z, x=tenantId, y=assocTypeId, z=config #
    private static WaitingGeneratorConfiguration CONFIG_1_1_1;
    private static WaitingGeneratorConfiguration CONFIG_1_1_2;
    private static WaitingGeneratorConfiguration CONFIG_1_1_3;
    private static WaitingGeneratorConfiguration CONFIG_1_2_1;
    private static WaitingGeneratorConfiguration CONFIG_2_1_1;

    @BeforeClass
    public static void beforeClass() {
        CONFIG_NULL_NAME = new WaitingGeneratorConfiguration();
        CONFIG_NULL_NAME.setConfigurationName(null);

        CONFIG_1_1_1 = new WaitingGeneratorConfiguration();
        CONFIG_1_1_1.setConfigurationName("Configuration 1");
        CONFIG_1_1_1.setAssociationType("1-1-1");
        CONFIG_1_1_1.setTimeout(1000);

        CONFIG_1_1_2 = new WaitingGeneratorConfiguration();
        CONFIG_1_1_2.setConfigurationName("Configuration 2");
        CONFIG_1_1_2.setAssociationType("1-1-2");
        CONFIG_1_1_2.setTimeout(2000);

        CONFIG_1_1_3 = new WaitingGeneratorConfiguration();
        CONFIG_1_1_3.setConfigurationName("Configuration 3");
        CONFIG_1_1_3.setAssociationType("1-1-3");
        CONFIG_1_1_3.setTimeout(3000);

        CONFIG_1_2_1 = new WaitingGeneratorConfiguration();
        CONFIG_1_2_1.setConfigurationName("Configuration 1");
        CONFIG_1_2_1.setAssociationType("1-2-1");
        CONFIG_1_2_1.setTimeout(1000);

        CONFIG_2_1_1 = new WaitingGeneratorConfiguration();
        CONFIG_2_1_1.setConfigurationName("Configuration 1");
        CONFIG_2_1_1.setAssociationType("2-1-1");
        CONFIG_2_1_1.setTimeout(1000);
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        waitingGenerator = new WaitingGenerator();

        pluginRegistry.getGenerators().put(waitingGenerator.getId(),
                // fu generics ...
                (Generator<GeneratorConfiguration, GeneratorStatistics>)
                        (Generator<? extends GeneratorConfiguration, ? extends GeneratorStatistics>) waitingGenerator);
    }

    @Test
    @DataSet("/dbunit/pluginContainer/plugin_configuration+erronous_name_config.xml")
    public void readConfiguration_shouldReturnUnmarshalFailedConfiguration() {
        NamedConfiguration config;

        config = namedConfigurationDAO.readConfiguration(1, 1, waitingGenerator.getId(), "Configuration 1");
        assertThat(config, is(not(nullValue())));
        assertThat(config.getName(), is("Configuration 1"));
        assertThat(config.getConfiguration(), is(GeneratorConfigurationConstants.CONF_UNMARSHAL_FAILED));

        // this should print a warning in the log
        config = namedConfigurationDAO.readConfiguration(1, 1, waitingGenerator.getId(), "XXXX");
    }

    @Test
    public void readConfiguration_getsConfigurationOrNull() {
        NamedConfiguration config;

        config = namedConfigurationDAO.readConfiguration(1, 1, waitingGenerator.getId(), "Configuration 1");

        assertThat(config, is(not(nullValue())));
        assertThat(config.getName(), is("Configuration 1"));
        assertThat(config.getConfiguration().getAssociationType(), is("1-1-1"));

        config = namedConfigurationDAO.readConfiguration(1, 1, waitingGenerator.getId(), "Configuration 2");

        assertThat(config, is(not(nullValue())));
        assertThat(config.getName(), is("Configuration 2"));
        assertThat(config.getConfiguration().getAssociationType(), is("1-1-2"));

        config = namedConfigurationDAO.readConfiguration(1, 1, waitingGenerator.getId(), "Configuration 3");

        assertThat(config, is(nullValue()));

        config = namedConfigurationDAO.readConfiguration(1, 2, waitingGenerator.getId(), "Configuration 1");

        assertThat(config, is(not(nullValue())));
        assertThat(config.getName(), is("Configuration 1"));
        assertThat(config.getConfiguration().getAssociationType(), is("1-2-1"));

        config = namedConfigurationDAO.readConfiguration(2, 1, waitingGenerator.getId(), "Configuration 1");

        assertThat(config, is(not(nullValue())));
        assertThat(config.getName(), is("Configuration 1"));
        assertThat(config.getConfiguration().getAssociationType(), is("2-1-1"));
    }

    @Test(expected = NullPointerException.class)
    public void readConfiguration_shouldThrowOnNullPluginId() {
        namedConfigurationDAO.readConfiguration(1, 1, null, "Configuration 1");
    }

    @Test(expected = NullPointerException.class)
    public void readConfiguration_shouldThrowOnNullName() {
        namedConfigurationDAO.readConfiguration(1, 1, waitingGenerator.getId(), null);
    }

    @Test
    public void readConfigurations_getsConfigurationsOrEmptyList() {
        List<NamedConfiguration> configs;

        configs = namedConfigurationDAO.readConfigurations(1, 1, waitingGenerator.getId());

        assertThat(configs, is(not(nullValue())));
        assertThat(configs.size(), is(2));

        configs = namedConfigurationDAO.readConfigurations(1, 2, waitingGenerator.getId());

        assertThat(configs, is(not(nullValue())));
        assertThat(configs.size(), is(1));

        configs = namedConfigurationDAO.readConfigurations(1, 3, waitingGenerator.getId());

        assertThat(configs, is(not(nullValue())));
        assertThat(configs.size(), is(0));

        configs = namedConfigurationDAO.readConfigurations(2, 1, waitingGenerator.getId());

        assertThat(configs, is(not(nullValue())));
        assertThat(configs.size(), is(1));
    }

    @Test(expected = NullPointerException.class)
    public void readConfigurations_shouldThrowOnNullPluginId() {
        namedConfigurationDAO.readConfigurations(1, 1, null);
    }

    @Test
    public void readActiveConfiguration_shouldGetActiveConfigurationOrNull() {
        NamedConfiguration config;

        config = namedConfigurationDAO.readActiveConfiguration(1, 1);
        assertThat(config, not(nullValue()));
        assertThat(config.getName(), is("Configuration 1"));
        assertThat(config.getConfiguration().getAssociationType(), is("1-1-1"));

        config = namedConfigurationDAO.readActiveConfiguration(1, 2);
        assertThat(config, not(nullValue()));
        assertThat(config.getName(), is("Configuration 1"));
        assertThat(config.getConfiguration().getAssociationType(), is("1-2-1"));

        config = namedConfigurationDAO.readActiveConfiguration(1, 3);
        assertThat(config, nullValue());

        config = namedConfigurationDAO.readActiveConfiguration(2, 1);
        assertThat(config, not(nullValue()));
        assertThat(config.getName(), is("Configuration 1"));
        assertThat(config.getConfiguration().getAssociationType(), is("2-1-1"));

        config = namedConfigurationDAO.readActiveConfiguration(3, 1);
        assertThat(config, nullValue());
    }

    @Test
    @ExpectedDataSet("/dbunit/pluginContainer/plugin_configuration+updated_active.xml")
    public void updateConfiguration_updatesActive() {
        int rowsModified;
        NamedConfiguration config;

        config = new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 2", CONFIG_1_1_2, true);
        rowsModified = namedConfigurationDAO.updateConfiguration(config);

        assertThat(rowsModified, is(1));
    }

    @Test
    @ExpectedDataSet("/dbunit/pluginContainer/plugin_configuration+renamed.xml")
    public void updateConfiguration_renamesConfiguration() {
        int rowsModified;
        NamedConfiguration config;

        config = new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 1", CONFIG_1_1_1, false);
        config.getConfiguration().setConfigurationName("Configuration 1 RENAMED");
        rowsModified = namedConfigurationDAO.updateConfiguration(config);
        assertThat(rowsModified, is(1));

        config = new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 2", CONFIG_1_1_2, false);
        config.getConfiguration().setConfigurationName("Configuration 2 RENAMED");
        rowsModified = namedConfigurationDAO.updateConfiguration(config);
        assertThat(rowsModified, is(1));

        config = new NamedConfiguration(1, 2, waitingGenerator.getId(), "Configuration 1", CONFIG_1_2_1, false);
        config.getConfiguration().setConfigurationName("Configuration 1 RENAMED");
        rowsModified = namedConfigurationDAO.updateConfiguration(config);
        assertThat(rowsModified, is(1));

        config = new NamedConfiguration(2, 1, waitingGenerator.getId(), "Configuration 1", CONFIG_2_1_1, false);
        config.getConfiguration().setConfigurationName("Configuration 1 RENAMED");
        rowsModified = namedConfigurationDAO.updateConfiguration(config);
        assertThat(rowsModified, is(1));
    }

    @Test
    @ExpectedDataSet("/dbunit/pluginContainer/plugin_configuration.xml")
    public void updateConfiguration_shouldIgnoreWhenNotFound() {
        NamedConfiguration config =
                new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 3", CONFIG_1_1_3, false);
        int rowsModified = namedConfigurationDAO.updateConfiguration(config);

        assertThat(rowsModified, is(0));
    }

    @Test(expected = DuplicateKeyException.class)
    @ExpectedDataSet("/dbunit/pluginContainer/plugin_configuration.xml")
    public void updateConfiguration_shouldThrowOnExistingName() {
        WaitingGeneratorConfiguration config_1_1_2 = new WaitingGeneratorConfiguration();
        config_1_1_2.setConfigurationName("Configuration 2");
        config_1_1_2.setAssociationType("1-1-2");
        config_1_1_2.setTimeout(2000);

        NamedConfiguration config =
                new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 1", config_1_1_2, false);
        namedConfigurationDAO.updateConfiguration(config);
    }

    @Test(expected = NullPointerException.class)
    public void updateConfiguration_shouldThrowOnNullNamedConfiguration() {
        namedConfigurationDAO.updateConfiguration(null);
    }

    @Test(expected = NullPointerException.class)
    public void updateConfiguration_shouldThrowOnNullPluginId() {
        NamedConfiguration config =
                new NamedConfiguration(1, 1, null, "Configuration 2", CONFIG_1_1_2, false);
        namedConfigurationDAO.updateConfiguration(config);
    }

    @Test(expected = NullPointerException.class)
    public void updateConfiguration_shouldThrowOnNullName() {
        NamedConfiguration config =
                new NamedConfiguration(1, 1, waitingGenerator.getId(), null, CONFIG_1_1_1, false);
        namedConfigurationDAO.updateConfiguration(config);
    }

    @Test(expected = NullPointerException.class)
    public void updateConfiguration_shouldThrowOnNullConfiguration() {
        NamedConfiguration config =
                new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 1", null, false);
        namedConfigurationDAO.updateConfiguration(config);
    }

    @Test(expected = NullPointerException.class)
    public void updateConfiguration_shouldThrowOnNullConfigurationName() {
        NamedConfiguration config =
                new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 1", CONFIG_NULL_NAME, false);
        namedConfigurationDAO.updateConfiguration(config);
    }

    @Test
    @ExpectedDataSet("/dbunit/pluginContainer/plugin_configuration+saved.xml")
    public void createConfiguration_shouldSave() {
        NamedConfiguration namedConfiguration =
                new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 3", CONFIG_1_1_3, false);

        int rowsAffected = namedConfigurationDAO.createConfiguration(namedConfiguration);
        assertThat(rowsAffected, is(1));
    }

    @Test
    @ExpectedDataSet("/dbunit/pluginContainer/plugin_configuration+saved_active.xml")
    public void createConfiguration_shouldUpdateActive() {
        NamedConfiguration namedConfiguration =
                new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 3", CONFIG_1_1_3, true);

        int rowsAffected = namedConfigurationDAO.createConfiguration(namedConfiguration);
        assertThat(rowsAffected, is(1));
    }

    @Test
    @ExpectedDataSet("/dbunit/pluginContainer/plugin_configuration+saved.xml")
    public void createConfiguration_shouldOverwriteConfigurationName() {
        WaitingGeneratorConfiguration config_1_1_3 = new WaitingGeneratorConfiguration();
        config_1_1_3.setConfigurationName("BOGUS NAME");
        config_1_1_3.setAssociationType("1-1-3");
        config_1_1_3.setTimeout(3000);

        NamedConfiguration namedConfiguration =
                new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 3", config_1_1_3, false);

        int rowsAffected = namedConfigurationDAO.createConfiguration(namedConfiguration);
        assertThat(rowsAffected, is(1));
    }

    @Test(expected = NullPointerException.class)
    public void createConfiguration_shouldThrowOnNullNamedConfiguration() {
        namedConfigurationDAO.createConfiguration(null);
    }

    @Test(expected = NullPointerException.class)
    public void createConfiguration_shouldThrowOnNullPluginId() {
        NamedConfiguration namedConfiguration =
                new NamedConfiguration(1, 1, null, "Configuration 3", CONFIG_1_1_3, false);
        namedConfigurationDAO.createConfiguration(namedConfiguration);
    }

    @Test(expected = NullPointerException.class)
    public void createConfiguration_shouldThrowOnNullName() {
        NamedConfiguration namedConfiguration =
                new NamedConfiguration(1, 1, waitingGenerator.getId(), null, CONFIG_1_1_3, false);
        namedConfigurationDAO.createConfiguration(namedConfiguration);
    }

    @Test(expected = NullPointerException.class)
    public void createConfiguration_shouldThrowOnNullConfiguration() {
        NamedConfiguration namedConfiguration =
                new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 3", null, false);
        namedConfigurationDAO.createConfiguration(namedConfiguration);
    }

    @Test(expected = DuplicateKeyException.class)
    public void createConfiguration_shouldThrowOnDuplicateName() {
        NamedConfiguration namedConfiguration =
                new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 2", CONFIG_1_1_2, false);
        namedConfigurationDAO.createConfiguration(namedConfiguration);
    }

    @Test
    @ExpectedDataSet("/dbunit/pluginContainer/plugin_configuration+deleted.xml")
    public void deleteConfiguration_shouldDeleteConfigurationsAndNotDeleteActiveConfigurations() {
        int rowsAffected;
        NamedConfiguration config;

        config = new NamedConfiguration(1, 1, waitingGenerator.getId(), "Configuration 2", null, false);
        rowsAffected = namedConfigurationDAO.deleteConfiguration(config);
        assertThat(rowsAffected, is(1));

        config = new NamedConfiguration(1, 2, waitingGenerator.getId(), "Configuration 1", null, false);
        rowsAffected = namedConfigurationDAO.deleteConfiguration(config);
        assertThat(rowsAffected, is(0));

        config = new NamedConfiguration(2, 1, waitingGenerator.getId(), "Configuration 1", null, false);
        rowsAffected = namedConfigurationDAO.deleteConfiguration(config);
        assertThat(rowsAffected, is(0));

        config = new NamedConfiguration(3, 1, waitingGenerator.getId(), "Configuration 2", null, false);
        rowsAffected = namedConfigurationDAO.deleteConfiguration(config);
        assertThat(rowsAffected, is(0));
    }

    @Test(expected = NullPointerException.class)
    public void deleteConfiguration_shouldThrowOnNullNamedConfiguration() {
        namedConfigurationDAO.deleteConfiguration(null);
    }

    @Test(expected = NullPointerException.class)
    public void deleteConfiguration_shouldThrowOnNullPluginId() {
        NamedConfiguration config = new NamedConfiguration(1, 1, null, "Configuration 2", null, false);
        namedConfigurationDAO.deleteConfiguration(config);
    }

    @Test(expected = NullPointerException.class)
    public void deleteConfiguration_shouldThrowOnNullPluginName() {
        NamedConfiguration config = new NamedConfiguration(1, 1, waitingGenerator.getId(), null, null, false);
        namedConfigurationDAO.deleteConfiguration(config);
    }
}
