package org.activityinfo.legacy.shared.adapter.bindings;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.client.KeyGenerator;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.model.*;

import java.util.Map;
import java.util.Set;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.*;

/**
 * Creates a SiteBinding from a SchemaDTO
 */
public class SiteBindingFactory implements Function<SchemaDTO, SiteBinding> {

    private final int activityId;
    private final KeyGenerator keyGenerator = new KeyGenerator();

    public SiteBindingFactory(int activityId) {
        this.activityId = activityId;
    }

    @Override
    public SiteBinding apply(SchemaDTO input) {
        ActivityDTO activity = input.getActivityById(activityId);
        SiteBinding binding = new SiteBinding(activity);
        binding.addNestedField(partnerField(activity.getId()), PARTNER_DOMAIN, "partner");
        binding.addNestedField(projectField(activity.getId()), PROJECT_DOMAIN, "project");

        binding.addField(field(CuidAdapter.activityFormClass(activityId), START_DATE_FIELD), "date1");
        binding.addField(field(CuidAdapter.activityFormClass(activityId), END_DATE_FIELD), "date2");

        if (activity.getLocationType().isAdminLevel()) {
            binding.addField(new AdminLevelLocationBinding(activity.getLocationType().getBoundAdminLevelId()));
        } else {
            binding.addNestedField(locationField(activity.getId()), LOCATION_DOMAIN, "location");
        }

        for (AttributeGroupDTO group : activity.getAttributeGroups()) {
            binding.addField(new AttributeGroupBinding(activity, group));
        }

        for (IndicatorDTO indicator : activity.getIndicators()) {
            binding.addField(indicatorField(indicator.getId()), IndicatorDTO.getPropertyName(indicator.getId()));
        }

        binding.addField(commentsField(activityId), "comments");

        return binding;
    }


    private class AttributeGroupBinding implements FieldBinding<SiteDTO> {

        private final AttributeGroupDTO group;
        private ResourceId fieldId;

        private AttributeGroupBinding(ActivityDTO activity, AttributeGroupDTO group) {
            this.group = group;
            fieldId = CuidAdapter.attributeGroupField(activity, group);
        }

        @Override
        public void updateInstanceFromModel(FormInstance instance, SiteDTO model) {
            Set<ResourceId> references = Sets.newHashSet();
            for (AttributeDTO attribute : group.getAttributes()) {
                int id = attribute.getId();
                if (model.getAttributeValue(id)) {
                    references.add(CuidAdapter.attributeId(id));
                }
            }
            if (!references.isEmpty()) {
                instance.set(fieldId, references);
            }
        }

        @Override
        public void populateChangeMap(FormInstance instance, Map<String, Object> changeMap) {
            Set<ResourceId> references = instance.getReferences(fieldId);
            for (ResourceId attributeResourceId : references) {
                changeMap.put(AttributeDTO.getPropertyName(getLegacyIdFromCuid(attributeResourceId)), true);
            }
        }
    }

    private class AdminLevelLocationBinding implements FieldBinding<SiteDTO> {

        private final int levelId;

        private AdminLevelLocationBinding(int levelId) {
            this.levelId = levelId;
        }

        @Override
        public void updateInstanceFromModel(FormInstance instance, SiteDTO model) {
            LocationDTO dummyLocation = model.getLocation();
            final AdminEntityDTO adminEntity = dummyLocation.getAdminEntity(levelId);
            instance.set(locationField(activityId), Sets.newHashSet(entity(adminEntity.getId())));
        }

        @Override
        public void populateChangeMap(FormInstance instance, Map<String, Object> changeMap) {
            changeMap.put("locationId", keyGenerator.generateInt());
        }
    }

}
