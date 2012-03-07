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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.plugin.waiting;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author fkleedorfer
 */
public class WaitingGeneratorConfigurationValidator implements Validator {

    public boolean supports(Class clazz) {
        return WaitingGeneratorConfiguration.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        if (((WaitingGeneratorConfiguration) target).getNumberOfPhases() <= 0) {
            errors.rejectValue("numberOfPhases", "waiting.phases.invalid",
                    "Invalid input for the number of phases specified. Must be an int greater than zero.");
        }
        if (((WaitingGeneratorConfiguration) target).getTimeout() <= 0) {
            errors.rejectValue("timeout", "waiting.timeout.invalid",
                    "Invalid input for timeout specified. Must be a long greater than zero.");
        }
    }


}
