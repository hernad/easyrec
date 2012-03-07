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

package org.easyrec.model.web;

import com.google.common.base.Strings;
import org.easyrec.model.web.statistics.*;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.generator.GeneratorConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * this tenant is a Website, shop or portal where
 * easyrec operates on. This Model holds tenant specfic information
 * like name, url, descritpion, statistical information (nr.o.items, users,...)
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: szavrel $<br/>
 * $Date: 2012-02-02 18:49:38 +0100 (Do, 02 Feb 2012) $<br/>
 * $Revision: 18703 $</p>
 *
 * @author phlavac
 * @version <CURRENT PROJECT VERSION>
 * @since <PROJECT VERSION ON FILE CREATION>
 */
public class RemoteTenant {
    private Integer id;
    private String stringId;
    private String operatorId;
    private String url;
    private String description;
    private String creationDate;
    private Properties tenantConfigProperties;
    private Properties tenantStatisticProperties;
    private TenantStatistic tenantStatistic;
    private UserStatistic userStatistic;
    private RuleMinerStatistic ruleMinerStatistic;
    private HashMap<String, AssocStatistic> assocStatistic;
    private ConversionStatistic conversionStatistic;
    private Boolean maxActionLimitExceeded;
    private Boolean maxActionLimitAlmostExceeded;
    private GeneratorConfiguration generatorConfig;


    // TODO: move to vocabulary?
    public static final String DEFAULT_TENANT_ID = "EASYREC_DEMO";
    public static final String DEFAULT_TENANT_DESCRIPITON = "This is a demo tenant.";

    public static final String SCHEDULER_ENABLED = "AUTO_RULEMINER.enabled";
    public static final String SCHEDULER_EXECUTION_TIME = "AUTO_RULEMINER.executionTime";
    public static final String SCHEDULER_DEFAULT_EXECUTION_TIME = "02:00";

    public static final String AUTO_ARCHIVER_ENABLED = "AUTO_ARCHIVER.enabled";
    public static final String AUTO_ARCHIVER_TIME_RANGE = "AUTO_ARCHIVER.timeRange";
    public static final String AUTO_ARCHIVER_DEFAULT_TIME_RANGE = "1825"; // 5 years = 365*5

    public static final String TENANT_ACTIONS = "TENANT.actions";
    public static final String TENANT_USERS = "TENANT.users";
    public static final String TENANT_ITEMS = "TENANT.items";
    public static final String TENANT_BACKTRACKS = "TENANT.backtracks";
    public static final String TENANT_AVERAGE_ACTIONS_PER_USER = "TENANT.averageActionsPerUser";
    public static final String TENANT_RECOMMENDATION_COVERAGE = "TENANT.recommendationCoverage";

    public static final String USER_1_ACTION = "USER.1.action";
    public static final String USER_2_ACTIONS = "USER.2.actions";
    public static final String USER_3_10_ACTIONS = "USER.3.10.actions";
    public static final String USER_11_100_ACTIONS = "USER.11.100.actions";
    public static final String USER_101_AND_MORE_ACTIONS = "USER.101.and.more.actions";

    public static final String CONVERSION_RECOMMENDATION_TO_BUY_COUNT = "CONVERSION.recommendationToBuyCount";

    public static final String ASSOC_ACTIONS = "ASSOC.actions.";
    public static final String ASSOC_RULES = "ASSOC.rules.";
    public static final String ASSOC_ITEM_WITH_RULES = "ASSOC.itemsWithRules.";
    public static final String ASSOC_AVERAGE_NUMBER_OF_RULES_PER_ITEM = "ASSOC.averageNumberOfRulesPerItem.";
    public static final String ASSOC_STD_NUMBER_OF_RULES_PER_ITEM = "ASSOC.stdNumberOfRulesPerItem.";

    public static final String ASSOC_TOTAL_ITEMS_WITH_RULES = "ASSOC.total.items.with.rules";
    public static final String ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE1 = "ASSOC.value.greater.than.minassocvalue1";
    public static final String ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE2 = "ASSOC.value.greater.than.minassocvalue2";
    public static final String ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE3 = "ASSOC.value.greater.than.minassocvalue3";
    public static final String ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE4 = "ASSOC.value.greater.than.minassocvalue4";


    public static final String BACKTRACKING = "backtracking";
    public static final String BACKTRACKING_URL = "backtrackingURL";
    public static final String ENABLED = "true";
    public static final String DISABLED = "false";
    public static final String MONTHLY_ACTIONS = "TENANT.monthly.actions";
    public static final String MAXACTIONS = "TENANT.maxactions";
    public static final String MAXACTIONS_DEFAULT = "0"; // unlimited
    //    public static final String PLUGINS            = "plugins.enabled";
    //    public static final String GENERATOR          = "generator";

    public RemoteTenant(Integer id, String tenantId, String operatorId, String url, String description,
                        String creationDate, Properties tenantConfigProperties, Properties tenantStatisticProperties,
                        Collection<String> assocTypes) {

        super();
        this.id = id;
        this.stringId = tenantId;
        this.operatorId = operatorId;
        this.url = url;
        this.description = description;
        this.creationDate = creationDate;
        this.tenantConfigProperties = tenantConfigProperties;
        this.tenantStatisticProperties = tenantStatisticProperties;

        // map Statistic Properties to Objects;
        try {
            this.tenantStatistic = new TenantStatistic(
                    Integer.parseInt(tenantStatisticProperties.getProperty(TENANT_ACTIONS)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(TENANT_BACKTRACKS)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(TENANT_ITEMS)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(TENANT_USERS)),
                    Float.parseFloat(tenantStatisticProperties.getProperty(TENANT_AVERAGE_ACTIONS_PER_USER)),
                    Float.parseFloat(tenantStatisticProperties.getProperty(TENANT_RECOMMENDATION_COVERAGE)));
        } catch (Exception e) {
            this.tenantStatistic = new TenantStatistic(0, 0, 0, 0, 0.F, 0.F);
        }

        try {
            this.userStatistic = new UserStatistic(
                    Integer.parseInt(tenantStatisticProperties.getProperty(USER_1_ACTION)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(USER_2_ACTIONS)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(USER_3_10_ACTIONS)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(USER_11_100_ACTIONS)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(USER_101_AND_MORE_ACTIONS)));
        } catch (Exception e) {
            this.userStatistic = new UserStatistic(0, 0, 0, 0, 0);
        }

        try {
            this.ruleMinerStatistic = new RuleMinerStatistic(
                    Integer.parseInt(tenantStatisticProperties.getProperty(ASSOC_TOTAL_ITEMS_WITH_RULES)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE1)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE2)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE3)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE4)));
        } catch (Exception e) {
            this.ruleMinerStatistic = new RuleMinerStatistic(0, 0, 0, 0, 0);
        }


        try {
            this.conversionStatistic = new ConversionStatistic(
                    Integer.parseInt(tenantStatisticProperties.getProperty(CONVERSION_RECOMMENDATION_TO_BUY_COUNT)));
        } catch (Exception e) {
            this.conversionStatistic = new ConversionStatistic(0);
        }

        this.assocStatistic = new HashMap<String, AssocStatistic>();
        for (String assocType : assocTypes) {
            addTenantStatisticPropertiesToAssocStatistics(assocType);
        }
    }

    private void addTenantStatisticPropertiesToAssocStatistics(String assocType) {
        try {
            assocStatistic.put(assocType.replace("_", " ").toLowerCase(), new AssocStatistic(
                    Integer.parseInt(tenantStatisticProperties.getProperty(ASSOC_ACTIONS + assocType)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(ASSOC_RULES + assocType)),
                    Integer.parseInt(tenantStatisticProperties.getProperty(ASSOC_ITEM_WITH_RULES + assocType)),
                    Integer.parseInt(
                            tenantStatisticProperties.getProperty(ASSOC_AVERAGE_NUMBER_OF_RULES_PER_ITEM + assocType)),
                    Integer.parseInt(
                            tenantStatisticProperties.getProperty(ASSOC_STD_NUMBER_OF_RULES_PER_ITEM + assocType))));
        } catch (Exception e) {
            assocStatistic.put(assocType.replace("_", " "), new AssocStatistic(0, 0, 0, 0, 0));
        }
    }


    public void updatePluginAssocType(String assocType) {
        addTenantStatisticPropertiesToAssocStatistics(assocType);
    }

    public Properties getTenantConfigProperties() {
        return tenantConfigProperties;
    }

    public void setTenantConfigProperties(Properties tenantConfigProperties) {
        this.tenantConfigProperties = tenantConfigProperties;
    }

    public Properties getTenantStatisticProperties() {
        return tenantStatisticProperties;
    }

    public void setTenantStatisticProperties(Properties tenantStatisticProperties) {
        this.tenantStatisticProperties = tenantStatisticProperties;
    }


    public TenantStatistic getTenantStatistic() {
        return tenantStatistic;
    }

    // Map Object to Properties

    public void setTenantStatistic(TenantStatistic tenantStatistic) {

        this.tenantStatistic = tenantStatistic;

        if (tenantStatisticProperties.getProperty(TENANT_ACTIONS) == null) {
            tenantStatisticProperties.put(TENANT_ACTIONS, tenantStatistic.getActions().toString());
        } else {
            tenantStatisticProperties.setProperty(TENANT_ACTIONS, tenantStatistic.getActions().toString());
        }

        if (tenantStatisticProperties.getProperty(TENANT_USERS) == null) {
            tenantStatisticProperties.put(TENANT_USERS, tenantStatistic.getUsers().toString());
        } else {
            tenantStatisticProperties.setProperty(TENANT_USERS, tenantStatistic.getUsers().toString());
        }

        if (tenantStatisticProperties.getProperty(TENANT_ITEMS) == null) {
            tenantStatisticProperties.put(TENANT_ITEMS, tenantStatistic.getItems().toString());
        } else {
            tenantStatisticProperties.setProperty(TENANT_ITEMS, tenantStatistic.getItems().toString());
        }

        if (tenantStatisticProperties.getProperty(TENANT_BACKTRACKS) == null) {
            tenantStatisticProperties.put(TENANT_BACKTRACKS, tenantStatistic.getBacktracks().toString());
        } else {
            tenantStatisticProperties.setProperty(TENANT_BACKTRACKS, tenantStatistic.getBacktracks().toString());
        }

        if (tenantStatisticProperties.getProperty(TENANT_AVERAGE_ACTIONS_PER_USER) == null) {
            tenantStatisticProperties
                    .put(TENANT_AVERAGE_ACTIONS_PER_USER, tenantStatistic.getAverageActionsPerUser().toString());
        } else {
            tenantStatisticProperties.setProperty(TENANT_AVERAGE_ACTIONS_PER_USER,
                    tenantStatistic.getAverageActionsPerUser().toString());
        }

        if (tenantStatisticProperties.getProperty(TENANT_RECOMMENDATION_COVERAGE) == null) {
            tenantStatisticProperties
                    .put(TENANT_RECOMMENDATION_COVERAGE, tenantStatistic.getRecommendationCoverage().toString());
        } else {
            tenantStatisticProperties.setProperty(TENANT_RECOMMENDATION_COVERAGE,
                    tenantStatistic.getRecommendationCoverage().toString());
        }
    }

    public void setUserStatistic(UserStatistic userStatistic) {
        this.userStatistic = userStatistic;

        if (tenantStatisticProperties.getProperty(USER_1_ACTION) == null) {
            tenantStatisticProperties.put(USER_1_ACTION, userStatistic.getUsers_with_1_action().toString());
        } else {
            tenantStatisticProperties.setProperty(USER_1_ACTION, userStatistic.getUsers_with_1_action().toString());
        }

        if (tenantStatisticProperties.getProperty(USER_2_ACTIONS) == null) {
            tenantStatisticProperties.put(USER_2_ACTIONS, userStatistic.getUsers_with_2_actions().toString());
        } else {
            tenantStatisticProperties.setProperty(USER_2_ACTIONS, userStatistic.getUsers_with_2_actions().toString());
        }

        if (tenantStatisticProperties.getProperty(USER_3_10_ACTIONS) == null) {
            tenantStatisticProperties.put(USER_3_10_ACTIONS, userStatistic.getUsers_with_3_10_actions().toString());
        } else {
            tenantStatisticProperties
                    .setProperty(USER_3_10_ACTIONS, userStatistic.getUsers_with_3_10_actions().toString());
        }

        if (tenantStatisticProperties.getProperty(USER_11_100_ACTIONS) == null) {
            tenantStatisticProperties.put(USER_11_100_ACTIONS, userStatistic.getUsers_with_11_100_actions().toString());
        } else {
            tenantStatisticProperties
                    .setProperty(USER_11_100_ACTIONS, userStatistic.getUsers_with_11_100_actions().toString());
        }
        if (tenantStatisticProperties.getProperty(USER_101_AND_MORE_ACTIONS) == null) {
            tenantStatisticProperties
                    .put(USER_101_AND_MORE_ACTIONS, userStatistic.getUsers_with_101_and_more_actions().toString());
        } else {
            tenantStatisticProperties.setProperty(USER_101_AND_MORE_ACTIONS,
                    userStatistic.getUsers_with_101_and_more_actions().toString());
        }
    }

    public void setRuleMinerStatistic(RuleMinerStatistic ruleMinerStatistic) {
        this.ruleMinerStatistic = ruleMinerStatistic;

        if (tenantStatisticProperties.getProperty(ASSOC_TOTAL_ITEMS_WITH_RULES) == null) {
            tenantStatisticProperties
                    .put(ASSOC_TOTAL_ITEMS_WITH_RULES, ruleMinerStatistic.getItemsWithRules().toString());
        } else {
            tenantStatisticProperties
                    .setProperty(ASSOC_TOTAL_ITEMS_WITH_RULES, ruleMinerStatistic.getItemsWithRules().toString());
        }

        if (tenantStatisticProperties.getProperty(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE1) == null) {
            tenantStatisticProperties.put(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE1,
                    ruleMinerStatistic.getItemsWithAssocValueGreaterThanMinAssocValue1().toString());
        } else {
            tenantStatisticProperties.setProperty(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE1,
                    ruleMinerStatistic.getItemsWithAssocValueGreaterThanMinAssocValue1().toString());
        }

        if (tenantStatisticProperties.getProperty(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE2) == null) {
            tenantStatisticProperties.put(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE2,
                    ruleMinerStatistic.getItemsWithAssocValueGreaterThanMinAssocValue2().toString());
        } else {
            tenantStatisticProperties.setProperty(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE2,
                    ruleMinerStatistic.getItemsWithAssocValueGreaterThanMinAssocValue2().toString());
        }

        if (tenantStatisticProperties.getProperty(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE3) == null) {
            tenantStatisticProperties.put(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE3,
                    ruleMinerStatistic.getItemsWithAssocValueGreaterThanMinAssocValue3().toString());
        } else {
            tenantStatisticProperties.setProperty(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE3,
                    ruleMinerStatistic.getItemsWithAssocValueGreaterThanMinAssocValue3().toString());
        }

        if (tenantStatisticProperties.getProperty(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE4) == null) {
            tenantStatisticProperties.put(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE4,
                    ruleMinerStatistic.getItemsWithAssocValueGreaterThanMinAssocValue4().toString());
        } else {
            tenantStatisticProperties.setProperty(ASSOC_VALUE_GREATER_THAN_MIN_ASSOC_VALUE4,
                    ruleMinerStatistic.getItemsWithAssocValueGreaterThanMinAssocValue4().toString());
        }

    }

    public void setConversionStatistic(ConversionStatistic conversionStatistic) {
        this.conversionStatistic = conversionStatistic;

        if (tenantStatisticProperties.getProperty(CONVERSION_RECOMMENDATION_TO_BUY_COUNT) == null) {
            tenantStatisticProperties.put(CONVERSION_RECOMMENDATION_TO_BUY_COUNT,
                    conversionStatistic.getRecommendationToBuyCount().toString());
        } else {
            tenantStatisticProperties.setProperty(CONVERSION_RECOMMENDATION_TO_BUY_COUNT,
                    conversionStatistic.getRecommendationToBuyCount().toString());
        }
    }

    public void setAssocStatistic(HashMap<String, AssocStatistic> assocStatistic) {
        this.assocStatistic = assocStatistic;

        for (final String s : assocStatistic.keySet()) {
            String assocTypeId = s;
            if (tenantStatisticProperties.getProperty(ASSOC_ACTIONS + assocTypeId) == null) {
                tenantStatisticProperties
                        .put(ASSOC_ACTIONS + assocTypeId, assocStatistic.get(assocTypeId).getActions().toString());
            } else {
                tenantStatisticProperties.setProperty(ASSOC_ACTIONS + assocTypeId,
                        assocStatistic.get(assocTypeId).getActions().toString());
            }

            if (tenantStatisticProperties.getProperty(ASSOC_RULES + assocTypeId) == null) {
                tenantStatisticProperties
                        .put(ASSOC_RULES + assocTypeId, assocStatistic.get(assocTypeId).getRules().toString());
            } else {
                tenantStatisticProperties
                        .setProperty(ASSOC_RULES + assocTypeId, assocStatistic.get(assocTypeId).getRules().toString());
            }

            if (tenantStatisticProperties.getProperty(ASSOC_ITEM_WITH_RULES + assocTypeId) == null) {
                tenantStatisticProperties.put(ASSOC_ITEM_WITH_RULES + assocTypeId,
                        assocStatistic.get(assocTypeId).getItemsWithRules().toString());
            } else {
                tenantStatisticProperties.setProperty(ASSOC_ITEM_WITH_RULES + assocTypeId,
                        assocStatistic.get(assocTypeId).getItemsWithRules().toString());
            }

            if (tenantStatisticProperties.getProperty(ASSOC_AVERAGE_NUMBER_OF_RULES_PER_ITEM + assocTypeId) == null) {
                tenantStatisticProperties.put(ASSOC_AVERAGE_NUMBER_OF_RULES_PER_ITEM + assocTypeId,
                        assocStatistic.get(assocTypeId).getAverageNumberOfRulesPerItem().toString());
            } else {
                tenantStatisticProperties.setProperty(ASSOC_AVERAGE_NUMBER_OF_RULES_PER_ITEM + assocTypeId,
                        assocStatistic.get(assocTypeId).getAverageNumberOfRulesPerItem().toString());
            }

            if (tenantStatisticProperties.getProperty(ASSOC_STD_NUMBER_OF_RULES_PER_ITEM + assocTypeId) == null) {
                tenantStatisticProperties.put(ASSOC_STD_NUMBER_OF_RULES_PER_ITEM + assocTypeId,
                        assocStatistic.get(assocTypeId).getStdNumberOfRulesPerItem().toString());
            } else {
                tenantStatisticProperties.setProperty(ASSOC_STD_NUMBER_OF_RULES_PER_ITEM + assocTypeId,
                        assocStatistic.get(assocTypeId).getStdNumberOfRulesPerItem().toString());
            }
        }
    }

    public HashMap<String, AssocStatistic> getAssocStatistic() {
        return assocStatistic;
    }

    public ConversionStatistic getConversionStatistic() {
        return conversionStatistic;
    }


    public UserStatistic getUserStatistic() {
        return userStatistic;
    }

    public RuleMinerStatistic getRuleMinerStatistic() {
        return ruleMinerStatistic;
    }


    /**
     * Returns the scheduler execution time if set
     * otherwise the default execution time.
     *
     * @return execution time [HH:mm]
     */
    public String getSchedulerExecutionTime() {
        return Strings.isNullOrEmpty(tenantConfigProperties.getProperty(SCHEDULER_EXECUTION_TIME)) ?
                RemoteTenant.SCHEDULER_DEFAULT_EXECUTION_TIME :
                tenantConfigProperties.getProperty(SCHEDULER_EXECUTION_TIME);
    }

    /**
     * Sets the scheduler daily execution time.
     *
     * @param time [HH:mm]
     */
    public void setSchedulerExecutionTime(String time) {
        if (tenantConfigProperties.getProperty(SCHEDULER_EXECUTION_TIME) == null) {
            tenantConfigProperties.put(SCHEDULER_EXECUTION_TIME, time);
        } else {
            tenantConfigProperties.setProperty(SCHEDULER_EXECUTION_TIME, time);
        }
    }

    /**
     * Returns the time range for actions that are moved to the archive table.
     *
     * @return String
     */
    public String getAutoArchiverTimeRange() {
        return Strings.isNullOrEmpty(tenantConfigProperties.getProperty(AUTO_ARCHIVER_TIME_RANGE)) ?
                RemoteTenant.AUTO_ARCHIVER_DEFAULT_TIME_RANGE :
                tenantConfigProperties.getProperty(AUTO_ARCHIVER_TIME_RANGE);
    }

    /**
     * Sets the time range in days for actions that are moved to the
     * archive table.
     *
     * @param days String
     */
    public void setAutoArchiverTimeRange(String days) {
        if (tenantConfigProperties.getProperty(AUTO_ARCHIVER_TIME_RANGE) == null) {
            tenantConfigProperties.put(AUTO_ARCHIVER_TIME_RANGE, days);
        } else {
            tenantConfigProperties.setProperty(AUTO_ARCHIVER_TIME_RANGE, days);
        }
    }

    /**
     * Returns true if archiving is enabled
     *
     * @return boolean
     */
    public boolean autoArchivingEnabled() {
        return ENABLED.equals(tenantConfigProperties.getProperty(AUTO_ARCHIVER_ENABLED));
    }

    /**
     * Returns true if autorulemining is enabled
     *
     * @return boolean
     */
    public boolean isSchedulerEnabled() {
        return ENABLED.equals(tenantConfigProperties.getProperty(SCHEDULER_ENABLED));
    }

    /**
     * Returns true if backtracking is enabled
     *
     * @return boolean
     */
    public boolean backtrackingEnabled() {
        return ENABLED.equals(tenantConfigProperties.getProperty(BACKTRACKING));
    }

    /**
     * Enable/Disable the backtracking function for this tenant.
     *
     * @param value true, false
     */
    public void setBacktracking(String value) {
        if (tenantConfigProperties.getProperty(BACKTRACKING) == null) {
            tenantConfigProperties.put(BACKTRACKING, value);
        } else {
            tenantConfigProperties.setProperty(BACKTRACKING, value);
        }
    }
    
    public String getBacktrackingURL() {
        return tenantConfigProperties.getProperty(BACKTRACKING_URL);
    }
    
    public void setBackTrackingURL(String value) {
        if (tenantConfigProperties.getProperty(BACKTRACKING_URL) == null) {
            tenantConfigProperties.put(BACKTRACKING_URL, value);
        } else {
            tenantConfigProperties.setProperty(BACKTRACKING_URL, value);
        }
    }

    /**
     * Returns true if plugins are enabled
     *
     * @return boolean
     */
    public boolean getPluginsEnabled() {
        return true;
        //return ENABLED.equals(tenantConfigProperties.getProperty(PluginRegistry.PLUGINS_ENABLED_PROP));
    }

    /**
     * Enable/Disable the plugin support for this tenant.
     *
     * @param value true, false
     */
    public void setPlugins(String value) {
        if (tenantConfigProperties.getProperty(PluginRegistry.PLUGINS_ENABLED_PROP) == null) {
            tenantConfigProperties.put(PluginRegistry.PLUGINS_ENABLED_PROP, value);
        } else {
            tenantConfigProperties.setProperty(PluginRegistry.PLUGINS_ENABLED_PROP, value);
        }
    }

    /**
     * Sets the number of maximum actions allowed per month
     *
     * @param maxactions String
     */
    public void setMaxActions(String maxactions) {
        if (tenantConfigProperties.getProperty(MAXACTIONS) == null) {
            tenantConfigProperties.put(MAXACTIONS, maxactions);
        } else {
            tenantConfigProperties.setProperty(MAXACTIONS, maxactions);
        }
    }

    /**
     * Returns the maximum actions allowed per month
     *
     * @return String
     */
    public String getMaxActions() {
        return Strings.isNullOrEmpty(tenantConfigProperties.getProperty(MAXACTIONS)) ? RemoteTenant.MAXACTIONS_DEFAULT :
                tenantConfigProperties.getProperty(MAXACTIONS);
    }

    /**
     * Returns the number of actions for the current month
     *
     * @return String
     */
    public String getMonthlyActions() {
        return Strings.isNullOrEmpty(tenantStatisticProperties.getProperty(MONTHLY_ACTIONS)) ? "0" :
                tenantStatisticProperties.getProperty(MONTHLY_ACTIONS);
    }

    /**
     * Sets the number of monthly actions for this tenant.
     *
     * @param value String
     */
    public void setMonthlyActions(String value) {
        if (tenantStatisticProperties.getProperty(MONTHLY_ACTIONS) == null) {
            tenantStatisticProperties.put(MONTHLY_ACTIONS, value);
        } else {
            tenantStatisticProperties.setProperty(MONTHLY_ACTIONS, value);
        }
    }


    /**
     * Returns true, if the number of incoming action exceeds the monthly
     * maximum number of allowed actions for that tenant.
     * If maxActions is 0, there will be no limits.
     *
     * @return boolean
     */
    public boolean isMaxActionLimitExceeded() {
        try {
            int maxActions = Integer.parseInt(getMaxActions());
            int actions = Integer.parseInt(getMonthlyActions());

            maxActionLimitExceeded = maxActions < actions && maxActions != 0;
        } catch (Exception e) {
            maxActionLimitExceeded = false;
        }

        return maxActionLimitExceeded;
    }

    /**
     * Returns the percentage off reached maximum actions per month.
     *
     * @return Integer
     */
    public Integer getLimitReachedBy() {
        Integer l = null;
        try {
            int maxActions = Integer.parseInt(getMaxActions());
            int actions = Integer.parseInt(getMonthlyActions());
            l = Math.min(100, (actions * 100) / maxActions);
        } catch (Exception ignored) {
        }
        return l;
    }

    /**
     * returns true if number of maximum actions is reached by 80%
     *
     * @return boolean
     */
    public boolean isMaxActionLimitAlmostExceeded() {
        try {
            float maxActions = Integer.parseInt(getMaxActions());
            float actions = Integer.parseInt(getMonthlyActions());

            maxActionLimitAlmostExceeded = maxActions * 0.8 <= actions && maxActions != 0;
        } catch (Exception e) {
            maxActionLimitAlmostExceeded = false;
        }

        return maxActionLimitAlmostExceeded;
    }


    /**
     * Enable/Disable the scheduler for this tenant.
     *
     * @param value true, false
     */
    public void setSchedulingEnabled(String value) {
        if (tenantConfigProperties.getProperty(SCHEDULER_ENABLED) == null) {
            tenantConfigProperties.put(SCHEDULER_ENABLED, value);
        } else {
            tenantConfigProperties.setProperty(SCHEDULER_ENABLED, value);
        }
    }

    /**
     * Enable/Disable the archiving function for this tenant.
     *
     * @param value true, false
     */
    public void setAutoArchiving(String value) {
        if (tenantConfigProperties.getProperty(AUTO_ARCHIVER_ENABLED) == null) {
            tenantConfigProperties.put(AUTO_ARCHIVER_ENABLED, value);
        } else {
            tenantConfigProperties.setProperty(AUTO_ARCHIVER_ENABLED, value);
        }
    }


    /**
     * Returns the number of all actions for this tenant
     *
     * @return String
     */
    public String getActions() {
        return Strings.isNullOrEmpty(tenantStatisticProperties.getProperty(TENANT_ACTIONS)) ? "0" :
                tenantStatisticProperties.getProperty(TENANT_ACTIONS);
    }

    /**
     * Set the number of actions for this tenant.
     *
     * @param value String
     */
    public void setActions(String value) {
        if (tenantStatisticProperties.getProperty(TENANT_ACTIONS) == null) {
            tenantStatisticProperties.put(TENANT_ACTIONS, value);
        } else {
            tenantStatisticProperties.setProperty(TENANT_ACTIONS, value);
        }
    }

    /**
     * Get the number of all Rules for this tenant.
     *
     * @return Integer
     */
    public Integer getRules() {

        Integer totalnumberOfRules = 0;
        if (assocStatistic != null) {
            for (final Entry<String, AssocStatistic> o : assocStatistic.entrySet()) {
                Entry<String, AssocStatistic> m = o;
                if (m != null) {
                    AssocStatistic a = m.getValue();
                    if (a != null) {
                        totalnumberOfRules += a.getRules();
                    }
                }
            }
        }
        return totalnumberOfRules;
    }


    /**
     * Returns the key id of the tenant.
     *
     * @return Integer
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the string id of the tenant.
     * The stringId and operatorId is unique.
     *
     * @return String
     */
    public String getStringId() {
        return stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }

    /**
     * Returns the owner of the tenant.
     *
     * @return String
     */
    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * Returns the website of the tenant.
     *
     * @return String
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the description of the tenant.
     *
     * @return String
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the creation date of the tenant.
     *
     * @return String
     */
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public GeneratorConfiguration getGeneratorConfig() {
        return generatorConfig;
    }

    public void setGeneratorConfig(GeneratorConfiguration generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

}
