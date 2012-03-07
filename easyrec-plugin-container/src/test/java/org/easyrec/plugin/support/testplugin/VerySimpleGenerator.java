/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.plugin.support.testplugin;

import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.support.GeneratorPluginSupport;

import java.net.URI;

/**
 * @author fkleedorfer
 */
public class VerySimpleGenerator extends GeneratorPluginSupport<GeneratorConfiguration, ExampleStats> {

    public VerySimpleGenerator(String displayName, URI id, Version version) {
        super(displayName, id, version, GeneratorConfiguration.class, ExampleStats.class);
    }

    public String getPluginDescription() {
        return "This Generator creates the simplest rules ever!";
    }

    @Override
    protected void doExecute(ExecutionControl control, ExampleStats stats) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
