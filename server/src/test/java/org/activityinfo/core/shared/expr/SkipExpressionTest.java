package org.activityinfo.core.shared.expr;
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

import org.activityinfo.core.shared.expr.resolver.SimpleBooleanPlaceholderExprResolver;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author yuriyz on 7/24/14.
 */

public class SkipExpressionTest {

    private static final ResourceId GENDER_FIELD_ID = ResourceId.generateId();
    private static final ResourceId PREGNANT_FIELD_ID = ResourceId.generateId();

    FormClass formClass;

    @Before
    public void setUser() {
        formClass = createFormClass();
    }

    @Test
    public void enumType() {
        FormInstance instance = new FormInstance(ResourceId.generateId(), formClass.getId());
        instance.set(GENDER_FIELD_ID, enumValue(GENDER_FIELD_ID, "Male"));

        eval(String.format("{%s}=={%s}", GENDER_FIELD_ID.asString(), enumValue(GENDER_FIELD_ID, "Male").getId()), true, instance);
        eval(String.format("{%s}!={%s}", GENDER_FIELD_ID.asString(), enumValue(GENDER_FIELD_ID, "Male").getId()), false, instance);
        eval(String.format("{%s}=={%s}", GENDER_FIELD_ID.asString(), enumValue(GENDER_FIELD_ID, "Female").getId()), false, instance);
    }

    private void eval(String skipExpresison, Boolean expectedValue, FormInstance instance) {
        ExprLexer lexer = new ExprLexer(skipExpresison);
        ExprParser parser = new ExprParser(lexer, new SimpleBooleanPlaceholderExprResolver(instance, formClass));
        ExprNode<Boolean> expr = parser.parse();
        Assert.assertEquals(skipExpresison, expectedValue, expr.evalReal());
    }

    private EnumValue enumValue(ResourceId formField, String label) {
        EnumType enumType = (EnumType) formClass.getField(formField).getType();
        for (EnumValue value : enumType.getValues()) {
            if (value.getLabel().equalsIgnoreCase(label)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unable to find enumValue with label: " + label);
    }


    private static FormClass createFormClass() {
        EnumValue male = new EnumValue(ResourceId.generateId(), "Male");
        EnumValue female = new EnumValue(ResourceId.generateId(), "Female");

        EnumValue pregnantYes = new EnumValue(ResourceId.generateId(), "Yes");
        EnumValue pregnantNo = new EnumValue(ResourceId.generateId(), "No");

        FormField genderField = new FormField(GENDER_FIELD_ID);
        genderField.setLabel("Gender");
        genderField.setType(new EnumType(Cardinality.SINGLE, Arrays.asList(male, female)));

        FormField pregnantField = new FormField(PREGNANT_FIELD_ID);
        pregnantField.setLabel("are you currently pregnant?");
        pregnantField.setType(new EnumType(Cardinality.SINGLE, Arrays.asList(pregnantYes, pregnantNo)));

        final FormClass formClass = new FormClass(CuidAdapter.activityFormClass(1));
        formClass.addElement(genderField);
        formClass.addElement(pregnantField);
        return formClass;
    }
}
