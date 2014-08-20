package org.activityinfo.service.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerException;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.inmemory.InMemoryTestContainerFactory;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.ResourceTree;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.jaxrs.EntityTags;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;

/**
 * Tests the Jersey setup, not the actual service itself
 */
public class ResourceStoreTest extends JerseyTest {

    public ResourceStoreTest() {
        super(new InMemoryTestContainerFactory());
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new InMemoryTestContainerFactory();
    }

    @Override
    protected AppDescriptor configure() {
        return new LowLevelAppDescriptor.Builder(new TestResourceConfig()).build();
    }

    private WebResource getStoreService() {
        WebResource webResource = resource();
        return webResource.path("service").path("store");
    }

    @Test
    public void get() {
        Resource resource = getStoreService()
                .path("resource")
                .path(ResourceStoreStub.MY_RESOURCE_ID.asString())
                .accept(MediaType.APPLICATION_JSON)
                .get(Resource.class);

        Resource expected = ResourceStoreStub.getMyResource();
        assertThat(resource.getId(), equalTo(expected.getId()));
        assertThat(resource.getOwnerId(), equalTo(expected.getOwnerId()));
        assertThat(resource.getVersion(), equalTo(expected.getVersion()));
        assertThat(resource.getProperties(), equalTo(expected.getProperties()));
        assertThat(resource, equalTo(expected));
    }

    @Test
    public void create() {
        Resource newResource = ResourceStoreStub.getNewResourceThatWillCommit();
        ClientResponse response = getStoreService()
                .path("resource")
                .path(ResourceStoreStub.NEW_RESOURCE_ID_TO_COMMIT.asString())
                .entity(newResource, MediaType.APPLICATION_JSON_TYPE)
                .put(ClientResponse.class);

        assertThat(response.getStatusInfo(), equalTo((Response.StatusType)ClientResponse.Status.CREATED));
        assertThat(response.getEntityTag(), equalTo(EntityTags.ofResource(newResource.getId(),
                ResourceStoreStub.NEW_VERSION)));
    }

    @Test
    public void queryTree() {

        ResourceTreeRequest request = new ResourceTreeRequest(ResourceId.ROOT_ID);

        ResourceTree tree = getStoreService()
                .path("query")
                .path("tree")
                .accept(MediaType.APPLICATION_JSON)
                .entity(request, MediaType.APPLICATION_JSON_TYPE)
                .post(ResourceTree.class);

        assertThat(tree.getRootNode().getChildren(), hasSize(2));
    }

    @Test
    public void queryTable() {

        TableModel tableModel = new TableModel(ResourceStoreStub.MY_RESOURCE_ID);
        tableModel.addColumn("c1").select(ColumnType.STRING).fieldPath(ResourceId.valueOf("foo"));
        tableModel.addColumn("c2").select(ColumnType.STRING).fieldPath(ResourceId.valueOf("foo"));
        tableModel.addColumn("c3").select(ColumnType.STRING).fieldPath(ResourceId.valueOf("foo"));

        TableData tableData = getStoreService()
                .path("query")
                .path("table")
                .accept(MediaType.APPLICATION_JSON)
                .entity(tableModel, MediaType.APPLICATION_JSON_TYPE)
                .post(TableData.class);


        assertThat(tableData.getNumRows(), equalTo(3));
        assertThat(tableData.getColumns(), hasKey("c1"));
        assertThat(tableData.getColumns(), hasKey("c2"));
        assertThat(tableData.getColumns(), hasKey("c3"));

        assertThat(tableData.getColumnView("c1").getString(2), equalTo("foo"));

        ColumnView c2 = tableData.getColumnView("c2");
        assertThat(c2.getString(0), equalTo("a"));
        assertThat(c2.getString(1), equalTo("b"));
        assertThat(c2.getString(2), equalTo("c"));

        // 91, Double.NaN, 92
        ColumnView c3 = tableData.getColumnView("c3");
        assertThat(c3.getDouble(0), equalTo(91.0));
        assertTrue(Double.isNaN(c3.getDouble(1)));
        assertThat(c3.getDouble(2), equalTo(92.0));
    }

    @Test
    public void updateResultSerializes() throws IOException {
        ObjectMapper m = ObjectMapperFactory.get();
        String committedJson = m.writeValueAsString(UpdateResult.committed(ResourceId.valueOf("cxyz"), 94));
        String pendingJson = m.writeValueAsString(UpdateResult.pending());
        String rejectedJson = m.writeValueAsString(UpdateResult.rejected());

        UpdateResult committed = m.readValue(committedJson, UpdateResult.class);
        UpdateResult pending = m.readValue(pendingJson, UpdateResult.class);
        UpdateResult rejected = m.readValue(rejectedJson, UpdateResult.class);

        assertThat(committed.getStatus(), Matchers.equalTo(CommitStatus.COMMITTED));
        assertThat(committed.getResourceId(), equalTo(ResourceId.valueOf("cxyz")));
        assertThat(pending.getStatus(), Matchers.equalTo(CommitStatus.PENDING));
        assertThat(rejected.getStatus(), Matchers.equalTo(CommitStatus.REJECTED));

        ObjectNode pendingObject = (ObjectNode) m.readTree(pendingJson);
        assertThat(pendingObject.has("newVersion"), Matchers.equalTo(false));
    }


    @Test
    public void getUserRoots() {

        List<ResourceNode> roots = getStoreService()
                .path("query")
                .path("roots")
                .get(new GenericType<List<ResourceNode>>() { });


        assertThat(roots, hasSize(3));

    }
}