package org.activityinfo.ui.app.client.page.resource;

import com.google.common.base.Function;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceTree;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.app.client.page.folder.FolderPage;
import org.activityinfo.ui.app.client.page.form.FormPage;
import org.activityinfo.ui.store.remote.client.RemoteStoreService;

import javax.annotation.Nullable;

public class ResourcePageFactory implements Function<ResourceTree, Promise<ResourcePage>> {

    private RemoteStoreService store;

    public ResourcePageFactory(RemoteStoreService store) {
        this.store = store;
    }

    @Override
    public Promise<ResourcePage> apply(ResourceTree input) {
        ResourceId classId = input.getRootNode().getClassId();
        if(classId.equals(FolderClass.CLASS_ID)) {
            return Promise.resolved((ResourcePage)new FolderPage(input));

        } else if(classId.equals(FormClass.CLASS_ID)) {
            return fetchResource(input);

        } else {
            return Promise.rejected(new UnsupportedOperationException("classId: " + classId));
        }
    }

    private Promise<ResourcePage> fetchResource(ResourceTree tree) {
        return store.get(tree.getRootNode().getId()).then(new Function<Resource, ResourcePage>() {
            @Nullable
            @Override
            public ResourcePage apply(@Nullable Resource node) {
                return new FormPage(node);
            }
        });
    }
}
