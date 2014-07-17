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

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Validator with predicate. If predicate returns false then no failures are returned.
 *
 * @author yuriyz on 3/13/14.
 */
public class ValidatorWithPredicate implements Validator {

    private final Validator validator;
    private final SimplePredicate predicate;

    public ValidatorWithPredicate(@Nonnull Validator validator, @Nonnull SimplePredicate predicate) {
        this.validator = validator;
        this.predicate = predicate;
    }

    @Override
    public List<ValidationFailure> validate() {
        if (predicate.apply()) {
            return validator.validate();
        }
        return Lists.newArrayList();
    }
}
