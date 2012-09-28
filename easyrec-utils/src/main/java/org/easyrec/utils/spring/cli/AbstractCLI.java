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
package org.easyrec.utils.spring.cli;

/**
 * Base class for command line interfaces.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2006</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Florian Kleedorfer
 */

public abstract class AbstractCLI {
    public AbstractCLI() {
        super();
    }

    //////////////////////////////////////////////////////////////////////////////
    // abstract methods

    /**
     * override to output usage message
     */
    protected abstract void usage();

    /**
     * process the command line call here
     *
     * @param args
     * @return
     */
    protected abstract int processCommandLineCall(String[] args);

    //////////////////////////////////////////////////////////////////////////////
    // protected methods

    /**
     * outputs the usage message using <code>usage()</code>
     * and exits with exit status 1
     */
    protected void abort() {
        usage();
        System.exit(1);
    }

    /**
     * outputs the usage message using <code>usage()</code>
     * and the given <code>errorReason</code> message and finally exits with exit status 1
     */
    protected void abort(String errorReason) {
        usage(errorReason);
        System.exit(1);
    }

    /**
     * outputs the usage message and the given <code>errorReason</code> message
     */
    protected void usage(String errorReason) {
        usage();

        if (errorReason != null) {
            System.out.println("\n---> " + errorReason);
        }
    }
}
