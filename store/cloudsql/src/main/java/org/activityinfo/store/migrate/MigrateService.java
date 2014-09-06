package org.activityinfo.store.migrate;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.store.cloudsql.ConnectionProvider;
import org.activityinfo.store.hrd.HrdResourceStore;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/service/migrate")
public class MigrateService {


    @POST
    @Path("database/{id}")
    public void startMigration(@PathParam("id") int databaseId) {

        QueueFactory.getDefaultQueue().add(TaskOptions.Builder.withUrl("/service/migrate/run")
            .param("databaseId", Integer.toString(databaseId)));

    }



    @POST
    @Path("run")
    public void runMigration(@InjectParam AuthenticatedUser user,
                             @InjectParam ConnectionProvider provider,
                             @InjectParam HrdResourceStore store, @FormParam("databaseId") int databaseId) throws Exception {
        new MigrateDatabaseTask(user, store, provider).migrate(databaseId);
    }

}
