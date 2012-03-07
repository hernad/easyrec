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
package org.easyrec.service.core.impl;

import org.easyrec.service.core.ItemAssocService;
import org.easyrec.utils.io.autoimport.AutoImportCommand;

/**
 * Automatic import command for item associations.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 16:46:14 +0200 (Fr, 12 Aug 2011) $<br/>
 * $Revision: 113 $</p>
 *
 * @author Roman Cerny
 */
public class ItemAssocAICommandImpl implements AutoImportCommand {
    // members
    private ItemAssocService itemAssocService = null;

    // interface 'AutoImportCommand' implementation
    public ItemAssocAICommandImpl(ItemAssocService itemAssocService) {
        this.itemAssocService = itemAssocService;
    }

    public void execute(String fileName) throws Exception {
        if (itemAssocService == null) {
            throw new IllegalStateException("'itemAssocService' not set'");
        }
        itemAssocService.importItemAssocsFromCSV(fileName);
    }
}
