package org.activityinfo.model.shared;

import org.activityinfo.model.form.FieldId;
import org.activityinfo.model.resource.ResourceId;

/**
 * Provides an adapter between legacy ids, which are either random or sequential 32-bit integers but only
 * guaranteed to be unique within a table, and Collision Resistant Universal Ids (CUIDs) which
 * will serve as the identifiers for all user-created objects.
 */
public class CuidAdapter {

    public static final char COUNTRY_DOMAIN = 'c';

    public static final char SITE_DOMAIN = 's';

    public static final char ACTIVITY_DOMAIN = 'a';

    public static final char ACTIVITY_MONTHLY_REPORT = 'M';

    public static final char MONTHLY_REPORT_INSTANCE = 'q';

    public static final char LOCATION_DOMAIN = 'g'; // avoid lower case l !

    public static final char LOCATION_TYPE_DOMAIN = 'L'; // avoid lower case l !

    public static final char PARTNER_DOMAIN = 'p';

    public static final char PARTNER_FORM_CLASS_DOMAIN = 'P';

    public static final char INDICATOR_DOMAIN = 'i';

    public static final char ATTRIBUTE_GROUP_DOMAIN = 'A';

    public static final char ATTRIBUTE_DOMAIN = 't';

    public static final char DATABASE_DOMAIN = 'd';

    public static final char ADMIN_LEVEL_DOMAIN = 'E';

    public static final char ADMIN_ENTITY_DOMAIN = 'e';

    public static final char PROJECT_CLASS_DOMAIN = 'R';

    public static final char PROJECT_DOMAIN = 'r';

    public static final char ACTIVITY_CATEGORY_DOMAIN = 'C';

    public static final char USER_DOMAIN = 'u';


    public static final String NAME_FIELD = "name";
    public static final String ADMIN_PARENT_FIELD = "parent";
    public static final String CODE_FIELD = "code";
    public static final String AXE_FIELD = "axe";
    public static final String GEOMETRY_FIELD = "geometry";
    public static final String ADMIN_FIELD = "adminUnit";
    public static final String PARTNER_FIELD = "partner";
    public static final String PROJECT_FIELD = "project";
    public static final String DATE_FIELD = "date";
    public static final String FULL_NAME_FIELD = "description";
    public static final String LOCATION_FIELD = "location";
    public static final String START_DATE_FIELD = "startDate";
    public static final String END_DATE_FIELD = "endDate";
    public static final String COMMENT_FIELD = "comment";
    public static final String CLASS_FIELD = "class";

    public static final int BLOCK_SIZE = 6;

    public static final int RADIX = Character.MAX_RADIX;
    public static final String DESCRIPTION_FIELD = "description";

    /**
     * Avoid instance creation.
     */
    private CuidAdapter() {
    }

    public static ResourceId newFormInstance(ResourceId formClassId) {
        return ResourceId.generateId();
    }

    public static ResourceId newFormInstance() {
        return ResourceId.generateId();
    }

    public static ResourceId newFormClass() {
        return ResourceId.generateId();
    }


    public static final int getLegacyIdFromCuid(String cuid) {
        return Integer.parseInt(cuid.substring(1), Character.MAX_RADIX);
    }

    public static final ResourceId cuid(char domain, int id) {
        return ResourceId.create(domain + block(id));
    }

    public static int getLegacyIdFromCuid(ResourceId id) {
        return getLegacyIdFromCuid(id.asString());
    }

    /**
     * @return the {@code FormField} ResourceId for the Partner field of a given Activity {@code FormClass}
     * @param activityId
     */
    public static ResourceId partnerFieldId(int activityId) {
        return FieldId.fieldId(activityFormClass(activityId), PARTNER_FIELD);
    }

    public static ResourceId projectField() {
        return ResourceId.create("project");
    }

    public static ResourceId partnerInstanceId(int databaseId, int partnerId) {
        return ResourceId.create(Character.toString(DATABASE_DOMAIN) + databaseId +
                                 Character.toString(PARTNER_DOMAIN) + partnerId);
    }

    /**
     * @return the {@code FormField}  ResourceId for the Location field of a given Activity {@code FormClass}
     */
    public static ResourceId locationField() {
        return ResourceId.create("location");
    }

    /**
     * @return the {@code FormClass} ResourceId for a given LocationType
     */
    public static ResourceId locationFormClass(int locationTypeId) {
        return cuid(LOCATION_TYPE_DOMAIN, locationTypeId);
    }

    public static ResourceId locationInstanceId(int locationId) {
        return cuid(LOCATION_DOMAIN, locationId);
    }

    public static ResourceId adminLevelFormClass(int adminLevelId) {
        return cuid(ADMIN_LEVEL_DOMAIN, adminLevelId);
    }

    public static ResourceId entity(int adminEntityId) {
        return cuid(ADMIN_ENTITY_DOMAIN, adminEntityId);
    }

    /**
     * Generates a CUID for a FormField in a given previously-built-in FormClass using
     * the FormClass's CUID and a field index.
     *
     * @param fieldIndex
     * @return
     */
    public static ResourceId field(String fieldIndex) {
        return ResourceId.create(fieldIndex);
    }

    /**
     * @return the {@code FormClass} ResourceId for a given Activity
     */
    public static ResourceId activityFormClass(int activityId) {
        return ResourceId.create(ACTIVITY_DOMAIN + block(activityId));
    }


    /**
     * @return the {@code FormClass} ResourceId for a given Activity
     */
    public static ResourceId commentsField(int activityId) {
        //        return new ResourceId(ACTIVITY_DOMAIN + block(activityId) + "C");
        return field(COMMENT_FIELD);
    }

    /**
     * @return the {@code FormField} ResourceId for the indicator field within a given
     * Activity {@code FormClass}
     */
    public static String indicatorFieldName(int indicatorId) {
        return "I" + indicatorId;
    }

    public static ResourceId indicatorFieldId(int activityId, int indicatorId) {
        return FieldId.fieldId(activityFormClass(activityId), indicatorFieldName(indicatorId));
    }

    public static ResourceId siteField(int siteId) {
        return cuid(INDICATOR_DOMAIN, siteId);
    }

    /**
     * @return the {@code FormField} ResourceId for the field of a given Activity {@code FormClass} that
     * references the given AttributeGroup FormClass
     */
    public static String attributeGroupName(int attributeGroupId) {
        return "AG" + attributeGroupId;
    }

    public static ResourceId activityCategoryFolderId(int dbId, String category) {
        return ResourceId.create(ACTIVITY_CATEGORY_DOMAIN + block(dbId) + block(category.hashCode()));
    }

    public static ResourceId attributeGroupFormClass(int attributeGroupId) {
        return cuid(ATTRIBUTE_GROUP_DOMAIN, attributeGroupId);
    }

    public static ResourceId attributeId(int attributeId) {
        return cuid(ATTRIBUTE_DOMAIN, attributeId);
    }

    /**
     * @param databaseId the id of the user database
     * @return the {@code FormClass} ResourceId for a given database's list of partners.
     */
    public static ResourceId partnerFormClass(int databaseId) {
        return resourceId(PARTNER_FORM_CLASS_DOMAIN, databaseId);
    }

    /**
     * @param databaseId the id of the user database
     * @return the {@code FormClass} ResourceId for a given database's list of projects.
     */
    public static ResourceId projectFormClass(int databaseId) {
        return cuid(PROJECT_CLASS_DOMAIN, databaseId);
    }

    /**
     * @return the {@code FormSection} ResourceId for a given indicator category within an
     * Activity {@code FormClass}
     */
    public static ResourceId activityFormSection(int id, String name) {
        return ResourceId.create(ACTIVITY_DOMAIN + block(id) + block(name.hashCode()));
    }

    private static String block(int id) {
        return Integer.toString(id);
    }

    public static int getBlock(ResourceId resourceId, int blockIndex) {
        throw new UnsupportedOperationException("this should no longer be necessary!!");
    }

    public static ResourceId databaseId(int databaseId) {
        return cuid(DATABASE_DOMAIN, databaseId);
    }

    public static ResourceId generateLocationCuid() {
        return ResourceId.generateId();
    }

    public static ResourceId getFormInstanceLabelCuid() {
        return ResourceId.create("label");
    }

    public static ResourceId resourceId(char domain, int legacyId) {
        if(legacyId == 0) {
            return null;
        }
        return ResourceId.create(Character.toString(domain) + legacyId);
    }

    public static ResourceId getNameFieldId() {
        return field(NAME_FIELD);
    }

    public static ResourceId getPointFieldId() {
        return field(GEOMETRY_FIELD);
    }

    public static ResourceId getAxeFieldId() {
        return field(AXE_FIELD);
    }

    public static ResourceId getAdminFieldId() {
        return field(ADMIN_FIELD);
    }
}
