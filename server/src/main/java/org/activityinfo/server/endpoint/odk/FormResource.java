package org.activityinfo.server.endpoint.odk;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.server.command.ResourceLocatorSyncImpl;
import org.activityinfo.server.endpoint.odk.xform.Bind;
import org.activityinfo.server.endpoint.odk.xform.Html;
import org.activityinfo.server.endpoint.odk.xform.InstanceId;
import org.activityinfo.service.store.ResourceNotFound;

import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
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
    private ResourceLocatorSyncImpl locator;
    private OdkFormFieldBuilderFactory factory;
    private AuthenticationTokenService authenticationTokenService;

    @Inject
    public FormResource(ResourceLocatorSyncImpl locator, OdkAuthProvider authProvider, OdkFormFieldBuilderFactory factory,
                        AuthenticationTokenService authenticationTokenService) {
        this.locator = locator;
        this.authProvider = authProvider;
        this.factory = factory;
        this.authenticationTokenService = authenticationTokenService;
    }

    @VisibleForTesting
    FormResource(ResourceLocatorSyncImpl locator, Provider<AuthenticatedUser> authProvider, OdkFormFieldBuilderFactory factory,
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

        AuthenticationToken authenticationToken = authenticationTokenService.getAuthenticationToken(user.getId(), id);
        FormClass formClass;
        try {
            formClass = locator.getFormClass(CuidAdapter.activityFormClass(id));
        } catch (ResourceNotFound resourceNotFound) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        Set<String> fieldsSet = OdkHelper.extractFieldsSet(formClass);
        List<OdkField> fields = buildFieldList(formClass);

        Html html = new Html();
        html.head.title = formClass.getLabel();
        html.head.model.instance.data.id = authenticationToken.getToken();
        html.head.model.instance.data.meta.instanceID = new InstanceId();

        for (OdkField field : fields) {
            QName qName = new QName("http://www.w3.org/2002/xforms", toRelativeFieldName(field.getModel().getId().asString()));
            html.head.model.instance.data.elements.add(new JAXBElement<>(qName, String.class, ""));
        }

        html.head.model.bind.add(instanceIdBinding());

        for (OdkField field : fields) {
            Bind bind = new Bind();
            bind.nodeset = toAbsoluteFieldName(field.getModel().getId().asString());
            bind.type = field.getBuilder().getModelBindType();
            if (field.getModel().isReadOnly()) {
                bind.readonly = "true()";
            }
            //TODO Fix this
            //bind.calculate = formField.getExpression();
            bind.relevant = convertRelevanceConditionExpression(field.getModel().getRelevanceConditionExpression(), fieldsSet);
            if (field.getModel().isRequired()) {
                bind.required = "true()";
            }
            html.head.model.bind.add(bind);
        }

        for (OdkField formField : fields) {
            if (formField.getModel().isVisible()) {
                html.body.jaxbElement.add(formField.getBuilder().createPresentationElement(
                        toAbsoluteFieldName(formField.getModel().getId().asString()),
                        formField.getModel().getLabel(),
                        formField.getModel().getDescription()));
            }
        }
        return Response.ok(html).build();
    }

    private List<OdkField> buildFieldList(FormClass formClass) {
        List<FormField> formFields = formClass.getFields();

        List<OdkField> fieldBuilders = new ArrayList<>();
        for (FormField field : formFields) {
            OdkFormFieldBuilder builder = factory.get(field.getType());
            if (builder != null) {
                fieldBuilders.add(new OdkField(field, builder));
            }
        }
        return fieldBuilders;
    }

    private Bind instanceIdBinding() {
        Bind bind = new Bind();
        bind.nodeset = "/data/meta/instanceID";
        bind.type = "string";
        bind.readonly = "true()";
        bind.calculate = "concat('uuid:',uuid())";
        return bind;
    }
}
