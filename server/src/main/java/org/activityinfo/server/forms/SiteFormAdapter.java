package org.activityinfo.server.forms;

import com.google.common.base.Strings;
import org.activityinfo.core.shared.Cuid;
import java.lang.String;
import org.activityinfo.core.shared.form.*;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.legacy.shared.model.IndicatorGroup;
import org.activityinfo.server.database.hibernate.entity.Activity;
import org.activityinfo.server.database.hibernate.entity.AttributeGroup;
import org.activityinfo.server.database.hibernate.entity.LocationType;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.activityCategoryFolderId;
import static org.activityinfo.legacy.shared.adapter.CuidAdapter.attributeGroupField;


public class SiteFormAdapter extends FormClassAdapter {

    private final Provider<EntityManager> entityManager;
    private FormClass siteForm;

    public SiteFormAdapter(Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
    }

    public FormClass get(int activityId) {

        Activity activity = entityManager.get().find(Activity.class, activityId);

        Cuid classId = CuidAdapter.activityFormClass(activityId);
        siteForm = new FormClass(classId);
        siteForm.setLabel(activity.getName());

        if (!Strings.isNullOrEmpty(activity.getCategory())) {
            siteForm.setParentId(activityCategoryFolderId(activity.getDatabase().getId(), activity.getCategory()));
        } else {
            siteForm.setParentId(CuidAdapter.databaseId(activity.getDatabase().getId()));
        }

        FormField partnerField = new FormField(CuidAdapter.field(classId, CuidAdapter.PARTNER_FIELD));
        partnerField.setLabel(I18N.CONSTANTS.partner());
        partnerField.setRange(CuidAdapter.partnerFormClass(activity.getDatabase().getId()));
        partnerField.setType(FormFieldType.REFERENCE);
        partnerField.setCardinality(FormFieldCardinality.SINGLE);
        partnerField.setRequired(true);
        siteForm.addElement(partnerField);

        FormField projectField = new FormField(CuidAdapter.field(classId, CuidAdapter.PROJECT_FIELD));
        projectField.setLabel(I18N.CONSTANTS.project());
        projectField.setRange(CuidAdapter.projectFormClass(activity.getDatabase().getId()));
        projectField.setType(FormFieldType.REFERENCE);
        projectField.setCardinality(FormFieldCardinality.SINGLE);
        siteForm.addElement(projectField);

        FormField endDateField = new FormField(CuidAdapter.field(classId, CuidAdapter.END_DATE_FIELD));
        endDateField.setLabel(I18N.CONSTANTS.endDate());
        endDateField.setType(FormFieldType.LOCAL_DATE);
        endDateField.setRequired(true);
        siteForm.addElement(endDateField);

        FormField startDateField = new FormField(CuidAdapter.field(classId, CuidAdapter.START_DATE_FIELD));
        startDateField.setLabel(I18N.CONSTANTS.startDate());
        startDateField.setType(FormFieldType.LOCAL_DATE);
        startDateField.setRequired(true);
        siteForm.addElement(startDateField);


        FormField locationField = new FormField(CuidAdapter.locationField(activity.getId()));
        locationField.setLabel(activity.getLocationType().getName());
        locationField.setRange(locationClass(activity.getLocationType()));
        locationField.setType(FormFieldType.REFERENCE);
        locationField.setRequired(true);
        locationField.setCardinality(FormFieldCardinality.SINGLE);
        siteForm.addElement(locationField);

        for (AttributeGroup group : activity.getAttributeGroups()) {
            FormField attributeField = new FormField(attributeGroupField(activity.getId(), group.getId()));
            attributeField.setLabel(group.getName());
            attributeField.setRange(CuidAdapter.attributeGroupFormClass(group.getId()));
            attributeField.setType(FormFieldType.REFERENCE);
            attributeField.setRequired(group.isMandatory());
            if (group.isMultipleAllowed()) {
                attributeField.setCardinality(FormFieldCardinality.MULTIPLE);
            } else {
                attributeField.setCardinality(FormFieldCardinality.SINGLE);
            }
            siteForm.addElement(attributeField);
        }
//
//        for (IndicatorGroup group : activity.groupIndicators()) {
//            if (Strings.isNullOrEmpty(group.getName())) {
//                addIndicators(siteForm, group);
//            } else {
//                FormSection section = new FormSection(CuidAdapter.activityFormSection(activity.getId(),
//                        group.getName()));
//                section.setLabel(new LocalizedString(group.getName()));
//
//                addIndicators(section, group);
//
//                siteForm.addElement(section);
//            }
//        }

        FormField commentsField = new FormField(CuidAdapter.commentsField(activity.getId()));
        commentsField.setType(FormFieldType.NARRATIVE);
        commentsField.setLabel(I18N.CONSTANTS.comments());
        siteForm.addElement(commentsField);

        return siteForm;
    }


    private static Cuid locationClass(LocationType locationType) {
        if (locationType.getBoundAdminLevel() != null) {
            return CuidAdapter.adminLevelFormClass(locationType.getBoundAdminLevel().getId());
        } else {
            return CuidAdapter.locationFormClass(locationType.getId());
        }
    }

    private static void addIndicators(FormElementContainer container, IndicatorGroup group) {
        for (IndicatorDTO indicator : group.getIndicators()) {
            FormField field = new FormField(CuidAdapter.indicatorField(indicator.getId()));
            field.setLabel(indicator.getName());
            field.setDescription(indicator.getDescription());
            field.setType(FormFieldType.QUANTITY);
            field.setUnit(indicator.getUnits());
            container.addElement(field);
        }
    }

}
