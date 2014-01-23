package org.activityinfo.model.shared.form.adapter;

import com.google.appengine.repackaged.com.google.common.base.Strings;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import org.activityinfo.client.i18n.I18N;
import org.activityinfo.model.LocalizedString;
import org.activityinfo.model.shared.Iri;
import org.activityinfo.model.shared.Namespace;
import org.activityinfo.model.shared.form.*;
import org.activityinfo.server.database.hibernate.entity.AttributeGroup;
import org.activityinfo.shared.dto.ActivityDTO;
import org.activityinfo.shared.dto.AttributeGroupDTO;
import org.activityinfo.shared.dto.IndicatorDTO;
import org.activityinfo.shared.dto.IndicatorGroup;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Adapts a Legacy "Activity" object to a UserForm
 */
public class ActivityAdapter  {

    private final ActivityDTO activity;
    private List<FormElement> siteElements = Lists.newArrayList();

    private UserForm siteForm;

    public ActivityAdapter(ActivityDTO activity) {
        this.activity = activity;

        siteForm = new UserForm(Namespace.siteForm(activity.getId()));

        FormField partnerField = new FormField(Namespace.IMPLEMENTED_BY);
        partnerField.setLabel(new LocalizedString(I18N.CONSTANTS.partner()));
        partnerField.setRange(Namespace.PARTNER);
        partnerField.setType(FormFieldType.SINGLE_OBJECT);
        siteForm.addElement(partnerField);


        FormField locationField = new FormField(Namespace.LOCATED_AT);
        locationField.setLabel(new LocalizedString(activity.getLocationType().getName()));
        locationField.setRange(Namespace.locationType(activity.getLocationTypeId()));
        locationField.setType(FormFieldType.SINGLE_OBJECT);

        for(AttributeGroupDTO group : activity.getAttributeGroups()) {
            FormField attributeField = new FormField(Namespace.attributeGroup(group.getId()));
            attributeField.setLabel(new LocalizedString(group.getName()));
            attributeField.setRange(Namespace.attributeGroup(group.getId()));

            if(group.isMultipleAllowed()) {
                attributeField.setType(FormFieldType.MULTIPLE_OBJECT);
            } else {
                attributeField.setType(FormFieldType.SINGLE_OBJECT);
            }
            siteForm.addElement(attributeField);
        }

        for(IndicatorGroup group : activity.groupIndicators()) {
            if(Strings.isNullOrEmpty(group.getName())) {
                addIndicators(siteForm, group);
            } else {
                FormSection section = new FormSection(Namespace.activityFormSection(activity.getId(), group.getName()));
                section.setLabel(new LocalizedString(group.getName()));

                addIndicators(section, group);

                siteForm.addElement(section);

            }
        }

        FormField commentsField = new FormField(Namespace.COMMENTS_PROPERTY);
        commentsField.setType(FormFieldType.NARRATIVE);
        commentsField.setLabel(new LocalizedString(I18N.CONSTANTS.comments()));
        siteForm.addElement(commentsField);

    }

    private void addIndicators(FormElementContainer container, IndicatorGroup group) {
        for(IndicatorDTO indicator : group.getIndicators()) {
            FormField field = new FormField(Namespace.indicatorProperty(indicator.getId()));
            field.setLabel(new LocalizedString(indicator.getName()));
            field.setDescription(new LocalizedString(indicator.getDescription()));
            field.setType(FormFieldType.QUANTITY);
            container.addElement(field);
        }
    }
}
