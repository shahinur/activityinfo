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

import static org.activityinfo.model.resource.Resources.ROOT_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
        // The datastore should be empty when this test is loaded
        assertEquals(0, hrdResourceStore.getUpdates(user, null, 0).size());

        Resource resource = new FormInstance(ResourceId.valueOf("AAAAAAAA"), FolderClass.CLASS_ID).asResource();
        resource.setOwnerId(ROOT_ID);
        hrdResourceStore.create(user, resource);

        // The first object to be retrieved is the resource we just created, the second object is its ACR
        assertEquals(resource, hrdResourceStore.getUpdates(user, null, 0).get(0));
        assertNotEquals(resource, hrdResourceStore.getUpdates(user, null, 0).get(1));
        assertEquals(2, hrdResourceStore.getUpdates(user, null, 0).size());
        assertEquals(1, hrdResourceStore.getUpdates(user, null, 1).size());
        assertEquals(0, hrdResourceStore.getUpdates(user, null, 2).size());
        assertEquals(0, hrdResourceStore.getUpdates(user, null, 3).size());
        // Starting from version 0, all resources will be returned, then each new version gives us one more resource

        hrdResourceStore.put(user, resource);

        // We won't get back three objects when starting from version 0, because two of them represent the same resource
        assertEquals(0, hrdResourceStore.getUpdates(user, null, 3).size());
        assertEquals(1, hrdResourceStore.getUpdates(user, null, 2).size());
        assertEquals(2, hrdResourceStore.getUpdates(user, null, 1).size());
        assertEquals(2, hrdResourceStore.getUpdates(user, null, 0).size());
        assertEquals(resource, hrdResourceStore.getUpdates(user, null, 0).get(1));
        assertNotEquals(resource, hrdResourceStore.getUpdates(user, null, 0).get(0));
        // Updating the resource we created ourselves means that is now the most recent one, so it will be returned last
    }
}
