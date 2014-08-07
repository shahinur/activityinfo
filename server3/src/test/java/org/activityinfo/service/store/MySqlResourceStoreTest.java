package org.activityinfo.service.store;

import com.google.inject.Inject;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fixtures.MockHibernateModule;
import org.activityinfo.fixtures.Modules;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceTree;
import org.activityinfo.model.resource.ResourceTreeRequest;
import org.activityinfo.server.database.OnDataSet;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Iterator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

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
        Iterator<Resource> cursor = store.openCursor(ACTIVITY_FORM_CLASS_ID);
        int count = 0;
        while(cursor.hasNext()) {
            Resource resource = cursor.next();
            System.out.println(resource);
            count++;
        }
        assertThat(count, equalTo(3));
    }

    @Test
    public void testTree() {
        ResourceId databaseId = CuidAdapter.databaseId(1);
        ResourceTree tree = store.queryTree(new ResourceTreeRequest(databaseId));

        assertThat(tree.getRootNode(), hasProperty("id", equalTo(databaseId)));
        assertThat(tree.getRootNode(), hasProperty("label", equalTo("PEAR")));

    }
}