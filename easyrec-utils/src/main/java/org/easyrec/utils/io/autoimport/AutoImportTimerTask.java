/**Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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
package org.easyrec.utils.io.autoimport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.TimerTask;

/**
 * This class periodically scans a given directory for .CSV files and dispatches the content via a specific header line.
 * <p/>
 * The file is then sent to a specific { @link at.researchstudio.sat.utils.io.autoimport.AutoImportCommand } implementation
 * for further processing of an automatic import.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Roman Cerny
 */
public class AutoImportTimerTask extends TimerTask {
    ///////////////////////////////////////////////////////////////////////////
    // constants
    private final String DATA_FILE_EXTENSION = ".csv";
    private final String RUNNING_FILE_EXTENSION = ".running";
    private final String LOCK_FILE_EXTENSION = ".lock";

    //////////////////////////////////////////////////////////////////////////
    // members
    private File autoImportDirectory = null;
    private HashMap<String, AutoImportCommand> typeToServiceMap = null;
    private String defaultServiceKeyword = null;

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    ///////////////////////////////////////////////////////////////////////////
    // methods

    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public AutoImportTimerTask(File autoImportDirectory, HashMap<String, AutoImportCommand> typeToServiceMap,
                               String defaultServiceKeyword) {
        super();
        this.autoImportDirectory = autoImportDirectory;
        this.typeToServiceMap = typeToServiceMap;
        this.defaultServiceKeyword = defaultServiceKeyword;
    }

    //////////////////////////////////////////////////////////////////////////
    // public methods
    @Override
    public void run() {
        if (!autoImportDirectory.isDirectory()) {
            if (logger.isWarnEnabled()) {
                logger.warn("'AutoImport' couldn't find directory '" + autoImportDirectory.getAbsolutePath() +
                        "', searching not possible");
            }
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace(
                        "'AutoImport' searching for new files in '" + autoImportDirectory.getAbsolutePath() + "' ...");
            }
            File[] filesInDirectory = autoImportDirectory.listFiles();
            String currentFileName = null;
            boolean foundNewFile = false;
            for (File currentFile : filesInDirectory) {
                currentFileName = currentFile.getAbsolutePath();
                if (currentFileName.endsWith(DATA_FILE_EXTENSION)) {
                    if (isNewFile(currentFileName, filesInDirectory)) {
                        foundNewFile = true;
                        if (logger.isInfoEnabled()) {
                            logger.info("'AutoImport' found new file in '" + autoImportDirectory.getAbsolutePath() +
                                    "', import will be started...");
                        }
                        File runningFile = null;
                        String filePart = null;
                        try {
                            // generate empty .running file to avoid multiple
                            // import of same data (while still importing)
                            filePart = currentFileName.substring(0, currentFileName.indexOf(DATA_FILE_EXTENSION));
                            runningFile = new File(filePart + RUNNING_FILE_EXTENSION);
                            runningFile.createNewFile();

                            BufferedReader br = null;
                            String line = null;

                            try {
                                br = new BufferedReader(new InputStreamReader(new FileInputStream(currentFileName)));

                                // read type
                                line = br.readLine();
                            } catch (Exception e) {
                                if (logger.isErrorEnabled()) {
                                    logger.error("exception occured during AutoImport: ", e);
                                }
                            }

                            // parse first line of currentFile
                            String currentType = null;
                            try {
                                currentType = AutoImportUtils.retrieveTypeFromLine(line);
                            } catch (IllegalArgumentException e) {
                                if (defaultServiceKeyword == null) {
                                    throw e;
                                } else {
                                    currentType = defaultServiceKeyword;
                                    if (logger.isInfoEnabled()) {
                                        logger.info(
                                                "couldn't retrieve type of file, 'autoimport' will send the file to the 'defaultService' => '" +
                                                        defaultServiceKeyword + "'");
                                        logger.info("to define a specific 'type' for a .CSV file simple add '" +
                                                AutoImportUtils.VALID_TYPE +
                                                "' as FIRST LINE of your file, for example '" +
                                                AutoImportUtils.VALID_TYPE_EXAMPLE + "'");
                                    }
                                }
                            }

                            // import data from CSV
                            AutoImportCommand command = typeToServiceMap.get(currentType);
                            if (command != null) {
                                if (logger.isInfoEnabled()) {
                                    logger.info("sending file '" + currentFileName + "' to the class '" +
                                            command.getClass().getName() + "' for further processing ...");
                                }
                                command.execute(currentFileName);
                            } else {
                                if (logger.isWarnEnabled()) {
                                    logger.warn("no 'AutoImportCommand' is mapped to the given type '" + currentType +
                                            "', take a look at the spring bean config xml 'spring.sat-util.autoimport.xml' file");
                                }
                            }
                        } catch (Exception e) {
                            logger.error(
                                    "exception occured during AutoImport of file '" + currentFile.getAbsolutePath() +
                                            "'", e);
                        } finally {
                            // delete .running file
                            if (runningFile != null) {
                                runningFile.delete();
                            }

                            // generate empty .lock file to avoid multiple
                            // import of same data (after import has finished)
                            File lockFile = new File(filePart + LOCK_FILE_EXTENSION);
                            try {
                                lockFile.createNewFile();
                            } catch (Exception e) {
                                logger.error("exception occured during AutoImport of file '" +
                                        currentFile.getAbsolutePath() + "' while creating a corresponding '.LOCK' file",
                                        e);
                            }
                        }
                    }
                }
            }
            if (logger.isInfoEnabled() && !foundNewFile) {
                logger.trace("'AutoImport' did not find new files in '" + autoImportDirectory.getAbsolutePath() + "'");
            }
        }
    }

    public void deleteCurrentRunningFiles() {
        if (autoImportDirectory.isDirectory()) {
            File[] filesInDirectory = autoImportDirectory.listFiles();
            String currentFileName = null;
            for (File currentFile : filesInDirectory) {
                currentFileName = currentFile.getAbsolutePath();
                if (currentFileName.endsWith(RUNNING_FILE_EXTENSION)) {
                    currentFile.delete();
                    if (logger.isInfoEnabled()) {
                        logger.info("'AutoImport' deleted old .running file '" + currentFileName + "'");
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // private methods
    private boolean isNewFile(String fileName, File[] filesInDirectory) {
        String partFileName = fileName.substring(0, fileName.indexOf(DATA_FILE_EXTENSION));
        String runningFileName = partFileName + RUNNING_FILE_EXTENSION;
        String lockFileName = partFileName + LOCK_FILE_EXTENSION;
        for (File currentFile : filesInDirectory) {
            String currentFileName = currentFile.getAbsolutePath();
            if (currentFileName.endsWith(runningFileName) || currentFileName.endsWith(lockFileName)) {
                return false;
            }
        }
        return true;
    }
}
