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
package org.easyrec.store.dao.domain.impl;

import org.easyrec.model.core.AssociatedItemVO;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.transfer.IAConstraintVO;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.store.dao.core.ItemAssocDAO;
import org.easyrec.store.dao.domain.TypedItemAssocDAO;
import org.easyrec.store.dao.impl.AbstractBaseItemAssocDAOMysqlImpl;
import org.easyrec.utils.spring.store.ResultSetIteratorMysql;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides a typed implementation of the {@link org.easyrec.store.dao.domain.TypedItemAssocDAO} interface.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2012-02-23 16:01:50 +0100 (Do, 23 Feb 2012) $<br/>
 * $Revision: 140 $</p>
 *
 * @author Roman Cerny
 */

@DAO
public class TypedItemAssocDAOMysqlImpl extends
        AbstractBaseItemAssocDAOMysqlImpl<ItemAssocVO<Integer, String>, AssociatedItemVO<Integer, String>, String, String, ItemVO<Integer, String>, IAConstraintVO<Integer, String>>
        implements TypedItemAssocDAO {
    // members
    private TypedItemAssocVORowMapper itemAssocVORowMapper = new TypedItemAssocVORowMapper();

    private ItemAssocDAO itemAssocDAO;
    private TypeMappingService typeMappingService;

    // constructor
    public TypedItemAssocDAOMysqlImpl(DataSource dataSource, ItemAssocDAO itemAssocDAO,
                                      TypeMappingService typeMappingService, SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
        this.itemAssocDAO = itemAssocDAO;
        this.typeMappingService = typeMappingService;

        // output connection information
        if (logger.isInfoEnabled()) {
            try {
                logger.info(DaoUtils.getDatabaseURLAndUserName(dataSource));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    // abstract (generic) method implementation of 'AbstractBaseItemAssocDAOMysqlImpl<TypedItemAssocVO, TypedAssociatedItemVO, ItemType, AssocType, TypedItemVO, TypedIAConstraintVO>'
    @Override
    public Iterator<ItemAssocVO<Integer, String>> getItemAssocIterator(int bulkSize) {
        return new ResultSetIteratorMysql<ItemAssocVO<Integer, String>>(
                getDataSource(), bulkSize, getItemAssocIteratorQueryString(), itemAssocVORowMapper);

    }

    @Override
    public List<ItemAssocVO<Integer, String>> getItemAssocs(
            ItemVO<Integer, String> itemFrom, String assocType, ItemVO<Integer, String> itemTo,
            IAConstraintVO<Integer, String> constraints) {
        Integer tenantId = constraints.getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return typeMappingService.convertListOfItemAssocVOs(tenantId, itemAssocDAO
                .getItemAssocs(typeMappingService.convertTypedItemVO(tenantId, itemFrom),
                        typeMappingService.getIdOfAssocType(tenantId, assocType),
                        typeMappingService.convertTypedItemVO(tenantId, itemTo),
                        typeMappingService.convertTypedIAConstraintVO(tenantId, constraints)));
    }

    @Override
    public List<ItemAssocVO<Integer, String>> getItemAssocsQBE(
            ItemVO<Integer, String> itemFrom, String assocType, ItemVO<Integer, String> itemTo,
            IAConstraintVO<Integer, String> constraints) {

        Integer tenantId = constraints.getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return typeMappingService.convertListOfItemAssocVOs(tenantId, itemAssocDAO
                .getItemAssocsQBE(typeMappingService.convertTypedItemVO(tenantId, itemFrom),
                        typeMappingService.getIdOfAssocType(tenantId, assocType),
                        typeMappingService.convertTypedItemVO(tenantId, itemTo),
                        typeMappingService.convertTypedIAConstraintVO(tenantId, constraints)));
    }


    @Override
    public List<AssociatedItemVO<Integer, String>> getItemsFrom(String itemFromType, String assocType,
                                                                                 ItemVO<Integer, String> itemTo,
                                                                                 IAConstraintVO<Integer, String> constraints) {
        Integer tenantId = constraints.getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return typeMappingService.convertListOfAssociatedItemVOs(tenantId, itemAssocDAO
                .getItemsFrom(typeMappingService.getIdOfItemType(tenantId, itemFromType),
                        typeMappingService.getIdOfAssocType(tenantId, assocType),
                        typeMappingService.convertTypedItemVO(tenantId, itemTo),
                        typeMappingService.convertTypedIAConstraintVO(tenantId, constraints)));
    }

    @Override
    public List<AssociatedItemVO<Integer, String>> getItemsTo(
            ItemVO<Integer, String> itemFrom, String assocType, String itemToType,
            IAConstraintVO<Integer, String> constraints) {
        Integer tenantId = constraints.getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return typeMappingService.convertListOfAssociatedItemVOs(tenantId, itemAssocDAO
                .getItemsTo(typeMappingService.convertTypedItemVO(tenantId, itemFrom),
                        typeMappingService.getIdOfAssocType(tenantId, assocType),
                        typeMappingService.getIdOfItemType(tenantId, itemToType),
                        typeMappingService.convertTypedIAConstraintVO(tenantId, constraints)));
    }

    @Override
    public int insertItemAssoc(ItemAssocVO<Integer, String> itemAssoc) {
        Integer tenantId = itemAssoc.getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return itemAssocDAO.insertItemAssoc(typeMappingService.convertTypedItemAssocVO(tenantId, itemAssoc));
    }

    public int insertOrUpdateItemAssocs(
            List<ItemAssocVO<Integer, String>> typedItemAssocs) {
        List<ItemAssocVO<Integer,Integer>> itemAssocs =
                new ArrayList<ItemAssocVO<Integer,Integer>>(
                        typedItemAssocs.size());

        for (ItemAssocVO<Integer, String> typedItemAssoc : typedItemAssocs) {
            itemAssocs.add(typeMappingService.convertTypedItemAssocVO(typedItemAssoc.getTenant(), typedItemAssoc));
        }

        return itemAssocDAO.insertOrUpdateItemAssocs(itemAssocs);
    }

    @Override
    public ItemAssocVO<Integer, String> loadItemAssocByPrimaryKey(
            Integer itemAssocId) {
        ItemAssocVO<Integer,Integer> loadedItemAssoc = itemAssocDAO
                .loadItemAssocByPrimaryKey(itemAssocId);
        return typeMappingService.convertItemAssocVO(loadedItemAssoc.getTenant(), loadedItemAssoc);
    }

    @Override
    public ItemAssocVO<Integer, String> loadItemAssocByUniqueKey(
            ItemAssocVO<Integer, String> itemAssoc) {
        Integer tenantId = itemAssoc.getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return typeMappingService.convertItemAssocVO(tenantId,
                itemAssocDAO.loadItemAssocByUniqueKey(typeMappingService.convertTypedItemAssocVO(tenantId, itemAssoc)));
    }

    @Override
    public int removeItemAssocsQBE(ItemAssocVO<Integer, String> itemAssoc) {
        Integer tenantId = itemAssoc.getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return itemAssocDAO.removeItemAssocsQBE(typeMappingService.convertTypedItemAssocVO(tenantId, itemAssoc));
    }

    @Override
    public int updateItemAssocUsingPrimaryKey(ItemAssocVO<Integer, String> itemAssoc) {
        Integer tenantId = itemAssoc.getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return itemAssocDAO
                .updateItemAssocUsingPrimaryKey(typeMappingService.convertTypedItemAssocVO(tenantId, itemAssoc));
    }

    @Override
    public int updateItemAssocUsingUniqueKey(ItemAssocVO<Integer, String> itemAssoc) {
        Integer tenantId = itemAssoc.getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return itemAssocDAO
                .updateItemAssocUsingUniqueKey(typeMappingService.convertTypedItemAssocVO(tenantId, itemAssoc));
    }

    public int removeItemAssocByTenant(Integer tenantId, String assocType, Integer sourceType, Date changeDate) {
        return itemAssocDAO
                .removeItemAssocByTenant(tenantId, typeMappingService.getIdOfAssocType(tenantId, assocType), sourceType,
                        changeDate);
    }

    //////////////////////////////////////////////////////////////////////////////
    // private inner classes
    private class TypedItemAssocVORowMapper
            implements RowMapper<ItemAssocVO<Integer, String>> {
        public ItemAssocVO<Integer, String> mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            Integer tenantId = DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME);
            if (tenantId == null) {
                throw new IllegalArgumentException(
                        "tenant not specified, can not retrieve type mapping without tenant");
            }
            return new ItemAssocVO<Integer, String>(
                    DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME), tenantId,
                    new ItemVO<Integer, String>(DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_FROM_COLUMN_NAME), typeMappingService
                            .getItemTypeById(tenantId, DaoUtils.getInteger(rs, DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME))),
                    typeMappingService
                            .getAssocTypeById(tenantId, DaoUtils.getInteger(rs, DEFAULT_ASSOC_TYPE_COLUMN_NAME)),
                    DaoUtils.getDouble(rs, DEFAULT_ASSOC_VALUE_COLUMN_NAME),
                    new ItemVO<Integer, String>(DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_TO_COLUMN_NAME), typeMappingService
                            .getItemTypeById(tenantId, DaoUtils.getInteger(rs, DEFAULT_ITEM_TO_TYPE_COLUMN_NAME))),
                    typeMappingService
                            .getSourceTypeById(tenantId, DaoUtils.getInteger(rs, DEFAULT_SOURCE_TYPE_COLUMN_NAME)),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_SOURCE_INFO_COLUMN_NAME), typeMappingService
                    .getViewTypeById(tenantId, DaoUtils.getInteger(rs, DEFAULT_VIEW_TYPE_COLUMN_NAME)),
                    DaoUtils.getBoolean(rs, DEFAULT_ACTIVE_COLUMN_NAME),
                    DaoUtils.getDate(rs, DEFAULT_CHANGE_DATE_COLUMN_NAME));
        }
    }
}
