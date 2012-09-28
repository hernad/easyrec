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
package org.easyrec.utils.spring.profile;

/**
 * <p>
 * Provides functionality for timing of java code.
 * </p>
 * Example of usage:
 * <pre>
 *  Stopwatch stw = new Stopwatch();
 *  stw.start();
 *  //do some stuff
 *  long timePassed = stw.timePassed();
 *  stw.restart();
 *  //do some other stuff
 *  long timePassed2 = stw.stop();
 * </pre>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2005</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Florian Kleedorfer
 */

public class Stopwatch {
    private long start = 0;
    private boolean running = false;

    public Stopwatch() {

    }

    public void start() {
        if (running) {
            throw new IllegalStateException("stopwatch is already running!");
        }
        running = true;
        start = System.currentTimeMillis();
    }

    public void restart() {
        if (!running) {
            throw new IllegalStateException("stopwatch is not running!");
        }
        start = System.currentTimeMillis();
    }

    public long timePassed() {
        if (!running) {
            throw new IllegalStateException("stopwatch is not running!");
        }
        return System.currentTimeMillis() - start;
    }

    public long stop() {
        if (!running) {
            throw new IllegalStateException("stopwatch is not running!");
        }
        running = false;
        return System.currentTimeMillis() - start;
    }
}
