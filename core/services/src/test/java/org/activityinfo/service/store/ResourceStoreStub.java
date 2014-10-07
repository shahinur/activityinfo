package org.activityinfo.service.store;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.table.*;
import org.activityinfo.model.table.views.ConstantColumnView;
import org.activityinfo.model.table.views.DoubleArrayColumnView;
import org.activityinfo.model.table.views.EmptyColumnView;
import org.activityinfo.model.table.views.StringArrayColumnView;

import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class ResourceStoreStub implements ResourceStore {

    public static final ResourceId MY_RESOURCE_ID = ResourceId.valueOf("myid");

    public static final ResourceId NEW_RESOURCE_ID_TO_COMMIT = ResourceId.valueOf("newid1");
    public static final int NEW_VERSION = 21;

    private int nextClientId = 1;


    public UserResource get(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId) {

        if(!resourceId.equals(MY_RESOURCE_ID)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return UserResource.userResource(getMyResource()).
                setEditAllowed(true).
                setOwner(true);
    }

    public static Resource getMyResource() {
        Resource resource = Resources.createResource();
        resource.setId(MY_RESOURCE_ID);
        resource.setOwnerId(Resources.ROOT_ID);
        resource.setVersion(15);
        resource.setValue(Records.builder()
            .set("stringProperty", "stringValue")
            .set("booleanValue", true)
            .set("recordProperty", Records.builder()
                .set("subStringProp", "A")
                .set("anotherSubProp", "B")
                .set("subBoolProp", true)
                .build())
            .build());
        return resource;
    }

    public static Resource getNewResourceThatWillCommit() {
        Resource resource = Resources.createResource();
        resource.setId(NEW_RESOURCE_ID_TO_COMMIT);
        resource.setOwnerId(ResourceId.valueOf("parent32"));
        resource.setValue(Records.builder()
            .set("stringProperty", "_string1__")
            .set("falseValue", false)
            .set("recordProperty", Records.builder()
                .set("recordId", "record1")
                .set("subRecord", Records.builder().set("subSubString", "downUnder").build())
                .build())
            .build());
        return resource;
    }

    private List<Resource> get(@InjectParam AuthenticatedUser user, Set<ResourceId> resourceIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FolderProjection queryTree(@InjectParam AuthenticatedUser user, FolderRequest request) {

        ResourceNode parentFolder = new ResourceNode(Resources.generateId(), FolderClass.CLASS_ID);
        parentFolder.setLabel("Parent Folder");
        parentFolder.setOwnerId(Resources.ROOT_ID);

        ResourceNode child1 = new ResourceNode(Resources.generateId(), FormClass.CLASS_ID);
        child1.setOwnerId(parentFolder.getOwnerId());
        child1.setLabel("Child 1");

        ResourceNode child2 = new ResourceNode(Resources.generateId(), FormClass.CLASS_ID);
        child2.setOwnerId(parentFolder.getOwnerId());
        child2.setLabel("Child 2");

        parentFolder.getChildren().add(child1);
        parentFolder.getChildren().add(child2);

        return new FolderProjection(parentFolder);
    }

    @Override
    public TableData queryTable(@InjectParam AuthenticatedUser user, TableModel tableModel) {

        Preconditions.checkArgument(tableModel.getRowSources().size() == 1);
        Preconditions.checkArgument(tableModel.getRowSources().get(0).getRootFormClass().equals(MY_RESOURCE_ID));

        int numRows = 3;

        Map<String, ColumnView> columns = Maps.newHashMap();
        for(ColumnModel model : tableModel.getColumns()) {
            columns.put(model.getId(), createView(model, numRows));
        }

        return new TableData(numRows, columns);
    }

    @Override
    public List<Bucket> queryCube(@InjectParam AuthenticatedUser user, PivotTableModel tableModel) {
        throw new UnsupportedOperationException();
    }

    private ColumnView createView(ColumnModel model, int numRows) {
        switch(model.getId()) {
            case "c1":
                return new ConstantColumnView(numRows, "foo");
            case "c2":
                return new StringArrayColumnView(new String[] { "a", "b", "c" });
            case "c3":
                return new DoubleArrayColumnView(new double[] { 91, Double.NaN, 92});
            default:
                return new EmptyColumnView(numRows, ColumnType.STRING);
        }
    }

    @Override
    public UpdateResult create(AuthenticatedUser user, Resource resource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UpdateResult put(AuthenticatedUser user, Resource resource) {
        return put(user, resource.getId(), resource);
    }

    private UpdateResult put(@InjectParam AuthenticatedUser user, ResourceId resourceId, Resource resource) {
        Preconditions.checkNotNull(resourceId, "resourceId");
        Preconditions.checkNotNull(resource, "resource");

        if(NEW_RESOURCE_ID_TO_COMMIT.equals(resource.getId())) {

            Resource expected = getNewResourceThatWillCommit();
            assertTrue(Records.deepEquals(resource.getValue(), expected.getValue()));

            return UpdateResult.committed(resource.getId(), NEW_VERSION);
        }

        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @Override
    public UpdateResult delete(@InjectParam AuthenticatedUser user, ResourceId resourceId) {
        return UpdateResult.committed(resourceId, 1);
    }


    @Override
    public List<ResourceNode> getOwnedOrSharedWorkspaces(@InjectParam AuthenticatedUser user) {

        ResourceNode folder1 = new ResourceNode(Resources.generateId(), FormClass.CLASS_ID);
        folder1.setOwnerId(Resources.ROOT_ID);
        folder1.setVersion(41);
        folder1.setLabel("Child 1");

        ResourceNode folder2 = new ResourceNode(Resources.generateId(), FormClass.CLASS_ID);
        folder2.setOwnerId(Resources.ROOT_ID);
        folder2.setVersion(42);
        folder2.setLabel("Child 2");

        ResourceNode folder3 = new ResourceNode(Resources.generateId(), FormClass.CLASS_ID);
        folder3.setVersion(43);
        folder3.setOwnerId(Resources.ROOT_ID);
        folder3.setLabel("Child 3");


        return Arrays.asList(folder1, folder2, folder3);
    }

    @Override
    public List<Resource> getAccessControlRules(@InjectParam AuthenticatedUser user,
                                                @PathParam("id") ResourceId resourceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Resource> getUpdates(@InjectParam AuthenticatedUser user, ResourceId workspaceId, long version) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StoreLoader beginLoad(AuthenticatedUser user, ResourceId parentId) {
        throw new UnsupportedOperationException();
    }

}
