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

package org.easyrec.plugin.pearson;

/**
 * <p>
 * Interface for a generator that produces predictions based on the pearson algorithm as described in [Breese et al,
 * 1998].
 * <p/>
 * [Breese et al, 1998] J. S. Breese, D. Heckerman, and C. Kadie. Empirical analysis of predictive algorithms for
 * collaborative filtering. In UAI ’98: Proceedings of the Fourteenth Conference on Uncertainty in Artificial
 * Intelligence, pages 43–52. Morgan Kaufmann, 1998.
 * </p>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2009
 * </p>
 * <p/>
 * <p>
 * <b>last modified:</b><br/>
 * $Author$<br/>
 * $Date$<br/>
 * $Revision$
 * </p>
 *
 * @author Patrick Marschik
 */
public interface PearsonService {
    /**
     * @param tenantId
     */
    void perform(Integer tenantId);
}
