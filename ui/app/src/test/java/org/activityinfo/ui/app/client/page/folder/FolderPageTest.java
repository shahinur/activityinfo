package org.activityinfo.ui.app.client.page.folder;

import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.junit.Test;

public class FolderPageTest {

    private final ResourceId folderId = Resources.generateId();

    @Test
    public void test() {


        ResourceNode folderNode = new ResourceNode(folderId, FolderClass.CLASS_ID);
        folderNode.setLabel("My Folder");

        FolderProjection tree = new FolderProjection(folderNode);
//
//        // Create the page and render while in loading state
//        FolderPage page = new FolderPage(ApplicationStub.get(), new FolderPlace(folderId));
//
//        VTree pageLoading = page.getView().force(null);
//
//
//        // Complete the response and render the completed page
//        ApplicationStub.getService().resolveFolderRequest(tree);
//
//
//        // Verify that page is rendered when completed
//        VTree pageComplete = page.getView().force(null);


    }

}