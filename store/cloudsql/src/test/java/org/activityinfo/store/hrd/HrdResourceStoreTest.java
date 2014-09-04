package org.activityinfo.store.hrd;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.store.cloudsql.TestingEnvironment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HrdResourceStoreTest {
    @Rule
    public final TestingEnvironment testingEnvironment = new TestingEnvironment();

    private HrdResourceStore hrdResourceStore = new HrdResourceStore();
    private AuthenticatedUser user = new AuthenticatedUser();

    @Before
    public void setUp() {
        hrdResourceStore = (HrdResourceStore) testingEnvironment.getStore();
        user = testingEnvironment.getUser();
    }

    @Test
    public void testGetUpdates() {
        assertEquals(0, hrdResourceStore.getUpdates(user, null, 0).size());

        Resource resource = new FormInstance(ResourceId.valueOf("AAAAAAAA"), FolderClass.CLASS_ID).asResource();
        hrdResourceStore.create(user, resource);

        assertEquals(resource, hrdResourceStore.getUpdates(user, null, 0).get(0));
        assertEquals(1, hrdResourceStore.getUpdates(user, null, 0).size());
        assertEquals(0, hrdResourceStore.getUpdates(user, null, 1).size());
        assertEquals(0, hrdResourceStore.getUpdates(user, null, 2).size());

        hrdResourceStore.put(user, resource);

        assertEquals(0, hrdResourceStore.getUpdates(user, null, 2).size());
        assertEquals(1, hrdResourceStore.getUpdates(user, null, 1).size());
        assertEquals(1, hrdResourceStore.getUpdates(user, null, 0).size());
        assertEquals(resource, hrdResourceStore.getUpdates(user, null, 0).get(0));
    }
}
