package org.easyrec.plugin.support.testplugin;

public class ForeverExecutingExampleGenerator extends ExampleGenerator {
    private Object lock;

    public ForeverExecutingExampleGenerator(Object lock) {
        super();
        this.lock = lock;
    }

    @Override
    protected void doExecute(ExecutionControl executionControl, ExampleStats stats) throws Exception {
        while (!getExecutionState().isAbortRequested()) {
            System.out.println("looping in doExecute(), waiting for abort()");
            Thread.sleep(1000);
            synchronized (lock) {
                lock.notify();
            }
        }
    }
}
