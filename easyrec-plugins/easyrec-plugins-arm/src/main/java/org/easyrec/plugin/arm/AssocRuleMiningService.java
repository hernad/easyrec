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
package org.easyrec.plugin.arm;

import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Collection;

import java.util.List;
import java.util.SortedSet;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.plugin.arm.model.ARMConfiguration;
import org.easyrec.plugin.arm.model.ARMConfigurationInt;
import org.easyrec.plugin.arm.model.ARMStatistics;
import org.easyrec.plugin.arm.model.TupleVO;

/**
 * <DESCRIPTION>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-11 11:04:49 +0100 (Fr, 11 Feb 2011) $<br/>
 * $Revision: 17656 $</p>
 *
 * @author Stephan Zavrel
 */
public interface AssocRuleMiningService {
    public static final String PARAM_SUPPORT_PRCT = "supportPrcnt";
    public static final String PARAM_SUPPORT_ABS = "supportMinAbs";
    public static final String PARAM_CONFIDENCE_PRCT = "confidencePrcnt";
    public static final String PARAM_METRIC = "metricType";
    public static final String PARAM_EXCL_SINGLE_ITEM_BASKETS = "excludeSingleItemBaskets";
    // Added PH: dynamic L1
    public static final String PARAM_MAX_SIZE_L1 = "maxSizeL1";
    public static final String GENERATOR_ID = "ARM";
    public static final String GENERATOR_VERSION = "0.95";

    public void archive(Integer tenantId, Integer days) throws Exception;

    public Integer getNumberOfBaskets(ARMConfigurationInt configuration);

    public Integer getNumberOfProducts(ARMConfigurationInt configuration);

    public Integer getNumberOfActions(ARMConfigurationInt configuration);

    public TObjectIntHashMap<ItemVO<Integer, Integer>> defineL1(ARMConfigurationInt configuration);

    public List<TupleVO> defineL2(TObjectIntHashMap<ItemVO<Integer, Integer>> L1, TupleCounter tupleCounter, ARMConfigurationInt configuration, ARMStatistics stats);

    public List<ItemAssocVO<Integer,Integer>> createRules(List<TupleVO> tuples,
                                                                                                TObjectIntHashMap<ItemVO<Integer, Integer>> L1,
                                                                                                ARMConfigurationInt configuration,
                                                                                                ARMStatistics stats,
                                                                                                Double minConfidence);
    
    public Collection<SortedSet<ItemAssocVO<Integer,Integer>>> createBestRules(List<TupleVO> tuples,
                                                                                                TObjectIntHashMap<ItemVO<Integer, Integer>> L1,
                                                                                                ARMConfigurationInt configuration,
                                                                                                ARMStatistics stats,
                                                                                                Double minConfidence);
    public void removeOldRules(ARMConfigurationInt configuration, ARMStatistics stats);

    public ARMConfigurationInt mapTypesToConfiguration(ARMConfiguration configuration) throws Exception;

    /**
     * Return true if ruleminer is running.
     *
     * @return
     */
    public boolean isRunning();

    /**
     * Return the integer id of the current running tenantid.
     *
     * @return
     */
    public Integer getRunningTenantId();
}
