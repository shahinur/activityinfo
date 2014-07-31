package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.AttributeGroupDTO;
import org.activityinfo.legacy.shared.model.IsFormField;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.server.endpoint.odk.xform.*;
import org.activityinfo.service.ResourceLocatorSync;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Path("/activityForm")
public class FormResource extends ODKResource {

    private ResourceLocatorSync locator;

    @Inject
    public FormResource(ResourceLocatorSync locator) {
        this.locator = locator;
    }

    @GET @Produces(MediaType.TEXT_XML)
    public Response form(@QueryParam("id") int id) throws Exception {
        if (enforceAuthorization()) {
            return askAuthentication();
        }
        LOGGER.finer("ODK activity form " + id + " requested by " +
                     getUser().getEmail() + " (" + getUser().getId() + ")");

        //TODO This isn't anywhere near finished, but it does something and it makes the tests pass
        Resource resource = locator.getResource(CuidAdapter.activityFormClass(id));
        FormClass formClass = FormClass.fromResource(resource);
        List<FormField> formFields = formClass.getFields();

        Html html = new Html();
        html.head = new Head();
        html.head.title = formClass.getLabel();
        html.head.model = new Model();
        html.head.model.instance = new Instance();
        html.head.model.bind = Lists.newArrayListWithCapacity(formFields.size());
        for (FormField formField : formFields) {
            Bind bind = new Bind();
            bind.nodeset = "/data/" + formField.getId().asString();
            bind.type = formField.getType().getXFormType();
            if (formField.isReadOnly()) bind.readonly = "true()";
            bind.calculate = formField.getCalculation();
            if (formField.isRequired()) bind.required = "true()";
            html.head.model.bind.add(bind);
        }
        html.body = new Body();
        return Response.ok(html).build();
    }

    private List<IsFormField> sortFieldsTogether(ActivityDTO activity) {
        List<IsFormField> fields = Lists.newArrayList();

        // add only attribute groups with at least one attribute
        for(AttributeGroupDTO group : activity.getAttributeGroups()) {
            if(group.getAttributes().size() > 0) {
                fields.add(group);
            }
        }

        // add indicators if this nto monthly reporting
        if(activity.getReportingFrequency() == 0) {
            fields.addAll(activity.getIndicators());
        }

        Collections.sort(fields, new Comparator<IsFormField>() {
            @Override
            public int compare(IsFormField o1, IsFormField o2) {
                return o1.getSortOrder() - o2.getSortOrder();
            }
        });
        return fields;
    }
}
