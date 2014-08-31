package org.activityinfo.ui.client.service;


import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.formTree.FieldPath;
import org.activityinfo.model.legacy.InstanceQuery;
import org.activityinfo.model.legacy.Projection;
import org.activityinfo.model.legacy.criteria.ClassCriteria;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.geo.GeoPoint;
import org.activityinfo.promise.Promise;
import org.activityinfo.promise.PromiseMatchers;
import org.activityinfo.service.store.ResourceLocator;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.activityinfo.model.legacy.CuidAdapter.*;
import static org.activityinfo.store.test.TestResourceStore.createLocator;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ResourceLocatorAdaptorTest {

    private static final int CAUSE_ATTRIBUTE_GROUP_ID = 1;

    private static final int PROVINCE_ADMIN_LEVEL_ID = 1;

    private static final ResourceId PROVINCE_CLASS = adminLevelFormClass(PROVINCE_ADMIN_LEVEL_ID);

    private static final int PEAR_DATABASE_ID = 1;

    private static final int HEALTH_CENTER_LOCATION_TYPE = 1;

    private static final ResourceId HEALTH_CENTER_CLASS = locationFormClass(HEALTH_CENTER_LOCATION_TYPE);

    private static final int NFI_DIST_ID = 1;

    private static final ResourceId NFI_DIST_FORM_CLASS = activityFormClass(NFI_DIST_ID);

    public static final int VILLAGE_TYPE_ID = 1;

    public static final ResourceId VILLAGE_CLASS = locationFormClass(VILLAGE_TYPE_ID);

    public static final int IRUMU = 21;


    private ResourceLocator resourceLocator;

    @Before
    public final void setup() throws IOException {
        resourceLocator = createLocator("sites-simple1.json");
    }

    @Test
    public void simpleAdminEntityQuery() {

        assertThat(queryByClass(adminLevelFormClass(PROVINCE_ADMIN_LEVEL_ID)), Matchers.hasSize(4));
    }

    @Test
    public void simplePartnerQuery() {
        assertThat(queryByClass(partnerFormClass(PEAR_DATABASE_ID)), Matchers.hasSize(3));
    }

    @Test
    public void simpleLocationQuery() {
        assertThat(queryByClass(locationFormClass(HEALTH_CENTER_LOCATION_TYPE)), Matchers.hasSize(4));
    }

    @Test
    public void getLocation() throws IOException {

        ResourceLocator jordanLocator = createLocator("jordan-locations.json");

        ResourceId classId = locationFormClass(50512);
        FormInstance instance = PromiseMatchers.assertResolves(jordanLocator.getFormInstance(locationInstanceId(
                1590565828)));
        Set<ResourceId> adminUnits = instance.getReferences(field(classId, ADMIN_FIELD));
        System.out.println(adminUnits);
    }


    @Test
    public void persistLocation() {

        FormInstance instance = new FormInstance(newLegacyFormInstanceId(HEALTH_CENTER_CLASS),
                HEALTH_CENTER_CLASS);
        instance.set(field(HEALTH_CENTER_CLASS, NAME_FIELD), "CS Ubuntu");
        instance.set(field(HEALTH_CENTER_CLASS, GEOMETRY_FIELD), new GeoPoint(-1, 13));
        instance.set(field(HEALTH_CENTER_CLASS, ADMIN_FIELD), entity(IRUMU));

        PromiseMatchers.assertResolves(resourceLocator.persist(instance));

        // ensure that everything worked out
//        GetLocations query = new GetLocations(getLegacyIdFromCuid(instance.getId()));
//        LocationResult result = execute(query);
//        LocationDTO location = result.getData().get(0);
//
//        assertThat(location.getName(), equalTo("CS Ubuntu"));
//        assertThat(location.getAdminEntity(1).getName(), equalTo("Ituri"));
//        assertThat(location.getAdminEntity(2).getName(), equalTo("Irumu"));
//        assertThat(location.getLatitude(), equalTo(-1d));
//        assertThat(location.getLongitude(), equalTo(13d));
    }

    @Test
    public void updateLocation() {

//        <location locationId="1" name="Penekusu Kivu" locationTypeId="1"
//        X="1.532" Y="27.323" timeEdited="1"/>
//        <locationAdminLink locationId="1" adminEntityId="2"/>
//        <locationAdminLink locationId="1" adminEntityId="12"/>

        FormInstance instance = PromiseMatchers.assertResolves(resourceLocator.getFormInstance(locationInstanceId(1)));
        instance.set(field(HEALTH_CENTER_CLASS, NAME_FIELD), "New Penekusu");

        PromiseMatchers.assertResolves(resourceLocator.persist(instance));

//        GetLocations query = new GetLocations(1);
//        LocationResult result = execute(query);
//        LocationDTO location = result.getData().get(0);
//
//        assertThat(location.getName(), equalTo("New Penekusu"));
//        assertThat(location.getLocationTypeId(), equalTo(1));
//        assertThat(location.getLatitude(), equalTo(27.323));
//        assertThat(location.getLongitude(), equalTo(1.532));
//        assertThat(location.getAdminEntity(1).getId(), equalTo(2));
//        assertThat(location.getAdminEntity(2).getId(), equalTo(12));
    }

    @Test
    public void projection() {

        // fields to request
        FieldPath locationName = new FieldPath(getNameFieldId(HEALTH_CENTER_CLASS));
        FieldPath locationAdminUnit = new FieldPath(getAdminFieldId(HEALTH_CENTER_CLASS));
        FieldPath locationAdminUnitName = new FieldPath(locationAdminUnit,
                getNameFieldId(PROVINCE_CLASS));


        List<Projection> projections = PromiseMatchers.assertResolves(resourceLocator.query(new InstanceQuery(Lists
                .newArrayList(locationName, locationAdminUnitName), new ClassCriteria(HEALTH_CENTER_CLASS))));

        System.out.println(Joiner.on("\n").join(projections));
    }

    private List<FormInstance> queryByClass(ResourceId classId) {
        Promise<List<FormInstance>> promise = resourceLocator.queryInstances(new ClassCriteria(classId));

        List<FormInstance> list = PromiseMatchers.assertResolves(promise);

        System.out.println(Joiner.on("\n").join(list));
        return list;
    }


    @Test
    public void locationProjection() {

        FieldPath villageName = new FieldPath(getNameFieldId(VILLAGE_CLASS));
        FieldPath provinceName = new FieldPath(
                getAdminFieldId(VILLAGE_CLASS),
                field(PROVINCE_CLASS, NAME_FIELD));

        List<Projection> projections = PromiseMatchers.assertResolves(resourceLocator.query(new InstanceQuery(asList(
                        villageName,
                        provinceName), new ClassCriteria(VILLAGE_CLASS))));

        System.out.println(Joiner.on("\n").join(projections));

        assertThat(projections.size(), equalTo(4));
        assertThat(projections.get(0).getStringValue(provinceName), equalTo("Sud Kivu"));
    }


    @Test
    public void deleteLocation() {

        ResourceId instanceToDelete = locationInstanceId(1);
        resourceLocator.remove(Arrays.asList(instanceToDelete));

        List<FormInstance> formInstances = PromiseMatchers.assertResolves(resourceLocator.queryInstances(new
                ClassCriteria(
                locationFormClass(1))));

        for(FormInstance instance : formInstances) {
            if(instance.getId().equals(instanceToDelete)) {
                throw new AssertionError();
            }
        }
    }

    @Test
    public void siteProjections() {

        ResourceId partnerClassId = partnerFormClass(PEAR_DATABASE_ID);

        FieldPath villageName = new FieldPath(
                locationField(NFI_DIST_ID),
                getNameFieldId(VILLAGE_CLASS));
        FieldPath provinceName = new FieldPath(
                locationField(NFI_DIST_ID),
                getAdminFieldId(VILLAGE_CLASS),
                field(PROVINCE_CLASS, NAME_FIELD));
        FieldPath partnerName = new FieldPath(field(activityFormClass(NFI_DIST_ID), PARTNER_FIELD),
                field(partnerClassId, NAME_FIELD));
        FieldPath partnerFullName = new FieldPath(field(activityFormClass(NFI_DIST_ID), PARTNER_FIELD),
                field(partnerClassId, FULL_NAME_FIELD));
        FieldPath indicator1 = new FieldPath(indicatorField(1));
        FieldPath startDate = new FieldPath(field(NFI_DIST_FORM_CLASS, START_DATE_FIELD));
        FieldPath endDate = new FieldPath(field(NFI_DIST_FORM_CLASS, END_DATE_FIELD));


        List<Projection> projections = PromiseMatchers.assertResolves(resourceLocator.query(new InstanceQuery(asList(
                partnerName,
                villageName,
          //      provinceName,
                indicator1,
                endDate,
                partnerFullName), new ClassCriteria(NFI_DIST_FORM_CLASS))));

        System.out.println(Joiner.on("\n").join(projections));

        final Projection firstProjection = projections.get(0);
        assertThat(projections.size(), equalTo(3));
        assertThat(firstProjection.getValue(partnerFullName), is(nullValue()));
        assertThat(firstProjection.getStringValue(villageName), equalTo("Boga"));
   //     assertThat(firstProjection.getStringValue(provinceName), equalTo("Sud Kivu"));
        assertThat(firstProjection.getValue(startDate), is(nullValue()));
        assertThat(firstProjection.getValue(indicator1), is(not(nullValue())));
    //    assertThat(firstProjection.getValue(endDate), equalTo((Object) new LocalDate(2009, 1, 2)));
        assertThat(firstProjection.getValue(endDate), equalTo((Object)"2008-10-06"));
    }

}
