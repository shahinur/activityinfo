package org.activityinfo.store.hrd;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.FolderClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.activityinfo.model.resource.Resources.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class SyncTest {
    @Rule
    public final TestingEnvironment testingEnvironment = new TestingEnvironment();

    private HrdResourceStore hrdResourceStore = new HrdResourceStore();
    private AuthenticatedUser user = new AuthenticatedUser();

    @Before
    public void setUp() {
        hrdResourceStore = testingEnvironment.getStore();
        user = testingEnvironment.getUser();
    }

    @Ignore
    @Test
    public void testGetUpdates() {
        ResourceId workspaceId = generateId();

        // The datastore should be empty when this test is loaded
        assertEquals(0, hrdResourceStore.getUpdates(user, workspaceId, 0).size());

        Resource resource = createResource();
        resource.setId(workspaceId);
        resource.setOwnerId(ROOT_ID);
        resource.setValue(Records.builder(FolderClass.CLASS_ID)
            .set(FolderClass.LABEL_FIELD_ID.asString(), "SyncWorkspace")
            .build());
        hrdResourceStore.create(user, resource);

        // The first object to be retrieved is the resource we just created, the second object is its ACR
        assertEquals(resource.getId(), hrdResourceStore.getUpdates(user, workspaceId, 0).get(0).getId());
        assertNotEquals(resource.getId(), hrdResourceStore.getUpdates(user, workspaceId, 0).get(1).getId());
        assertThat(hrdResourceStore.getUpdates(user, workspaceId, 0), hasSize(2));
        assertThat(hrdResourceStore.getUpdates(user, workspaceId, 1), hasSize(1));
        assertThat(hrdResourceStore.getUpdates(user, workspaceId, 2), hasSize(0));
        assertThat(hrdResourceStore.getUpdates(user, workspaceId, 3), hasSize(0));
        // Starting from version 0, all resources will be returned, then each new version gives us one more resource

        hrdResourceStore.put(user, resource);

        // We won't get back three objects when starting from version 0, because two of them represent the same resource
        assertEquals(0, hrdResourceStore.getUpdates(user, workspaceId, 3).size());
        assertEquals(1, hrdResourceStore.getUpdates(user, workspaceId, 2).size());
        assertEquals(2, hrdResourceStore.getUpdates(user, workspaceId, 1).size());
        assertEquals(2, hrdResourceStore.getUpdates(user, workspaceId, 0).size());
        assertEquals(resource.getId(), hrdResourceStore.getUpdates(user, workspaceId, 0).get(1).getId());
        assertNotEquals(resource.getId(), hrdResourceStore.getUpdates(user, workspaceId, 0).get(0).getId());
        // Updating the resource we created ourselves means that is now the most recent one, so it will be returned last
    }
}
