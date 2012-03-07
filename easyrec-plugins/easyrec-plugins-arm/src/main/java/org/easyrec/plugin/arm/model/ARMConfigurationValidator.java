/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.plugin.arm.model;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author szavrel
 */
public class ARMConfigurationValidator implements Validator {

    public boolean supports(Class<?> type) {
        return type.equals(ARMConfiguration.class);
    }

    public void validate(Object target, Errors errors) {
        ARMConfiguration configuration = (ARMConfiguration) target;

        if (configuration.getConfidencePrcnt() != null && configuration.getConfidencePrcnt() < 0.0 && configuration.getConfidencePrcnt() >100.0) {
            errors.rejectValue("confidencePrcnt", "error.outOfRange", "The confidence must be a valid percentage, thus between 0.0 and 100.0!");
        }

        if (configuration.getSupportPrcnt() != null && configuration.getSupportPrcnt() < 0.0 && configuration.getSupportPrcnt() > 100.0) {
            errors.rejectValue("supportPrcnt", "error.outOfRange", "The support percantage must be between 0.0 and 100.0!");
        }

        if (configuration.getSupportMinAbs() != null && configuration.getSupportMinAbs() < 1) {
            errors.rejectValue("supportMinAbs", "error.outOfRange", "The minimum absolute support must be a value greater than 1!");
        }

        if (configuration.getMaxRulesPerItem() != null && configuration.getMaxRulesPerItem() < 1) {
            errors.rejectValue("maxRulesPerItem", "error.outOfRange", "Valid values for maximum rules per item must be greater than 1!");
        }

    }

}
