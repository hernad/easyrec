package org.easyrec.plugin.support.testplugin;

import org.easyrec.plugin.Progress;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.support.GeneratorPluginSupport;

import java.net.URI;

public class ExampleGenerator extends GeneratorPluginSupport<ExampleConfiguration, ExampleStats> {

    protected boolean throwException = false;

    public ExampleGenerator() {
        super("ExampleGenerator", URI.create("http://easyrec.org/plugin/gen/example"), new Version("0.1"),
                ExampleConfiguration.class, ExampleStats.class);
    }

    @Override
    protected void doExecute(ExecutionControl executionControl, ExampleStats stats) throws Exception {
        System.out.println("stats=" + stats);
        updateProgress(new Progress(3, 1, "step one"));
        updateProgress(new Progress(3, 2, "step one"));
        updateProgress(new Progress(3, 3, "final step"));
        throwExceptionIfConfigured();

    }

    @Override
    protected void doCleanup() throws Exception {
        throwExceptionIfConfigured();
    }

    @Override
    protected void doInitialize() throws Exception {
        throwExceptionIfConfigured();
    }

    @Override
    protected void doInstall() throws Exception {
        throwExceptionIfConfigured();
    }

    @Override
    protected void doUninstall() throws Exception {
        throwExceptionIfConfigured();
    }

    public String getPluginDescription() {
        return "This is an ExapmpleGenerator that creates rules withour meaning:-)";
    }

    public ExampleConfiguration newConfiguration() {
        return new ExampleConfiguration();
    }

    private void throwExceptionIfConfigured() throws Exception {
        if (throwException) {
            throw new Exception("Test exception");
        }
    }

    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }

}
