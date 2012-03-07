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

/**
 *
 */
package org.easyrec.plugin.configuration.testconfig;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class SameValuesValidator implements Validator {
    public SameValuesValidator() {

    }

    public boolean supports(Class<?> clazz) {
        return clazz.equals(SameValuesTestConfiguration.class);
    }

    public void validate(Object target, Errors errors) {
        TestConfiguration myTarget = (TestConfiguration) target;
        if (myTarget.getDoubleObjectField() == null) {
            if (myTarget.getDoublePrimitiveField() != 0.0d) {
                errors.rejectValue("doublePrimitiveField", "error.sameValue",
                        "if the double object field is null, the double primitive field must be 0.0");
            }
        } else if (myTarget.getDoubleObjectField() != myTarget.getDoublePrimitiveField()) {
            errors.reject("error.sameValue", "double object and privmitive double don't have the same value");
        }
    }
}