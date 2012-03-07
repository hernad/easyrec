package org.easyrec.plugin.support;

import org.easyrec.plugin.Executable;
import org.easyrec.plugin.Progress;
import org.easyrec.plugin.exception.PluginException;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.stats.ExecutableStatistics;
import org.easyrec.plugin.util.ObserverRegistry;
import org.easyrec.plugin.util.ObserverRegistryImpl;

import java.net.URI;
import java.util.Date;

public abstract class ExecutablePluginSupport<S extends ExecutableStatistics> extends PluginSupport
        implements Executable<S> {
    private ObserverRegistryImpl<Executable> executableObserverRegistry = new ObserverRegistryImpl<Executable>(this);
    private ExecutionState executionState = ExecutionState.STOPPED;
    private Progress progress;
    private ExecutionControl executionControl = new ExecutionControl();
    private Class<S> statsClass;

    /**
     * Constructor that sets the displayName, id, and version. As Java makes it
     * almost impossible to create an instance of a class that is passed as a
     * generic type argument, and as we need to do it anyway, the class object of
     * the stats class is necessary here, too.
     *
     * @param displayName
     * @param id
     * @param version
     * @param statsClass
     */
    public ExecutablePluginSupport(String displayName, URI id, Version version, Class<S> statsClass) {
        super(displayName, id, version);
        this.statsClass = statsClass;
        this.progress = Progress.NOT_RUNNING;
    }

    public final void abort() {
        if (this.executionState.isRunning()) {
            changeExecutionState(ExecutionState.ABORT_REQUESTED);
        }
    }

    public final S execute() throws Exception {
        if (!getLifecyclePhase().isInitialized()) {
            throw new IllegalStateException("Executable has not been initialized");
        }
        S stats = null;
        try {
            stats = createExecutableStats();
            stats.setStartDate(new Date());
            changeExecutionState(ExecutionState.RUNNING);
            doExecute(executionControl, stats);
        } catch (Throwable t) {
            throw new PluginException(this, "Caught error during doExecute", t);
        } finally {
            changeExecutionState(ExecutionState.STOPPED);
            if (stats != null) stats.setEndDate(new Date());
        }
        return stats;
    }

    public ObserverRegistry<Executable> getExecutableObserverRegistry() {
        return this.executableObserverRegistry;
    }

    public ExecutionState getExecutionState() {
        return this.executionState;
    }

    public Progress getProgress() {
        return this.progress;
    }

    /**
     * Implementations place their main code here. Any Exception is wrapped in a
     * PluginException and thrown on. Implementations should check the
     * executionState frequently so that the application is responsive when
     * <code>abort()</code> is called. Likewise, the implementation should call
     * <code>updateProgress(Progress)</code> frequently so that the application
     * state can be monitored easily.
     * <p/>
     * The ExecutableStats
     *
     * @throws Exception
     */
    protected abstract void doExecute(ExecutionControl control, S stats) throws Exception;

    protected void updateProgress(Progress progress) {
        this.progress = progress;
        this.executableObserverRegistry.notifyObservers();
    }

    /**
     * Factory method for creating in instance of ExecutableStats.
     */
    private S createExecutableStats() {
        try {
            return this.statsClass.newInstance();
        } catch (Exception ex) {
            logger.warn("could not create instance of stats class " + this.statsClass.getName());
            throw new IllegalStateException("could not create instance of stats class " + this.statsClass.getName(),
                    ex);
        }
    }

    public Class<S> getStatisticsClass() {
        return statsClass;
    }

    private void changeExecutionState(ExecutionState newState) {
        this.executionState = newState;
        this.executableObserverRegistry.notifyObservers();
    }

    public class ExecutionControl {

        public boolean isAbortRequested() {
            return executionState.isAbortRequested();
        }

        public void updateProgress(Progress progress) {
            ExecutablePluginSupport.this.updateProgress(progress);
        }

        public void updateProgress(int currentStep, int totalSteps, String message) {
            updateProgress(new Progress(currentStep, totalSteps, message));
        }

        /**
         * Updates the {@code message} but leaves {@code currentStep} and {@code totalSteps} at the old values.
         *
         * @param message
         */
        public void updateProgress(String message) {
            updateProgress(getProgress().getCurrentSteps(), message);
        }

        /**
         * Updates the {@code @message} and {@code currentStep} but leaves {@code totalSteps} at the old value.
         *
         * @param currentStep
         * @param message
         */
        public void updateProgress(int currentStep, String message) {
            updateProgress(currentStep, getProgress().getTotalSteps(), message);
        }
    }
}
