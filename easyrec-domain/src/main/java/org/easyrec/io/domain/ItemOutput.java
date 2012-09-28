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
package org.easyrec.io.domain;

import org.easyrec.model.core.ItemVO;
import org.easyrec.store.thirdparty.ThirdPartyAccess;

import java.util.Collection;

/**
 * Simple utility methods for the output of an {@link at.researchstudio.sat.recommender.model.core.ItemVO}.
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
public class ItemOutput {
    public static String retrieveItemsOutputAsString(Collection<ItemVO<Integer, String>> items, int maxResult,
                                                     ThirdPartyAccess thirdPartyAccess, String comment)
            throws Exception {
        int count = 0;
        StringBuilder out = new StringBuilder();

        if (comment != null) {
            out.append(comment);
        }
        for (ItemVO<Integer, String> currentItem : items) {
            if (count >= maxResult) {
                break;
            }
            out.append(thirdPartyAccess.getItemRepresentation(currentItem));
            out.append(" : ");
            out.append(currentItem.toString());
            out.append("\n");
            count++;
        }
        return out.toString();
    }

    public static String retrieveItemOutputAsString(ItemVO<Integer, String> item,
                                                    ThirdPartyAccess thirdPartyAccess, String comment)
            throws Exception {
        StringBuilder out = new StringBuilder();
        if (comment != null) {
            out.append(comment);
        }
        out.append(thirdPartyAccess.getItemRepresentation(item));
        out.append("\n");
        return out.toString();
    }
}
