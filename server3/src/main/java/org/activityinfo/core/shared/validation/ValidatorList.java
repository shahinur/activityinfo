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

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author yuriyz on 3/11/14.
 */
public class ValidatorList implements Validator {

    private final List<Validator> validators = Lists.newArrayList();

    public ValidatorList() {
    }

    public List<Validator> getValidators() {
        return validators;
    }

    @Override
    public List<ValidationFailure> validate() {
        final List<ValidationFailure> result = Lists.newArrayList();
        for (Validator validator : validators) {
            final List<ValidationFailure> failures = validator.validate();
            if (failures != null && !failures.isEmpty()) {
                result.addAll(failures);
            }
        }
        return result;
    }
}
