package org.activityinfo.model.table;

import com.google.common.collect.Lists;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;

import java.util.List;

/**
 * Sources a column from one or more Fields within the
 * {@code FormTree}
 */
public class FieldSource extends ColumnSource {

    private final List<FieldSelector> selectors = Lists.newArrayList();

    public List<FieldSelector> getSelectors() {
        return selectors;
    }

    /**
     * Selects the given root field by name, regardless
     * of the FormClass.
     */
    public FieldSource fieldPath(String... fieldName) {
        selectors.add(new FieldPathSelector(fieldName));
        return this;
    }

    public FieldSource formClass(ResourceId formClassId) {
        selectors.add(new FormClassSelector(formClassId));
        return this;
    }

    public List<FormTree.Node> select(FormTree tree) {
        List<FormTree.Node> nodes = Lists.newArrayList();
        for(FieldSelector selector : selectors) {
            nodes.addAll(selector.select(tree));
        }
        return nodes;
    }

}
