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
package org.easyrec.store.dao.web;

import org.easyrec.model.web.Operator;

/**
 * This class is used for checking the avaibility of the database server,
 * and creating the db itself.
 *
 * @author szavrel
 */
public interface LoaderDAO {

    public static final Float INITIAL_VERSION = new Float(0.9);

    public void testConnection(String url, String username, String password) throws Exception;

    public void createDB() throws Exception;

    public void migrateDB() throws Exception;

    public void reloadContext();

    public void reloadFrontend();

    public void reloadBackend();

    public Operator addOperator(String id, String password, String firstName, String lastName, String email,
                                String phone, String company, String address, String apiKey, String ip);

    /**
     * This function returns the current version of easyrec,
     * depending on the presence of a version table
     *
     * @return
     */
    public Float checkVersion() throws Exception;

}
