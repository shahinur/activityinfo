package org.activityinfo.store.migrate;

import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.model.DTOViews;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.legacy.shared.model.UserDatabaseDTO;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.server.command.handler.GetSchemaHandler;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.endpoint.rest.SchemaCsvWriter;
import org.activityinfo.server.util.jaxrs.Utf8JacksonJsonProvider;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.store.hrd.HrdResourceStore;
import org.activityinfo.store.test.TestResourceStore;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MigrateDatabaseTaskTest {

    @Test
    public void test() throws Exception {

        TestResourceStore testStore = new TestResourceStore();
        testStore.setUp();
        HrdResourceStore store = (HrdResourceStore) testStore.unwrap();

        Properties properties = new Properties();
       // properties.setProperty(MigrateDatabaseTask.MIGRATION_SOURCE_URL, "jdbc:mysql://173.194.241.81:3306/activityinfo");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_SOURCE_URL, "jdbc:mysql://localhost:3306/activityinfo");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_USER, "root");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_PASS, "root");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_DRIVER_CLASS, "com.mysql.jdbc.Driver");

        DeploymentConfiguration config = new DeploymentConfiguration(properties);

        AuthenticatedUser mithun = new AuthenticatedUser(11);
        MigrateDatabaseTask migrator = new MigrateDatabaseTask(store, config, mithun);
        migrator.migrate(524);

        User mithunEntity = new User();
        mithunEntity.setId(mithun.getId());
        GetSchemaHandler handler = new GetSchemaHandler(store);
        SchemaDTO schema = (SchemaDTO) handler.execute(new GetSchema(), mithunEntity);

        assertThat(schema.getCountries(), hasSize(1));
        assertThat(schema.getCountries().get(0).getId(), equalTo(267));
        assertThat(schema.getCountries().get(0).getName(), equalTo("Bangladesh"));

        assertThat(schema.getDatabases(), hasSize(1));

        UserDatabaseDTO db = schema.getDatabases().get(0);
        assertThat(db.getId(), equalTo(524));
        assertThat(db.getName(), equalTo("QIS 3rd round "));
        assertThat(db.getPartners().size(), equalTo(1));
        assertThat(db.getPartners().get(0).get("id"), equalTo((Object)5240572));
        assertThat(db.getActivities().get(0).getIndicators().get(0).getAggregation(), notNullValue());

        SchemaCsvWriter writer = new SchemaCsvWriter();
        writer.write(db);
        System.out.println(writer.toString());

        ObjectMapper objectMapper = Utf8JacksonJsonProvider.createObjectMapper();
        System.out.println(objectMapper
                .writerWithView(DTOViews.Schema.class)
                .writeValueAsString(schema.getDatabases().get(0)));

    }
}