package org.activityinfo.core.shared.expr.resolver;
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

import com.google.common.collect.Maps;
import org.activityinfo.core.shared.expr.PlaceholderExpr;
import org.activityinfo.core.shared.expr.PlaceholderExprResolver;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;

import java.util.Map;

/**
 * @author yuriyz on 7/24/14.
 */
public class SimpleBooleanPlaceholderExprResolver implements PlaceholderExprResolver {

    // value map contains:
    // 1. instance values
    // 2. enum type values
    // 3. to do
    private final Map<ResourceId, Object> valueMap = Maps.newHashMap();

    public SimpleBooleanPlaceholderExprResolver(FormInstance instance, FormClass formClass) {

        // 1. instance values
        valueMap.putAll(instance.getValueMap());

        // 2. type values
        for (FormField formField : formClass.getFields()) {
            FieldType type = formField.getType();
            if (type instanceof EnumType) {
                EnumType enumType = (EnumType) type;
                for (EnumValue value : enumType.getValues()) {
                    valueMap.put(value.getId(), value);
                }
            }
        }
    }

    @Override
    public void resolve(PlaceholderExpr placeholderExpr) {
        String placeholder = placeholderExpr.getPlaceholder();
        placeholderExpr.setValue(valueMap.get(ResourceId.create(placeholder)));
    }
}
