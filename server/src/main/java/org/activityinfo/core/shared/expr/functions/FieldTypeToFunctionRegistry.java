package org.activityinfo.core.shared.expr.functions;
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
import com.google.common.collect.Maps;
import org.activityinfo.core.shared.expr.ExprFunction;
import org.activityinfo.model.type.*;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.time.LocalDateType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yuriyz on 7/25/14.
 */
public class FieldTypeToFunctionRegistry {

    private static FieldTypeToFunctionRegistry INSTANCE;

    public static FieldTypeToFunctionRegistry get() {
        if(INSTANCE == null) {
            INSTANCE = new FieldTypeToFunctionRegistry();
        }
        return INSTANCE;
    }

    private Map<FieldTypeClass, List<ExprFunction>> typeMap = Maps.newHashMap();

    private FieldTypeToFunctionRegistry() {
        typeMap.put(EnumType.TypeClass.INSTANCE, Lists.<ExprFunction>newArrayList(
                BooleanFunctions.EQUAL,
                BooleanFunctions.NOT_EQUAL
        ));
        typeMap.put(TextType.INSTANCE, Lists.<ExprFunction>newArrayList(
                BooleanFunctions.EQUAL,
                BooleanFunctions.NOT_EQUAL
        ));
        typeMap.put(NarrativeType.INSTANCE, Lists.<ExprFunction>newArrayList(
                BooleanFunctions.EQUAL,
                BooleanFunctions.NOT_EQUAL
        ));
        typeMap.put(BooleanType.INSTANCE, Lists.<ExprFunction>newArrayList(
                BooleanFunctions.EQUAL,
                BooleanFunctions.NOT_EQUAL
        ));
        typeMap.put(QuantityType.TypeClass.INSTANCE, Lists.<ExprFunction>newArrayList(
                BooleanFunctions.EQUAL,
                BooleanFunctions.NOT_EQUAL
        ));
        typeMap.put(LocalDateType.INSTANCE, Lists.<ExprFunction>newArrayList(
                BooleanFunctions.EQUAL,
                BooleanFunctions.NOT_EQUAL
        ));
    }

    public List<ExprFunction> getFunctions(FieldTypeClass typeClass) {
        List<ExprFunction> exprFunctions = typeMap.get(typeClass);
        return exprFunctions != null ? exprFunctions : Lists.<ExprFunction>newArrayList();
    }

    public Set<FieldTypeClass> getSupportedFieldTypes() {
        return typeMap.keySet();
    }
}
