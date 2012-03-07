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

package org.easyrec.store.dao.web.impl;

import org.easyrec.model.web.RemoteTenant;
import org.easyrec.plugin.Plugin.LifecyclePhase;
import org.easyrec.plugin.configuration.ConfigurationHelper;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.service.core.TenantService;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.Security;
import org.easyrec.utils.servlet.ServletUtils;
import org.easyrec.utils.spring.cache.annotation.InvalidatesCache;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.springframework.jdbc.core.RowMapper;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * The Class is the Implementation of the RemoteTenant DAO
 * <p/>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2007
 * <p/>
 * <b>last modified:</b><br/> $Author: szavrel $<br/> $Date: 2008-07-17
 * 20:00:46 +0200 (Do, 17 Jul 2008) $<br/> $Revision: 18568 $
 * </p>
 *
 * @author <AUTHOR>
 */
public class RemoteTenantDAOMysqlImpl extends BasicDAOMysqlImpl implements RemoteTenantDAO {
    private static final String FK_OPERATORID = "operatorId";

    private static final String SQL_GET_REMOTE_TENANT;
    private static final String SQL_GET_REMOTE_TENANT_BY_ID;
    private static final String SQL_DELETE_TENANT;
    private static final String SQL_UPDATE_TENANT;
    private static final String SQL_GET_TENANTS;
    private static final String SQL_GET_TENANTS_FROM_OPERATOR;
    private static final String SQL_GET_ALL_TENANTS;

    private static final int[] ARGTYPES_KEY = {Types.VARCHAR, Types.VARCHAR};

    static {
        SQL_GET_REMOTE_TENANT = new StringBuilder()
                .append("SELECT ID, STRINGID, OPERATORID, URL, DESCRIPTION, CREATIONDATE, TENANTCONFIG, TENANTSTATISTIC  FROM ")
                .append(DEFAULT_TABLE_NAME).append(" WHERE  OPERATORID = ? AND STRINGID = ?  ").toString();

        SQL_GET_REMOTE_TENANT_BY_ID = new StringBuilder()
                .append("SELECT ID, STRINGID, OPERATORID, URL, DESCRIPTION, CREATIONDATE, TENANTCONFIG, TENANTSTATISTIC  FROM ")
                .append(DEFAULT_TABLE_NAME).append(" WHERE ID = ?").toString();

        SQL_DELETE_TENANT = new StringBuilder().append("DELETE FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ")
                .append(DEFAULT_STRINGID_COLUMN_NAME).append(" = ? AND ").append(FK_OPERATORID).append(" = ? ")
                .toString();

        SQL_UPDATE_TENANT = new StringBuilder().append(" UPDATE ").append(DEFAULT_TABLE_NAME)
                .append(" SET URL = ?,  DESCRIPTION = ?,  OPERATORID = ?,  CREATIONDATE = ? WHERE ").append(DEFAULT_ID_COLUMN_NAME)
                .append(" = ? ").toString();

        SQL_GET_TENANTS = new StringBuilder().append(" SELECT ").append(DEFAULT_TABLE_KEY).append(", ")
                .append(DEFAULT_STRINGID_COLUMN_NAME)
                .append(", OPERATORID, URL, DESCRIPTION, CREATIONDATE, TENANTCONFIG, TENANTSTATISTIC ").append(" FROM ")
                .append(DEFAULT_TABLE_NAME).append(" WHERE STRINGID != ? ").append(" ORDER BY  CREATIONDATE DESC  LIMIT ?,?").toString();

        SQL_GET_ALL_TENANTS = new StringBuilder().append(" SELECT ").append(DEFAULT_TABLE_KEY).append(", ")
                .append(DEFAULT_STRINGID_COLUMN_NAME).append(", OPERATORID, URL, DESCRIPTION, CREATIONDATE, TENANTCONFIG, TENANTSTATISTIC FROM ")
                .append(DEFAULT_TABLE_NAME).append(" ORDER BY CREATIONDATE DESC ").toString();

        SQL_GET_TENANTS_FROM_OPERATOR = new StringBuilder().append(" SELECT ").append(DEFAULT_TABLE_KEY).append(", ")
                .append(DEFAULT_STRINGID_COLUMN_NAME)
                .append(", OPERATORID, URL, DESCRIPTION, CREATIONDATE, TENANTCONFIG, TENANTSTATISTIC ").append(" FROM ")
                .append(DEFAULT_TABLE_NAME).append(" WHERE ").append("   OPERATORID = ? ").append(" ORDER BY  ")
                .append("   CREATIONDATE ").toString();

    }

    private RemoteTenantRowMapper remoteTenantRowMapper = new RemoteTenantRowMapper();


    private HashMap<String, RemoteTenant> remoteTenantCache = new HashMap<String, RemoteTenant>();
    private HashMap<Integer, RemoteTenant> remoteTenantIntCache = new HashMap<Integer, RemoteTenant>();

    private PluginRegistry pluginRegistry;
    private TenantService tenantService;

    public RemoteTenantDAOMysqlImpl(DataSource dataSource) {
        super(dataSource);
        this.setTableId(DEFAULT_TABLE_KEY);
        this.setTableStringId(DEFAULT_STRINGID_COLUMN_NAME);
        this.setTableName(DEFAULT_TABLE_NAME);
    }

    public void setPluginRegistry(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }


    /*
     * (non-Javadoc)
     * @see at.researchstudio.sat.recommender.remote.store.dao.ItemDAO#tenantExists(java.lang.String)
     */
    public boolean exists(String operatorId, String tenantId) {
        return (get(operatorId, tenantId) != null);
    }


    /*
    * (non-Javadoc)
    * @see at.researchstudio.sat.recommender.remote.store.dao.OperatorDAO#removeOperator(java.lang.String)
    */
    public void remove(String operatorId, String tenantId) {
        RemoteTenant r = get(operatorId, tenantId);
        if (r != null) {
            try {
                getJdbcTemplate().update(SQL_DELETE_TENANT, new Object[]{tenantId, operatorId}, ARGTYPES_KEY);

                removeTenantDependencies(r.getId());
                remoteTenantIntCache.remove(r.getId());
                remoteTenantCache.remove(tenantId + ":::" + operatorId);
            } catch (Exception e) {
                logger.debug(e);
            }
        }
    }

    /*
    * (non-Javadoc)
    * @see at.researchstudio.sat.recommender.remote.store.dao.OperatorDAO#get(java.lang.String)
    */
    public RemoteTenant get(String operatorId, String tenantId) {

        String coreTenantId = tenantId + ":::" + operatorId;

        RemoteTenant r = remoteTenantCache.get(coreTenantId);

        if (r != null) {
            return r;
        } else {
            try {
                Object[] args = {operatorId, tenantId};

                r = getJdbcTemplate().queryForObject(SQL_GET_REMOTE_TENANT, args, ARGTYPES_KEY, remoteTenantRowMapper);

                remoteTenantCache.put(coreTenantId, r);
                remoteTenantIntCache.put(r.getId(), r);
                return r;

            } catch (Exception e) {
                return null;
            }
        }
    }

    public RemoteTenant get(Integer tenantId) {
        RemoteTenant r = remoteTenantIntCache.get(tenantId);

        if (r != null) {
            return r;
        } else {

            Object[] args = {tenantId};
            int[] argTypes = {Types.INTEGER};
            try {
                r = getJdbcTemplate()
                        .queryForObject(SQL_GET_REMOTE_TENANT_BY_ID, args, argTypes, remoteTenantRowMapper);

                remoteTenantCache.put(r.getStringId() + ":::" + r.getOperatorId(), r);
                remoteTenantIntCache.put(tenantId, r);

                return r;

            } catch (Exception e) {
                return r;
            }
        }
    }

    /**
     * This function returns a tenant for a given tenantid that is retrieved
     * from the given request Object that contains the parameters tenantId and
     * operatorId if signed in as administrator. If signed in as operator the
     * operatorId of the signed in operator is used to query for the tenant.
     */
    public RemoteTenant get(HttpServletRequest request) {
        return get(Security.getOperatorId(request), ServletUtils.getSafeParameter(request, "tenantId", ""));
    }

    /*
     * (non-Javadoc)
     * @see at.researchstudio.sat.recommender.remote.store.dao.RemoteTenantDAO#update(java.lang.String, java.lang.String)
     */
    public void update(String operatorId, Integer tenantId, String url, String description) {

        try {
            getJdbcTemplate()
                    .update(SQL_UPDATE_TENANT, new Object[]{url, description, operatorId, new Date(), tenantId},
                            new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.INTEGER});

            remoteTenantCache.remove(get(tenantId).getStringId() + ":::" + operatorId);
            remoteTenantIntCache.remove(tenantId);

        } catch (Exception e) {
            logger.debug(e);
        }
    }


    /*
    * (non-Javadoc)
    * @see at.researchstudio.sat.recommender.remote.store.dao.RemoteTenantDAO#getTenantsFromOperator(java.lang.String)
    */
    public List<RemoteTenant> getTenantsFromOperator(String operatorId) {

        Object[] args = {operatorId};
        int[] argTypes = {Types.VARCHAR};

        try {
            return getJdbcTemplate()
                    .query(SQL_GET_TENANTS_FROM_OPERATOR.replace("{0}", operatorId), args, argTypes, remoteTenantRowMapper);
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see at.researchstudio.sat.recommender.remote.store.dao.RemoteTenantDAO#getTenantsFromOperator(java.lang.String)
     */
    public List<RemoteTenant> getAllTenants() {
        try {
            return getJdbcTemplate().query(SQL_GET_ALL_TENANTS, remoteTenantRowMapper);
        } catch (Exception e) {
            return null;
        }
    }


    /*
     * (non-Javadoc)
     * @see at.researchstudio.sat.recommender.remote.store.dao.RemoteTenantDAO
     */
    public List<RemoteTenant> getTenants(int offset, int limit) {
        return getTenants(offset,limit,false) ;
    }

    /*
     * (non-Javadoc)
     * @see at.researchstudio.sat.recommender.remote.store.dao.RemoteTenantDAO
     */
    public List<RemoteTenant> getTenants(int offset, int limit, boolean filterDemoTenants) {
        try {
            String filter = "";

            if(filterDemoTenants){
                  filter = "EASYREC_DEMO";
            }

            Object[] args = {filter,offset,limit};
            int[] argTypes = {Types.VARCHAR,Types.INTEGER,Types.INTEGER};
            return getJdbcTemplate()
                    .query(SQL_GET_TENANTS, args, argTypes, remoteTenantRowMapper);

        } catch (Exception e) {
            return null;
        }
    }


    /*
     * (non-Javadoc)
     * @see at.researchstudio.sat.recommender.remote.store.dao.OperatorDAO#removeOperator(java.lang.String)
     */
    @InvalidatesCache
    public boolean removeTenantDependencies(int tenantId) {
        try {
            Object[] argsDelete = {tenantId};
            int[] argTypesDelete = {Types.INTEGER};
            execute("DELETE FROM action WHERE tenantId = ?", argsDelete, argTypesDelete);
            execute("DELETE FROM aggregatetype WHERE tenantId = ?", argsDelete, argTypesDelete);
            execute("DELETE FROM actiontype WHERE tenantId = ?", argsDelete, argTypesDelete);
            execute("DELETE FROM assoctype WHERE tenantId = ?", argsDelete, argTypesDelete);
            execute("DELETE FROM authentication WHERE tenantId = ?", argsDelete, argTypesDelete);
            execute("DELETE FROM itemtype WHERE tenantId = ?", argsDelete, argTypesDelete);
            execute("DELETE FROM itemassoc WHERE tenantId = ?", argsDelete, argTypesDelete);
            execute("DELETE FROM sourcetype WHERE tenantId = ?", argsDelete, argTypesDelete);
            execute("DELETE FROM viewtype WHERE tenantId = ?", argsDelete, argTypesDelete);
            execute("DELETE FROM plugin_log WHERE tenantId = ?", argsDelete, argTypesDelete);
            execute("DELETE FROM plugin_configuration WHERE tenantId = ?", argsDelete, argTypesDelete);

        } catch (Exception e) {
            logger.debug(e);
            return false;
        }
        return true;
    }

    /**
     * updates Tenant from Cache
     */
    public void updateTenantInCache(RemoteTenant r) {
        remoteTenantCache.remove(r.getStringId() + ":::" + r.getOperatorId());
        remoteTenantIntCache.remove(r.getId());
        get(r.getId());
    }


    /**
     * Execute a given sql string with the given parameters
     */
    private void execute(String sql, Object[] args, int[] argTypes) throws Exception {
        try {
            getJdbcTemplate().update(sql, args, argTypes);
        } catch (Exception e) {
            throw new Exception();
        }
    }

    /**
     * Clears the statistic properties
     */
    public void reset(Integer tenantId) {
        getJdbcTemplate().update("UPDATE tenant SET tenantStatistic='' WHERE id=" + tenantId);
    }


    /******************************************************************************************/
    /************************************** Rowmappers ****************************************/
    /**
     * **************************************************************************************
     */


    private class RemoteTenantRowMapper implements RowMapper<RemoteTenant> {
        public RemoteTenant mapRow(ResultSet rs, int rowNum) throws SQLException {
            String sTenantConfig = DaoUtils.getStringIfPresent(rs, DEFAULT_TENANT_CONFIG_COLUMN_NAME);

            Properties tenantConfig = new Properties();
            try {
                tenantConfig.load(new ByteArrayInputStream(new StringBuffer(sTenantConfig).toString().getBytes()));

            } catch (Exception ignored) {
            } // no tenantConfig available

            String sTenantStatistic = DaoUtils.getStringIfPresent(rs, DEFAULT_TENANT_STATISTIC_COLUMN_NAME);

            Properties tenantStatistic = new Properties();
            try {
                tenantStatistic
                        .load(new ByteArrayInputStream(new StringBuffer(sTenantStatistic).toString().getBytes()));

            } catch (Exception ignored) {
            } // no tenantStatistic available

            RemoteTenant remoteTenant = new RemoteTenant(DaoUtils.getIntegerIfPresent(rs, DEFAULT_ID_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_STRINGID_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_OPERATORID_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_URL_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_DESCRIPTION_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_CREATIONDATE_COLUMN_NAME), tenantConfig, tenantStatistic,
                    tenantService.getDefaultTenantConfig().getAssocTypes()
                    //typeMappingService.getAssocTypes(DaoUtils.getIntegerIfPresent(rs,DEFAULT_ID_COLUMN_NAME))
            );
            try {
                if (remoteTenant.getPluginsEnabled() &&
                        (remoteTenant.getTenantConfigProperties().containsKey(PluginRegistry.GENERATOR_PROP))) {
                    String pluginIdStr = remoteTenant.getTenantConfigProperties().getProperty(PluginRegistry
                            .GENERATOR_PROP);
                    PluginId pluginId = PluginId.parsePluginId(pluginIdStr);
                    Generator<GeneratorConfiguration, GeneratorStatistics> generator = pluginRegistry.getGenerators()
                            .get(pluginId);
                    if ((generator == null) || (LifecyclePhase.NOT_INSTALLED.equals(generator.getLifecyclePhase()))) {
                        remoteTenant.setPlugins(RemoteTenant.DISABLED);
                        tenantService.updateConfigProperty(remoteTenant.getId(), PluginRegistry.PLUGINS_ENABLED_PROP,
                                RemoteTenant.DISABLED);
                    } else {
                        GeneratorConfiguration conf = generator.newConfiguration();
                        ConfigurationHelper confhelper = new ConfigurationHelper(conf);
                        confhelper.setValues(remoteTenant.getTenantConfigProperties(), pluginId.toString());
                        remoteTenant.setGeneratorConfig(conf);

                        remoteTenant.updatePluginAssocType(conf.getAssociationType());
                    }
                }
            } catch (Exception ex) {
                logger.warn("Failed to initialize plugins!", ex);
            }

            return remoteTenant;
        }
    }
}