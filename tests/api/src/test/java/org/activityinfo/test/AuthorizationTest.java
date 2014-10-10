package org.activityinfo.test;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.activityinfo.client.ActivityInfoClient;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.junit.Test;

import java.util.List;

import static javax.ws.rs.core.Response.Status.*;
import static org.activityinfo.model.resource.Resources.ROOT_ID;
import static org.junit.Assert.*;

public class AuthorizationTest {
    final private ResourceId workspaceId = Resources.generateId();
    final private ResourceId resourceId = Resources.generateId();

    @Test
    public void testAnonymousUser() {
        final ActivityInfoClient client = new ActivityInfoClient(TestConfig.getRootURI(), "A.Nonymous@example.com", "");

        // At least as of the time of writing anonymous users shouldn't be able to view any workspaces on the dev server
        assertTrue(client.getOwnedOrSharedWorkspaces().isEmpty());

        // Prepare a workspace so creating it can be attempted
        Resource workspace = Resources.createResource();
        workspace.setId(workspaceId);
        workspace.setOwnerId(ROOT_ID);
        workspace.setVersion(1);
        workspace.setValue(Records.builder(FolderClass.CLASS_ID)
                .set(FolderClass.LABEL_FIELD_ID.asString(), "An unwanted workspace that was never actually meant to be")
                .build());

        // Try and fail to create a workspace anonymously
        try {
            client.create(workspace);
            fail("Anonymous users can create new workspaces!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(UNAUTHORIZED.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }
    }

    @Test
    public void testAuthorizedUser() {
        final ActivityInfoTestClient client = new ActivityInfoTestClient(TestConfig.getRootURI());

        // Check to see if the initial environment is sane
        try {
            client.get(resourceId);
            fail("An ID locally generated at random should not already be present inside the server's resource store!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(NOT_FOUND.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        assertTrue(client.getOwnedOrSharedWorkspaces().isEmpty());

        Resource workspace = Resources.createResource();
        workspace.setId(workspaceId);
        workspace.setOwnerId(ROOT_ID);
        workspace.setVersion(1);
        workspace.setValue(Records.builder(FolderClass.CLASS_ID)
                .set(FolderClass.LABEL_FIELD_ID.asString(), "Workspace")
                .build());
        client.create(workspace);

        // Create a folder inside the newly created workspace
        Resource folder = Resources.createResource();
        folder.setId(resourceId);
        folder.setOwnerId(workspaceId);
        folder.setVersion(2);
        folder.setValue(Records.builder(FolderClass.CLASS_ID)
                .set(FolderClass.LABEL_FIELD_ID.asString(), "Folder")
                .build());
        client.create(folder);

        // Retrieve the workspace that was just created, along with its contents
        for (ResourceNode resourceNode : client.getOwnedOrSharedWorkspaces()) {
            Resource resource = client.get(resourceNode.getId());
            assertEquals(workspace, resource);
            assertNotEquals(folder, resource);

            resource = client.get(resourceId);
            assertEquals(folder, resource);
            assertNotEquals(workspace, resource);
        }

        // Retrieve the workspace's ACR
        List<Resource> acrs = client.getAccessControlRules(workspaceId);
        assertEquals(1, acrs.size());

        // Perform a sanity check on the updates endpoint
        List<Resource> resources = client.getUpdates(workspaceId, 0);
        assertEquals(3, resources.size());
        assertEquals(workspace, resources.get(0));
        assertEquals(acrs.get(0), resources.get(1));
        assertEquals(folder, resources.get(2));

        // Now introduce a second user to really test the authorization functionality
        ActivityInfoTestClient newClient = new ActivityInfoTestClient(TestConfig.getRootURI());

        assertTrue(newClient.getOwnedOrSharedWorkspaces().isEmpty());

        // Only a resource's owner should be able to view its ACRs
        try {
            newClient.getAccessControlRules(workspaceId);
            fail("Non-owners can access a resource's ACR!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(UNAUTHORIZED.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        // Try and fail to access the other user's resources
        for (Resource resource : resources) {
            try {
                newClient.get(resource.getId());
                fail("Users can access each other's resources without permission!");
            } catch (UniformInterfaceException uniformInterfaceException) {
                assertEquals(UNAUTHORIZED.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
            }
        }

        Resource newWorkspace = Resources.createResource();
        newWorkspace.setId(Resources.generateId());
        newWorkspace.setOwnerId(ROOT_ID);
        newWorkspace.setVersion(1);
        newWorkspace.setValue(Records.builder(FolderClass.CLASS_ID)
                .set(FolderClass.LABEL_FIELD_ID.asString(), "Another workspace")
                .build());
        newClient.create(newWorkspace);
        newClient.create(newWorkspace); // Not a mistake - resource creation should be idempotent

        List<ResourceNode> workspaces = newClient.getOwnedOrSharedWorkspaces();
        assertEquals(1, workspaces.size());
        assertEquals(newWorkspace, newClient.get(workspaces.get(0).getId()));

        // Try and fail to access the new user's workspace from the other user's account
        try {
            client.get(workspaces.get(0).getId());
            fail("Users can access each other's resources without permission!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(UNAUTHORIZED.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        // Create the folder a second time, with identical contents - once again, resource creation should be idempotent
        client.create(folder);

        // Rename the folder and check that it no longer matches the version on the server
        folder.setValue(Records.builder(FolderClass.CLASS_ID)
                .set(FolderClass.LABEL_FIELD_ID.asString(), "Renamed folder")
                .build());
        folder.setVersion(4);
        assertNotEquals(folder, client.get(resourceId));

        // Try and fail to rename the folder through the wrong user account
        try {
            newClient.update(folder);
            fail("Users can update each other's resources without permission!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(UNAUTHORIZED.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        // Try and fail to create the folder a second time, but with changes
        try {
            client.create(folder);
            fail("Users can create resources with IDs that are already in use with different contents!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(CONFLICT.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        // Rename the folder on the server, for real this time
        assertNotEquals(folder, client.get(resourceId));
        client.update(folder);
        assertEquals(folder, client.get(resourceId));

        // Fetch updates about the other user's workspace, none should be returned
        assertTrue(newClient.getUpdates(workspaceId, 0).isEmpty());

        // Try and fail to delete the folder through the wrong user account
        try {
            newClient.delete(resourceId);
            fail("Users can delete each other's resources without permission!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(UNAUTHORIZED.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        // Really delete the folder
        client.delete(resourceId);

        // Check that the HTTP status code for "Gone" is returned, instead of "Not Found", "Internal Server Error", etc.
        try {
            client.get(resourceId);
            fail("Deleting resources does not work!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(GONE.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        // Unauthorized users should not be able to see that a resource was deleted, for privacy reasons in edge cases
        try {
            newClient.get(resourceId);
            fail("Users can see deletion of each other's resources without permission!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(UNAUTHORIZED.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        // Perform all the same deletion tests again, but this time for the workspace instead of the folder
        try {
            newClient.delete(workspaceId);
            fail("Users can delete each other's workspaces without permission!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(UNAUTHORIZED.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        client.delete(workspaceId);

        try {
            client.get(workspaceId);
            fail("Deleting workspaces does not work!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(GONE.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        try {
            newClient.get(workspaceId);
            fail("Users can see deletion of each other's workspaces without permission!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(UNAUTHORIZED.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        // Try and fail to recreate the deleted workspace
        try {
            client.create(workspace);
            fail("Deleted resources can be recreated!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(CONFLICT.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        // Try and fail to update the deleted workspace
        try {
            client.update(workspace);
            fail("Deleted resources can still be updated!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(GONE.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        // Try and fail to update the deleted workspace as another user
        try {
            newClient.update(workspace);
            fail("Deleted resources can still be updated by another user!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(UNAUTHORIZED.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }

        // Try and fail to recreate the deleted workspace as another user
        try {
            newClient.create(workspace);
            fail("Deleted resources can be recreated by another user!");
        } catch (UniformInterfaceException uniformInterfaceException) {
            assertEquals(CONFLICT.getStatusCode(), uniformInterfaceException.getResponse().getStatus());
        }
    }
}
