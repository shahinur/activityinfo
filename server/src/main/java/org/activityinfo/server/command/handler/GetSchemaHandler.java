package org.activityinfo.server.command.handler;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.server.command.handler.adapter.CountryProvider;
import org.activityinfo.server.command.handler.adapter.UserDatabaseBuilder;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.StoreReader;

import java.util.Collections;
import java.util.List;

public class GetSchemaHandler implements CommandHandler<GetSchema> {

    private final ResourceStore store;

    @Inject
    public GetSchemaHandler(ResourceStore store) {
        this.store = store;
    }

    @Override
    public CommandResult execute(GetSchema cmd, User userEntity) throws CommandException {
        AuthenticatedUser user = new AuthenticatedUser(userEntity.getId());
        try(StoreReader reader = store.openReader(user)) {

            List<ResourceNode> workspaces = reader.getOwnedOrSharedWorkspaces();
            Collections.sort(workspaces, Ordering.natural().onResultOf(LabelOf.INSTANCE));

            CountryProvider countryProvider = new CountryProvider(reader);

            SchemaDTO schema = new SchemaDTO();
            for(ResourceNode workspace : workspaces) {
                UserDatabaseBuilder database = new UserDatabaseBuilder(user, reader, countryProvider, workspace);
                schema.getDatabases().add(database.build());
            }

            schema.getCountries().addAll(countryProvider.getCountries());

            return schema;
        }
    }

    public enum LabelOf implements Function<ResourceNode, String> {
        INSTANCE {
            @Override
            public String apply(ResourceNode input) {
                return input.getLabel();
            }
        }
    }

}
