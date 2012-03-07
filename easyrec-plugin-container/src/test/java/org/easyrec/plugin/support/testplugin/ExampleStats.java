/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.plugin.support.testplugin;

import org.easyrec.plugin.stats.GeneratorStatistics;

/**
 * @author fkleedorfer
 */
public class ExampleStats extends GeneratorStatistics {
    private int someInterstingValue;

    public int getSomeInterstingValue() {
        return someInterstingValue;
    }

    public void setSomeInterstingValue(int someInterstingValue) {
        this.someInterstingValue = someInterstingValue;
    }

}
