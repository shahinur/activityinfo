package org.activityinfo.store.migrate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.command.GetSites;
import org.activityinfo.legacy.shared.command.result.SiteResult;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.server.command.handler.GetSchemaHandler;
import org.activityinfo.server.command.handler.GetSitesHandler;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.endpoint.kml.JreIndicatorValueFormatter;
import org.activityinfo.server.endpoint.rest.SchemaCsvWriter;
import org.activityinfo.server.util.jaxrs.Utf8JacksonJsonProvider;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.store.hrd.HrdResourceStore;
import org.activityinfo.store.test.TestResourceStore;
import org.activityinfo.ui.client.page.entry.form.SiteRenderer;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MigrateDatabaseTaskTest {

    public static final int DB_OWNER_ID = 11;
    private TestResourceStore testStore;
    private HrdResourceStore store;
    private DeploymentConfiguration config;

    @Before
    public void setup() {

        testStore = new TestResourceStore();
        testStore.setUp();
        store = (HrdResourceStore) testStore.unwrap();

        Properties properties = new Properties();
        // properties.setProperty(MigrateDatabaseTask.MIGRATION_SOURCE_URL, "jdbc:mysql://173.194.241.81:3306/activityinfo");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_SOURCE_URL, "jdbc:mysql://localhost:3306/activityinfo");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_USER, "root");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_PASS, "root");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_DRIVER_CLASS, "com.mysql.jdbc.Driver");

        config = new DeploymentConfiguration(properties);
    }

    @Test
    public void testUsers() {
        AuthenticatedUser mithun = new AuthenticatedUser(11);
        MigrateDatabaseTask migrator = new MigrateDatabaseTask(store, config, mithun);
        migrator.migrateUsers();
    }

    @Test
    public void test() throws Exception {

        runMigrator(config, store);

        // verify that if we run again we get only updates
        runMigrator(config, store);


        User mithunEntity = new User();
        mithunEntity.setId(DB_OWNER_ID);
        GetSchemaHandler handler = new GetSchemaHandler(store);
        SchemaDTO schema = (SchemaDTO) handler.execute(new GetSchema(), mithunEntity);

        assertThat(schema.getCountries(), hasSize(1));
        assertThat(schema.getCountries().get(0).getId(), equalTo(267));
        assertThat(schema.getCountries().get(0).getName(), equalTo("Bangladesh"));

        assertThat(schema.getDatabases(), hasSize(1));

        UserDatabaseDTO db = schema.getDatabases().get(0);
        assertThat(db.getId(), not(equalTo(524)));
        assertThat(db.getName(), equalTo("QIS 3rd round "));
        assertThat(db.getPartners().size(), equalTo(1));
        assertThat(db.getPartners().get(0).get("id"), equalTo((Object)5240572));

        ActivityDTO activity = findActivity(db, "HH QIS 3rd round");
        assertThat(activity.getIndicators().get(0).getAggregation(), notNullValue());
        assertThat(activity.getLocationType().getAdminLevels(), hasSize(0));

        IndicatorDTO barcode = findIndicator(activity, "Barcode");
        AttributeGroupDTO sex = findAttribute(activity, "HHsx:Sex of the interviewee");


        SchemaCsvWriter writer = new SchemaCsvWriter();
        writer.write(db);
        System.out.println(writer.toString());

        ObjectMapper objectMapper = Utf8JacksonJsonProvider.createObjectMapper();
        String schemaJson = objectMapper
                .writerWithView(DTOViews.Schema.class)
                .writeValueAsString(schema.getDatabases().get(0));
       // System.out.println(schemaJson);

        GetSitesHandler getSitesHandler = new GetSitesHandler(store);
        SiteResult sites = (SiteResult) getSitesHandler.execute(GetSites.byActivity(activity.getId()), mithunEntity);

        assertThat(sites.getData().get(0).getId(), instanceOf(Integer.class));

        SiteRenderer renderer = new SiteRenderer(new JreIndicatorValueFormatter());
        System.out.println(renderer.renderSite(sites.getData().get(100), activity, true));
//
//        for(SiteDTO site : sites.getData()) {
//      //      System.out.println(site.get(barcode.getPropertyName()));
//            System.out.println(site.get(AttributeDTO.getPropertyName(sex.getAttributes().get(0))) +
//                    ", " +
//                    site.get(AttributeDTO.getPropertyName(sex.getAttributes().get(1))));
//        }
    }

    private AuthenticatedUser runMigrator(DeploymentConfiguration config, HrdResourceStore store) throws Exception {
        AuthenticatedUser mithun = new AuthenticatedUser(11);
        MigrateDatabaseTask migrator = new MigrateDatabaseTask(store, config, mithun);
        migrator.migrate(524);
        return mithun;
    }

    private ActivityDTO findActivity(UserDatabaseDTO db, String label) {
        for(ActivityDTO activity : db.getActivities()) {
            if(activity.getName().equals(label)) {
                return activity;
            }
        }
        throw new IllegalArgumentException(label);
    }

    private AttributeGroupDTO findAttribute(ActivityDTO activity, String label) {
        for(AttributeGroupDTO group : activity.getAttributeGroups()) {
            if(group.getLabel().equals(label)) {
                return group;
            }
        }
        throw new IllegalArgumentException(label);
    }

    private IndicatorDTO findIndicator(ActivityDTO activity, String label) {
        for(IndicatorDTO dto : activity.getIndicators()) {
            if(dto.getLabel().equals(label)) {
                return dto;
            }
        }
        throw new IllegalArgumentException(label);
    }
}