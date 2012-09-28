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
import org.easyrec.model.core.AssociatedItemVO;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.transfer.IAConstraintVO;
import org.easyrec.service.core.ItemAssocService;
import org.easyrec.store.dao.core.ItemAssocDAO;
import org.easyrec.utils.io.autoimport.AutoImportService;
import org.easyrec.utils.io.autoimport.AutoImportUtils;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of the {@link org.easyrec.service.core.ItemAssocService} interface.
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
public class ItemAssocServiceImpl implements ItemAssocService {
    //////////////////////////////////////////////////////////////////////////////
    // constants

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    //////////////////////////////////////////////////////////////////////////////
    // members
    private ItemAssocDAO itemAssocDAO;

    // CSV import
    private boolean importOverwriteDuplicates = AutoImportService.DEFAULT__OVERWRITE_DUPLICATES;
    private int reportBlockSize = AutoImportService.DEFAULT__REPORT__BLOCK_SIZE;

    public ItemAssocServiceImpl(ItemAssocDAO itemAssocDAO) {
        this.itemAssocDAO = itemAssocDAO;
    }

    //////////////////////////////////////////////////////////////////////////////
    // interface "ItemAssocService" implementation
    public List<ItemAssocVO<Integer,Integer>> getItemAssocs(
            ItemVO<Integer, Integer> itemFrom, Integer assocTypeId, ItemVO<Integer, Integer> itemTo,
            IAConstraintVO<Integer, Integer> constraints) {
        return itemAssocDAO.getItemAssocs(itemFrom, assocTypeId, itemTo, constraints);
    }

    public List<ItemAssocVO<Integer,Integer>> getItemAssocsFromTenant(
            Integer tenant, Integer numberOfResults) {
        return itemAssocDAO.getItemAssocsQBE(null, null, null,
                new IAConstraintVO<Integer, Integer>(numberOfResults, tenant));
    }

    public List<ItemAssocVO<Integer,Integer>> getItemAssocsForItem(Integer tenant,
                                                                                                        ItemVO<Integer, Integer> itemFrom,
                                                                                                        Integer numberOfResults) {
        return itemAssocDAO.getItemAssocsQBE(itemFrom, null, null,
                new IAConstraintVO<Integer, Integer>(numberOfResults, null, null, null, tenant, null, false));
    }

    public List<AssociatedItemVO<Integer, Integer>> getItemsFrom(Integer itemFromTypeId,
                                                                                   Integer assocTypeId,
                                                                                   ItemVO<Integer, Integer> itemTo,
                                                                                   IAConstraintVO<Integer, Integer> constraints) {
        return itemAssocDAO.getItemsFrom(itemFromTypeId, assocTypeId, itemTo, constraints);
    }

    public List<AssociatedItemVO<Integer, Integer>> getItemsTo(
            ItemVO<Integer, Integer> itemFrom, Integer assocTypeId, Integer itemToTypeId,
            IAConstraintVO<Integer, Integer> constraints) {
        return itemAssocDAO.getItemsTo(itemFrom, assocTypeId, itemToTypeId, constraints);
    }

    public void importItemAssocsFromCSV(String fileName) {
        importItemAssocsFromCSV(fileName, null);
    }

    public void importItemAssocsFromCSV(String fileName,
                                        ItemAssocVO<Integer,Integer> defaults) {
        long start = System.currentTimeMillis();
        if (logger.isInfoEnabled()) {
            logger.info("==================== starting importing 'itemAssoc' ====================");
            logger.info("importing 'itemAssoc' from CSV '" + fileName + "'");
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
        int currentRemovedCounter = 0;
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

            boolean commandEqualsInsertCommand = AutoImportUtils.COMMAND_INSERT.equalsIgnoreCase(command);

            // read header and generate headerDefaults
            try {
                line = br.readLine();
            } catch (IOException e) {
                throw new IllegalStateException("unexpected IOException", e);
            }

            ItemAssocVO<Integer,Integer> headerDefaults = generateDefaultsFromHeader(
                    line, defaults);
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
                    elementsOfLine = line.split(AutoImportUtils.CSV_SEPARATOR, ItemAssocVO.CSV_NUMBER_OF_COLUMNS);
                    if (elementsOfLine.length != ItemAssocVO.CSV_NUMBER_OF_COLUMNS) {
                        StringBuilder s = new StringBuilder("', number of columns should be '");
                        s.append(ItemAssocVO.CSV_NUMBER_OF_COLUMNS);
                        s.append("', but was '");
                        s.append(elementsOfLine.length);
                        s.append("'");
                        AutoImportUtils.logSkippedLine(logger, lineCounter, line, s.toString());
                        skippedCounter++;
                        continue;
                    }

                    ItemAssocVO<Integer,Integer> iAssoc = null;
                    try {
                        iAssoc = (ItemAssocVO<Integer,Integer>) headerDefaults
                                .clone();
                    } catch (CloneNotSupportedException e) {
                        throw new IllegalStateException(
                                "value object 'ItemAssocVO' does not support .clone() anymore, check that!!");
                    }

                    // parse 'tenantId'
                    if (!"".equals(elementsOfLine[0])) {
                        try {
                            iAssoc.setTenant(Integer.parseInt(elementsOfLine[0]));
                        } catch (NumberFormatException nfe) {
                            if (commandEqualsInsertCommand) {
                                AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                        "value for field 'tenantId' is no valid 'Integer'");
                                skippedCounter++;
                                continue;
                            }
                        }
                    } else { // 'tenantId' NOT NULL
                        if (commandEqualsInsertCommand && iAssoc.getTenant() == null) {
                            AutoImportUtils
                                    .logSkippedLine(logger, lineCounter, line, "no value for field 'tenantId' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'itemFromId'
                    if (!"".equals(elementsOfLine[1])) {
                        try {
                            iAssoc.getItemFrom().setItem(Integer.parseInt(elementsOfLine[1]));
                        } catch (NumberFormatException nfe) {
                            if (commandEqualsInsertCommand) {
                                AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                        "value for field 'itemFromId' is no valid 'Integer'");
                                skippedCounter++;
                                continue;
                            }
                        }
                    } else { // 'itemFromId' NOT NULL
                        if (commandEqualsInsertCommand && iAssoc.getItemFrom().getItem() == null) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "no value for field 'itemFromId' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'itemFromTypeId'
                    if (!"".equals(elementsOfLine[2])) {
                        try {
                            iAssoc.getItemFrom().setType(Integer.parseInt(elementsOfLine[2]));
                        } catch (NumberFormatException nfe) {
                            if (commandEqualsInsertCommand) {
                                AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                        "value for field 'itemFromTypeId' is no valid 'Integer'");
                                skippedCounter++;
                                continue;
                            }
                        }
                    } else { // 'itemFromTypeId' NOT NULL
                        if (commandEqualsInsertCommand && iAssoc.getItemFrom().getType() == null) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "no value for field 'itemFromTypeId' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'assocTypeId'
                    if (!"".equals(elementsOfLine[3])) {
                        try {
                            iAssoc.setAssocType(Integer.parseInt(elementsOfLine[3]));
                        } catch (NumberFormatException nfe) {
                            if (commandEqualsInsertCommand) {
                                AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                        "value for field 'assocTypeId' is no valid 'Integer'");
                                skippedCounter++;
                                continue;
                            }
                        }
                    } else { // 'assocTypeId' NOT NULL
                        if (commandEqualsInsertCommand && iAssoc.getAssocType() == null) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "no value for field 'assocTypeId' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'assocValue'
                    if (!"".equals(elementsOfLine[4])) {
                        try {
                            iAssoc.setAssocValue(Double.parseDouble(elementsOfLine[4]));
                        } catch (NumberFormatException nfe) {
                            if (commandEqualsInsertCommand) {
                                AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                        "value for field 'assocValue' is no valid 'Double'");
                                skippedCounter++;
                                continue;
                            }
                        }
                    } else { // 'assocValue' NOT NULL
                        if (commandEqualsInsertCommand && iAssoc.getAssocValue() == null) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "no value for field 'assocValue' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'itemToId'
                    if (!"".equals(elementsOfLine[5])) {
                        try {
                            iAssoc.getItemTo().setItem(Integer.parseInt(elementsOfLine[5]));
                        } catch (NumberFormatException nfe) {
                            if (commandEqualsInsertCommand) {
                                AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                        "value for field 'itemToId' is no valid 'Integer'");
                                skippedCounter++;
                                continue;
                            }
                        }
                    } else {
                        if (commandEqualsInsertCommand && iAssoc.getItemTo().getItem() == null) {
                            AutoImportUtils
                                    .logSkippedLine(logger, lineCounter, line, "no value for field 'itemToId' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'itemToTypeId'
                    if (!"".equals(elementsOfLine[6])) {
                        try {
                            iAssoc.getItemTo().setType(Integer.parseInt(elementsOfLine[6]));
                        } catch (NumberFormatException nfe) {
                            if (commandEqualsInsertCommand) {
                                AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                        "value for field 'itemToTypeId' is no valid 'Integer'");
                                skippedCounter++;
                                continue;
                            }
                        }
                    } else { // 'itemToTypeId' NOT NULL
                        if (commandEqualsInsertCommand && iAssoc.getItemTo().getType() == null) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "no value for field 'itemToTypeId' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'sourceTypeId'
                    if (!"".equals(elementsOfLine[7])) {
                        try {
                            iAssoc.setSourceType(Integer.parseInt(elementsOfLine[7]));
                        } catch (NumberFormatException nfe) {
                            if (commandEqualsInsertCommand) {
                                AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                        "value for field 'sourceTypeId' is no valid 'Integer'");
                                skippedCounter++;
                                continue;
                            }
                        }
                    } else { // 'sourceTypeId' NOT NULL
                        if (commandEqualsInsertCommand && iAssoc.getSourceType() == null) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "no value for field 'sourceTypeId' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'sourceInfo'
                    if (!"".equals(elementsOfLine[8])) {
                        iAssoc.setSourceInfo(elementsOfLine[8]);
                    }

                    // parse 'viewTypeId'
                    if (!"".equals(elementsOfLine[9])) {
                        try {
                            iAssoc.setViewType(Integer.parseInt(elementsOfLine[9]));
                        } catch (NumberFormatException nfe) {
                            if (commandEqualsInsertCommand) {
                                AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                        "value for field 'viewTypeId' is no valid 'Integer'");
                                skippedCounter++;
                                continue;
                            }
                        }
                    } else { // 'viewTypeId' NOT NULL
                        if (commandEqualsInsertCommand && iAssoc.getViewType() == null) {
                            AutoImportUtils.logSkippedLine(logger, lineCounter, line,
                                    "no value for field 'viewTypeId' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    // parse 'active'
                    if (!"".equals(elementsOfLine[10])) {
                        iAssoc.setActive(Boolean.parseBoolean(elementsOfLine[10]));
                    } else { // 'active' NOT NULL
                        if (commandEqualsInsertCommand && iAssoc.isActive() == null) {
                            AutoImportUtils
                                    .logSkippedLine(logger, lineCounter, line, "no value for field 'active' is set");
                            skippedCounter++;
                            continue;
                        }
                    }

                    try {
                        if (AutoImportUtils.COMMAND_INSERT.equals(command)) {
                            int savedThisIteration = 0;
                            if (importOverwriteDuplicates) {
                                savedThisIteration = insertOrUpdateItemAssoc(iAssoc);
                            } else {
                                savedThisIteration = insertItemAssoc(iAssoc);
                            }
                            currentSavedCounter += savedThisIteration;
                            savedCounter += savedThisIteration;
                            int currentSavedMultiplier = currentSavedCounter / reportBlockSize;
                            if (currentSavedMultiplier > 0) {
                                if (logger.isInfoEnabled()) {
                                    logger.info("number of saved 'itemAssoc' entries: " +
                                            (currentSavedMultiplier * reportBlockSize));
                                }
                                currentSavedCounter %= reportBlockSize;
                            }
                        } else { // COMMAND_REMOVE
                            int removedThisIteration = itemAssocDAO.removeItemAssocsQBE(iAssoc);
                            currentRemovedCounter += removedThisIteration;
                            removedCounter += removedThisIteration;
                            int currentRemovedMultiplier = currentRemovedCounter / reportBlockSize;
                            if (currentRemovedMultiplier > 0) {
                                if (logger.isInfoEnabled()) {
                                    logger.info("number of removed 'itemAssoc' entries: " +
                                            (currentRemovedMultiplier * reportBlockSize));
                                }
                                currentRemovedCounter %= reportBlockSize;
                            }
                        }
                    } catch (Exception e) {
                        errorCounter++;
                        logger.error("error occured during insertItemAssoc() '" + iAssoc + "'", e);
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
        if (AutoImportUtils.COMMAND_INSERT.equals(command)) {
            if (logger.isInfoEnabled()) {
                if (currentSavedCounter > 0) {
                    logger.info("number of saved 'itemAssoc' entries: " + currentSavedCounter);
                }
            }
        } else {
            if (logger.isInfoEnabled()) {
                if (currentRemovedCounter > 0) {
                    logger.info("number of removed 'itemAssoc' entries: " + currentRemovedCounter);
                }
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("==================== finished importing from file '" + fileName + "' ====================");
            logger.info("total number of saved 'itemAssoc' entries: " + savedCounter);
            logger.info("total number of removed 'itemAssoc' entries: " + removedCounter);
            logger.info("total number of skipped 'itemAssoc' entries: " + skippedCounter);
            logger.info("total number of errors occured while import: " + errorCounter);
            logger.info("used defaults: '" + defaults + "'");
            logger.info("time taken: " + (System.currentTimeMillis() - start) + " ms");
            logger.info("========================================================================");
        }
    }

    public Iterator<ItemAssocVO<Integer,Integer>> getItemAssocIterator(
            int bulkSize) {
        return itemAssocDAO.getItemAssocIterator(bulkSize);
    }

    public ItemAssocVO<Integer,Integer> loadItemAssoc(Integer itemAssocId) {
        return itemAssocDAO.loadItemAssocByPrimaryKey(itemAssocId);
    }

    public int removeAllItemAssocs() {
        return itemAssocDAO.removeAllItemAssocs();
    }

    public int removeAllItemAssocsFromSource(Integer sourceTypeId) {
        return itemAssocDAO.removeItemAssocsQBE(
                new ItemAssocVO<Integer,Integer>(null, null, null, null, null,
                        sourceTypeId, null, null, null));
    }

    public int removeAllItemAssocsFromSource(Integer sourceTypeId, String sourceInfo) {
        return itemAssocDAO.removeItemAssocsQBE(
                new ItemAssocVO<Integer,Integer>(null, null, null, null, null,
                        sourceTypeId, sourceInfo, null, null));
    }

    /**
     * Removes an ItemAssoc-Entry with the given ID from the DB.
     *
     * @param itemAssocId Integer
     */
    public int removeItemAssoc(Integer itemAssocId) {
        // validate input parameters
        if (itemAssocId == null) {
            throw new IllegalArgumentException("missing 'itemAssocId'");
        }

        return itemAssocDAO.removeItemAssocsQBE(
                new ItemAssocVO<Integer,Integer>(itemAssocId, (Integer) null, null,
                        null, null, null, null, null, null, null));
    }

    /**
     * This is a QBE Implementation for removing ItemAssocs-Entries. Removes several ItemAssoc-Entries that match the given
     * Example. Attributes that are left out (set to NULL) will act like a wildcard. Meaning if you for example just
     * pass a sourceInfo="16" and an ItemToType="track", all itemassociations pointing to tracks with sourceInfo="16" will
     * be removed from the DB.
     */
    public int removeItemAssocQBE(ItemAssocVO<Integer,Integer> itemAssoc) {
        return itemAssocDAO.removeItemAssocsQBE(itemAssoc);
    }

    public int insertItemAssoc(ItemAssocVO<Integer,Integer> itemAssoc) {
        // validate input parameters
        if (itemAssoc == null) {
            throw new IllegalArgumentException("missing 'itemAssoc'");
        }

        int rowsAffected = 0;
        if (itemAssoc.getId() == null) {
            rowsAffected = itemAssocDAO.insertItemAssoc(itemAssoc);
        } else {
            rowsAffected = itemAssocDAO.updateItemAssocUsingPrimaryKey(itemAssoc);
        }

        return rowsAffected;
    }

    /**
     * This Method inserts or updates a given ItemAssoc. An Update should occur,
     * only if the assocValue has changed.
     *
     * @param itemAssoc ItemAssocVO
     */
    public int insertOrUpdateItemAssoc(ItemAssocVO<Integer,Integer> itemAssoc) {
        // validate input parameters
        if (itemAssoc == null) {
            throw new IllegalArgumentException("missing 'itemAssoc'");
        }

        int rowsAffected = 0;
        if (itemAssoc.getId() == null) {
            // check if itemAssoc already exists
            ItemAssocVO<Integer,Integer> itemAssocResult = itemAssocDAO
                    .loadItemAssocByUniqueKey(itemAssoc);
            if (itemAssocResult == null) {
                // insert a new itemAssoc entry
                rowsAffected = itemAssocDAO.insertItemAssoc(itemAssoc);
            } else {
                // update existing itemAssoc entry (without knowing the id),
                // only if value, viewType or active attribute has changed
                if (itemAssoc.getAssocValue() != null &&
                        !itemAssoc.getAssocValue().equals(itemAssocResult.getAssocValue()) ||
                        itemAssoc.getViewType() != null &&
                                !itemAssoc.getViewType().equals(itemAssocResult.getViewType()) ||
                        itemAssoc.isActive() != null && !itemAssoc.isActive().equals(itemAssocResult.isActive())) {
                    rowsAffected = itemAssocDAO.updateItemAssocUsingUniqueKey(itemAssoc);
                    // set id since update doesn't set it and we already got it from load
                    itemAssoc.setId(itemAssocResult.getId());
                }
            }
        } else {
            // update existing itemAssoc entry (using the id)
            rowsAffected = itemAssocDAO.updateItemAssocUsingPrimaryKey(itemAssoc);
        }
        return rowsAffected;
    }

    public int insertOrUpdateItemAssocs(
            final List<ItemAssocVO<Integer,Integer>> itemAssocs) {
        return itemAssocDAO.insertOrUpdateItemAssocs(itemAssocs);
    }

    public int removeAllItemAssocsFromTenant(Integer tenantId) {
        return itemAssocDAO.removeItemAssocsQBE(
                new ItemAssocVO<Integer,Integer>(null, tenantId, null, null, null,
                        null, null, null, null, null));
    }

    public boolean isActiveItemAssoc(Integer itemAssocId) {
        return itemAssocDAO.loadItemAssocByPrimaryKey(itemAssocId).isActive();
    }

    public int activateItemAssoc(Integer itemAssocId) {
        if (!isActiveItemAssoc(itemAssocId)) {
            ItemAssocVO<Integer,Integer> loadedItemAssoc = itemAssocDAO
                    .loadItemAssocByPrimaryKey(itemAssocId);
            loadedItemAssoc.setActive(true);
            return itemAssocDAO.updateItemAssocUsingPrimaryKey(loadedItemAssoc);
        } else {
            return 0;
        }
    }

    public int deactivateItemAssoc(Integer itemAssocId) {
        if (isActiveItemAssoc(itemAssocId)) {
            ItemAssocVO<Integer,Integer> loadedItemAssoc = itemAssocDAO
                    .loadItemAssocByPrimaryKey(itemAssocId);
            loadedItemAssoc.setActive(false);
            return itemAssocDAO.updateItemAssocUsingPrimaryKey(loadedItemAssoc);
        } else {
            return 0;
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // private methods
    private ItemAssocVO<Integer,Integer> generateDefaultsFromHeader(String header,
                                                                                                         ItemAssocVO<Integer,Integer> defaults)
            throws IllegalArgumentException {
        String[] elementsOfHeader = header.split(AutoImportUtils.CSV_SEPARATOR, ItemAssocVO.CSV_NUMBER_OF_COLUMNS);

        if (elementsOfHeader.length != ItemAssocVO.CSV_NUMBER_OF_COLUMNS) {
            throw new IllegalArgumentException(
                    "the number of columns in the header of an 'itemassoc' .CSV file must be '" +
                            ItemAssocVO.CSV_NUMBER_OF_COLUMNS + "', but was '" + elementsOfHeader.length + "'");
        }

        ItemAssocVO<Integer,Integer> iAssoc;
        if (defaults != null) {
            try {
                iAssoc = (ItemAssocVO<Integer,Integer>) defaults.clone();
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(
                        "value object 'ItemAssocVO' does not support .clone() anymore, check that!!");
            }
        } else {
            iAssoc = new ItemAssocVO<Integer,Integer>(null, null,
                    new ItemVO<Integer, Integer>(null, null, null), null, null,
                    new ItemVO<Integer, Integer>(null, null, null), null, null, null, null);
        }

        // parse 'tenantId'
        String defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[0]);
        if (defaultValue != null) {
            try {
                iAssoc.setTenant(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn("the default value for 'tenantId' in the CSV header is no valid 'Integer', passed type='" +
                        defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'itemFromTypeId'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[2]);
        if (defaultValue != null) {
            try {
                iAssoc.getItemFrom().setType(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn(
                        "the default value for 'itemFromTypeId' in the CSV header is no valid 'Integer', passed type='" +
                                defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'assocTypeId'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[3]);
        if (defaultValue != null) {
            try {
                iAssoc.setAssocType(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn(
                        "the default value for 'assocTypeId' in the CSV header is no valid 'Integer', passed type='" +
                                defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'assocValue'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[4]);
        if (defaultValue != null) {
            try {
                iAssoc.setAssocValue(Double.parseDouble(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn("the default value for 'assocValue' in the CSV header is no valid 'Double', passed type='" +
                        defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'itemToTypeId'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[6]);
        if (defaultValue != null) {
            try {
                iAssoc.getItemTo().setType(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn(
                        "the default value for 'itemToTypeId' in the CSV header is no valid 'Integer', passed type='" +
                                defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'sourceTypeId'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[7]);
        if (defaultValue != null) {
            try {
                iAssoc.setSourceType(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn(
                        "the default value for 'sourceTypeId' in the CSV header is no valid 'Integer', passed type='" +
                                defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'sourceInfo'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[8]);
        if (defaultValue != null) {
            iAssoc.setSourceInfo(defaultValue);
        }

        // parse 'viewTypeId'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[9]);
        if (defaultValue != null) {
            try {
                iAssoc.setViewType(Integer.parseInt(defaultValue));
            } catch (NumberFormatException nfe) {
                logger.warn(
                        "the default value for 'viewTypeId' in the CSV header is no valid 'Integer', passed type='" +
                                defaultValue + "'; the passed type will be ignored!");
            }
        }

        // parse 'active'
        defaultValue = AutoImportUtils.getDefaultFromHeaderPart(elementsOfHeader[10]);
        if (defaultValue != null) {
            iAssoc.setActive(Boolean.parseBoolean(defaultValue));
        }

        return iAssoc;
    }
}
