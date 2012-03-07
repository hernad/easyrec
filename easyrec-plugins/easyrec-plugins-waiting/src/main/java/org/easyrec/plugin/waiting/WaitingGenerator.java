/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.plugin.waiting;

import org.easyrec.plugin.Progress;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.support.GeneratorPluginSupport;

import java.net.URI;

/**
 * @author fkleedorfer
 */
public class WaitingGenerator extends GeneratorPluginSupport<WaitingGeneratorConfiguration, WaitingGeneratorStats> {

    // the display name is the name of the generator that will show up in the admin tool when the plugin has been loaded.
    public static final String DISPLAY_NAME = "Waiting Generator";
    // version of the generator, should be ascending for each new release
    // you might increment the versioning components (major,minor,misc) like this:
    //   major - complete reworks of your generator, major new features
    //   minor - small feature improvements
    //   misc  - bugfix releases or anything else
    public static final Version VERSION = new Version("0.97");

    // The URI that uniquely identifies the plugin. While any valid URI is technically ok here, implementors
    // should choose their URIs wisely, ideally the URI should be 'cool'
    // (@see <a href="http://www.dfki.uni-kl.de/~sauermann/2006/11/cooluris/#cooluris">Cool URIs for the
    // Semantic Web</a>) If unsure, use an all-lowercase http URI pointing to a host/path that you control,
    // ending with '#[plugin-name]'.
    public static final URI ID = URI.create("http://www.easyrec.org/plugins/waiting");


    public WaitingGenerator() {
        super(DISPLAY_NAME, ID, VERSION, WaitingGeneratorConfiguration.class, WaitingGeneratorStats.class);
    }


    /**
     * This method does nothing but sleep for a specified time.
     *
     * @param control
     * @param stats
     * @throws Exception
     */
    @Override
    protected void doExecute(ExecutionControl control, WaitingGeneratorStats stats) throws Exception {
        //fetch config
        WaitingGeneratorConfiguration conf = this.getConfiguration();
        //calculate total steps
        int totalSteps = conf.getNumberOfPhases() + 1;
        //update progress
        Progress progress = new Progress(0, totalSteps, "preparing");
        this.updateProgress(progress);
        //sleep N times for M seconds and check isAbortRequested between sleeps
        try {
            for (int i = 0; i < conf.getNumberOfPhases() && !control.isAbortRequested(); i++) {
                //check if aborted
                if (control.isAbortRequested()) break;
                //update progress
                progress = new Progress(i + 1, totalSteps, "sleeping");
                this.updateProgress(progress);
                //sleep
                Thread.sleep(conf.getTimeout());
            }
        } catch (InterruptedException e) {
            //ignore
        }
        //final progress update
        progress = new Progress(totalSteps, totalSteps, "done");
        this.updateProgress(progress);
    }

    @Override
    public String getPluginDescription() {
        return "This plugin is used for testing purposes. It can be configured to wait for a specified timeout, and it doesn't do anything else.";
    }


}
