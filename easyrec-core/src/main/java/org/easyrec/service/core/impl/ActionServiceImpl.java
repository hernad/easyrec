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
package org.easyrec.service.core.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ActionVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.service.core.ActionService;
import org.easyrec.store.dao.core.ActionDAO;
import org.easyrec.utils.io.autoimport.AutoImportService;
import org.easyrec.utils.io.autoimport.AutoImportUtils;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of the {@link org.easyrec.service.core.ActionService} interface.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2012-02-23 16:01:50 +0100 (Thu, 23 Feb 2012) $<br/>
 * $Revision: 140 $</p>
 *
 * @author Roman Cerny
 */
public class ActionServiceImpl implements ActionService {
    //////////////////////////////////////////////////////////////////////////////
    // constants

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    //////////////////////////////////////////////////////////////////////////////
    // members
    private ActionDAO actionDAO;

    // CSV import
    private int reportBlockSize = AutoImportService.DEFAULT__REPORT__BLOCK_SIZE;

    public ActionServiceImpl(ActionDAO actionDAO) {
        this.actionDAO = actionDAO;
    }

    // interface "ActionService" implementation
    public Iterator<ActionVO<Integer, Integer>> getActionIterator(int bulkSize) {
        return actionDAO.getActionIterator(bulkSize);
    }

    public Iterator<ActionVO<Integer, Integer>> getActionIterator(int bulkSize,
                                                                                             TimeConstraintVO timeConstraint) {
        return actionDAO.getActionIterator(bulkSize, timeConstraint);
    }

    public List<ActionVO<Integer, Integer>> getActionsFromUser(Integer tenantId,
                                                                                          Integer userId,
                                                                                          String sessionId) {
        return actionDAO.getActionsFromUser(tenantId, userId, sessionId);
    }

    public int insertAction(ActionVO<Integer, Integer> action) {
        return actionDAO.insertAction(action, false);
    }

    public int insertAction(ActionVO<Integer, Integer> action, boolean useDateFromVO) {
        return actionDAO.insertAction(action, useDateFromVO);
    }

    public int removeActionsByTenant(Integer tenantId) {
        return actionDAO.removeActionsByTenant(tenantId);
    }

    public List<ItemVO<Integer, Integer>> getItemsOfTenant(final Integer tenant,
                                                                    final Integer consideredItemType) {
        return actionDAO.getItemsOfTenant(tenant, consideredItemType);
    }

    public List<ItemVO<Integer, Integer>> getItemsByUserActionAndType(Integer tenantId, Integer userId,
                                                                               String sessionId,
                                                                               Integer consideredActionType,
                                                                               Integer consideredItemType,
                                                                               Double ratingThreshold,
                                                                               Integer numberOfLastActionsConsidered) {
        return actionDAO
                .getItemsByUserActionAndType(tenantId, userId, sessionId, consideredActionType, consideredItemType, ratingThreshold,
                        numberOfLastActionsConsidered);
    }

    public void importActionsFromCSV(String fileName) {
        importActionsFromCSV(fileName, null);
    }

    public void importActionsFromCSV(String fileName, ActionVO<Integer, Integer> defaults) {
        long start = System.currentTimeMillis();
        if (logger.isInfoEnabled()) {
            logger.info("==================== starting importing 'action' =======================");
            logger.info("importing 'action' from CSV '" + fileName + "'");
            logger.info("using interface defaults '" + defaults + "'");
            logger.info("========================================================================");
        }

        if (fileName == null) {
            throw new IllegalArgumentException("missing 'fileName'");
        }

        BufferedReader br = null;
        int lineCounter = 3;
        int savedCounter = 0;
        int removedCounter = 0;
        int skippedCounter = 0;
        int errorCounter = 0;
        int currentSavedCounter = 0;
        String command = null;

        try {
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("file '" + fileName + "' not found", e);
            }
            String line = null;
            String elementsOfLine[] = null;

            // read command
            try {
                line = br.readLine();
            } catch (IOException e) {
                throw new IllegalStateException("unexpected IOException", e);
            }
            try {
                // in case of we have an 'old style' file format, containing no 'type' definition, then the first line would contain the command
                command = AutoImportUtils.retrieveCommandFromLine(line);
            } catch (IllegalArgumentException e) {
                // this should be the normal case, the file contains a 'type' definition, so we need to skip this line
                // read/skip type
                try {
                    line = br.readLine();
                } catch (IOException ioe) {
                    throw new IllegalStateException("unexpected IOException", ioe);
                }
                command = AutoImportUtils.retrieveCommandFromLine(line);
            }

            // skip file if the command is not an 'insert' command
            if (!AutoImportUtils.COMMAND_INSERT.equalsIgnoreCase(command)) {
                throw new IllegalStateException("command '" + command + "' is not allowed for the type 'action'");
            }

            // read header and generate headerDefaults
            try {
                line = br.readLine();
            } catch (IOException e) {
                throw new IllegalStateException("unexpected IOException", e);
            }

            ActionVO<Integer, Integer> headerDefaults = generateDefaultsFromHeader(line,
                    defaults);
            if (logger.isInfoEnabled()) {
                logger.info("extracted header defaults from csv file, using: " + headerDefaults);
            }

            // fetch next line
            try {
                while ((line = br.readLine()) != null) {
                    lineCounter++;

                    // skip empty lines
                    if ("".equals(line)) {
                        AutoImportUtils.logSkippedLine(logger, lineCounter, line, "line is empty");
                        skippedCounter++;
                        continue;
                    }

                    // skip comment lines
                    if (line.startsWith(Character.toString(AutoImportUtils.CSV_COMMENT_CHAR))) {
                        AutoImportUtils.logSkippedLine(logger, lineCounter, line, "line is a comment");
                        skippedCounter++;
                        continue;
                    }

                    // skip lines, with wrong number of columns
                    elementsOfLine = line.split(AutoImportUtils.CSV_SEPARATOR, ActionVO.CSV_NUMBER_OF_COLUMNS);
                    if (elementsOfLine.length != ActionVO.CSV_NUMBER_OF_COLUMNS) {
                        StringBuilder s = new StringBuilder("', number of columns should be '");
                        s.append(ActionVO.CSV_NUMBER_OF_COLUMNS);
                        s.append("', but was '");
                        s.append(elementsOfLine.length);
                        s.append("'");
                        AutoImportUtils.logSkippedLine(logger, lineCounter, line, s.toString());
                        skippedCounter++;
                        continue;
                    }

                    ActionVO<Integer, Integer> action = null;
                    try {
                        action = (ActionVO<Integer, Integer>) headerDefaults.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new IllegalStateException(
                                "value object 'ActionVO' does not support .clone() anymore, check that!!");
                    }

                    // parse 'tenantId'
                    if (!"".equals(elementsOfLine[0])) {
                        try {
                            action.setTenant(Integer.parseInt(elementsOfLine[0]));
                        } catch (NumberFormatException nfe) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "value for field 'tenantId' is no valid 'Integer'");
                            skippedCounter++;
                            continue;
                        }
                    } else { // 'tenantId' NOT NULL
                        if (action.getTenant() == null) {
                            AutoImportUtils
                                    .logSkippedLine(logger, lineCounter, line, "no value for field 'tenantId' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'userId'
                    if (!"".equals(elementsOfLine[1])) {
                        try {
                            action.setUser(Integer.parseInt(elementsOfLine[1]));
                        } catch (NumberFormatException nfe) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "value for field 'userId' is no valid 'Integer'");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'sessionId'
                    if (!"".equals(elementsOfLine[2])) {
                        action.setSessionId(elementsOfLine[2]);
                    }

                    // parse 'ip'
                    if (!"".equals(elementsOfLine[3])) {
                        action.setIp(elementsOfLine[3]);
                    }

                    // parse 'itemId'
                    if (!"".equals(elementsOfLine[4])) {
                        try {
                            action.getItem().setItem(Integer.parseInt(elementsOfLine[4]));
                        } catch (NumberFormatException nfe) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "value for field 'itemId' is no valid 'Integer'");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'itemTypeId'
                    if (!"".equals(elementsOfLine[5])) {
                        try {
                            action.getItem().setType(Integer.parseInt(elementsOfLine[5]));
                        } catch (NumberFormatException nfe) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "value for field 'itemTypeId' is no valid 'Integer'");
                            skippedCounter++;
                            continue;
                        }
                    } else { // 'itemTypeId' NOT NULL
                        if (action.getItem().getType() == null) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "no value for field 'itemTypeId' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'actionTypeId'
                    if (!"".equals(elementsOfLine[6])) {
                        try {
                            action.setActionType(Integer.parseInt(elementsOfLine[6]));
                        } catch (NumberFormatException nfe) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "value for field 'actionTypeId' is no valid 'Integer'");
                            skippedCounter++;
                            continue;
                        }
                    } else { // 'actionTypeId' NOT NULL
                        if (action.getActionType() == null) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "no value for field 'actionTypeId' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'ratingValue'
                    if (!"".equals(elementsOfLine[7])) {
                        try {
                            action.setRatingValue(Integer.parseInt(elementsOfLine[7]));
                        } catch (NumberFormatException nfe) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "value for field 'ratingValue' is no valid 'Integer'");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'searchSucceeded'
                    if (!"".equals(elementsOfLine[8])) {
                        action.setSearchSucceeded(Boolean.parseBoolean(elementsOfLine[8]));
                    }

                    // parse 'numberOfFoundItems'
                    if (!"".equals(elementsOfLine[9])) {
                        try {
                            action.setNumberOfFoundItems(Integer.parseInt(elementsOfLine[9]));
                        } catch (NumberFormatException nfe) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "value for field 'numberOfFoundItems' is no valid 'Integer'");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'description'
                    if (!"".equals(elementsOfLine[10])) {
                        action.setDescription(elementsOfLine[10]);
                    }

                    try {
                        int savedThisIteration = 0;
                        // inserting action (no update allowed for type 'action')
                        savedThisIteration = insertAction(action);
                        currentSavedCounter += savedThisIteration;
                        savedCounter += savedThisIteration;
                        int currentSavedMultiplier = currentSavedCounter / reportBlockSize;
                        if (currentSavedMultiplier > 0) {
                            if (logger.isInfoEnabled()) {
                                logger.info("number of saved 'action' entries: " +
                                        (currentSavedMultiplier * reportBlockSize));
                            }
                            currentSavedCounter %= reportBlockSize;
                        }
                        /*} catch (DataIntegrityViolationException dive) {
                        // wait a little, we have two identical actions, without an actionDate
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            logger.info("error occured during (delayed try of) insertAction() '" + action + "'", ie);
                        }

                        int savedThisIteration = 0;
                        // inserting action (no update allowed for type 'action')
                        savedThisIteration = insertAction(action);
                        currentSavedCounter += savedThisIteration;
                        savedCounter += savedThisIteration;
                        int currentSavedMultiplier = currentSavedCounter / reportBlockSize;
                        if (currentSavedMultiplier > 0) {
                            if (logger.isInfoEnabled()) {
                                logger.info("number of saved 'action' entries: " + (currentSavedMultiplier * reportBlockSize));
                            }
                            currentSavedCounter %= reportBlockSize;
                        }*/
                    } catch (Exception e) {
                        errorCounter++;
                        logger.error("error occured during insertAction() '" + action + "'", e);
                    }
                } // end of while
            } catch (IOException e) {
                throw new IllegalStateException("unexpected IOException", e);
            }

        } finally {
            // close stream
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new IllegalStateException("unexpected IOException", e);
                }
            }
        }
        if (logger.isInfoEnabled()) {
            if (currentSavedCounter > 0) {
                logger.info("number of saved 'action' entries: " + currentSavedCounter);
            }
            logger.info("==================== finished importing from file '" + fileName + "' ====================");
            logger.info("total number of saved 'action' entries: " + savedCounter);
            logger.info("total number of removed 'action' entries: " + removedCounter);
            logger.info("total number of skipped 'action' entries: " + skippedCounter);
            logger.info("total number of errors occured while import: " + errorCounter);
            logger.info("used defaults: '" + defaults + "'");
            logger.info("time taken: " + (System.currentTimeMillis() - start) + " ms");
            logger.info("========================================================================");
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // private methods
    private ActionVO<Integer, Integer> generateDefaultsFromHeader(String header,
                                                                                             ActionVO<Integer, Integer> defaults)
            throws IllegalArgumentException {
        String[] elementsOfHeader = header.split(AutoImportUtils.CSV_SEPARATOR, ActionVO.CSV_NUMBER_OF_COLUMNS);

        if (elementsOfHeader.length != ActionVO.CSV_NUMBER_OF_COLUMNS) {
            throw new IllegalArgumentException(
                    "the number of columns in the header of an 'action' .CSV file must be '" +
                            ActionVO.CSV_NUMBER_OF_COLUMNS + "', but was '" + elementsOfHeader.length + "'");
        }

        ActionVO<Integer, Integer> action;
        if (defaults != null) {
            try {
                action = (ActionVO<Integer, Integer>) defaults.clone();
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(
                        "value object 'ActionVO' does not support .clone() anymore, check that!!");
            }
        } else {
            action = new ActionVO<Integer, Integer>(null, null, null, null,
                    new ItemVO<Integer, Integer>(null, null, null), null, null, null, null, null);
        }

        // parse 'tenantId'
        String defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[0]);
        if (defaultValue != null) {
            try {
                action.setTenant(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn("the default value for 'tenantId' in the CSV header is no valid 'Integer', passed type='" +
                        defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'userId'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[1]);
        if (defaultValue != null) {
            try {
                action.setUser(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn("the default value for 'userId' in the CSV header is no valid 'Integer', passed type='" +
                        defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'sessionId'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[2]);
        if (defaultValue != null) {
            action.setSessionId(defaultValue);
        }

        // parse 'ip'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[3]);
        if (defaultValue != null) {
            action.setIp(defaultValue);
        }

        // parse 'itemId'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[4]);
        if (defaultValue != null) {
            try {
                action.getItem().setItem(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn("the default value for 'itemId' in the CSV header is no valid 'Integer', passed type='" +
                        defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'itemTypeId'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[5]);
        if (defaultValue != null) {
            try {
                action.getItem().setType(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn(
                        "the default value for 'itemTypeId' in the CSV header is no valid 'Integer', passed type='" +
                                defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'actionTypeId'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[6]);
        if (defaultValue != null) {
            try {
                action.setActionType(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn(
                        "the default value for 'actionTypeId' in the CSV header is no valid 'Integer', passed type='" +
                                defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'ratingValue'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[7]);
        if (defaultValue != null) {
            try {
                action.setRatingValue(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn(
                        "the default value for 'ratingValue' in the CSV header is no valid 'Integer', passed type='" +
                                defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'searchSucceeded'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[8]);
        if (defaultValue != null) {
            action.setSearchSucceeded(Boolean.parseBoolean(defaultValue));
        }

        // parse 'numberOfFoundItems'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[9]);
        if (defaultValue != null) {
            try {
                action.setNumberOfFoundItems(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn(
                        "the default value for 'numberOfFoundItems' in the CSV header is no valid 'Integer', passed type='" +
                                defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'description'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[10]);
        if (defaultValue != null) {
            action.setDescription(defaultValue);
        }

        return action;
    }
}
