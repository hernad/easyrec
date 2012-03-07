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

package org.easyrec.plugin.pearson.cli;

import org.easyrec.plugin.pearson.PearsonService;
import org.easyrec.utils.spring.cli.AbstractDependencyInjectionSpringCLI;

public class PearsonServiceCLI extends AbstractDependencyInjectionSpringCLI {
    public static void main(final String[] args) {
        final PearsonServiceCLI pearsonServiceCli = new PearsonServiceCLI();
        pearsonServiceCli.processCommandLineCall(args);
    }

    private PearsonService pearsonService;

    public PearsonService getPearsonService() {
        return pearsonService;
    }

    public void setPearsonService(final PearsonService pearsonService) {
        this.pearsonService = pearsonService;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"spring/content/pearson/AllInOne_Pearson.xml"};
    }

    @SuppressWarnings("deprecation")
    @Override
    protected int processCommandLineCall(final String[] args) {
        if (args.length == 0) pearsonService.perform((Integer) null);
        else if (args[0].equals("-t") && args[1] != null) try {
            final int tenantId = Integer.parseInt(args[1]);
            pearsonService.perform(tenantId);
        } catch (final Exception e) {
            usage();
            return -2;
        }
        else {
            usage();
            return -1;
        }
        return 0;
    }

    @Override
    protected void usage() {
        System.out.println("Usage: java -...PearsonServiceCLI [OPTIONS]");
        System.out.println("Options:");
        System.out.println("   -t <TENANT>              specifiy a tenant to generate rules for;");
        System.out.println("                            by default rules for all tenants are generated");
    }
}
