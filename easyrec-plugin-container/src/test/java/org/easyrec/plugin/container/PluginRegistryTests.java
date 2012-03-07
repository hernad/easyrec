/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easyrec.plugin.container;

import org.apache.commons.io.IOUtils;
import org.easyrec.model.plugin.PluginVO;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.plugin.waiting.WaitingGenerator;
import org.easyrec.plugin.waiting.WaitingGeneratorConfiguration;
import org.easyrec.store.dao.plugin.PluginDAO;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import static junit.framework.Assert.fail;

/**
 * @author fkleedorfer
 */

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext("/spring/pluginContainer/Plugins_AllInOne.xml")
@DataSet("/dbunit/pluginContainer/default_db.xml")
public class PluginRegistryTests {

    @SpringBeanByName
    private PluginRegistry pluginRegistry;

    @SpringBeanByName
    private PluginDAO pluginDAO;

    private static Resource PLUGIN_INPUT_FOLDER = new FileSystemResource(new File(URI.create(
            PluginRegistryTests.class.getClassLoader().getResource("plugins").toString())));
    private static Resource PLUGIN_FOLDER = new FileSystemResource(System.getProperty("java.io.tmpdir"));
    private static boolean createdTmpDir;

    @BeforeClass
    public static void beforeClass() throws Exception {
        File tmpDir = new File(PLUGIN_FOLDER.getFile(), "tmp");

        if(!tmpDir.exists() ) {
            if(!tmpDir.mkdir())
                fail("Could not create tmp directory necessary for tests");

            createdTmpDir = true;
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if(createdTmpDir) {
            File tmpDir = new File(PLUGIN_FOLDER.getFile(), "tmp");

            if(tmpDir.exists()) tmpDir.delete();
        }
    }

    @Test
    public void testIsAllExecutablesStoppedOnEmptyRegistry() {
        Assert.assertTrue(this.pluginRegistry.isAllExecutablesStopped());
    }

    @Test
    public void testIsAllExecutablesStoppedLoadWaiting() throws Exception {
        File pluginFile = new File(PLUGIN_INPUT_FOLDER.getFile(), "waitingPlugin.jar");
        byte[] pluginContent = IOUtils.toByteArray(new FileInputStream(pluginFile));

        this.pluginRegistry.setPluginFolder(PLUGIN_FOLDER);
        PluginVO pluginVO = this.pluginRegistry.checkPlugin(pluginContent);
        this.pluginDAO.storePlugin(pluginVO);
        this.pluginRegistry.installPlugin(WaitingGenerator.ID, WaitingGenerator.VERSION);
        int found = 0;
        for (Generator<GeneratorConfiguration, GeneratorStatistics> gen : this.pluginRegistry.getGenerators()
                .values()) {
            if (WaitingGenerator.class.isAssignableFrom(gen.getClass())) {
                found++;
            }
        }
        Assert.assertEquals(1, found);
        Assert.assertEquals(2, this.pluginRegistry.getGenerators().size());
        this.pluginRegistry.deactivatePlugin(WaitingGenerator.ID, WaitingGenerator.VERSION);
        found = 0;
        for (Generator<GeneratorConfiguration, GeneratorStatistics> gen : this.pluginRegistry.getGenerators()
                .values()) {
            if (WaitingGenerator.class.isAssignableFrom(gen.getClass())) {
                found++;
            }
        }
        Assert.assertEquals(0, found);
        Assert.assertEquals(1, this.pluginRegistry.getGenerators().size());
    }

    @Test
    public void testIsAllExecutablesStoppedWaitingLoaded() throws Exception {
        File pluginFile = new File(PLUGIN_INPUT_FOLDER.getFile(), "waitingPlugin.jar");
        byte[] pluginContent = IOUtils.toByteArray(new FileInputStream(pluginFile));

        this.pluginRegistry.setPluginFolder(PLUGIN_FOLDER);
        PluginVO pluginVO = this.pluginRegistry.checkPlugin(pluginContent);
        this.pluginDAO.storePlugin(pluginVO);
        this.pluginRegistry.installPlugin(WaitingGenerator.ID, WaitingGenerator.VERSION);
        int found = 0;
        for (Generator<GeneratorConfiguration, GeneratorStatistics> gen : this.pluginRegistry.getGenerators()
                .values()) {
            if (WaitingGenerator.class.isAssignableFrom(gen.getClass())) {
                found++;
            }
        }
        Assert.assertEquals(1, found);
        Assert.assertEquals(2, this.pluginRegistry.getGenerators().size());
        final Generator<GeneratorConfiguration, GeneratorStatistics> generator = this.pluginRegistry.getGenerators()
                .get(new PluginId(WaitingGenerator.ID, WaitingGenerator.VERSION));
        WaitingGeneratorConfiguration conf = (WaitingGeneratorConfiguration) generator.getConfiguration();
        conf.setTimeout(2000);
        Assert.assertTrue(this.pluginRegistry.isAllExecutablesStopped());
        final CountDownLatch latch = new CountDownLatch(1);
        Thread runner = new Thread(new Runnable() {

            public void run() {
                try {
                    latch.countDown();
                    generator.execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        runner.start();
        latch.await();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        Assert.assertFalse(this.pluginRegistry.isAllExecutablesStopped());
        runner.join();
        Assert.assertTrue(this.pluginRegistry.isAllExecutablesStopped());
        this.pluginRegistry.deactivatePlugin(WaitingGenerator.ID, WaitingGenerator.VERSION);
        found = 0;
        for (Generator<GeneratorConfiguration, GeneratorStatistics> gen : this.pluginRegistry.getGenerators()
                .values()) {
            if (WaitingGenerator.class.isAssignableFrom(gen.getClass())) {
                found++;
            }
        }
        Assert.assertEquals(0, found);
        Assert.assertEquals(1, this.pluginRegistry.getGenerators().size());
    }


}
