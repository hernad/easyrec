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

package org.easyrec.plugin.mahout;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * DOCUMENT ME!
 * <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p>
 * <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class MahoutBooleanGeneratorConfigValidator implements Validator {
    @Override
    public boolean supports(final Class clazz) {
        return clazz.equals(MahoutBooleanGeneratorConfig.class);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        MahoutBooleanGeneratorConfig configuration = (MahoutBooleanGeneratorConfig) target;


        Integer userSimilarityMethod = configuration.getUserSimilarityMethod();

        Integer userNeighborhoodMethod = configuration.getUserNeighborhoodMethod();//1-2
        Double userNeighborhoodSamplingRate = configuration.getUserNeighborhoodSamplingRate(); // 0-1
        Double userNeighborhoodThreshold = configuration.getUserNeighborhoodThreshold(); //0-1
        Integer userNeighborhoodSize = configuration.getUserNeighborhoodSize(); // ! < 0
        Integer cacheDataInMemory = configuration.getCacheDataInMemory();

        if (userSimilarityMethod != null && (userSimilarityMethod < 1 || userSimilarityMethod > 4))
            errors.rejectValue("userSimilarityMethod", "error.outOfRange",
                    "userSimilarityMethod must be a value between 1-4. Read the help text of userSimilarityMethod for more information.");

        if (userNeighborhoodMethod != null && (userNeighborhoodMethod < 1 || userNeighborhoodMethod > 2))
            errors.rejectValue("userNeighborhoodMethod", "error.outOfRange",
                    "userNeighborhoodMethod must be either 1 or 2. Read the help text of userNeighborhoodMethod for more information.");

        if (userNeighborhoodSamplingRate != null && (userNeighborhoodSamplingRate < 0 || userNeighborhoodSamplingRate > 1))
            errors.rejectValue("userNeighborhoodSamplingRate", "error.outOfRange",
                    "userNeighborhoodSamplingRate must between 0 and 1. (0% to 100%) for example: 0.90 == 90% ");

        if (userNeighborhoodThreshold != null && (userNeighborhoodThreshold < 0 || userNeighborhoodThreshold > 1))
            errors.rejectValue("userNeighborhoodThreshold", "error.outOfRange",
                    "userNeighborhoodThreshold must between 0 and 1. (0% to 100%) for example: 0.60 == 60% ");

        if (userNeighborhoodSize != null && (userNeighborhoodSize < 0))
            errors.rejectValue("userNeighborhoodSize", "error.outOfRange",
                    "userNeighborhoodSize must be greater then 0.");

        if (userNeighborhoodSize != null && (cacheDataInMemory != 0 && cacheDataInMemory != 1 ))
            errors.rejectValue("cacheDataInMemory", "error.outOfRange",
                    "cacheDataInMemory must be 1(true) or 0(false) ");




    }
}
