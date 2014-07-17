package org.activityinfo.core.shared.validation;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.gwt.user.client.ui.HasValue;
import org.activityinfo.core.shared.validation.widget.NotEmptyValidator;

/**
 * @author yuriyz on 3/11/14.
 */
public class ValidatorBuilder {

    private final ValidatorList validators = new ValidatorList();

    private ValidatorBuilder() {
    }

    public static ValidatorBuilder instance() {
        return new ValidatorBuilder();
    }

    public ValidatorBuilder addNotEmpty(HasValue control, String controlName) {
        addValidator(new NotEmptyValidator(control, controlName));
        return this;
    }

    public ValidatorBuilder addValidator(Validator validator) {
        if (validator != null) {
            validators.getValidators().add(validator);
        }
        return this;
    }

    public Validator build() {
        return validators;
    }
}
