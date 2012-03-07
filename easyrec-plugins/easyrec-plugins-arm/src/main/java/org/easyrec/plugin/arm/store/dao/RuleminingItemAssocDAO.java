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

package org.easyrec.plugin.arm.store.dao;

import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.store.dao.core.ItemAssocDAO;

/**
 * A extension of the ItemAssocDAO interface with just one method that allows for faster inserts by the Ruleminer.
 * Since the unique ID of the ItemAssoc is not needed in the rule mining process, the insert can be perfomed
 * without the need for a spring KeyHolder, thus the MySQL function 'ON DUPLICATE KEY UPDATE' can be used.
 *
 * @author Stephan Zavrel
 */
public interface RuleminingItemAssocDAO extends ItemAssocDAO {

    public int insertOrUpdateItemAssoc(ItemAssocVO<Integer,Integer> itemAssoc);

}
