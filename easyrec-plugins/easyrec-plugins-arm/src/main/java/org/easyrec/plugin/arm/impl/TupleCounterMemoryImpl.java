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
package org.easyrec.plugin.arm.impl;

import java.util.ArrayList;
import org.easyrec.model.core.ItemVO;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import org.easyrec.plugin.arm.TupleCounter;
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
public class TupleCounterMemoryImpl implements TupleCounter {
    // NOTE: integrate TupleInfo, consider Double val in second method!
    private HashMap<ItemVO<Integer, Integer>, HashMap<ItemVO<Integer, Integer>, Integer>> map;
    int counter;

    public TupleCounterMemoryImpl() {
        map = new HashMap<ItemVO<Integer, Integer>, HashMap<ItemVO<Integer, Integer>, Integer>>();
        counter = 0;
    }

    public void init() {
        map.clear();
        counter = 0;
    }

    public void count(ItemVO<Integer, Integer> item1, ItemVO<Integer, Integer> item2)
            throws Exception {
        ItemVO<Integer, Integer> mainKey, subKey;
        HashMap<ItemVO<Integer, Integer>, Integer> set;

        if (item1.getItem() < item2.getItem()) {
            mainKey = item1;
            subKey = item2;
        } else {
            mainKey = item2;
            subKey = item1;
        }

        set = map.get(mainKey);
        if (set == null) {
            set = new HashMap<ItemVO<Integer, Integer>, Integer>();
            map.put(mainKey, set);
        }

        Integer cnt = set.get(subKey);
        if (cnt == null) {
            counter++;
            cnt = 1;
        } else {
            cnt++;
        }
        set.put(subKey, cnt);
    }

    public ArrayList<TupleVO> getTuples(int support) throws Exception {
        ArrayList<TupleVO> ret = new ArrayList<TupleVO>();
        Set<ItemVO<Integer, Integer>> mainKeys = map.keySet();

        for (ItemVO<Integer, Integer> itemVO : mainKeys) {
            Set<Entry<ItemVO<Integer, Integer>, Integer>> entries = map.get(itemVO).entrySet();
            for (Entry<ItemVO<Integer, Integer>, Integer> entry : entries) {
                if (entry.getValue() >= support) {
                    ret.add(new TupleVO(itemVO, entry.getKey(), entry.getValue()));
                }
            }
        }
        return ret;
    }

    public int size() throws Exception {
        return counter;
    }

}
