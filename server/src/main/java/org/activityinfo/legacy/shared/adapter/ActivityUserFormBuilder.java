package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Strings;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.*;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.activityCategoryFolderId;

/**
 * Adapts a Legacy "Activity" model to a FormClass
 */
public class ActivityUserFormBuilder {


    private final ActivityDTO activity;

    private FormClass siteForm;

    public ActivityUserFormBuilder(ActivityDTO activity) {
        assert activity != null;
        this.activity = activity;
    }

    public FormClass build() {
        ResourceId classId = CuidAdapter.activityFormClass(activity.getId());

        siteForm = new FormClass(classId);
        siteForm.setLabel(activity.getName());

        if (!Strings.isNullOrEmpty(activity.getCategory())) {
            siteForm.setParentId(activityCategoryFolderId(activity.getDatabaseId(), activity.getCategory()));
        } else {
            siteForm.setParentId(CuidAdapter.databaseId(activity.getDatabaseId()));
        }

        FormField partnerField = new FormField(CuidAdapter.field(classId, CuidAdapter.PARTNER_FIELD));
        partnerField.setLabel(I18N.CONSTANTS.partner());
        partnerField.setRange(CuidAdapter.partnerFormClass(activity.getDatabaseId()));
        partnerField.setType(FormFieldType.REFERENCE);
        partnerField.setCardinality(FormFieldCardinality.SINGLE);
        partnerField.setRequired(true);
        siteForm.addElement(partnerField);

        FormField projectField = new FormField(CuidAdapter.field(classId, CuidAdapter.PROJECT_FIELD));
        projectField.setLabel(I18N.CONSTANTS.project());
        projectField.setRange(CuidAdapter.projectFormClass(activity.getDatabaseId()));
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

        for (AttributeGroupDTO group : activity.getAttributeGroups()) {
            FormField attributeField = new FormField(CuidAdapter.attributeGroupField(activity, group));
            attributeField.setLabel(group.getName());
            attributeField.setRange(CuidAdapter.attributeGroupFormClass(group));
            attributeField.setType(FormFieldType.REFERENCE);
            attributeField.setRequired(group.isMandatory());
            if (group.isMultipleAllowed()) {
                attributeField.setCardinality(FormFieldCardinality.MULTIPLE);
            } else {
                attributeField.setCardinality(FormFieldCardinality.SINGLE);
            }
            siteForm.addElement(attributeField);
        }

        for (IndicatorGroup group : activity.groupIndicators()) {
            if (Strings.isNullOrEmpty(group.getName())) {
                addIndicators(siteForm, group);
            } else {
                FormSection section = new FormSection(CuidAdapter.activityFormSection(activity.getId(),
                        group.getName()));
                section.setLabel(group.getName());

                addIndicators(section, group);

                siteForm.addElement(section);
            }
        }

        FormField commentsField = new FormField(CuidAdapter.commentsField(activity.getId()));
        commentsField.setType(FormFieldType.NARRATIVE);
        commentsField.setLabel(I18N.CONSTANTS.comments());
        siteForm.addElement(commentsField);

        return siteForm;
    }

    private static ResourceId locationClass(LocationTypeDTO locationType) {
        if (locationType.isAdminLevel()) {
            return CuidAdapter.adminLevelFormClass(locationType.getBoundAdminLevelId());
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
            field.setCalculation(indicator.getExpression());
            container.addElement(field);
        }
    }
}
