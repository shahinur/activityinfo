package org.activityinfo.server.endpoint.odk;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.server.endpoint.odk.xform.Bind;
import org.activityinfo.server.endpoint.odk.xform.Body;
import org.activityinfo.server.endpoint.odk.xform.Data;
import org.activityinfo.server.endpoint.odk.xform.Head;
import org.activityinfo.server.endpoint.odk.xform.Html;
import org.activityinfo.server.endpoint.odk.xform.Instance;
import org.activityinfo.server.endpoint.odk.xform.InstanceId;
import org.activityinfo.server.endpoint.odk.xform.Meta;
import org.activityinfo.server.endpoint.odk.xform.Model;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.service.store.ResourceStore;

import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static org.activityinfo.server.endpoint.odk.OdkHelper.convertRelevanceConditionExpression;
import static org.activityinfo.server.endpoint.odk.OdkHelper.toAbsoluteFieldName;
import static org.activityinfo.server.endpoint.odk.OdkHelper.toRelativeFieldName;

@Path("/activityForm")
public class FormResource {

    private static final Logger LOGGER = Logger.getLogger(FormResource.class.getName());

    private Provider<AuthenticatedUser> authProvider;
    private ResourceStore locator;
    private OdkFormFieldBuilderFactory factory;
    private AuthenticationTokenService authenticationTokenService;

    @Inject
    public FormResource(ResourceStore locator, OdkAuthProvider authProvider, OdkFormFieldBuilderFactory factory,
                        AuthenticationTokenService authenticationTokenService) {
        this.locator = locator;
        this.authProvider = authProvider;
        this.factory = factory;
        this.authenticationTokenService = authenticationTokenService;
    }

    @VisibleForTesting
    FormResource(ResourceStore locator, Provider<AuthenticatedUser> authProvider, OdkFormFieldBuilderFactory factory,
                 AuthenticationTokenService authenticationTokenService) {
        this.authProvider = authProvider;
        this.locator = locator;
        this.factory = factory;
        this.authenticationTokenService = authenticationTokenService;
    }

    @GET @Produces(MediaType.TEXT_XML)
    public Response form(@QueryParam("id") int id) {

        AuthenticatedUser user = authProvider.get();

        LOGGER.finer("ODK activity form " + id + " requested by " +
                     user.getEmail() + " (" + user.getId() + ")");

        //TODO Authorization is the main thing that's still missing, plus refactoring and more testing
        AuthenticationToken authenticationToken = authenticationTokenService.getAuthenticationToken(user.getId(), id);
        Resource resource;

        try {
            resource = locator.get(CuidAdapter.activityFormClass(id));
        } catch (ResourceNotFound resourceNotFound) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        FormClass formClass = FormClass.fromResource(resource);
        List<FormField> formFields = formClass.getFields();
        Set<String> fieldsSet = OdkHelper.extractFieldsSet(formFields);

        Html html = new Html();
        html.head = new Head();
        html.head.title = formClass.getLabel();
        html.head.model = new Model();
        html.head.model.instance = new Instance();
        html.head.model.instance.data = new Data();
        html.head.model.instance.data.id = authenticationToken.getToken();
        html.head.model.instance.data.meta = new Meta();
        html.head.model.instance.data.meta.instanceID = new InstanceId();
        html.head.model.instance.data.jaxbElement = Lists.newArrayListWithCapacity(formFields.size());
        for (FormField formField : formFields) {
            OdkFormFieldBuilder odkFormFieldBuilder = factory.fromFieldType(formField.getType());
            if (odkFormFieldBuilder == null) continue;
            QName qName = new QName("http://www.w3.org/2002/xforms", toRelativeFieldName(formField.getId().asString()));
            html.head.model.instance.data.jaxbElement.add(new JAXBElement<>(qName, String.class, ""));
        }
        html.head.model.bind = Lists.newArrayListWithCapacity(formFields.size() + 1);
        Bind bind = new Bind();
        bind.nodeset = "/data/meta/instanceID";
        bind.type = "string";
        bind.readonly = "true()";
        bind.calculate = "concat('uuid:',uuid())";
        html.head.model.bind.add(bind);
        for (FormField formField : formFields) {
            OdkFormFieldBuilder odkFormFieldBuilder = factory.fromFieldType(formField.getType());
            if (odkFormFieldBuilder == null) continue;
            bind = new Bind();
            bind.nodeset = toAbsoluteFieldName(formField.getId().asString());
            bind.type = odkFormFieldBuilder.getModelBindType();
            if (formField.isReadOnly()) bind.readonly = "true()";
            //TODO Fix this
            //bind.calculate = formField.getExpression();
            bind.relevant = convertRelevanceConditionExpression(formField.getRelevanceConditionExpression(), fieldsSet);
            if (formField.isRequired()) bind.required = "true()";
            html.head.model.bind.add(bind);
        }
        html.body = new Body();
        html.body.jaxbElement = Lists.newArrayListWithCapacity(formFields.size());
        for (FormField formField : formFields) {
            OdkFormFieldBuilder odkFormFieldBuilder = factory.fromFieldType(formField.getType());
            if (odkFormFieldBuilder == null) continue;
            html.body.jaxbElement.add(odkFormFieldBuilder.createPresentationElement(toAbsoluteFieldName(
                    formField.getId().asString()), formField.getLabel(), formField.getDescription()));
        }
        return Response.ok(html).build();
    }
}
