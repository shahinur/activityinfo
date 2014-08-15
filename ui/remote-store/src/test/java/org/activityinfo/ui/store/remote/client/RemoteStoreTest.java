package org.activityinfo.ui.store.remote.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;

import java.util.List;

public class RemoteStoreTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "org.activityinfo.ui.store.remote.RemoteStoreTest";
    }

    public void testGet() throws Exception {

        RemoteStoreService service = getStore();
        service.get(ResourceId.valueOf("test")).then(new AsyncCallback<Resource>() {
            @Override
            public void onFailure(Throwable throwable) {
                fail(throwable.getMessage());
            }

            @Override
            public void onSuccess(Resource resource) {

                assertEquals("resource.id", ResourceId.valueOf("test"), resource.getId());
                assertEquals("resource.ownerId", ResourceId.ROOT_ID, resource.getOwnerId());
                assertEquals("resource.version", 42L, resource.getVersion());
                assertEquals("resource.hello", "world", resource.get("hello"));

                finishTest();
            }
        });
        delayTestFinish(1000);
    }

    private RemoteStoreService getStore() {
        return new RemoteStoreServiceImpl(new RestEndpoint(GWT.getModuleBaseURL() + "store"));
    }

    public void testQueryRoots() throws Exception {

        RemoteStoreService service = getStore();
        service.queryRoots().then(new AsyncCallback<List<ResourceNode>>() {
            @Override
            public void onFailure(Throwable throwable) {
                fail(throwable.getMessage());
            }

            @Override
            public void onSuccess(List<ResourceNode> roots) {
                assertEquals("size", 3, roots.size());

                ResourceNode child0 = roots.get(0);
                assertEquals("roots[0].id", ResourceId.valueOf("chyuwn09e1"), child0.getId());
                assertEquals("roots[0].label", "Child 1", child0.getLabel());

                ResourceNode child1 = roots.get(1);
                assertEquals("roots[1].label", "Child 2", child1.getLabel());
                assertEquals("roots[1].version", 42L, child1.getVersion());

                ResourceNode child2 = roots.get(2);
                assertEquals("roots[2].label", "Child 3", child2.getLabel());

                finishTest();
            }
        });
        delayTestFinish(5000);
    }
}
