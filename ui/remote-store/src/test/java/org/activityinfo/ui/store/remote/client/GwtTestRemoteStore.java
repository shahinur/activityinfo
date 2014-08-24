package org.activityinfo.ui.store.remote.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.ui.store.remote.client.table.JsTableDataBuilder;

import java.util.List;

public class GwtTestRemoteStore extends GWTTestCase {
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
                assertEquals("resource.ownerId", Resources.ROOT_ID, resource.getOwnerId());
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
                assertEquals("roots[0].classId", FormClass.CLASS_ID, child0.getClassId());
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

    public void testQueryTable() {

        String jsonResponse =
          ("{'rows':3," +
             "'columns':" +
                  "{'c1':{'type':'STRING','storage':'constant','value':'foo'}," +
                   "'c2':{'type':'STRING','storage':'array','values':['a','b','c']}," +
                   "'c3':{'type':'NUMBER','storage':'array','values':[91.0,'NaN',92.0]}}}")
                .replace('\'', '"');


        TableData tableData = new JsTableDataBuilder().build(jsonResponse);
        assertEquals(3, tableData.getNumRows());
        assertTrue(tableData.getColumns().containsKey("c1"));
        assertTrue(tableData.getColumns().containsKey("c2"));
        assertTrue(tableData.getColumns().containsKey("c3"));

        assertEquals("foo", tableData.getColumnView("c1").getString(2));

        ColumnView c2 = tableData.getColumnView("c2");
        assertEquals("a", c2.getString(0));
        assertEquals("b", c2.getString(1));
        assertEquals("c", c2.getString(2));

        // 91, Double.NaN, 92
        ColumnView c3 = tableData.getColumnView("c3");
        assertEquals(91.0, c3.getDouble(0));
        assertTrue(Double.isNaN(c3.getDouble(1)));
        assertEquals(92.0, c3.getDouble(2));
    }
}
