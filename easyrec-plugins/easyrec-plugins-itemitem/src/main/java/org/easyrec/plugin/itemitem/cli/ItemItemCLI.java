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

package org.easyrec.plugin.itemitem.cli;

import org.easyrec.plugin.cli.AbstractGeneratorCLI;
import org.easyrec.plugin.itemitem.ItemItemGenerator;
import org.easyrec.plugin.itemitem.model.ItemItemConfiguration;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Command line interface for {@link org.easyrec.plugin.itemitem.ItemItemGenerator} <p><b>Company:&nbsp;</b> SAT, Research
 * Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/>
 * $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class ItemItemCLI extends AbstractGeneratorCLI<ItemItemConfiguration, GeneratorStatistics> {
    // ------------------------------ FIELDS ------------------------------

    private ItemItemGenerator itemItemGenerator;

    // -------------------------- OTHER METHODS --------------------------

    @Override
    public String[] getConfigurations() {
        return new String[]{"spring/plugins/itemitem/ItemItem_AllInOne.xml"};
    }

    @Override
    public ItemItemGenerator getGenerator() {
        return itemItemGenerator;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setItemItemGenerator(final ItemItemGenerator itemItemGenerator) {
        this.itemItemGenerator = itemItemGenerator;

        super.context.getAutowireCapableBeanFactory()
                .autowireBeanProperties(itemItemGenerator, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
    }

    // --------------------------- main() method ---------------------------

    public static void main(final String[] args) {
        final ItemItemCLI itemItemCLI = new ItemItemCLI();
        itemItemCLI.processCommandLineCall(args);
    }
}
