package org.activityinfo.ui.client.component.form.model;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.application.ApplicationProperties;
import org.activityinfo.core.shared.criteria.IdCriteria;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.component.form.field.hierarchy.Hierarchy;
import org.activityinfo.ui.client.component.form.field.hierarchy.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.activityinfo.core.shared.application.ApplicationProperties.LABEL_PROPERTY;
import static org.activityinfo.core.shared.application.ApplicationProperties.PARENT_PROPERTY;

/**
 * View Model for classes which represent a hierarchical selection
 */
public class HierarchyViewModel implements FieldViewModel {
    private ResourceId fieldId;
    private Hierarchy hierarchy;
    private Map<ResourceId, Projection> selection = new HashMap<>();

    public HierarchyViewModel(ResourceId fieldId, Hierarchy hierarchy) {
        this.fieldId = fieldId;
        this.hierarchy = hierarchy;
    }

    @Override
    public ResourceId getFieldId() {
        return fieldId;
    }

    public Hierarchy getTree() {
        return hierarchy;
    }


    public static Promise<FieldViewModel> build(ResourceLocator locator, FormTree.Node node, Object fieldValue) {
        HierarchyViewModel model = new HierarchyViewModel(node.getFieldId(), new Hierarchy(node));

        Set<ResourceId> referenceIds = (Set<ResourceId>)fieldValue;
        if(referenceIds == null || referenceIds.isEmpty()) {
            return Promise.resolved((FieldViewModel)model);
        } else {
            return fetchLabelAndParentIds(locator, model, referenceIds);
        }
    }

    private static Promise<FieldViewModel> fetchLabelAndParentIds(final ResourceLocator locator,
                                                                  final HierarchyViewModel model,
                                                                  Set<ResourceId> instanceIds) {
        return locator.query(InstanceQuery
        .select(LABEL_PROPERTY, PARENT_PROPERTY)
        .where(new IdCriteria(instanceIds))
        .build())
        .join(new Function<List<Projection>, Promise<FieldViewModel>>() {
            @Nullable
            @Override
            public Promise<FieldViewModel> apply(List<Projection> projections) {

                Set<ResourceId> parents = model.populateSelection(projections);
                if (parents.isEmpty()) {
                    return Promise.<FieldViewModel>resolved(model);
                } else {
                    return fetchLabelAndParentIds(locator, model, parents);
                }
            }
        });
    }

    private Set<ResourceId> populateSelection(List<Projection> projections) {
        Set<ResourceId> parents = Sets.newHashSet();
        for(Projection projection : projections) {
            Level level = hierarchy.getLevel(projection.getRootClassId());
            if(level != null) {
                selection.put(projection.getRootClassId(), projection);
                if(!level.isRoot()) {
                    ResourceId parentId = projection.getReferenceValue(ApplicationProperties.PARENT_PROPERTY)
                            .iterator().next();
                    assert parentId != null;
                    parents.add(parentId);
                }
            }
        }
        parents.removeAll(selection.keySet());
        return parents;
    }

    public Map<ResourceId, Projection> getSelection() {
        return selection;
    }
}
