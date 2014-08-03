package org.activityinfo.service.core.store;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fixtures.MockHibernateModule;
import org.activityinfo.fixtures.Modules;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.server.database.LoadDataSet;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.server.database.TestDatabaseModule;
import org.activityinfo.ui.client.service.TestResourceStore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(InjectionSupport.class)
@Modules(MockHibernateModule.class)
@OnDataSet("/dbunit/resources.db.xml")
public class MySqlResourceStoreTest {

    public static final ResourceId ACTIVITY_FORM_CLASS_ID = CuidAdapter.activityFormClass(1);
    @Inject
    private MySqlResourceStore store;

    @Test
    public void testGet() throws IOException {
        Resource resource = store.get(ACTIVITY_FORM_CLASS_ID);
        FormClass formClass = FormClass.fromResource(resource);

        assertThat(formClass.getId(), equalTo(ACTIVITY_FORM_CLASS_ID));
        assertThat(formClass.getLabel(), equalTo("NFI"));
    }

    @Test
    public void testCursor() {
        ResourceCursor cursor = store.openCursor(ACTIVITY_FORM_CLASS_ID);
        int count = 0;
        while(cursor.next()) {
            Resource resource = cursor.getResource();
            System.out.println(resource);
            count++;
        }
        assertThat(count, equalTo(3));
    }
}