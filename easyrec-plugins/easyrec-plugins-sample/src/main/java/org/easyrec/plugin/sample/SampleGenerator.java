/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.easyrec.plugin.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.support.GeneratorPluginSupport;
import org.easyrec.service.core.ActionService;
import org.easyrec.service.core.ItemAssocService;
import org.easyrec.service.domain.TypeMappingService;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sample generator plugin that demonstrates how to use the easyrec plugin API. <p/> <p><b>Company:&nbsp;</b> SAT,
 * Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/>
 * $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class SampleGenerator extends GeneratorPluginSupport<SampleGeneratorConfig, SampleGeneratorStats> {
    // ------------------------------ FIELDS ------------------------------

    // the display name is the name of the generator that will show up in the admin tool when the plugin has been loaded.
    public static final String DISPLAY_NAME = "Demo Generator";
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
    public static final URI ID = URI.create("http://www.easyrec.org/plugins/sample");

    private static final Log logger = LogFactory.getLog(SampleGenerator.class);

    // the service will be auto-wired when the plugin is loaded, see {@link #setActionService(ActionService)}.
    private ActionService actionService;

    // --------------------------- CONSTRUCTORS ---------------------------

    public SampleGenerator() {
        // we need to call the constructor of GeneratorPluginSupport to provide the name, id and version
        //additionally, we have to pass the class objects of config and stats classes.
        super(DISPLAY_NAME, ID, VERSION, SampleGeneratorConfig.class, SampleGeneratorStats.class);
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    // this method will be called when the plugin is being loaded and Spring injects the service, you need to make sure
    // that everything after the "set" part of the method name is named exactly like the Spring-bean.
    // For all beans that can be injected look in the wiki.
    public void setActionService(final ActionService actionService) {
        this.actionService = actionService;
    }

    // ------------------------ INTERFACE METHODS ------------------------

    @Override
    public String getPluginDescription() {
        return "This is a sample generator that crates random recommendations for each item found. It just takes one item and creates a random list of recommendations." +
                "The number of recommendations can be defined using the easyrec admin tool.";
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected void doCleanup() throws Exception {
        logger.info("The plugin is now being uninstalled.");
        // remove all tables/files/resources you created in {@link #doInitialize()}.
        // optional - you don't have to implement this method
    }

    @Override
    protected void doExecute(ExecutionControl executionControl, SampleGeneratorStats stats) throws Exception {
        // when doExecute() is called, the generator has been initialized with the configuration we should use
        SampleGeneratorConfig config = getConfiguration();

        //
        // get some parameters that are required for storing our generated item associations.

        TypeMappingService typeMappingService = (TypeMappingService) super.getTypeMappingService();

        // get the id for the type of association between two items
        Integer assocType = typeMappingService.getIdOfAssocType(config.getTenantId(), config.getAssociationType());
        // the itemType used for searching items.
        Integer itemType = typeMappingService.getIdOfItemType(config.getTenantId(), config.getItemType());
        // the sourceType used for storing item associations.
        Integer sourceType = typeMappingService.getIdOfSourceType(config.getTenantId(), getSourceType());
        // the viewType used for storing item associations.
        Integer viewType = typeMappingService.getIdOfViewType(config.getTenantId(), config.getViewType());

        // store the date when this doExecute() run was started.
        Date execution = new Date();

        // the generator needs to check periodically if abort was requested and stop operation in a clean manner
        if (executionControl.isAbortRequested()) return;

        //
        // get the items we need to work on

        // use the service to get all items
        List<ItemVO<Integer, Integer>> items = actionService.getItemsOfTenant(config.getTenantId(), itemType);

        //
        // start the "calculation" of the plugin.

        stats.setNumberOfItems(items.size());

        int numberOfRecs = Math.min(config.getNumberOfRecs(), items.size() - 1);
        final int MAX_PROGRESS = items.size() * numberOfRecs;
        int currentProgress = 0;

        for (ItemVO<Integer, Integer> item : items) {
            Set<ItemVO<Integer, Integer>> alreadyUsedOtherItems = new HashSet<ItemVO<Integer, Integer>>();
            INNER:
            for (int i = 0; i < config.getNumberOfRecs(); i++) {
                // update the progress using the execution control. the progress will be displayed in the administration
                // tool or - when using the commandline interface - on stdout.
                executionControl.updateProgress(currentProgress, MAX_PROGRESS, "Generating item associations.");
                currentProgress++;

                // abortion might be requested at anytime from the admin tool.
                // therefore check again if abort was requested, we don't need to check every operation.
                // but before and after possibly lengthy operations is a good place to check.
                if (executionControl.isAbortRequested()) return;

                // some random calculations:
                // fetch a random item (making sure its not the current item)
                ItemVO<Integer, Integer> otherItem = null;
                int maxTries = 3;
                int tries = 0;
                while ((otherItem == null || otherItem.equals(item) || alreadyUsedOtherItems.contains(otherItem))) {
                    otherItem = items.get((int)(Math.random() * items.size()));
                    if (tries++ > maxTries) break INNER;
                }
                alreadyUsedOtherItems.add(otherItem);
                // create association between the current item and the random item
                ItemAssocVO<Integer,Integer> itemAssoc = new ItemAssocVO<Integer,Integer>(
                        config.getTenantId(), item, assocType, Math.random() * 100.0, otherItem, sourceType,
                        "Demo Generator", viewType, null, execution);

                // we use the item-assoc service to access a standard easyrec table, some services like this one are
                // already injected to the superclass so we have ready access to them without the need to code the
                // injection method (like {@link #setActionService} ourselves.
                ItemAssocService itemAssocService = getItemAssocService();

                itemAssocService.insertOrUpdateItemAssoc(itemAssoc);
                //for reporting purposes, we remember how many rules we create
                stats.incNumberOfRulesCreated();
            }
        }
        //another information for reporting: the number of user actions considered in
        //rule creation. This plugin, however, is so simple that it doesn't need
        //to consider any actions
        stats.setNumberOfActionsConsidered(0);
    }

    @Override
    protected void doInitialize() throws Exception {
        // This method will be run each time easyrec starts-up and you can do some preinitialization of your plugin here.
        logger.info("The plugin is now being initialized by easyrec.");
        // optional - you don't have to implement this method
    }

    @Override
    protected void doInstall() throws Exception {
        // This method will only be called once when the plugin is uploaded to easyrec.
        // You can set-up your database here or do some other run-once tasks.
        logger.info("The plugin is now being installed.");
        // optional - you don't have to implement this method
    }

    @Override
    protected void doUninstall() throws Exception {
        // This method will only be called once when the plugin is deleted from easyrec.
        // You need to remove all resources created by your plugin (including entries in easyrec database tables.)
        logger.info("The plugin is now being uninstalled.");
        // optional - you don't have to implement this method
    }
}
