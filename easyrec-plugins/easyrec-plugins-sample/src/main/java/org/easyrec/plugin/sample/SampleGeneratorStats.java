package org.easyrec.plugin.sample;

import org.easyrec.plugin.stats.GeneratorStatistics;

/**
 * @author fkleedorfer
 */
public class SampleGeneratorStats extends GeneratorStatistics {
    private int numberOfItems = 0;

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public void incNumberOfItems() {
        this.numberOfItems++;
    }

}
