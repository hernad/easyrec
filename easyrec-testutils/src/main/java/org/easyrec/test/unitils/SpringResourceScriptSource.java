package org.easyrec.test.unitils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptContentHandle;
import org.unitils.dbmaintainer.script.impl.DefaultScriptSource;
import org.unitils.util.PropertyUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DOCUMENT ME!
 * <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p>
 * <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class SpringResourceScriptSource extends DefaultScriptSource {

    /* Logger instance for this class */
    private static final Log logger = LogFactory.getLog(SpringResourceScriptSource.class);

    private File getScriptFile(String scriptLocation) {
        try {
            return ResourceUtils.getFile(scriptLocation);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return A List containing all scripts in the given script locations, not null
     */
    protected List<Script> loadAllScripts() {
        List<String> scriptLocations = PropertyUtils.getStringList(PROPKEY_SCRIPT_LOCATIONS, configuration);
        List<Script> scripts = new ArrayList<Script>();
        for (String scriptLocation : scriptLocations) {
            logger.info("Script location: " + scriptLocation);

            if (scriptLocation.startsWith(PathMatchingResourcePatternResolver.CLASSPATH_URL_PREFIX) ||
                    scriptLocation.startsWith(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
                logger.info("using CLASSPATH_ALL_URL_PREFIX");

                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                String relativeLocation;

                if (scriptLocation.startsWith(PathMatchingResourcePatternResolver.CLASSPATH_URL_PREFIX))
                    relativeLocation =
                            scriptLocation.substring(PathMatchingResourcePatternResolver.CLASSPATH_URL_PREFIX.length());
                else
                    relativeLocation = scriptLocation.substring(
                            PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX.length());
                relativeLocation = relativeLocation.replaceAll("\\*", "");
                relativeLocation = relativeLocation.replaceAll("\\?", "");

                try {
                    Resource[] resources = resolver.getResources(scriptLocation);

                    logger.info("found " + resources.length + " resources.");

                    if (resources.length == 0) {
                        logger.info("no resources found for " + scriptLocation);
                        return scripts;
                    }

                    for (Resource resource : resources) {
                        try {
                            String name = resource.getURL().toString();

                            logger.info("processing resource " + name);

                            if (name.endsWith("/")) {
                                logger.info("    skipped " + name + " because it is a directory");
                                continue;
                            }

                            name = name.substring(name.lastIndexOf('/') + 1);

                            Script script = new Script(name, resource.lastModified(),
                                    new ScriptContentHandle.UrlScriptContentHandle(resource.getURL()));
                            scripts.add(script);
                        } catch (IOException ignored) {
                            logger.warn("Error resolving resource " + resource.getURL(), ignored);
                        }
                    }
                } catch (IOException ignored) {
                    logger.warn("Exception resolving resources for " + scriptLocation, ignored);
                }
            } else {
                if (!getScriptFile(scriptLocation).exists()) {
                    throw new UnitilsException(
                            "File location " + scriptLocation + " defined in property " + PROPKEY_SCRIPT_LOCATIONS +
                                    " doesn't exist");
                }
                getScriptsAt(scripts, scriptLocation, "");
            }
        }
        return scripts;
    }


    /**
     * Adds all scripts available in the given directory or one of its subdirectories to the
     * given List of files
     *
     * @param relativeLocation The current script location, not null
     * @param scriptRoot       The indexes of the current parent folders, not null
     * @param scripts          The list to which the available script have to be added
     */
    protected void getScriptsAt(List<Script> scripts, String scriptRoot, String relativeLocation) {
        File currentLocation = getScriptFile(scriptRoot + "/" + relativeLocation);
        if (currentLocation.isFile() && isScriptFile(currentLocation)) {
            Script script = createScript(currentLocation, relativeLocation);
            scripts.add(script);
            return;
        }
        // recursively scan sub folders for script files
        if (currentLocation.isDirectory()) {
            for (File subLocation : currentLocation.listFiles()) {
                getScriptsAt(scripts, scriptRoot,
                        "".equals(relativeLocation) ? subLocation.getName()
                                                    : relativeLocation + "/" + subLocation.getName());
            }
        }
    }
}
