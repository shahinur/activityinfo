package org.activityinfo.model.table;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Selects the given root field
 */
public class FieldPathSelector implements FieldSelector {

    public static class Step implements Predicate<FormTree.Node> {
        private ResourceId formClass;
        private String fieldName;

        /**
         * Step which matches a specific formClass and fieldName
         */
        public Step(ResourceId formClass, String fieldName) {
            this.formClass = formClass;
            this.fieldName = fieldName;
        }

        /**
         * Step which matches only on fieldName
         */
        public Step(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public boolean apply(FormTree.Node input) {
            if(formClass != null && !Objects.equals(formClass, input.getDefiningFormClass().getId())) {
                return false;
            }
            throw new UnsupportedOperationException(); // TODO(alex)
           // return fieldName.equals(input.getField().getName());
        }
    }

    private final LinkedList<Step> steps = Lists.newLinkedList();

    public FieldPathSelector(String... fieldNames) {
        for(String fieldName : fieldNames) {
            steps.add(new Step(fieldName));
        }
    }

    @Override
    public List<FormTree.Node> select(FormTree tree) {
        List<FormTree.Node> matching = Lists.newArrayList();
        collect(steps, tree.getRootFields(), matching);
        return matching;
    }

    private void collect(List<Step> steps, List<FormTree.Node> fields, List<FormTree.Node> matching) {
        Step head = steps.get(0);
        List<Step> tail = steps.subList(1, steps.size());

        for(FormTree.Node field : fields) {
            if(head.apply(field)) {
                if(tail.isEmpty()) {
                    // end of the road
                    matching.add(field);
                } else {
                    collect(tail, field.getChildren(), matching);
                }
            }
        }
    }
}
