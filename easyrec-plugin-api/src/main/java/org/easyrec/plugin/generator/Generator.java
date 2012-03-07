package org.easyrec.plugin.generator;

import org.easyrec.plugin.Executable;
import org.easyrec.plugin.Plugin;
import org.easyrec.plugin.configuration.Configurable;
import org.easyrec.plugin.stats.GeneratorStatistics;

/**
 * Item association generator interface.
 * <p/>
 * Implementations need not be thread-safe.
 *
 * @author
 */
public interface Generator<C extends GeneratorConfiguration, S extends GeneratorStatistics>
        extends Executable<S>, Plugin, Configurable<C> {

    /**
     * Gets the source type that must be used by the generator.
     *
     * @return The source type.
     */
    public String getSourceType();
}
