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
package org.easyrec.model.web;

import java.util.ArrayList;
import java.util.List;

/**
 * @author phlavac
 */
public class Queue {

    private List<RemoteTenant> queue = null;

    public Queue() {
        queue = new ArrayList<RemoteTenant>();
    }

    public void add(RemoteTenant r) {
        queue.add(r);
    }

    public RemoteTenant poll() {
        if (!queue.isEmpty()) {
            return queue.remove(0);
        }
        return null;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}