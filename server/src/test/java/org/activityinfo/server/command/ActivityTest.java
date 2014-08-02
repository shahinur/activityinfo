package org.activityinfo.server.command;

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

import com.google.common.base.Joiner;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.legacy.shared.command.BatchCommand;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.command.UpdateEntity;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.GetResource;
import org.activityinfo.legacy.shared.model.Published;
import org.activityinfo.legacy.shared.model.PutResource;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormElement;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.server.database.OnDataSet;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/schema1.db.xml")
public class ActivityTest extends CommandTestCase2 {


    public static final ResourceId NFI_FORM_ID = CuidAdapter.activityFormClass(1);

    @Before
    public void setUser() {
        setUser(1);
    }

    @Test
    public void testActivity() throws CommandException {


        /*
         * Create a new form
         */

        FormClass act = new FormClass(ResourceId.generateId());
        act.setOwnerId(CuidAdapter.databaseId(1));
        act.setLabel("Warshing the dishes");

        execute(new PutResource(act));

        /*
         * Reload schema to verify the changes have stuck
         */

        act = FormClass.fromResource(execute(new GetResource(act.getId())).parseResource());
        assertThat(act.getLabel(), equalTo("Warshing the dishes"));
//
//        assertEquals("public", Published.NOT_PUBLISHED.getIndex(), act.getPublished());
    }

    @Test
    public void updateSortOrderTest() throws Throwable {

        /* Update Sort Order */
        Map<String, Object> changes1 = new HashMap<String, Object>();
        changes1.put("sortOrder", 2);
        Map<String, Object> changes2 = new HashMap<String, Object>();
        changes2.put("sortOrder", 1);

        execute(new BatchCommand(
                new UpdateEntity("Activity", 1, changes1),
                new UpdateEntity("Activity", 2, changes2)));

        /* Confirm the order is changed */

        SchemaDTO schema = execute(new GetSchema());
        assertEquals(2, schema.getDatabaseById(1).getActivities().get(0).getId());
        assertEquals(1, schema.getDatabaseById(1).getActivities().get(1).getId());
    }

    @Test
    public void updatePublished() throws Throwable {

        /* Update Sort Order */
        Map<String, Object> changes = new HashMap<String, Object>();
        changes.put("published", Published.ALL_ARE_PUBLISHED.getIndex());

        execute(new UpdateEntity("Activity", 1, changes));

        /* Confirm the order is changed */

        SchemaDTO schema = execute(new GetSchema());
        assertEquals(Published.ALL_ARE_PUBLISHED.getIndex(), schema.getActivityById(1).getPublished());
    }

    @Test
    public void orderFields() {

        ResourceId classId = ResourceId.generateId();
        FormClass formClass = new FormClass(classId);
        formClass.setOwnerId(CuidAdapter.databaseId(1));
        formClass.setLabel("Household Survey");

        execute(new PutResource(formClass));

        ResourceLocatorAdaptor resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        formClass = assertResolves(resourceLocator.getFormClass(classId));

        // create three new fields with an order that mixes "attributes" and "indicators"

        FormField newField = new FormField(ResourceId.generateId());
        newField.setLabel("How old are you?");
        newField.setType(new QuantityType().setUnits("years"));
        formClass.addElement(newField);

        FormField newGenderField = new FormField(ResourceId.generateId());
        newGenderField.setLabel("Gender");
        EnumValue male = new EnumValue(ResourceId.generateId(), "Male");
        EnumValue female = new EnumValue(ResourceId.generateId(), "Female");
        newGenderField.setType(new EnumType(Cardinality.SINGLE, Arrays.asList(male, female)));
        formClass.addElement(newGenderField);

        FormField newTextField = new FormField(ResourceId.generateId());
        newTextField.setLabel("What is your name?");
        newTextField.setType(TextType.INSTANCE);
        formClass.addElement(newTextField);

        resourceLocator.persist(formClass);

        FormClass reform = assertResolves(resourceLocator.getFormClass(formClass.getId()));

        System.out.println(Joiner.on("\n").join(reform.getFields()));

        int a = indexOf(reform.getFields(), hasProperty("label", equalTo("How old are you?")));
        int b = indexOf(reform.getFields(), hasProperty("label", equalTo("Gender")));
        int c = indexOf(reform.getFields(), hasProperty("label", equalTo("What is your name?")));

        assertTrue(a < b && b < c);

    }

    @Test
    public void createForm() {

        ResourceId classId = ResourceId.generateId();
        FormClass formClass = new FormClass(classId);
        formClass.setOwnerId(CuidAdapter.databaseId(1));
        formClass.setLabel("Household Survey");

        execute(new PutResource(formClass));

        ResourceLocatorAdaptor resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        formClass = assertResolves(resourceLocator.getFormClass(classId));

        FormField newField = new FormField(ResourceId.generateId());
        newField.setLabel("How old are you?");
        newField.setType(new QuantityType().setUnits("years"));
        formClass.addElement(newField);

        FormField newTextField = new FormField(ResourceId.generateId());
        newTextField.setLabel("What is your name?");
        newTextField.setType(TextType.INSTANCE);
        formClass.addElement(newTextField);

        assertResolves(resourceLocator.persist(formClass));
        FormClass reform = assertResolves(resourceLocator.getFormClass(formClass.getId()));
        assertHasFieldWithLabel(reform, "How old are you?");

        newField.setLabel("How old are you today?");
        // save again
        assertResolves(resourceLocator.persist(formClass));

        reform = assertResolves(resourceLocator.getFormClass(formClass.getId()));
        assertHasFieldWithLabel(reform, "How old are you today?");
        System.out.println(reform.getFields().toString());
        assertThat(reform.getFields(), hasSize(2));

    }

    @Test
    public void createEnumField() {

        ResourceLocatorAdaptor resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        FormClass formClass = assertResolves(resourceLocator.getFormClass(NFI_FORM_ID));

        FormField newField = new FormField(ResourceId.generateId());
        newField.setLabel("New Group");
        EnumValue yes = new EnumValue(ResourceId.generateId(), "Yes");
        EnumValue no = new EnumValue(ResourceId.generateId(), "No");
        newField.setType(new EnumType(Cardinality.SINGLE, Arrays.asList(yes, no)));

        formClass.getElements().add(newField);

        resourceLocator.persist(formClass);

        // verify that it appears as attribute group
        formClass = assertResolves(resourceLocator.getFormClass(formClass.getId()));
        FormField enumField = findField(formClass, "New Group");
        EnumType type = (EnumType) enumField.getType();
        assertThat(type.getCardinality(), equalTo(Cardinality.SINGLE));
        assertThat(type.getValues(), hasSize(2));
        assertThat(type.getValues().get(0), hasProperty("label", Matchers.equalTo("Yes")));
        assertThat(type.getValues().get(1), hasProperty("label", Matchers.equalTo("No")));

        // Now update the same attribute group and a value
        enumField.setLabel("Do you like ice cream?");
        type.getValues().get(0).setLabel("Oui");
        type.getValues().get(1).setLabel("Non");
        resourceLocator.persist(formClass);

        formClass = getForm(NFI_FORM_ID);
        enumField = findField(formClass, "Do you like ice cream?");
        type = (EnumType) enumField.getType();
        assertThat(type.getCardinality(), equalTo(Cardinality.SINGLE));
        assertThat(type.getValues(),
                contains(hasProperty("label", Matchers.equalTo("Oui")),
                         hasProperty("label", Matchers.equalTo("Non"))));

        // Remove one of our new enum values
        enumField.setType(new EnumType(Cardinality.SINGLE, Arrays.asList(yes)));
        resourceLocator.persist(formClass);

        enumField = findField(getForm(formClass.getId()), "Do you like ice cream?");
        type = (EnumType) enumField.getType();

        assertThat(type.getCardinality(), equalTo(Cardinality.SINGLE));
        assertThat(type.getValues(), contains(hasProperty("label", Matchers.equalTo("Yes"))));
    }

    @Test
    public void updateField() {

        ResourceLocatorAdaptor resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        FormClass formClass = assertResolves(resourceLocator.getFormClass(NFI_FORM_ID));

        FormField beneficiaries = find(formClass.getFields(), hasProperty("label", equalTo("beneficiaries")));
        beneficiaries.setLabel("Number of benes");
        resourceLocator.persist(formClass);

        FormClass form = getForm(NFI_FORM_ID);
        assertThat(findField(form, "Number of benes"), hasProperty("label", Matchers.equalTo("Number of benes")));
    }


    @Test
    public void updateIndicatorWithLongUnits() {

        ResourceLocatorAdaptor resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        FormClass formClass = assertResolves(resourceLocator.getFormClass(NFI_FORM_ID));

        FormField beneficiaries = find(formClass.getFields(), hasProperty("label", equalTo("beneficiaries")));
        QuantityType updatedType = new QuantityType().setUnits("imperial tonne with very long qualifying text");
        beneficiaries.setType(updatedType);
        resourceLocator.persist(formClass);

        FormClass activity = getForm(NFI_FORM_ID);
        assertThat(findField(activity, "beneficiaries").getType(), hasProperty("units", Matchers.equalTo(updatedType.getUnits())));
    }

    @Test
    public void deleteEnumFields() {

        ResourceLocatorAdaptor resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        FormClass formClass = assertResolves(resourceLocator.getFormClass(NFI_FORM_ID));

        // Remove attribute
        ListIterator<FormElement> it = formClass.getElements().listIterator();
        while(it.hasNext()) {
            FormElement element = it.next();
            if(element.getLabel().equals("Cause")) {
                it.remove();
            }
        }
        resourceLocator.persist(formClass);

        // Ensure deleted
        formClass = assertResolves(resourceLocator.getFormClass(NFI_FORM_ID));
        assertThat(formClass.getFields(), not(contains(hasProperty("label", Matchers.equalTo("Cause")))));
    }


    private <T> T find(List<T> list, Matcher<? super T> matcher) {

        assertThat(list, hasItem(matcher));

        for(T t : list) {
            if(matcher.matches(t)) {
                return t;
            }
        }
        throw new AssertionError();
    }



    private <T> int indexOf(List<T> list, Matcher<? super T> matcher) {

        assertThat(list, hasItem(matcher));

        for(int i=0;i!=list.size();++i) {
            if(matcher.matches(list.get(i))) {
                return i;
            }
        }
        throw new AssertionError();
    }



    private FormClass getForm(ResourceId id) {
        return FormClass.fromResource(execute(new GetResource(id)).parseResource());
    }

    private FormField findField(FormClass form, String label) {
        for(FormField field : form.getFields()) {
            if(field.getLabel().equals(label)) {
                return field;
            }
        }
        throw new AssertionError("No such field: " + label);
    }


    private static void assertHasFieldWithLabel(FormClass formClass, String label) {
        for (FormField field : formClass.getFields()) {
            if (label.equals(field.getLabel())) {
                return;
            }
        }
        throw new RuntimeException("No field with label: " + label);
    }
}
