package org.activityinfo.legacy.shared.adapter;

import junit.framework.TestCase;
import org.activityinfo.core.client.PromiseMatchers;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import sun.text.resources.FormatData_fi;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
public class ActivityUserFormBuilderTest extends CommandTestCase2 {

    public static final int BAVON_USER_ID = 2;

    @Test @OnDataSet("/dbunit/sites-simple1.db.xml")
    public void partnersFieldIsNotVisibleIfThereIsOnlyOneChoice() {

        setUser(BAVON_USER_ID);

        ResourceLocatorAdaptor locator = new ResourceLocatorAdaptor(getDispatcher());

        FormClass formClass = PromiseMatchers.assertResolves(locator.getFormClass(CuidAdapter.activityFormClass(1)));

        ResourceId partnerFieldId = CuidAdapter.field(formClass.getId(), CuidAdapter.PARTNER_FIELD);
        FormField partnerField = formClass.getField(partnerFieldId);

        assertThat(partnerField, hasProperty("visible", equalTo(false)));
    }

}