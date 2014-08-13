package org.activityinfo.server.endpoint.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

public class SitesResources {


    @GET @Produces(MediaType.APPLICATION_JSON)
    public String query(@QueryParam("activity") List<Integer> activityIds,
                        @QueryParam("database") List<Integer> databaseIds,
                        @QueryParam("indicator") List<Integer> indicatorIds,
                        @QueryParam("partner") List<Integer> partnerIds,
                        @QueryParam("attribute") List<Integer> attributeIds,
                        @QueryParam("location") List<Integer> locationIds,
                        @QueryParam("format") String format) throws IOException {


        throw new WebApplicationException(ServiceUnavailable.serviceUnavailable());

    }


    @GET @Path("/points")
    public Response queryPoints(@QueryParam("activity") List<Integer> activityIds,
                                @QueryParam("database") List<Integer> databaseIds,
                                @QueryParam("callback") String callback) throws IOException {

        throw new WebApplicationException(ServiceUnavailable.serviceUnavailable());

    }


    @GET
    @Path("{id}/monthlyReports")
    @Produces("application/json")
    public String queryMonthlyReports(@PathParam("id") int siteId) throws IOException {

        throw new WebApplicationException(ServiceUnavailable.serviceUnavailable());
    }

    @Path("/cube")
    public Response getCube() {
        return ServiceUnavailable.serviceUnavailable();
    }
}
