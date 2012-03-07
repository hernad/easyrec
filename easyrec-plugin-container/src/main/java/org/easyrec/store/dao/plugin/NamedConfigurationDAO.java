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

package org.easyrec.store.dao.plugin;

import org.easyrec.model.plugin.NamedConfiguration;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.utils.spring.store.dao.TableCreatingDAO;

import java.util.List;

/**
 * @author pmarschik
 */
public interface NamedConfigurationDAO extends TableCreatingDAO {

    int createConfiguration(NamedConfiguration namedConfiguration);

    NamedConfiguration readConfiguration(int tenantId, int assocTypeId, PluginId pluginId, String name);

    List<NamedConfiguration> readConfigurations(int tenantId, int assocTypeId, PluginId pluginId);

    /**
     * Get the currently active configuration for this <tenantId, assocTypeId> tuple.
     *
     * @param tenantId First part of the unique tuple.
     * @param assocTypeId Second part of the unique tuple.
     * @return The currently active configuration.
     */
    NamedConfiguration readActiveConfiguration(int tenantId, int assocTypeId);

    /**
     * Updates a configuration with the following semantics:
     * If the {@code namedConfiguration.getName()} is different from {@code namedConfiguration.getConfiguration()
     * .getConfigurationName()} rename the configuration to {@code namedConfiguration.getConfiguration()
     * .getConfigurationName()}.
     * If {@code namedConfiguration.isActive()} is {@code false} ignore the active flag and only update
     * name/configuration.
     * If {@code namedConfiguration.isActive()} is {@code true} set all configurations with the same <tenantId,
     * assocTypeId> tuple to inactive and set {@code namedConfiguration} as active.
     *
     * @param namedConfiguration The configuration to update.
     * @return Number of rows updated.
     */
    int updateConfiguration(NamedConfiguration namedConfiguration);

    /**
     * Delete the passed configuration.
     * The configuration will not be deleted if the configuration was active, if this was the case the returned value
     * will be 0.
     *
     * @param namedConfiguration The configuration to delete.
     * @return Number of rows updated.
     */
    int deleteConfiguration(NamedConfiguration namedConfiguration);

    /**
     * Sets all plugin configurations to inactive in case the given plugin is deactivated!
     * @param pluginId
     * @return Number of rows updated.
     */
    public int deactivateByPlugin(PluginId pluginId);
    
    /**
     * Sets all plugin configurations for a given tenant and assocType to inactive
     * @param tenantId
     * @param assocTypeId
     * @return Number of rows updated.
     */
    public int deactivateByAssocType(Integer tenantId, Integer assocTypeId);

}
