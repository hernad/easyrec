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
package org.easyrec.service.domain.profile;

import org.easyrec.model.core.ItemVO;

/**
 * @author szavrel
 */
public interface ProfileMatcherService {

    //public float match(Integer tenantId, Integer itemId1, Integer itemTypeId1, Integer itemId2, Integer itemTypeId2);
    public float match(ItemVO<Integer, String> item1, ItemVO<Integer, String> item2);
}
