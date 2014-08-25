package org.activityinfo.service.store;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.table.ColumnModel;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.columns.ConstantColumnView;
import org.activityinfo.model.table.columns.DoubleArrayColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;
import org.activityinfo.model.table.columns.StringArrayColumnView;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ResourceStoreStub implements ResourceStore {

    public static final ResourceId MY_RESOURCE_ID = ResourceId.valueOf("myid");

    public static final ResourceId NEW_RESOURCE_ID_TO_COMMIT = ResourceId.valueOf("newid1");
    public static final int NEW_VERSION = 21;

    @Override
    public Resource get(@InjectParam AuthenticatedUser user, ResourceId resourceId) {

        if(!resourceId.equals(MY_RESOURCE_ID)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return getMyResource();
    }

    public static Resource getMyResource() {
        Resource resource = Resources.createResource();
        resource.setId(MY_RESOURCE_ID);
        resource.setOwnerId(Resources.ROOT_ID);
        resource.setVersion(15);
        resource.set("stringProperty", "stringValue");
        resource.set("booleanValue", true);
        resource.set("recordProperty", new Record()
                    .set("subStringProp", "A")
                    .set("anotherSubProp", "B")
                    .set("subBoolProp", true));
        return resource;
    }

    public static Resource getNewResourceThatWillCommit() {
        Resource resource = Resources.createResource();
        resource.setId(NEW_RESOURCE_ID_TO_COMMIT);
        resource.setOwnerId(ResourceId.valueOf("parent32"));
        resource.set("stringProperty", "_string1__");
        resource.set("falseValue", false);
        resource.set("recordProperty", new Record()
                .set("recordId", "record1")
                .set("subRecord", new Record().set("subSubString", "downUnder")));
        return resource;
    }

    @Override
    public Set<Resource> get(@InjectParam AuthenticatedUser user, Set<ResourceId> resourceIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceTree queryTree(@InjectParam AuthenticatedUser user, ResourceTreeRequest request) {

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

        return new ResourceTree(parentFolder);
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

    private ColumnView createView(ColumnModel model, int numRows) {
        switch(model.getId()) {
            case "c1":
                return new ConstantColumnView(numRows, "foo");
            case "c2":
                return new StringArrayColumnView(new String[] { "a", "b", "c" });
            case "c3":
                return new DoubleArrayColumnView(new double[] { 91, Double.NaN, 92});
            default:
                return new EmptyColumnView(numRows, model.getType());
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

    @Override
    public UpdateResult put(@InjectParam AuthenticatedUser user, ResourceId resourceId, Resource resource) {
        Preconditions.checkNotNull(resourceId, "resourceId");
        Preconditions.checkNotNull(resource, "resource");

        if(NEW_RESOURCE_ID_TO_COMMIT.equals(resource.getId())) {

            assertThat(resource, equalTo(getNewResourceThatWillCommit()));

            return UpdateResult.committed(resource.getId(), NEW_VERSION);
        }

        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }


    @Override
    public List<ResourceNode> getUserRootResources(@InjectParam AuthenticatedUser user) {

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
}
