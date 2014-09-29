package org.activityinfo.ui.store.remote.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.ui.store.remote.client.resource.ResourceParser;
import org.activityinfo.ui.store.remote.client.resource.UserResourceParser;
import org.activityinfo.ui.store.remote.client.table.JsTableDataBuilder;

import java.util.List;

public class GwtTestRemoteStore extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "org.activityinfo.ui.store.remote.RemoteStoreTest";
    }

    public void testGet() throws Exception {

        GWT.log("testGet() starting...");

        RemoteStoreService service = getStore();
        service.get(ResourceId.valueOf("test")).then(new AsyncCallback<UserResource>() {
            @Override
            public void onFailure(Throwable caught) {
                fail(caught.getMessage());
            }

            @Override
            public void onSuccess(UserResource resource) {
                FormClass form = FormClass.fromResource(resource.getResource());
                assertEquals("resource.id", ResourceId.valueOf("a1"), form.getId());
                assertEquals("resource.ownerId", ResourceId.valueOf("C1c4a0524b"),  form.getOwnerId());
             //   assertEquals("resource.version", 41L, resource.getVersion());
                assertEquals("resource.label", "NFI", form.getLabel());

                assertEquals("resource.elements.size", 12, form.getElements().size());

                FormField field = (FormField) form.getElements().get(0);
                assertEquals("a1f7", field.getId().asString());
                assertEquals("Partner", field.getLabel());

                finishTest();
            }
        });

        delayTestFinish(1000);
    }

    private RemoteStoreService getStore() {
        return new RemoteStoreServiceImpl(new RestEndpoint(GWT.getModuleBaseURL()));
    }

    public void testQueryRoots() throws Exception {

        RemoteStoreService service = getStore();
        service.getWorkspaces().then(new AsyncCallback<List<ResourceNode>>() {
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

    public static String resourceJson() {
        return ("{" +
                "   '@id':'c123'," +
                "   '@owner':'c456'," +
                "   'classId':'_class'," +
                "   '_class_label':'My Form'," +
                "   'elements':[" +
                "      {" +
                "         'id':'ci07lcztd1'," +
                "         'visible':true," +
                "         'primaryKey':false," +
                "         'label':'Default Field'," +
                "         'code':'ABC'," +
                "         'defaultValue':{" +
                "            '@type':'QUANTITY'," +
                "            'value':41.0," +
                "            'units':'%'" +
                "         }," +
                "         'required':false," +
                "         'type':{" +
                "            'parameters':{" +
                "               'classId':'_type:QUANTITY'," +
                "               'units':'%'" +
                "            }," +
                "            'typeClass':'QUANTITY'" +
                "         }" +
                "      }" +
                "   ]" +
                "}").replace('\'', '"');
    }
    
    public void testParseResource() {
        Resource form = ResourceParser.parse(resourceJson());
        FormClass formClass = FormClass.fromResource(form);
        assertEquals("c123", formClass.getId().asString());
        assertEquals("c456", formClass.getOwnerId().asString());
        assertEquals(new Quantity(41, "%"), formClass.getFields().get(0).getDefaultValue());
    }

    public static String userResourceJson() {
        return "{\"@editAllowed\":true," +
                "\"@owner\":true," +
                "\"@resource\":{" +
                "    \"@id\":\"ci0j74r0n1\"," +
                "    \"@owner\":\"_root\"," +
                "    \"@version\":7," +
                "    \"@class\":\"_folder\"," +
                "    \"_folder_description\":\"w1\"," +
                "    \"_folder_label\":\"f1\"}" +
                "}";
    }

    public void testParseUserResource() {
        UserResource resource = UserResourceParser.parse(userResourceJson());

        assertEquals(resource.getEditAllowed().booleanValue(), true);
        assertEquals(resource.isOwner().booleanValue(), true);
        assertEquals(resource.getResourceId().asString(), "ci0j74r0n1");
        assertEquals(resource.getResource().getOwnerId().asString(), "_root");
        assertEquals(resource.getResource().getValue().getClassId().asString(), "_folder");
        assertEquals(resource.getResource().getValue().getString("_folder_description"), "w1");
        assertEquals(resource.getResource().getValue().getString("_folder_label"), "f1");
        assertEquals(resource.getResource().getVersion(), 7);
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
