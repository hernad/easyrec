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
package org.easyrec.model.web.statistics;

import java.io.Serializable;

/**
 * @author phlavac
 */
public class ConversionStatistic implements Serializable {

    private static final long serialVersionUID = -4946081075754796702L;

    private Integer recommendationToBuyCount;

    public ConversionStatistic(Integer recommendationToBuyCount) {
        this.recommendationToBuyCount = recommendationToBuyCount;
    }


    public Integer getRecommendationToBuyCount() {
        return recommendationToBuyCount;
    }

    public void setRecommendationToBuyCount(Integer recommendationToBuyCount) {
        this.recommendationToBuyCount = recommendationToBuyCount;
    }

}
   