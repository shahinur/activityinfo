package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.model.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Extracts a list of a {@code AttributeDTO}s from a SchemaDTO and converts them to
 * {@code FormInstances}
 */
public class AttributeInstanceListAdapter implements Function<SchemaDTO, List<FormInstance>> {

    private final Predicate<ResourceId> criteria;

    public AttributeInstanceListAdapter(Criteria criteria) {
        this.criteria = CriteriaEvaluation.evaluatePartiallyOnClassId(criteria);
    }

    @Nullable @Override
    public List<FormInstance> apply(SchemaDTO schema) {
        Set<Integer> added = Sets.newHashSet();
        List<FormInstance> instances = Lists.newArrayList();
        for (UserDatabaseDTO db : schema.getDatabases()) {
            for (ActivityDTO activity : db.getActivities()) {
                for (AttributeGroupDTO group : activity.getAttributeGroups()) {
                    ResourceId classId = CuidAdapter.attributeGroupFormClass(group.getId());
                    if (criteria.apply(classId)) {
                        for (AttributeDTO attribute : group.getAttributes()) {
                            if (!added.contains(attribute.getId())) {
                                ResourceId instanceId = CuidAdapter.attributeId(attribute.getId());
                                FormInstance instance = new FormInstance(instanceId, classId);
                                instance.set(CuidAdapter.getFormInstanceLabelCuid(instance), attribute.getName());
                                instances.add(instance);
                                added.add(attribute.getId());
                            }
                        }
                    }
                }
            }
        }

        return instances;
    }
}
