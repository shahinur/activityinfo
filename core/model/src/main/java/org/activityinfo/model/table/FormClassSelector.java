package org.activityinfo.model.table;

import com.google.common.collect.Lists;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;

import java.util.List;

/**
 * Selects all reference fields in the tree that have a range
 * that includes the given formClassId;
 */
public class FormClassSelector implements FieldSelector {

    private final ResourceId formClassId;

    public FormClassSelector(ResourceId formClassId) {
        this.formClassId = formClassId;
    }

    @Override
    public List<FormTree.Node> select(FormTree tree) {
        List<FormTree.Node> matching = Lists.newArrayList();
        collect(tree.getRootFields(), matching);
        return matching;
    }

    private void collect(List<FormTree.Node> fields, List<FormTree.Node> matching) {
        for(FormTree.Node field : fields) {
            if(field.isReference()) {
                if(field.getRange().contains(formClassId)) {
                    matching.add(field);
                } else {
                    collect(field.getChildren(), matching);
                }
            }
        }
    }
}
