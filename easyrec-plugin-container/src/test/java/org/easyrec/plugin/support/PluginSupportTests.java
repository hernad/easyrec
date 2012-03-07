package org.easyrec.plugin.support;

import org.easyrec.plugin.Executable;
import org.easyrec.plugin.Plugin;
import org.easyrec.plugin.exception.PluginException;
import org.easyrec.plugin.support.testplugin.ExampleGenerator;
import org.easyrec.plugin.support.testplugin.ForeverExecutingExampleGenerator;
import org.easyrec.plugin.util.Observer;
import org.junit.Test;

import static org.junit.Assert.*;

public class PluginSupportTests {
    @Test
    public void testNormalCase() throws Exception {
        ExampleGenerator generator = new ExampleGenerator();
        assertEquals(Plugin.LifecyclePhase.NOT_INSTALLED, generator.getLifecyclePhase());
        generator.install(true);
        assertEquals(Plugin.LifecyclePhase.INSTALLED, generator.getLifecyclePhase());
        generator.initialize();
        assertEquals(Plugin.LifecyclePhase.INITIALIZED, generator.getLifecyclePhase());
        generator.execute();
        assertEquals(Executable.ExecutionState.STOPPED, generator.getExecutionState());
        generator.cleanup();
        assertEquals(Plugin.LifecyclePhase.INSTALLED, generator.getLifecyclePhase());
        generator.uninstall();
        assertEquals(Plugin.LifecyclePhase.NOT_INSTALLED, generator.getLifecyclePhase());
    }

    @Test
    public void testExceptionInInit() throws Exception {
        ExampleGenerator generator = new ExampleGenerator();
        generator.install(true);
        assertEquals(Plugin.LifecyclePhase.INSTALLED, generator.getLifecyclePhase());
        generator.setThrowException(true);
        try {
            generator.initialize();
            fail("should have thrown PluginException");
        } catch (PluginException e) {
        }
        assertEquals(Plugin.LifecyclePhase.INIT_FAILED, generator.getLifecyclePhase());
    }

    @Test
    public void testExceptionInExec() throws Exception {
        ExampleGenerator generator = new ExampleGenerator();
        generator.install(true);
        assertEquals(Plugin.LifecyclePhase.INSTALLED, generator.getLifecyclePhase());
        generator.initialize();
        assertEquals(Plugin.LifecyclePhase.INITIALIZED, generator.getLifecyclePhase());
        generator.setThrowException(true);
        try {
            generator.execute();
            fail("should have thrown PluginException");
        } catch (PluginException e) {
        }
        assertEquals(Executable.ExecutionState.STOPPED, generator.getExecutionState());
    }

    @Test
    public void testExceptionInCleanup() throws Exception {
        ExampleGenerator generator = new ExampleGenerator();
        generator.install(true);
        assertEquals(Plugin.LifecyclePhase.INSTALLED, generator.getLifecyclePhase());
        generator.initialize();
        assertEquals(Plugin.LifecyclePhase.INITIALIZED, generator.getLifecyclePhase());
        generator.execute();
        assertEquals(Executable.ExecutionState.STOPPED, generator.getExecutionState());
        generator.setThrowException(true);
        try {
            generator.cleanup();
            fail("should have thrown PluginException");
        } catch (PluginException e) {
        }
        assertEquals(Plugin.LifecyclePhase.CLEANUP_FAILED, generator.getLifecyclePhase());
    }

    @Test
    public void testAbort() throws Exception {
        final Object lock = new Object();
        final ExampleGenerator generator = new ForeverExecutingExampleGenerator(lock);
        generator.install(true);
        assertEquals(Plugin.LifecyclePhase.INSTALLED, generator.getLifecyclePhase());
        generator.initialize();
        assertEquals(Plugin.LifecyclePhase.INITIALIZED, generator.getLifecyclePhase());
        new Thread(new Runnable() {
            public void run() {
                try {
                    generator.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        synchronized (lock) {
            lock.wait();
        }
        generator.abort();
        assertEquals(Executable.ExecutionState.ABORT_REQUESTED, generator.getExecutionState());
    }

    @Test
    public void testPluginObserver() throws Exception {
        final boolean[] observerCalled = new boolean[1];
        Observer<Plugin> pluginObserver = new Observer<Plugin>() {
            public void stateChanged(Plugin target) {
                observerCalled[0] = true;
            }
        };
        ExampleGenerator generator = new ExampleGenerator();
        generator.install(true);
        generator.getPluginObserverRegistry().addObserver(pluginObserver);
        observerCalled[0] = false;
        generator.initialize();
        assertTrue(observerCalled[0]);
        observerCalled[0] = false;
        generator.execute();
        assertFalse(observerCalled[0]); // execute does not trigger plugin
        // observers!
        observerCalled[0] = false;
        generator.cleanup();
        assertTrue(observerCalled[0]);
        observerCalled[0] = false;
    }

    @Test
    public void testExecutableObserver() throws Exception {
        final int[] observerCalled = new int[1];
        Observer<Executable> executableObserver = new Observer<Executable>() {
            public void stateChanged(Executable target) {
                observerCalled[0]++;
            }
        };
        ExampleGenerator generator = new ExampleGenerator();
        generator.install(true);
        generator.getExecutableObserverRegistry().addObserver(executableObserver);
        observerCalled[0] = 0;
        generator.initialize();
        assertEquals(0, observerCalled[0]);
        generator.execute();
        // we were called 5 times: 3 from the doExecute method, and 2 times for
        // the
        // execution state changes
        assertEquals(5, observerCalled[0]);
        generator.cleanup();
        assertEquals(5, observerCalled[0]);
    }

}
