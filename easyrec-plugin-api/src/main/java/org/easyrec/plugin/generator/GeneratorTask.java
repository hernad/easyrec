package org.easyrec.plugin.generator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeneratorTask implements Runnable {

    private final Log logger = LogFactory.getLog(getClass());

    /**
     * Configuration specific for this task. The same object must not be used
     * for another task.
     */
    private final GeneratorConfiguration config;

    /**
     * Generator instance shared by all tasks that use this Generator class.
     */
    private final Generator generator;

    public GeneratorTask(GeneratorConfiguration config, Generator generator) {
        super();
        this.config = config;
        this.generator = generator;
    }

    @SuppressWarnings({"unchecked"})
    public void run() {
        try {
            this.generator.setConfiguration(config);
            this.generator.execute();
        } catch (Throwable t) {
            logger.warn("Caught exception while executing generator: " + generator.getDisplayName());
        }
    }

}
