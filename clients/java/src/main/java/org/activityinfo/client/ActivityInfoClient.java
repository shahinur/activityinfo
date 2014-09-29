package org.activityinfo.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.io.ByteSource;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.activityinfo.client.xform.XFormInstanceBuilder;
import org.activityinfo.client.xform.XFormItem;
import org.activityinfo.client.xform.XFormList;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.UploadCredentials;
import org.activityinfo.service.tasks.UserTask;
import org.w3c.dom.Document;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.fromStatusCode;

public class ActivityInfoClient {

    private final Client client;
    private final URI rootUri;
    private final WebResource root;
    private final String accountEmail;
    private final String password;
    private WebResource store;

    public ActivityInfoClient(URI rootUri, String accountEmail, String password) {
        ClientConfig clientConfig = new DefaultClientConfig(
                JacksonJsonProvider.class,
                ObjectMapperProvider.class);

        this.rootUri = rootUri;
        this.accountEmail = accountEmail;
        this.password = password;

        client = Client.create(clientConfig);
        client.addFilter(new HTTPBasicAuthFilter(accountEmail, password));
        client.addFilter(new LoggingFilter());

        root = client.resource(this.rootUri);
        store = root.path("service").path("store");
    }



    /**
     * Submits a completed XForm instance
     */
    public void submitXForm(XFormInstanceBuilder instance) {
        WebResource submission = client.resource(rootUri).path("submission");
        submission
            .type(MediaType.MULTIPART_FORM_DATA_TYPE)
            .entity(instance.build())
            .post();
    }

    /**
     *
     * @return a list of XForms which the user is authorized to submit.
     */
    public List<XFormItem> getXForms() {
        return client.resource(rootUri).path("formList")
            .type(MediaType.APPLICATION_XML_TYPE)
            .get(XFormList.class)
            .getForms();
    }

    /**
     * Retrieves an XForm as an XML document.
     */
    public Document getXForm(XFormItem formItem) {
        return client.resource(formItem.getUrl())
            .type(MediaType.APPLICATION_XML_TYPE)
            .get(Document.class);
    }

    /**
     * Submits an XForm Instance asynchronously.
     *
     * @param instance a completed XForm Instance
     */
    public Future<ClientResponse> submitXFormAsync(XFormInstanceBuilder instance) {
        FormDataMultiPart multipartBody = instance.build();

        AsyncWebResource.Builder resource = client.asyncResource(rootUri)
            .path("submission")
            .type(MediaType.MULTIPART_FORM_DATA_TYPE)
            .entity(multipartBody);

        return resource.post(ClientResponse.class);
    }

    /**
     * Retrieves the resource from the server
     * @param resourceId the resource's id.
     * @return
     */
    public Resource get(ResourceId resourceId) {
        return store.path("resource").path(resourceId.asString())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get(Resource.class);
    }

    /**
     * Creates a new resource
     */
    public void create(Resource resource) {
        store.path("resources")
            .type(MediaType.APPLICATION_JSON_TYPE)
            .post(resource);
    }

    /**
     *
     * @return a list of workspaces which the authenticated user owns
     * or to which they have been explicitly granted access.
     */
    public List<ResourceNode> getOwnedOrSharedWorkspaces() {
        return store.path("query")
            .path("roots")
            .type(MediaType.APPLICATION_JSON_TYPE)
            .get(new ResourceNodeListGenericType());
    }


    /**
     * Uploads a blob to the server.
     *
     * <p>Blobs are used to store large text and binary content apart from the resources
     * with which they are associated.</p>
     *
     * @param blobId
     * @param byteSource
     */
    public void postBlob(BlobId blobId, String fileName, MediaType mediaType, ByteSource byteSource) throws IOException {

        Form form = new Form();
        form.putSingle("blobId", blobId.asString());
        form.putSingle("filename", fileName);

        // First retrieve the upload URL
        UploadCredentials credentials = root.path("service")
            .path("blob")
            .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
            .accept(MediaType.APPLICATION_JSON)
            .post(UploadCredentials.class, form);

        System.out.println(credentials);

        // Create a multipart body that include the credentials
        // we just received
        FormDataMultiPart entity = new FormDataMultiPart();
        for (Map.Entry<String, String> entry : credentials.getFormFields().entrySet()) {
            entity.field(entry.getKey(), entry.getValue());
        }

        // Add the blob to upload
        InputStream inputStream = byteSource.openStream();
        entity.field("file", inputStream, mediaType);


        // Create a new client instance to submit to GCS without our
        // AI credentials. (authorization is included the params received above)
        Client.create()
            .resource(credentials.getUrl())
            .type(MediaType.MULTIPART_FORM_DATA_TYPE)
            .post(entity);
    }

    /**
     * Retrieves the blob with the given {@code blobId} from the server.
     *
     */
    public ByteSource getBlob(final BlobId blobId) {
        return new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                ClientResponse response = root.path("service")
                    .path("blob")
                    .path(blobId.asString())
                    .accept(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);

                System.out.println(response);

                return response.getEntityInputStream();
            }
        };
    }

    public List<UserTask> getTaskStatus() {
        return root.path("service").path("tasks")
            .accept(MediaType.APPLICATION_JSON)
            .get(new TasksListGenericType());
    }

    /**
     * Starts an import of a data file into an ActivityInfo Form and FormInstances.
     *
     * @param ownerId the workspace or folder where the new form will be created.
     * @param blobId the blobId of the data file to import.
     * @return a UserTask handle that can be used to track the status of the import job.
     */
    public UserTask startImport(ResourceId ownerId, BlobId blobId) {

        Form form = new Form();
        form.putSingle("ownerId", ownerId.asString());
        form.putSingle("blobId", blobId.asString());

        return root.path("service").path("load")
            .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
            .accept(MediaType.APPLICATION_JSON)
            .post(UserTask.class, form);
    }

    public boolean createUser() {
        Form form = new Form();
        form.put("email", Collections.singletonList(accountEmail));
        form.put("password", Collections.singletonList(password));

        return CREATED.equals(fromStatusCode(
                root.path("test").path("createUser")
                .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(ClientResponse.class, form)
                .getStatus()));
    }

    final static private class ResourceNodeListGenericType extends GenericType<List<ResourceNode>> {
    }

    final static private class TasksListGenericType extends GenericType<List<UserTask>> {
    }
}
