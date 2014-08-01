package org.activityinfo.server.endpoint.export;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.activityinfo.legacy.shared.auth.AuthenticatedUser;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.command.FilterUrlSerializer;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.legacy.shared.model.UserDatabaseDTO;
import org.activityinfo.server.authentication.ServerSideAuthProvider;
import org.activityinfo.server.command.DispatcherSync;

import javax.inject.Provider;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;

@Singleton
public class ExportSitesTask extends HttpServlet {

    public static final String END_POINT = "/tasks/export";

    public static final String EXPORT_BUCKET_NAME = "activityinfo-generated";

    private Provider<DispatcherSync> dispatcher;
    private ServerSideAuthProvider authProvider;

    @Inject
    public ExportSitesTask(Provider<DispatcherSync> dispatcher, ServerSideAuthProvider authProvider) {
        this.dispatcher = dispatcher;
        this.authProvider = authProvider;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // authenticate this task
        authProvider.set(new AuthenticatedUser("",
                Integer.parseInt(req.getParameter("userId")),
                req.getParameter("userEmail")));


        // create the workbook
        SiteExporter export = buildExcelWorkbook(req);

        // Save to GCS
        GcsService gcs = GcsServiceFactory.createGcsService();
        GcsFileOptions fileOptions = new GcsFileOptions.Builder()
                .mimeType("application/vnd.ms-excel")
                .contentDisposition("attachment; filename=" + req.getParameter("filename"))
                .build();
        GcsFilename fileName = new GcsFilename(EXPORT_BUCKET_NAME,
                req.getParameter("exportId"));

        try(OutputStream outputStream = Channels.newOutputStream(gcs.createOrReplace(fileName, fileOptions))) {
            export.getBook().write(outputStream);
        }
    }

    private SiteExporter buildExcelWorkbook(HttpServletRequest req) {
        Filter filter = FilterUrlSerializer.fromQueryParameter(req.getParameter("filter"));
        SchemaDTO schema = dispatcher.get().execute(new GetSchema());

        SiteExporter export = new SiteExporter(dispatcher.get());
        for (UserDatabaseDTO db : schema.getDatabases()) {
            for (ActivityDTO activity : db.getActivities()) {
                if (!filter.isRestricted(DimensionType.Activity) ||
                    filter.getRestrictions(DimensionType.Activity).contains(activity.getId())) {
                    export.export(activity, filter);
                }
            }
        }
        export.done();
        return export;
    }
}
