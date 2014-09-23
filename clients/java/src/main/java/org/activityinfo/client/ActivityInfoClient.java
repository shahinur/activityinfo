package org.activityinfo.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.activityinfo.client.xform.XFormInstanceBuilder;
import org.activityinfo.client.xform.XFormItem;
import org.activityinfo.client.xform.XFormList;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.UploadCredentials;
import org.activityinfo.service.store.UpdateResult;
import org.w3c.dom.Document;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class ActivityInfoClient {

    private final Client client;
    private final URI rootUri;
    private final WebResource root;
    private WebResource store;

    public ActivityInfoClient(URI rootUri, String accountEmail, String password) {
        ClientConfig clientConfig = new DefaultClientConfig(
                JacksonJsonProvider.class,
                ObjectMapperProvider.class);

        this.rootUri = rootUri;

        client = Client.create(clientConfig);
        client.addFilter(new HTTPBasicAuthFilter(accountEmail, password));

        root = client.resource(this.rootUri);
        store = root.path("service").path("store");
    }


    /**
     * Creates or updates a FormClass resource
     *
     * <p>When creating a new FormClass, you must generate and assign
     * its id before submission using {@link org.activityinfo.model.resource.Resources#generateId()}
     *
     * <p>You must also provide the resource's owner by calling
     * {@link org.activityinfo.model.form.FormClass#setOwnerId(org.activityinfo.model.resource.ResourceId)}.
     * The resource's owner can be a folder, or another form in the case of subforms.</p>
     *
     * @param formClass a FormClass instance.
     * @return the result of the transaction
     */
    public UpdateResult putForm(FormClass formClass) {
        Preconditions.checkNotNull(formClass.getId(), "formClass.id must be assigned before submitting");
        Preconditions.checkNotNull(formClass.getOwnerId(), "formClass.id must be assigned before submitting");

        return store.path("resource")
                .path(formClass.getId().asString())
                .entity(formClass, MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(UpdateResult.class);
    }

    /**
     * Submits a completed XForm instance
     */
    public ClientResponse submitXForm(XFormInstanceBuilder instance) {
        WebResource submission = client.resource(rootUri).path("submission");
        return submission
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .entity(instance.build())
                .post(ClientResponse.class);
    }

    /**
     *
     * @return a list of XForms which the user is authorized to submit.
     */
    public List<XFormItem> getXForms() {
        return root.path("formList")
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
            .accept(MediaType.APPLICATION_JSON)
            .post(UploadCredentials.class, form);


        // Create a multipart body that include the credentials
        // we just received
        FormDataMultiPart entity = new FormDataMultiPart();
        for (Map.Entry<String, String> entry : credentials.getFormFields().entrySet()) {
            entity.field(entry.getKey(), entry.getValue());
        }

        // Add the blob to upload
        FormDataBodyPart blobPart = new FormDataBodyPart();
        blobPart.setEntity(byteSource.openBufferedStream());
        blobPart.setName("filename");
        blobPart.setMediaType(mediaType);
        blobPart.setContentDisposition(
            FormDataContentDisposition
                .name("filename")
                .fileName(fileName)
                .build());
        entity.bodyPart(blobPart);

        // Create a new client instance to submit to GCS without our
        // AI credentials. (authorization is included the params received above)
        Client.create()
            .resource(credentials.getUrl())
            .post(ClientResponse.class, entity);
    }


    public ByteSource getBlob(final BlobId blobId) {
        return new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                ClientResponse response = root.path("service")
                    .path("blob")
                    .path(blobId.asString())
                    .accept(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);

                return response.getEntityInputStream();
            }
        };

    }

    public Resource get(ResourceId resourceId) {
        return store.path("resource").path(resourceId.asString())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get(Resource.class);
    }

    public List<ResourceNode> getOwnedOrSharedWorkspaces() {
        return store.path("query")
                .path("roots")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get(new ResourceNodeListGenericType());
    }

    final static private class ResourceNodeListGenericType extends GenericType<List<ResourceNode>> {
    }
}
