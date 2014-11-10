package org.activityinfo.server.command.handler;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.GetFormClass;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.FormClassResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.exception.UnexpectedCommandException;
import org.activityinfo.legacy.shared.impl.CommandHandlerAsync;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.server.database.hibernate.entity.Activity;
import org.activityinfo.server.database.hibernate.entity.User;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

public class GetFormClassHandler implements CommandHandler<GetFormClass> {

    private PermissionOracle permissionOracle;
    private Provider<EntityManager> entityManager;

    @Inject
    public GetFormClassHandler(PermissionOracle permissionOracle, Provider<EntityManager> entityManager) {
        this.permissionOracle = permissionOracle;
        this.entityManager = entityManager;
    }

    @Override
    public CommandResult execute(GetFormClass cmd, User user) throws CommandException {

        Activity activity = entityManager.get().find(Activity.class, CuidAdapter.getLegacyIdFromCuid(cmd.getResourceId()));

        String json = readJson(activity);
        return new FormClassResult(json);
    }

    private String readJson(Activity activity)  {
        if(activity.getGzFormClass() != null) {
            try(Reader reader = new InputStreamReader(
                                    new GZIPInputStream(
                                        new ByteArrayInputStream(activity.getGzFormClass())), Charsets.UTF_8)) {

                return CharStreams.toString(reader);

            } catch (IOException e) {
                throw new UnexpectedCommandException(e);
            }

        } else {
            return activity.getFormClass();
        }
    }
}
