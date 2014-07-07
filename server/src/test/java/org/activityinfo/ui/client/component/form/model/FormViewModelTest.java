package org.activityinfo.ui.client.component.form.model;

import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.activityinfo.core.client.PromiseMatchers.assertResolves;

@SuppressWarnings("GwtClientClassFromNonInheritedModule")
@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class FormViewModelTest extends CommandTestCase2 {

    @Test
    public void test() {
        ResourceLocator locator = new ResourceLocatorAdaptor(getDispatcher());
        FormViewModelProvider builder = new FormViewModelProvider(locator,
                CuidAdapter.locationFormClass(1),
                CuidAdapter.locationInstanceId(1));

        FormViewModel formViewModel = assertResolves(builder.get());
        for(FormField field : formViewModel.getFormClass().getFields()) {
            System.out.println(field.getLabel());
            if(field.getType() == FieldTypeClass.REFERENCE) {
                System.out.println(formViewModel.getFieldViewModel(field.getId()));
            }
        }

    }
}
