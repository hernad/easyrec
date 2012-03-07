/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.slopeone.cli;

import org.easyrec.plugin.cli.AbstractGeneratorCLI;
import org.easyrec.plugin.slopeone.SlopeOneGenerator;
import org.easyrec.plugin.slopeone.model.SlopeOneConfiguration;
import org.easyrec.plugin.slopeone.model.SlopeOneStats;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;


/**
 * Command line tool for calling the slope one recommender <p/> <p><b>Company:&nbsp;</b> SAT, Research Studios
 * Austria</p> <p/> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p/> <p><b>last modified:</b><br/> $Author: pmarschik $<br/> $Date: 2011-06-14 15:02:31 +0200 (Di, 14 Jun 2011) $<br/>
 * $Revision: 18436 $</p>
 *
 * @author Patrick Marschik
 */
public class SlopeOneCLI extends AbstractGeneratorCLI<SlopeOneConfiguration, SlopeOneStats> {
    private SlopeOneGenerator slopeOneGenerator;

    @Override
    public String[] getConfigurations() {
        return new String[]{"spring/plugins/slopeone/SlopeOne_AllInOne.xml"};
    }

    @Override
    public SlopeOneGenerator getGenerator() {
        return slopeOneGenerator;
    }

    public void setSlopeoneGenerator(final SlopeOneGenerator slopeOneGenerator) {
        this.slopeOneGenerator = slopeOneGenerator;

        super.context.getAutowireCapableBeanFactory()
                .autowireBeanProperties(slopeOneGenerator, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
    }

    public static void main(final String[] args) {
        final SlopeOneCLI slopeOneServiceCli = new SlopeOneCLI();
        slopeOneServiceCli.processCommandLineCall(args);
    }
}
