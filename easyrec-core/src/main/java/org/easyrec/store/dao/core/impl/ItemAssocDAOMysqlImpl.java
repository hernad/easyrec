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
package org.easyrec.store.dao.core.impl;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.AssociatedItemVO;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.transfer.IAConstraintVO;
import org.easyrec.store.dao.core.ItemAssocDAO;
import org.easyrec.store.dao.impl.AbstractBaseItemAssocDAOMysqlImpl;
import org.easyrec.utils.spring.store.ResultSetIteratorMysql;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides a Mysql implementation of the {@link org.easyrec.store.dao.core.ItemAssocDAO} interface.
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
@DAO
public class ItemAssocDAOMysqlImpl extends
        AbstractBaseItemAssocDAOMysqlImpl<ItemAssocVO<Integer,Integer>, AssociatedItemVO<Integer, Integer>, Integer, Integer, ItemVO<Integer, Integer>, IAConstraintVO<Integer, Integer>>
        implements ItemAssocDAO {
    private ItemAssocVORowMapper itemAssocVORowMapper = new ItemAssocVORowMapper();
    private AssociatedItemFromVORowMapper associatedItemFromVORowMapper = new AssociatedItemFromVORowMapper();
    private AssociatedItemToVORowMapper associatedItemToVORowMapper = new AssociatedItemToVORowMapper();

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    // constructor
    public ItemAssocDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);

        // output connection information
        if (logger.isInfoEnabled()) {
            try {
                logger.info(DaoUtils.getDatabaseURLAndUserName(dataSource));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    // abstract (generic) method implementation of 'AbstractBaseItemAssocDAOMysqlImpl<ItemAssocVO, AssociatedItemVO, Integer, Integer, ItemVO, IAConstraintVO>'
    @Override
    public int insertItemAssoc(ItemAssocVO<Integer,Integer> itemAssoc) {
        // validate input parameters
        if (itemAssoc == null) {
            throw new IllegalArgumentException("missing 'itemAssoc'");
        }

        // validate unique key
        validateUniqueKey(itemAssoc);
        validateAssocValue(itemAssoc);
        validateViewType(itemAssoc);

        if (logger.isDebugEnabled()) {
            logger.debug("inserting 'itemAssoc': " + itemAssoc);
        }

        // @HINT: maybe use UniqueIdService later (instead of auto_imcrement)
        StringBuilder sqlString = new StringBuilder("INSERT INTO ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" (");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_FROM_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ASSOC_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ASSOC_VALUE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_TO_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_SOURCE_INFO_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_VIEW_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        if (itemAssoc.isActive() != null) {
            sqlString.append(DEFAULT_ACTIVE_COLUMN_NAME);
            sqlString.append(", ");
        }
        sqlString.append(DEFAULT_CHANGE_DATE_COLUMN_NAME);
        if (itemAssoc.getChangeDate() == null) {
            itemAssoc.setChangeDate(new Date(System.currentTimeMillis()));
        }
        if (itemAssoc.isActive() != null) {
            sqlString.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        } else {
            sqlString.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }

        Object[] args;
        int[] argTypes;
        if (itemAssoc.isActive() != null) {
            args = new Object[]{itemAssoc.getTenant(), itemAssoc.getItemFrom().getItem(),
                    itemAssoc.getItemFrom().getType(), itemAssoc.getAssocType(), itemAssoc.getAssocValue(),
                    itemAssoc.getItemTo().getItem(), itemAssoc.getItemTo().getType(), itemAssoc.getSourceType(),
                    itemAssoc.getSourceInfo(), itemAssoc.getViewType(), itemAssoc.isActive(),
                    itemAssoc.getChangeDate()};
            argTypes = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.DOUBLE,
                    Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.BOOLEAN,
                    Types.TIMESTAMP};
        } else {
            args = new Object[]{itemAssoc.getTenant(), itemAssoc.getItemFrom().getItem(),
                    itemAssoc.getItemFrom().getType(), itemAssoc.getAssocType(), itemAssoc.getAssocValue(),
                    itemAssoc.getItemTo().getItem(), itemAssoc.getItemTo().getType(), itemAssoc.getSourceType(),
                    itemAssoc.getSourceInfo(), itemAssoc.getViewType(), itemAssoc.getChangeDate()};
            argTypes = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.DOUBLE,
                    Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.TIMESTAMP};
        }
        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(sqlString.toString(), argTypes);
        factory.setReturnGeneratedKeys(true);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = getJdbcTemplate().update(factory.newPreparedStatementCreator(args), keyHolder);

        // retrieve auto increment id, and set to VO
        itemAssoc.setId(keyHolder.getKey().intValue());

        return rowsAffected;
    }

    public int insertOrUpdateItemAssocs(
            List<ItemAssocVO<Integer,Integer>> itemAssocs) {
        if (itemAssocs == null) throw new IllegalArgumentException("Missing 'itemAssocs'");

        StringBuilder sqlString = new StringBuilder("LOAD DATA LOCAL INFILE ? REPLACE INTO TABLE ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" (");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_FROM_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ASSOC_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ASSOC_VALUE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_TO_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_SOURCE_INFO_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_VIEW_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ACTIVE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_CHANGE_DATE_COLUMN_NAME);
        sqlString.append(")");

        File tempFile = null;
        BufferedWriter writer;

        try {
            tempFile = File.createTempFile("insert_item_assocs", ".tmp");
            writer = new BufferedWriter(new FileWriter(tempFile));

            for (ItemAssocVO<Integer,Integer> itemAssoc : itemAssocs) {
                validateUniqueKey(itemAssoc);

                writeItemAssoc(writer, itemAssoc);
            }

            writer.close();

            getJdbcTemplate().update(sqlString.toString(), new Object[]{tempFile.getAbsolutePath()}, new int[]{Types.VARCHAR});

            return itemAssocs.size();
        } catch (IOException ex) {
            if (logger.isWarnEnabled()) logger.warn("Exception when writing item assocaitions to file.", ex);

            throw new RuntimeException(ex);
        } finally {
            if (tempFile != null) tempFile.delete();

            if (logger.isDebugEnabled()) logger.debug("closed file");
        }
    }

    private static final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private void writeItemAssoc(Writer writer,
                                ItemAssocVO<Integer,Integer> itemAssoc)
            throws IOException {
        writer.append(itemAssoc.getTenant().toString());
        writer.append('\t');

        writer.append(itemAssoc.getItemFrom().getItem().toString());
        writer.append('\t');

        writer.append(itemAssoc.getItemFrom().getType().toString());
        writer.append('\t');

        writer.append(itemAssoc.getAssocType().toString());
        writer.append('\t');

        writer.append(itemAssoc.getAssocValue().toString());
        writer.append('\t');

        writer.append(itemAssoc.getItemTo().getItem().toString());
        writer.append('\t');

        writer.append(itemAssoc.getItemTo().getType().toString());
        writer.append('\t');

        writer.append(itemAssoc.getSourceType().toString());
        writer.append('\t');

        String sourceInfo = itemAssoc.getSourceInfo();
        sourceInfo = sourceInfo.replace("\\", "\\\\");
        sourceInfo = sourceInfo.replace("\0", "\\0");
        sourceInfo = sourceInfo.replace("\b", "\\b");
        sourceInfo = sourceInfo.replace("\n", "\\n");
        sourceInfo = sourceInfo.replace("\r", "\\r");
        sourceInfo = sourceInfo.replace("\t", "\\t");
        writer.append(sourceInfo);
        writer.append('\t');

        writer.append(itemAssoc.getViewType().toString());
        writer.append('\t');

        String isActive = "1";
        if (itemAssoc.isActive() != null) isActive = itemAssoc.isActive() ? "1" : "0";
        writer.append(isActive);
        writer.append('\t');

        String changeDate = sqlDateFormat.format(itemAssoc.getChangeDate());
        writer.append(changeDate);
        writer.append('\t');

        writer.append('\n');
    }

    /**
     * updates an item association entry in the database, uses the primary key to retrieve the entry
     * only changes columns:<br/>
     * - assocValue<br/>
     * - viewType<br/>
     * - changeDate<br/>
     */
    @Override
    public int updateItemAssocUsingPrimaryKey(
            ItemAssocVO<Integer,Integer> itemAssoc) {
        // validate input parameters
        if (itemAssoc == null) {
            throw new IllegalArgumentException("missing 'itemAssoc'");
        }

        validatePrimaryKey(itemAssoc);
        validateAssocValue(itemAssoc);
        validateViewType(itemAssoc);

        if (logger.isDebugEnabled()) {
            logger.debug("updating 'itemAssoc' by primary key: " + itemAssoc);
        }

        StringBuilder sqlString;

        sqlString = new StringBuilder("UPDATE ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" SET ");
        sqlString.append(DEFAULT_ASSOC_VALUE_COLUMN_NAME);
        sqlString.append("=?, ");
        sqlString.append(DEFAULT_VIEW_TYPE_COLUMN_NAME);
        sqlString.append("=?, ");
        if (itemAssoc.isActive() != null) {
            sqlString.append(DEFAULT_ACTIVE_COLUMN_NAME);
            sqlString.append("=?, ");
        }
        sqlString.append(DEFAULT_CHANGE_DATE_COLUMN_NAME);
        if (itemAssoc.getChangeDate() == null) {
            itemAssoc.setChangeDate(new Date(System.currentTimeMillis()));
        }
        sqlString.append("=? WHERE ");
        sqlString.append(DEFAULT_ID_COLUMN_NAME);
        sqlString.append("=?");

        Object[] args;
        int[] argTypes;
        if (itemAssoc.isActive() != null) {
            args = new Object[]{itemAssoc.getAssocValue(), itemAssoc.getViewType(), itemAssoc.isActive(),
                    itemAssoc.getChangeDate(), itemAssoc.getId()};
            argTypes = new int[]{Types.DOUBLE, Types.INTEGER, Types.BOOLEAN, Types.TIMESTAMP, Types.INTEGER};
        } else {
            args = new Object[]{itemAssoc.getAssocValue(), itemAssoc.getViewType(), itemAssoc.getChangeDate(),
                    itemAssoc.getId()};
            argTypes = new int[]{Types.DOUBLE, Types.INTEGER, Types.TIMESTAMP, Types.INTEGER};
        }

        return getJdbcTemplate().update(sqlString.toString(), args, argTypes);
    }

    /**
     * updates an item association entry in the database, uses the unique key to retrieve the entry
     * only changes columns:<br/>
     * - assocValue<br/>
     * - viewType<br/>
     * - changeDate<br/>
     * <br/>
     * unique key: (tenantId, itemFromId, itemToId, itemFromTypeId, itemToTypeId, assocTypeId, sourceTypeId, sourceInfo)
     */
    @Override
    public int updateItemAssocUsingUniqueKey(
            ItemAssocVO<Integer,Integer> itemAssoc) {
        // validate input parameters
        if (itemAssoc == null) {
            throw new IllegalArgumentException("missing 'itemAssoc'");
        }

        validateUniqueKey(itemAssoc);
        validateAssocValue(itemAssoc);
        validateViewType(itemAssoc);

        if (logger.isDebugEnabled()) {
            logger.debug("updating 'itemAssoc' by unique key: " + itemAssoc);
        }

        StringBuilder sqlString;

        sqlString = new StringBuilder("UPDATE ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" SET ");
        sqlString.append(DEFAULT_ASSOC_VALUE_COLUMN_NAME);
        sqlString.append("=?, ");
        sqlString.append(DEFAULT_VIEW_TYPE_COLUMN_NAME);
        sqlString.append("=?, ");
        if (itemAssoc.isActive() != null) {
            sqlString.append(DEFAULT_ACTIVE_COLUMN_NAME);
            sqlString.append("=?, ");
        }
        sqlString.append(DEFAULT_CHANGE_DATE_COLUMN_NAME);
        if (itemAssoc.getChangeDate() == null) {
            itemAssoc.setChangeDate(new Date(System.currentTimeMillis()));
        }
        sqlString.append("=? WHERE ");
        sqlString.append(DEFAULT_ITEM_FROM_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_ITEM_TO_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_ASSOC_TYPE_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append("=?");

        Object[] args;
        int[] argTypes;
        if (itemAssoc.isActive() != null) {
            args = new Object[]{itemAssoc.getAssocValue(), itemAssoc.getViewType(), itemAssoc.isActive(),
                    itemAssoc.getChangeDate(), itemAssoc.getItemFrom().getItem(), itemAssoc.getItemTo().getItem(),
                    itemAssoc.getItemFrom().getType(), itemAssoc.getItemTo().getType(), itemAssoc.getAssocType(),
                    itemAssoc.getSourceType(), itemAssoc.getTenant()};
            argTypes = new int[]{Types.DOUBLE, Types.INTEGER, Types.BOOLEAN, Types.TIMESTAMP, Types.INTEGER,
                    Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};
        } else {
            args = new Object[]{itemAssoc.getAssocValue(), itemAssoc.getViewType(), itemAssoc.getChangeDate(),
                    itemAssoc.getItemFrom().getItem(), itemAssoc.getItemTo().getItem(),
                    itemAssoc.getItemFrom().getType(), itemAssoc.getItemTo().getType(), itemAssoc.getAssocType(),
                    itemAssoc.getSourceType(), itemAssoc.getTenant()};
            argTypes = new int[]{Types.DOUBLE, Types.INTEGER, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER,
                    Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};
        }
        return getJdbcTemplate().update(sqlString.toString(), args, argTypes);
    }

    @Override
    public List<ItemAssocVO<Integer,Integer>> getItemAssocsQBE(
            ItemVO<Integer, Integer> itemFrom, Integer assocTypeId, ItemVO<Integer, Integer> itemTo,
            IAConstraintVO<Integer, Integer> constraints) {
        // validate input parameters
        if (itemFrom != null) validateItemFrom(itemFrom);
        if (itemTo != null) validateItemTo(itemTo);
        if ((itemFrom == null) && (itemTo == null) && (constraints == null)) {
            throw new IllegalArgumentException(
                    "No example criteria set for QBE query! Use 'getItemAssocIterator()' instead! ");
        }

        if (logger.isDebugEnabled()) {
            logger.debug(
                    "fetching 'itemAssocs' with itemFrom=" + itemFrom + ", assocTypeId='" + assocTypeId + "', itemTo=" +
                            itemTo + " and constraints=" + constraints);
        }

        List<Object> args = Lists.newArrayList();
        List<Integer> argt = Lists.newArrayList();

        // generate sql string as well as parameter and type arrays
        StringBuilder sqlString = new StringBuilder("SELECT * FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");

        if (itemFrom != null) {
            sqlString.append(DEFAULT_ITEM_FROM_COLUMN_NAME);
            sqlString.append("=? AND ");

            args.add(itemFrom.getItem());
            argt.add(Types.INTEGER);

            sqlString.append(DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME);
            sqlString.append("=? ");

            args.add(itemFrom.getType());
            argt.add(Types.INTEGER);
        }

        if (assocTypeId != null) {
            if (args.size() > 0)
                sqlString.append(" AND ");

            sqlString.append(DEFAULT_ASSOC_TYPE_COLUMN_NAME);
            sqlString.append("=? ");

            args.add(assocTypeId);
            argt.add(Types.INTEGER);
        }

        if (itemTo != null) {
            if (args.size() > 0)
                sqlString.append(" AND ");

            sqlString.append(DEFAULT_ITEM_TO_COLUMN_NAME);
            sqlString.append("=? AND ");

            args.add(itemTo.getItem());
            argt.add(Types.INTEGER);

            sqlString.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(itemTo.getType());
            argt.add(Types.INTEGER);
        }


        if (constraints.getSourceType() != null) {
            if (args.size() > 0)
                sqlString.append(" AND ");

            sqlString.append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(constraints.getSourceType());
            argt.add(Types.INTEGER);

            if (constraints.getSourceInfo() != null) {
                sqlString.append(" AND ");
                sqlString.append(DEFAULT_SOURCE_INFO_COLUMN_NAME);
                sqlString.append(" LIKE ?");

                args.add(constraints.getSourceInfo());
                argt.add(Types.VARCHAR);
            }
        }

        if (constraints.getViewType() != null) {
            if (args.size() > 0)
                sqlString.append(" AND ");

            sqlString.append(DEFAULT_VIEW_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(constraints.getViewType());
            argt.add(Types.INTEGER);
        }

        if (constraints.getTenant() != null) {
            if (args.size() > 0)
                sqlString.append(" AND ");

            sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
            sqlString.append("=?");

            args.add(constraints.getTenant());
            argt.add(Types.INTEGER);
        }

        if (constraints.isActive() != null) {
            if (args.size() > 0)
                sqlString.append(" AND ");

            sqlString.append(DEFAULT_ACTIVE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(constraints.isActive());
            argt.add(Types.BOOLEAN);
        }

        if (constraints.getSortAsc() != null) {
            sqlString.append(" ORDER BY ");
            sqlString.append(constraints.getSortField());
            sqlString.append(" ");

            if (constraints.getSortAsc())
                sqlString.append(DaoUtils.ORDER_ASC);
            else
                sqlString.append(DaoUtils.ORDER_DESC);
        }

        // Note: for a non-mysql implementation this needs to be changed
        if (constraints.getNumberOfResults() != null && constraints.getNumberOfResults() > 0) {
            sqlString.append(" LIMIT ?");

            args.add(constraints.getNumberOfResults());
            argt.add(Types.INTEGER);
        }

        return getJdbcTemplate().query(sqlString.toString(), args.toArray(), Ints.toArray(argt), itemAssocVORowMapper);
    }

    @Override
    public List<ItemAssocVO<Integer,Integer>> getItemAssocs(
            ItemVO<Integer, Integer> itemFrom, Integer assocTypeId, ItemVO<Integer, Integer> itemTo,
            IAConstraintVO<Integer, Integer> constraints) {
        // validate input parameters
        if(itemFrom != null)
            validateItemFrom(itemFrom);
        if(itemTo != null)
            validateItemTo(itemTo);
        validateConstraints(constraints);

        return getItemAssocsQBE(itemFrom, assocTypeId, itemTo, constraints);
    }

    @Override
    public List<AssociatedItemVO<Integer, Integer>> getItemsFrom(Integer itemFromTypeId,
                                                                                   Integer assocTypeId,
                                                                                   ItemVO<Integer, Integer> itemTo,
                                                                                   IAConstraintVO<Integer, Integer> constraints) {
        // validate input parameters
        validateItemTo(itemTo);
        validateConstraints(constraints);

        if (logger.isDebugEnabled()) {
            logger.debug(
                    "fetching 'itemsFrom' with itemFromTypeId='" + itemFromTypeId + "', assocTypeId='" + assocTypeId +
                            "', itemTo=" + itemTo + " and constraints=" + constraints);
        }

        // generate sql string
        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_ITEM_FROM_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ASSOC_VALUE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ID_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ASSOC_TYPE_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");

        sqlString.append(DEFAULT_ITEM_TO_COLUMN_NAME);
        sqlString.append("=? AND ");

        sqlString.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
        sqlString.append("=?");

        List<Object> args = Lists.newArrayList((Object)itemTo.getItem(), itemTo.getType());
        List<Integer> argt= Lists.newArrayList(Types.INTEGER, Types.INTEGER);

        if (itemFromTypeId != null) {
            sqlString.append(" AND ");
            sqlString.append(DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(itemFromTypeId);
            argt.add(Types.INTEGER);
        }

        if (assocTypeId != null) {
            sqlString.append(" AND ");
            sqlString.append(DEFAULT_ASSOC_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(assocTypeId);
            argt.add(Types.INTEGER);
        }

        if (constraints.getSourceType() != null) {
            sqlString.append(" AND ");
            sqlString.append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(constraints.getSourceType());
            argt.add(Types.INTEGER);

            if (constraints.getSourceInfo() != null) {
                sqlString.append(" AND ");
                sqlString.append(DEFAULT_SOURCE_INFO_COLUMN_NAME);
                sqlString.append(" LIKE ?");

                args.add(constraints.getSourceInfo());
                argt.add(Types.VARCHAR);
            }
        }

        if (constraints.getViewType() != null) {
            sqlString.append(" AND ");
            sqlString.append(DEFAULT_VIEW_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(constraints.getViewType());
            argt.add(Types.INTEGER);
        }


        if (constraints.isActive() != null) {
            sqlString.append(" AND ");
            sqlString.append(DEFAULT_ACTIVE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(constraints.isActive());
            argt.add(Types.BOOLEAN);
        }

        if (constraints.getTenant() != null) {
            sqlString.append(" AND ");
            sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
            sqlString.append("=?");

            args.add(constraints.getTenant());
            argt.add(Types.INTEGER);
        }

        if (constraints.getSortAsc() != null) {
            sqlString.append(" ORDER BY ");
            sqlString.append(constraints.getSortField());
            sqlString.append(" ");

            if (constraints.getSortAsc())
                sqlString.append(DaoUtils.ORDER_ASC);
            else
                sqlString.append(DaoUtils.ORDER_DESC);
        }

        // Note: for a non-mysql implementation this is need to be changed
        if (constraints.getNumberOfResults() != null && constraints.getNumberOfResults() > 0) {
            sqlString.append(" LIMIT ?");

            args.add(constraints.getNumberOfResults());
            argt.add(Types.INTEGER);
        }

        return getJdbcTemplate().query(sqlString.toString(), args.toArray(), Ints.toArray(argt),
                associatedItemFromVORowMapper);
    }

    @Override
    public List<AssociatedItemVO<Integer, Integer>> getItemsTo(
            ItemVO<Integer, Integer> itemFrom, Integer assocTypeId, Integer itemToTypeId,
            IAConstraintVO<Integer, Integer> constraints) {
        // validate input parameters
        validateItemFrom(itemFrom);
        validateConstraints(constraints);

        if (logger.isDebugEnabled()) {
            logger.debug("fetching 'itemsTo' with itemToTypeId='" + itemToTypeId + "', assocTypeId='" + assocTypeId +
                    "', itemFrom=" + itemFrom + " and constraints=" + constraints);
        }

        // generate sql string
        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(DEFAULT_ITEM_TO_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ASSOC_VALUE_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ID_COLUMN_NAME);
        sqlString.append(", ");
        sqlString.append(DEFAULT_ASSOC_TYPE_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");

        sqlString.append(DEFAULT_ITEM_FROM_COLUMN_NAME);
        sqlString.append("=? AND ");

        sqlString.append(DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME);
        sqlString.append("=?");

        List<Object> args = Lists.newArrayList((Object)itemFrom.getItem(), itemFrom.getType());
        List<Integer> argt = Lists.newArrayList(Types.INTEGER, Types.INTEGER);

        if (itemToTypeId != null) {
            sqlString.append(" AND ");
            sqlString.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(itemToTypeId);
            argt.add(Types.INTEGER);
        }

        if (assocTypeId != null) {
            sqlString.append(" AND ");
            sqlString.append(DEFAULT_ASSOC_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(assocTypeId);
            argt.add(Types.INTEGER);
        }

        if (constraints.getSourceType() != null) {
            sqlString.append(" AND ");
            sqlString.append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(constraints.getSourceType());
            argt.add(Types.INTEGER);

            if (constraints.getSourceInfo() != null) {
                sqlString.append(" AND ");
                sqlString.append(DEFAULT_SOURCE_INFO_COLUMN_NAME);
                sqlString.append(" LIKE ?");

                args.add(constraints.getSourceInfo());
                argt.add(Types.VARCHAR);
            }
        }

        if (constraints.getViewType() != null) {
            sqlString.append(" AND ");
            sqlString.append(DEFAULT_VIEW_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(constraints.getViewType());
            argt.add(Types.INTEGER);
        }

        if (constraints.getTenant() != null) {
            sqlString.append(" AND ");
            sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
            sqlString.append("=?");

            args.add(constraints.getTenant());
            argt.add(Types.INTEGER);
        }

        if (constraints.isActive() != null) {
            sqlString.append(" AND ");
            sqlString.append(DEFAULT_ACTIVE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(constraints.isActive());
            argt.add(Types.BOOLEAN);
        }

        if (constraints.getSortAsc() != null) {
            sqlString.append(" ORDER BY ");
            sqlString.append(constraints.getSortField());
            sqlString.append(" ");

            if (constraints.getSortAsc())
                sqlString.append(DaoUtils.ORDER_ASC);
            else
                sqlString.append(DaoUtils.ORDER_DESC);
        }

        // Note: for a non-mysql implementation this is need to be changed
        if (constraints.getNumberOfResults() != null && constraints.getNumberOfResults() > 0) {
            sqlString.append(" LIMIT ?");

            args.add(constraints.getNumberOfResults());
            argt.add(Types.INTEGER);
        }

        return getJdbcTemplate().query(sqlString.toString(), args.toArray(), Ints.toArray(argt),
                associatedItemToVORowMapper);
    }

    @Override
    public Iterator<ItemAssocVO<Integer,Integer>> getItemAssocIterator(
            int bulkSize) {
        return new ResultSetIteratorMysql<ItemAssocVO<Integer,Integer>>(
                getDataSource(), bulkSize, getItemAssocIteratorQueryString(), itemAssocVORowMapper);
    }

    /**
     * load an ItemAssocVO using the primary key (id)
     */
    @Override
    public ItemAssocVO<Integer,Integer> loadItemAssocByPrimaryKey(
            Integer itemAssocId) {
        // validate input parameters
        if (itemAssocId == null) {
            throw new IllegalArgumentException("missing 'itemAssocId'");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("loading 'itemAssoc' with primary key '" + itemAssocId + "'");
        }

        StringBuilder sqlString = new StringBuilder("SELECT * FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_ID_COLUMN_NAME);
        sqlString.append("=?");
        Object[] args = {itemAssocId};
        int[] argTypes = {Types.INTEGER};

        try {
            return getJdbcTemplate().queryForObject(sqlString.toString(), args, argTypes, itemAssocVORowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    /**
     * load an ItemAssocVO using the unique key
     * <p/>
     * unique key: (tenantId, itemFromId, itemToId, itemFromTypeId, itemToTypeId, assocTypeId, sourceTypeId, sourceInfo)
     */
    @Override
    public ItemAssocVO<Integer,Integer> loadItemAssocByUniqueKey(
            ItemAssocVO<Integer,Integer> itemAssoc) {
        // validate input parameters & unique key
        validateUniqueKey(itemAssoc);

        if (logger.isDebugEnabled()) {
            StringBuilder buff = new StringBuilder(
                    "loading 'itemAssoc' with unique key (itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType,sourceInfo,tenantId): (");
            buff.append(itemAssoc);
            buff.append(")");
            logger.debug(buff.toString());
        }

        StringBuilder sqlString = new StringBuilder("SELECT * FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_ITEM_FROM_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_ITEM_TO_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_ASSOC_TYPE_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_SOURCE_INFO_COLUMN_NAME);
        sqlString.append("=? AND ");
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append("=?");

        Object[] args = {itemAssoc.getItemFrom().getItem(), itemAssoc.getItemTo().getItem(),
                itemAssoc.getItemFrom().getType(), itemAssoc.getItemTo().getType(), itemAssoc.getAssocType(),
                itemAssoc.getSourceType(), itemAssoc.getSourceInfo(), itemAssoc.getTenant()};
        int[] argTypes = {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER,
                Types.VARCHAR, Types.INTEGER};
        try {
            return getJdbcTemplate().queryForObject(sqlString.toString(), args, argTypes, itemAssocVORowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public int removeItemAssocsQBE(ItemAssocVO<Integer,Integer> itemAssoc) {
        // validate input parameters
        validateOneAttributeSet(itemAssoc);

        if (logger.isDebugEnabled()) {
            logger.debug("removing 'itemAssoc' that match the following Example '" + itemAssoc + "'");
        }

        StringBuilder sqlString = new StringBuilder("DELETE FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");

        List<Object> args = Lists.newArrayList();
        List<Integer> argt = Lists.newArrayList();

        // add constraints to the query
        if (itemAssoc.getId() != null) {
            sqlString.append(DEFAULT_ID_COLUMN_NAME);
            sqlString.append("=? AND ");

            args.add(itemAssoc.getId());
            argt.add(Types.INTEGER);
        }
        if (itemAssoc.getTenant() != null) {
            sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
            sqlString.append("=? AND ");

            args.add(itemAssoc.getTenant());
            argt.add(Types.INTEGER);
        }
        if (itemAssoc.getItemFrom() != null) {
            ItemVO<Integer, Integer> itemFrom = itemAssoc.getItemFrom();
            if (itemFrom.getItem() != null) {
                sqlString.append(DEFAULT_ITEM_FROM_COLUMN_NAME);
                sqlString.append("=? AND ");

                args.add(itemFrom.getItem());
                argt.add(Types.INTEGER);
            }
            if (itemFrom.getType() != null) {
                sqlString.append(DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME);
                sqlString.append("=? AND ");

                args.add(itemFrom.getType());
                argt.add(Types.INTEGER);
            }
        }
        if (itemAssoc.getAssocType() != null) {
            sqlString.append(DEFAULT_ASSOC_TYPE_COLUMN_NAME);
            sqlString.append("=? AND ");

            args.add(itemAssoc.getAssocType());
            argt.add(Types.INTEGER);
        }
        if (itemAssoc.getAssocValue() != null) {
            sqlString.append(DEFAULT_ASSOC_VALUE_COLUMN_NAME);
            sqlString.append("=? AND ");

            args.add(itemAssoc.getAssocValue());
            argt.add(Types.DOUBLE);
        }
        if (itemAssoc.getItemTo() != null) {
            ItemVO<Integer, Integer> itemTo = itemAssoc.getItemTo();
            if (itemTo.getItem() != null) {
                sqlString.append(DEFAULT_ITEM_TO_COLUMN_NAME);
                sqlString.append("=? AND ");

                args.add(itemTo.getItem());
                argt.add(Types.INTEGER);
            }
            if (itemTo.getType() != null) {
                sqlString.append(DEFAULT_ITEM_TO_TYPE_COLUMN_NAME);
                sqlString.append("=? AND ");

                args.add(itemTo.getType());
                argt.add(Types.INTEGER);
            }
        }
        if (itemAssoc.getSourceType() != null) {
            sqlString.append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
            sqlString.append("=? AND ");

            args.add(itemAssoc.getSourceType());
            argt.add(Types.INTEGER);
        }
        if (itemAssoc.getSourceInfo() != null) {
            sqlString.append(DEFAULT_SOURCE_INFO_COLUMN_NAME);
            sqlString.append(" LIKE ? AND ");

            args.add(itemAssoc.getSourceInfo());
            argt.add(Types.VARCHAR);
        }
        if (itemAssoc.getViewType() != null) {
            sqlString.append(DEFAULT_VIEW_TYPE_COLUMN_NAME);
            sqlString.append("=? AND ");

            args.add(itemAssoc.getViewType());
            argt.add(Types.INTEGER);
        }
        if (itemAssoc.isActive() != null) {
            sqlString.append(DEFAULT_ACTIVE_COLUMN_NAME);
            sqlString.append("=? AND ");

            args.add(itemAssoc.isActive());
            argt.add(Types.BOOLEAN);
        }
        if (itemAssoc.getChangeDate() != null) {
            sqlString.append("changeDate");
            sqlString.append("=? AND ");

            args.add(itemAssoc.getChangeDate());
            argt.add(Types.TIMESTAMP);
        }

        // remove trailing " AND "
        sqlString.delete(sqlString.length() - 5, sqlString.length());

        return getJdbcTemplate().update(sqlString.toString(), args.toArray(), Ints.toArray(argt));
    }

    public int removeItemAssocByTenant(Integer tenantId, Integer assocType, Integer sourceType, Date changeDate) {

        if (tenantId == null) {
            throw new IllegalArgumentException("missing 'tenantId'");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("removing 'itemAssocs' for tenant " + tenantId);
        }

        StringBuilder sqlString = new StringBuilder("DELETE FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");

        List<Object> args = Lists.newArrayList();
        List<Integer> argt = Lists.newArrayList();

        // add constraints to the query
        sqlString.append(DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append("=?");

        args.add(tenantId);
        argt.add(Types.INTEGER);

        if (assocType != null) {
            sqlString.append(" AND ").append(DEFAULT_ASSOC_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(assocType);
            argt.add(Types.INTEGER);
        }

        if (sourceType != null) {
            sqlString.append(" AND ").append(DEFAULT_SOURCE_TYPE_COLUMN_NAME);
            sqlString.append("=?");

            args.add(sourceType);
            argt.add(Types.INTEGER);
        }

        if (changeDate != null) {
            sqlString.append(" AND ").append(DEFAULT_CHANGE_DATE_COLUMN_NAME);
            sqlString.append("<?");

            args.add(changeDate);
            argt.add(Types.TIMESTAMP);
        }

        // remove trailing " AND "
        sqlString.delete(sqlString.length(), sqlString.length());

        return getJdbcTemplate().update(sqlString.toString(), args.toArray(), Ints.toArray(argt));
    }

    ///////////////////////////////////////////////////////////////////////////
    // private methods
    private void validateUniqueKey(ItemAssocVO<Integer,Integer> itemAssoc) {
        if (itemAssoc.getItemFrom() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'itemFrom'");
        }
        if (itemAssoc.getItemTo() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'itemTo'");
        }
        if (itemAssoc.getItemFrom().getItem() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'itemFromId'");
        }
        if (itemAssoc.getItemFrom().getType() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'itemFromTypeId'");
        }
        if (itemAssoc.getItemTo().getItem() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'itemToId'");
        }
        if (itemAssoc.getItemTo().getType() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'itemToTypeId'");
        }
        if (itemAssoc.getAssocType() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'assocTypeId'");
        }
        if (itemAssoc.getSourceType() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'sourceTypeId'");
        }
        if (itemAssoc.getTenant() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId,itemFromId,itemToId,itemFromType,itemToType,assocType,sourceType) must be set, missing 'tenantId'");
        }
    }

    private void validatePrimaryKey(ItemAssocVO<Integer,Integer> itemAssoc) {
        if (itemAssoc.getId() == null) {
            throw new IllegalArgumentException("missing constraints, primary key (id) must be set");
        }
    }

    private void validateOneAttributeSet(ItemAssocVO<Integer,Integer> itemAssoc) {
        if (itemAssoc == null) {
            throw new IllegalArgumentException("missing 'itemAssoc'");
        }

        if ((itemAssoc.getId() == null) && (itemAssoc.getTenant() == null) && ((itemAssoc.getItemFrom() == null) ||
                                                                                       ((itemAssoc.getItemFrom()
                                                                                                 .getItem() == null) &&
                                                                                                (itemAssoc.getItemFrom()
                                                                                                         .getType() ==
                                                                                                         null))) &&
                (itemAssoc.getAssocType() == null) && (itemAssoc.getAssocValue() == null) &&
                ((itemAssoc.getItemTo() == null) ||
                         ((itemAssoc.getItemTo().getItem() == null) && (itemAssoc.getItemTo().getType() == null))) &&
                (itemAssoc.getSourceType() == null) && (itemAssoc.getSourceInfo() == null) &&
                (itemAssoc.getViewType() == null) && (itemAssoc.getChangeDate() == null)) {
            throw new IllegalArgumentException("missing constraints, at least one property of ItemAssocVO must be set");
        }
    }

    private void validateConstraints(IAConstraintVO<Integer, Integer> constraints) {
        if (constraints == null) {
            throw new IllegalArgumentException("missing Constraint 'constraints'");
        }
        if ((constraints.getSortAsc() != null) && (constraints.getSortField() == null)) {
            throw new IllegalArgumentException("wrong Constraint - if sortAsc is specified, a valid sortField needs to be specified as well");
        }
        if ((constraints.getSortAsc() != null) && (constraints.getSortField() != null)) {
            if (!constraints.getSortField().equals(ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME) &&
                !constraints.getSortField().equals(ItemAssocDAO.DEFAULT_CHANGE_DATE_COLUMN_NAME) &&
                !constraints.getSortField().equals(ItemAssocDAO.DEFAULT_ACTIVE_COLUMN_NAME) &&
                !constraints.getSortField().equals(ItemAssocDAO.DEFAULT_ASSOC_TYPE_COLUMN_NAME) &&
                !constraints.getSortField().equals(ItemAssocDAO.DEFAULT_ID_COLUMN_NAME) &&
                !constraints.getSortField().equals(ItemAssocDAO.DEFAULT_ITEM_FROM_COLUMN_NAME) &&
                !constraints.getSortField().equals(ItemAssocDAO.DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME) &&
                !constraints.getSortField().equals(ItemAssocDAO.DEFAULT_ITEM_TO_COLUMN_NAME) &&
                !constraints.getSortField().equals(ItemAssocDAO.DEFAULT_ITEM_TO_TYPE_COLUMN_NAME) &&
                !constraints.getSortField().equals(ItemAssocDAO.DEFAULT_SOURCE_TYPE_COLUMN_NAME) &&
                !constraints.getSortField().equals(ItemAssocDAO.DEFAULT_TENANT_COLUMN_NAME) &&
                !constraints.getSortField().equals(ItemAssocDAO.DEFAULT_VIEW_TYPE_COLUMN_NAME)) {
                throw new IllegalArgumentException("wrong Constraint - unknown sortField");
            }
        }
    }

    private void validateItemTo(ItemVO<Integer, Integer> itemTo) {
        if (itemTo == null) {
            throw new IllegalArgumentException("missing ItemVO 'itemTo'");
        } else if (itemTo.getItem() == null) {
            throw new IllegalArgumentException("missing value, ItemVO 'itemTo', missing 'id'");
        } else if (itemTo.getType() == null) {
            throw new IllegalArgumentException("missing value, ItemVO 'itemTo', missing 'typeId'");
        }
    }

    private void validateItemFrom(ItemVO<Integer, Integer> itemFrom) {
        if (itemFrom == null) {
            throw new IllegalArgumentException("missing ItemVO 'itemFrom'");
        } else if (itemFrom.getItem() == null) {
            throw new IllegalArgumentException("missing value, ItemVO 'itemFrom', missing 'id'");
        } else if (itemFrom.getType() == null) {
            throw new IllegalArgumentException("missing value, ItemVO 'itemFrom', missing 'typeId'");
        }
    }

    private void validateItemsAndConstraints(ItemVO<Integer, Integer> itemFrom,
                                             ItemVO<Integer, Integer> itemTo,
                                             IAConstraintVO<Integer, Integer> constraints) {
        validateItemFrom(itemFrom);
        validateItemTo(itemTo);
        validateConstraints(constraints);
    }

    private void validateAssocValue(ItemAssocVO<Integer,Integer> itemAssoc) {
        if (itemAssoc.getAssocValue() == null) {
            throw new IllegalArgumentException("missing constraints, missing 'assocValue', must not be NULL");
        }
    }

    private void validateViewType(ItemAssocVO<Integer,Integer> itemAssoc) {
        if (itemAssoc.getViewType() == null) {
            throw new IllegalArgumentException("missing constraints, missing 'viewTypeId', must not be NULL");
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // private inner classes
    private class ItemAssocVORowMapper
            implements RowMapper<ItemAssocVO<Integer,Integer>> {
        public ItemAssocVO<Integer,Integer> mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            ItemAssocVO<Integer,Integer> itemAssoc = new ItemAssocVO<Integer,Integer>(
                    DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                    new ItemVO<Integer, Integer>(DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_FROM_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME)),
                    DaoUtils.getInteger(rs, DEFAULT_ASSOC_TYPE_COLUMN_NAME),
                    DaoUtils.getDouble(rs, DEFAULT_ASSOC_VALUE_COLUMN_NAME),
                    new ItemVO<Integer, Integer>(DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_TO_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_TO_TYPE_COLUMN_NAME)),
                    DaoUtils.getInteger(rs, DEFAULT_SOURCE_TYPE_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_SOURCE_INFO_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_VIEW_TYPE_COLUMN_NAME),
                    DaoUtils.getBoolean(rs, DEFAULT_ACTIVE_COLUMN_NAME),
                    DaoUtils.getDate(rs, DEFAULT_CHANGE_DATE_COLUMN_NAME));
            return itemAssoc;
        }
    }

    private class AssociatedItemFromVORowMapper
            implements RowMapper<AssociatedItemVO<Integer, Integer>> {
        public AssociatedItemVO<Integer, Integer> mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            AssociatedItemVO<Integer, Integer> associatedItem = new AssociatedItemVO<Integer, Integer>(
                    new ItemVO<Integer, Integer>(DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_FROM_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME)),
                    DaoUtils.getDouble(rs, DEFAULT_ASSOC_VALUE_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_ASSOC_TYPE_COLUMN_NAME));
            return associatedItem;
        }
    }

    private class AssociatedItemToVORowMapper
            implements RowMapper<AssociatedItemVO<Integer, Integer>> {
        public AssociatedItemVO<Integer, Integer> mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            AssociatedItemVO<Integer, Integer> associatedItem = new AssociatedItemVO<Integer, Integer>(
                    new ItemVO<Integer, Integer>(DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_TO_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_TO_TYPE_COLUMN_NAME)),
                    DaoUtils.getDouble(rs, DEFAULT_ASSOC_VALUE_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_ASSOC_TYPE_COLUMN_NAME));
            return associatedItem;
        }
    }
}
