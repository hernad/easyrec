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
package org.easyrec.plugin.arm.model;

import org.easyrec.model.core.ItemVO;

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
public class TupleVO {
    private ItemVO<Integer, Integer> item1;
    private ItemVO<Integer, Integer> item2;
    private Integer support;

    public TupleVO(ItemVO<Integer, Integer> item1, ItemVO<Integer, Integer> item2, Integer support) {
        super();
        this.item1 = item1;
        this.item2 = item2;
        this.support = support;
    }

    public ItemVO<Integer, Integer> getItem1() {
        return item1;
    }

    public void setItem1(ItemVO<Integer, Integer> item1) {
        this.item1 = item1;
    }

    public ItemVO<Integer, Integer> getItem2() {
        return item2;
    }

    public void setItem2(ItemVO<Integer, Integer> item2) {
        this.item2 = item2;
    }

    public Integer getSupport() {
        return support;
    }

    public void setSupport(Integer support) {
        this.support = support;
    }


}
