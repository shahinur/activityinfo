package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Strings;
import org.activityinfo.core.client.CuidGenerator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Cuids;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.client.KeyGenerator;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.AttributeGroupDTO;
import org.activityinfo.legacy.shared.model.EntityDTO;

/**
 * Provides an adapter between legacy ids, which are either random or sequential 32-bit integers but only
 * guaranteed to be unique within a table, and Collision Resistant Universal Ids (CUIDs) which
 * will serve as the identifiers for all user-created objects.
 */
public class CuidAdapter {

    public static final char COUNTRY_DOMAIN = 'c';

    public static final char SITE_DOMAIN = 's';

    public static final char ACTIVITY_DOMAIN = 'a';

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

    public static final int NAME_FIELD = 1;
    public static final int ADMIN_PARENT_FIELD = 2;
    public static final int CODE_FIELD = 3;
    public static final int AXE_FIELD = 4;
    public static final int GEOMETRY_FIELD = 5;
    public static final int ADMIN_FIELD = 6;
    public static final int PARTNER_FIELD = 7;
    public static final int PROJECT_FIELD = 8;
    public static final int DATE_FIELD = 9;
    public static final int FULL_NAME_FIELD = 10;
    public static final int LOCATION_FIELD = 11;
    public static final int START_DATE_FIELD = 12;
    public static final int END_DATE_FIELD = 13;
    public static final int COMMENT_FIELD = 14;

    public static final int BLOCK_SIZE = 6;

    /**
     * Avoid instance creation.
     */
    private CuidAdapter() {
    }

    public static Cuid newLegacyFormInstanceId(Cuid formClassId) {
        if (formClassId != null) {
            final int newId = new KeyGenerator().generateInt();
            switch (formClassId.getDomain()) {
                case ACTIVITY_DOMAIN:
                    return cuid(SITE_DOMAIN, newId);
                case LOCATION_TYPE_DOMAIN:
                    return locationInstanceId(newId);
                case ATTRIBUTE_GROUP_DOMAIN:
                    return attributeId(newId);
            }
        }
        return CuidGenerator.INSTANCE.nextCuid();
    }

    public static Cuid getFormInstanceLabelCuid(FormInstance formInstance) {
        return CuidAdapter.field(formInstance.getClassId(), NAME_FIELD);
    }

    public static final int getLegacyIdFromCuid(String cuid) {
        return Integer.parseInt(cuid.substring(1), Cuids.RADIX);
    }

    public static final Cuid cuid(char domain, int id) {
        return new Cuid(domain + block(id));
    }

    public static int getLegacyIdFromCuid(Cuid id) {
        return getLegacyIdFromCuid(id.asString());
    }

    /**
     * @return the {@code FormField} Cuid for the Partner field of a given Activity {@code FormClass}
     */
    public static Cuid partnerField(int activityId) {
        return field(activityFormClass(activityId), PARTNER_FIELD);
    }

    public static Cuid projectField(int activityId) {
        return field(activityFormClass(activityId), PROJECT_FIELD);
    }

    public static Cuid partnerInstanceId(int partnerId) {
        return cuid(PARTNER_DOMAIN, partnerId);
    }

    /**
     * @return the {@code FormField}  Cuid for the Location field of a given Activity {@code FormClass}
     */
    public static Cuid locationField(int activityId) {
        return field(activityFormClass(activityId), LOCATION_FIELD);
    }

    /**
     * @return the {@code FormClass} Cuid for a given LocationType
     */
    public static Cuid locationFormClass(int locationTypeId) {
        return cuid(LOCATION_TYPE_DOMAIN, locationTypeId);
    }

    public static Cuid locationInstanceId(int locationId) {
        return cuid(LOCATION_DOMAIN, locationId);
    }

    public static Cuid adminLevelFormClass(int adminLevelId) {
        return cuid(ADMIN_LEVEL_DOMAIN, adminLevelId);
    }


    public static Cuid entity(int adminEntityId) {
        return cuid(ADMIN_ENTITY_DOMAIN, adminEntityId);

    }

    /**
     * Generates a CUID for a FormField in a given previously-built-in FormClass using
     * the FormClass's CUID and a field index.
     *
     * @param classId
     * @param fieldIndex
     * @return
     */
    public static Cuid field(Cuid classId, int fieldIndex) {
        return new Cuid(classId.asString() + block(fieldIndex));
    }

    /**
     * @return the {@code FormClass} Cuid for a given Activity
     */
    public static Cuid activityFormClass(int activityId) {
        return new Cuid(ACTIVITY_DOMAIN + block(activityId));
    }


    /**
     * @return the {@code FormClass} Cuid for a given Activity
     */
    public static Cuid commentsField(int activityId) {
        //        return new Cuid(ACTIVITY_DOMAIN + block(activityId) + "C");
        return field(activityFormClass(activityId), COMMENT_FIELD);
    }

    /**
     * @return the {@code FormField} Cuid for the indicator field within a given
     * Activity {@code FormClass}
     */
    public static Cuid indicatorField(int indicatorId) {
        return cuid(INDICATOR_DOMAIN, indicatorId);
    }

    public static Cuid attributeField(int attributeId) {
        return cuid(ATTRIBUTE_DOMAIN, attributeId);
    }

    public static Cuid siteField(int siteId) {
        return cuid(INDICATOR_DOMAIN, siteId);
    }

    /**
     * @return the {@code FormField} Cuid for the field of a given Activity {@code FormClass} that
     * references the given AttributeGroup FormClass
     */
    public static Cuid attributeGroupField(ActivityDTO activity, AttributeGroupDTO group) {
        return new Cuid(ACTIVITY_DOMAIN + block(activity) + "a" +
                        Integer.toString(group.getId(), Cuids.RADIX));
    }

    /**
     * @return the {@code FormField} Cuid for the field of a given Activity {@code FormClass} that
     * references the given AttributeGroup FormClass
     */
    public static Cuid attributeGroupField(int activityId, int attributeGroupId) {
        return new Cuid(ACTIVITY_DOMAIN + block(activityId) + "a" +
                        Integer.toString(attributeGroupId, Cuids.RADIX));
    }

    public static Cuid activityCategoryFolderId(int dbId, String category) {
        return new Cuid(ACTIVITY_CATEGORY_DOMAIN + block(dbId) + block(category.hashCode()));
    }

    /**
     * @return the {@code FormClass} Cuid for a given AttributeGroup
     */
    public static Cuid attributeGroupFormClass(AttributeGroupDTO group) {
        return attributeGroupFormClass(group.getId());
    }

    public static Cuid attributeGroupFormClass(int attributeGroupId) {
        return cuid(ATTRIBUTE_GROUP_DOMAIN, attributeGroupId);
    }

    public static Cuid attributeId(int attributeId) {
        return cuid(ATTRIBUTE_DOMAIN, attributeId);
    }

    /**
     * @param databaseId the id of the user database
     * @return the {@code FormClass} Cuid for a given database's list of partners.
     */
    public static Cuid partnerFormClass(int databaseId) {
        return cuid(PARTNER_FORM_CLASS_DOMAIN, databaseId);
    }

    /**
     * @param databaseId the id of the user database
     * @return the {@code FormClass} Cuid for a given database's list of projects.
     */
    public static Cuid projectFormClass(int databaseId) {
        return cuid(PROJECT_CLASS_DOMAIN, databaseId);
    }

    /**
     * @return the {@code FormSection} Cuid for a given indicator category within an
     * Activity {@code FormClass}
     */
    public static Cuid activityFormSection(int id, String name) {
        return new Cuid(ACTIVITY_DOMAIN + block(id) + block(name.hashCode()));
    }

    private static String block(int id) {
        return Strings.padStart(Integer.toString(id, Cuids.RADIX), BLOCK_SIZE, '0');
    }

    public static int getBlock(Cuid cuid, int blockIndex) {
        int startIndex = 1 + (blockIndex * BLOCK_SIZE);
        String block = cuid.asString().substring(startIndex, startIndex + BLOCK_SIZE);
        return Integer.parseInt(block, Cuids.RADIX);
    }

    private static String block(EntityDTO entity) {
        return block(entity.getId());
    }

    private static Cuid cuid(char domain, EntityDTO entityDTO) {
        return cuid(domain, entityDTO.getId());
    }

    public static Cuid databaseId(int databaseId) {
        return cuid(DATABASE_DOMAIN, databaseId);
    }

    public static Cuid generateLocationCuid() {
        return locationInstanceId(new KeyGenerator().generateInt());
    }
}
