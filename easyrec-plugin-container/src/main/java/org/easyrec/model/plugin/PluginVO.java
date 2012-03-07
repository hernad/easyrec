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
package org.easyrec.model.plugin;

import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.model.Version;

import java.net.URI;
import java.util.Date;

/**
 * @author szavrel
 */
public class PluginVO {

    private Integer id;
    private String displayName;
    private PluginId pluginId;
    private String state;
    private byte[] file;
    private Date changeDate;
    private String origFilename;

    public PluginVO(String displayName, URI pluginId, Version version, String state, byte[] file, String origFileame) {
        this.displayName = displayName;
        this.pluginId = new PluginId(pluginId, version);
        this.state = state;
        this.file = file;
        this.origFilename = origFileame;
    }

    public PluginVO(Integer id, String displayName, URI pluginId, Version version, String state, byte[] file,
                    Date changeDate, String origFilename) {
        this.id = id;
        this.displayName = displayName;
        this.pluginId = new PluginId(pluginId, version);
        this.state = state;
        this.file = file;
        this.changeDate = changeDate;
        this.origFilename = origFilename;
    }


    public Date getChangeDate() {
        return changeDate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public byte[] getFile() {
        return file;
    }

    public Integer getId() {
        return id;
    }

    public PluginId getPluginId() {
        return pluginId;
    }

    public String getState() {
        return state;
    }

    public String getOrigFilename() {
        return origFilename;
    }

    public void setOrigFilename(String origFilename) {
        this.origFilename = origFilename;
    }

}
