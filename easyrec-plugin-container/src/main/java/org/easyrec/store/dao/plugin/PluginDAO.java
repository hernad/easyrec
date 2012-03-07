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
package org.easyrec.store.dao.plugin;

import org.easyrec.model.plugin.PluginVO;
import org.easyrec.plugin.model.Version;

import java.net.URI;
import java.util.List;

/**
 * @author szavrel
 */
public interface PluginDAO {

    public static final String DEFAULT_TABLE_NAME = "plugin";

    public static final String DEFAULT_ID_COLUMN_NAME = "id";
    public static final String DEFAULT_DISPLAYNAME_COLUMN_NAME = "displayname";
    public static final String DEFAULT_PLUGINID_COLUMN_NAME = "pluginid";
    public static final String DEFAULT_VERSION_COLUMN_NAME = "version";
    public static final String DEFAULT_STATE_COLUMN_NAME = "state";
    public static final String DEFAULT_FILE_COLUMN_NAME = "file";
    public static final String DEFAULT_CHANGEDATE_COLUMN_NAME = "changeDate";
    public static final String DEFAULT_ORIG_FILENAME_COLUMN_NAME = "origfilename";

    public void storePlugin(PluginVO plugin);

    public void deletePlugin(URI pluginId, Version version);

    public PluginVO loadPlugin(URI pluginId, Version version);
    //    public PluginVO loadPlugin(Integer id);

    public void updatePluginState(URI pluginId, Version version, String state);

    public List<PluginVO> loadPlugins();

    public List<PluginVO> loadPlugins(String state);

    public List<PluginVO> loadPluginInfos();

    public List<PluginVO> loadPluginInfos(String state);
}
