package org.activityinfo.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.service.store.UpdateResult;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.concurrent.Future;

public class ActivityInfoClient {

    private final Client client;
    private final URI rootUri;
    private WebResource store;

    public ActivityInfoClient(URI rootUri, String accountEmail, String password) {
        ClientConfig clientConfig = new DefaultClientConfig(
                JacksonJsonProvider.class,
                ObjectMapperProvider.class);

        this.rootUri = rootUri;

        client = Client.create(clientConfig);
        client.addFilter(new HTTPBasicAuthFilter(accountEmail, password));

        store = client.resource(this.rootUri).path("service").path("store");
    }


    /**
     * Creates or updates a FormClass resource
     *
     * <p>When creating a new FormClass, you must generate and assign
     * its id before submission using {@link org.activityinfo.model.resource.ResourceId#generateId()}
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

    public ClientResponse submitXForm(XFormInstance instance) {
        WebResource submission = client.resource(rootUri).path("submission");
        return submission
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .entity(instance.build())
                .post(ClientResponse.class);
    }

    /**
     * Submits an XForm Instance asynchronously.
     *
     * @param instance a XForm Instance
     * @return
     */
    public Future<ClientResponse> submitXFormAsync(XFormInstance instance) {
        FormDataMultiPart multipartBody = instance.build();

        AsyncWebResource.Builder resource = client.asyncResource(rootUri)
                .path("submission")
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .entity(multipartBody);

        return resource.post(ClientResponse.class);
    }
}
