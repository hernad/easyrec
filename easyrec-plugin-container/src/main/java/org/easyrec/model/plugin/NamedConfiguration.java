/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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

import com.google.common.base.Objects;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.model.PluginId;

/**
 * @author pmarschik
 */
public class NamedConfiguration {

    private int id;
    private int tenantId;
    private int assocTypeId;
    private PluginId pluginId;
    private String name;
    private GeneratorConfiguration configuration;
    private boolean active;

    public NamedConfiguration(int id, int tenantId, int assocTypeId, PluginId pluginId, String name,
                              GeneratorConfiguration configuration, boolean active) {
        this.id = id;
        this.tenantId = tenantId;
        this.assocTypeId = assocTypeId;
        this.pluginId = pluginId;
        this.name = name;
        this.configuration = configuration;
        this.active = active;
    }

    public NamedConfiguration(int tenantId, int assocTypeId, PluginId pluginId, String name,GeneratorConfiguration configuration, boolean active) {
        this(-1, tenantId, assocTypeId, pluginId, name, configuration, active);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public int getAssocTypeId() {
        return assocTypeId;
    }

    public void setAssocTypeId(int assocTypeId) {
        this.assocTypeId = assocTypeId;
    }

    public PluginId getPluginId() {
        return pluginId;
    }

    public void setPluginId(PluginId pluginId) {
        this.pluginId = pluginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeneratorConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(GeneratorConfiguration configuration) {
        this.configuration = configuration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamedConfiguration that = (NamedConfiguration) o;

        if (active != that.active) return false;
        if (assocTypeId != that.assocTypeId) return false;
        if (tenantId != that.tenantId) return false;
        if (!configuration.equals(that.configuration)) return false;
        if (!name.equals(that.name)) return false;
        if (!pluginId.equals(that.pluginId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(assocTypeId, pluginId, name, configuration, active);
    }
}
