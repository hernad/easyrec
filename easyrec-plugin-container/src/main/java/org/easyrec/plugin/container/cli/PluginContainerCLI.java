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
package org.easyrec.plugin.container.cli;

//import org.easyrec.arm.AssocRuleMiningService;

import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.utils.spring.cli.AbstractDependencyInjectionSpringCLI;

/**
 * <DESCRIPTION>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: szavrel $<br/>
 * $Date: 2010-02-17 16:13:41 +0100 (Mi, 17 Feb 2010) $<br/>
 * $Revision: 15604 $</p>
 *
 * @author Stephan Zavrel
 */
public class PluginContainerCLI extends AbstractDependencyInjectionSpringCLI {

    private PluginRegistry pluginRegistry;

    public PluginContainerCLI() {
        super();
    }

    public PluginRegistry getPluginRegistry() {
        return pluginRegistry;
    }

    public void setPluginRegistry(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"spring/plugin/Plugins_AllInOne.xml"};
    }

    @Override
    protected int processCommandLineCall(String[] args) {


        return 0;
    }

    @Override
    protected void usage() {
        System.out.println("Usage: java -...AssocRuleMiningServiceCLI [OPTIONS]");
        System.out.println("Options:");
        System.out.println("   -t <TENANT>              specifiy a tenant to generate rules for;");
        System.out.println("                            by default rules for all tenants are generated");
    }

    //////////////////////////////////////////////////////////////////////////////
    // main method
    public static void main(String[] args) {
        PluginContainerCLI assocRuleMiningServiceCLI = new PluginContainerCLI();
        assocRuleMiningServiceCLI.processCommandLineCall(args);
    }

}
