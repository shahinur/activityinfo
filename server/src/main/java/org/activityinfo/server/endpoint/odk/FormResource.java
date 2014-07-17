package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Lists;
import com.sun.jersey.api.view.Viewable;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.model.form.FormField;
import org.activityinfo.server.database.hibernate.entity.Partner;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.database.hibernate.entity.UserDatabase;
import org.activityinfo.server.database.hibernate.entity.UserPermission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

@Path("/activityForm")
public class FormResource extends ODKResource {
    @GET @Produces(MediaType.TEXT_XML)
    public Response form(@QueryParam("id") int id) throws Exception {
        if (enforceAuthorization()) {
            return askAuthentication();
        }
        LOGGER.finer("ODK activityform " + id + " requested by " +
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
        fields.addAll(activity.getAttributeGroups());
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
