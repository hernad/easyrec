package org.easyrec.plugin;

import org.easyrec.plugin.stats.ExecutableStatistics;
import org.easyrec.plugin.util.ObserverRegistry;

public interface Executable<S extends ExecutableStatistics> {

    /**
     * The actual work is done here. Note: Calling this method must clear the
     * 'aborted' status.
     * The ExecutableStats object that must be returned contains information
     * about the execution.
     */
    public S execute() throws Exception;

    /**
     * Causes an execution to end as soon as possible. Call has no effect if the
     * <code>Executable</code> is not running.
     */
    public void abort();

    public ExecutionState getExecutionState();

    /**
     * Fetches the last progress information of the current execution.
     *
     * @return
     */
    public Progress getProgress();


    /**
     * Fetch the delegate used to (un)register observers that monitor the
     * <code>Executable</code>'s progress.
     *
     * @return
     */
    public ObserverRegistry<Executable> getExecutableObserverRegistry();

    /**
     * Returns the actual class of the <code>ExecutableStatistics</code> object used.
     *
     * @return
     */
    public Class<S> getStatisticsClass();

    /**
     * The execution states of a Executable and their transitions:
     * <p/>
     * <pre>
     *           STOPPED
     *          ^       ^
     *          |       |
     *          v       |
     *      RUNNING -> ABORT_REQUESTED
     * <p/>
     * </pre>
     *
     * @author fkleedorfer
     */
    public enum ExecutionState {
        STOPPED,
        RUNNING,
        ABORT_REQUESTED;

        public boolean isStopped() {
            return this == STOPPED;
        }

        public boolean isRunning() {
            return this == RUNNING;
        }

        public boolean isAbortRequested() {
            return this == ABORT_REQUESTED;
        }
    }


}
