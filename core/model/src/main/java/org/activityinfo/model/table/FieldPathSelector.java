package org.activityinfo.model.table;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Records;
import org.activityinfo.model.resource.ResourceId;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Selects the given root field
 */
public class FieldPathSelector implements FieldSelector {


    public static class Step implements Predicate<FormTree.Node>, IsRecord {
        private ResourceId formClass;
        private ResourceId fieldId;


        /**
         * Step which matches a specific formClass and fieldId
         */
        public Step(ResourceId formClass, ResourceId fieldId) {
            assert formClass != null;
            assert fieldId != null;

            this.formClass = formClass;
            this.fieldId = fieldId;
        }

        /**
         * Step which matches only on fieldId
         */
        public Step(ResourceId fieldId) {
            assert fieldId != null;

            this.fieldId = fieldId;
        }


        @Override
        public boolean apply(FormTree.Node input) {
            if(formClass != null) {
                if(!Objects.equals(formClass, input.getDefiningFormClass().getId())) {
                    return false;
                }
            }
            return input.getFieldId().equals(fieldId) ||
                   input.getField().getSuperProperties().contains(fieldId);
        }

        @Override
        public Record asRecord() {
            Record record = new Record();
            if(formClass != null) {
                record.set("formClass", formClass.asString());
            }
            record.set("fieldId", fieldId.asString());
            return record;
        }
    }

    private final LinkedList<Step> steps = Lists.newLinkedList();

    private FieldPathSelector() {

    }

    /**
     * Selects the specific field of the given FormClass, wherever it appears
     * in the heirarchy
     * @param classId
     * @param fieldId
     */
    public FieldPathSelector(ResourceId classId, ResourceId fieldId) {
        steps.add(new Step(classId, fieldId));
    }

    public FieldPathSelector(Iterable<ResourceId> fieldIds) {
        for(ResourceId fieldId : fieldIds) {
            steps.add(new Step(fieldId));
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

    @Override
    public Record asRecord() {
        Record record = new Record();
        record.set("path", Records.toRecordList(steps));

        return record;
    }

    public static FieldPathSelector fromRecord(Record record) {
        FieldPathSelector selector = new FieldPathSelector();
        for(Record stepRecord : record.getRecordList("path")) {
            String formClassId = stepRecord.isString("formClass");
            String fieldId = stepRecord.getString("fieldId");
            if(formClassId != null) {
                selector.steps.add(new Step(ResourceId.valueOf(formClassId), ResourceId.valueOf(fieldId)));
            } else {
                selector.steps.add(new Step(ResourceId.valueOf(fieldId)));
            }
        }
        return selector;
    }
}
