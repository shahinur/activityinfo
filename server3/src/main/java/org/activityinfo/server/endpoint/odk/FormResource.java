package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Lists;
import com.sun.jersey.api.view.Viewable;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.AttributeGroupDTO;
import org.activityinfo.legacy.shared.model.IsFormField;
import org.activityinfo.legacy.shared.model.SchemaDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Path("/activityForm")
public class FormResource extends ODKResource {

    @GET @Produces(MediaType.TEXT_XML)
    public Response form(@QueryParam("id") int id) throws Exception {
        if (enforceAuthorization()) {
            return askAuthentication();
        }
        LOGGER.finer("ODK activity form " + id + " requested by " +
                     getUser().getEmail() + " (" + getUser().getId() + ")");

        SchemaDTO schemaDTO = dispatcher.execute(new GetSchema());
        ActivityDTO activity = schemaDTO.getActivityById(id);

        if (activity == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        if (!activity.isEditAllowed()) {
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        // Quick fix to allow users to interleave attributes and indicators
        // together without break the legacy model

        activity.set("fields", sortFieldsTogether(activity));


        return Response.ok(new Viewable("/odk/form.ftl", activity)).build();
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
