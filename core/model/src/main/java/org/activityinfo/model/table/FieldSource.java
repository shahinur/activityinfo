package org.activityinfo.model.table;

import com.google.common.collect.Lists;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Records;
import org.activityinfo.model.resource.ResourceId;

import java.util.Arrays;
import java.util.List;

/**
 * Sources a column from one or more Fields within the
 * {@code FormTree}
 */
public class FieldSource extends ColumnSource {

    public static final String SOURCE_TYPE = "field";

    private final List<FieldSelector> selectors = Lists.newArrayList();

    public List<FieldSelector> getSelectors() {
        return selectors;
    }

    /**
     * Selects the given root field by name, regardless
     * of the FormClass.
     */
    public FieldSource fieldPath(ResourceId... fieldId) {
        selectors.add(new FieldPathSelector(Arrays.asList(fieldId)));
        return this;
    }

    /**
     * Selects the field of the given FormClass, wherever it appears
     * in the heirarchy.
     */
    public FieldSource formClassField(ResourceId classId, ResourceId fieldId) {
        selectors.add(new FieldPathSelector(classId, fieldId));
        return this;
    }

    public FieldSource fieldPath(Iterable<ResourceId> path) {
        selectors.add(new FieldPathSelector(path));
        return this;
    }



    public List<FormTree.Node> select(FormTree tree) {
        List<FormTree.Node> nodes = Lists.newArrayList();
        for(FieldSelector selector : selectors) {
            nodes.addAll(selector.select(tree));
        }
        return nodes;
    }

    @Override
    public Record asRecord() {
        Record record = new Record();
        record.set("type", SOURCE_TYPE);
        record.set("selectors", Records.toRecordList(selectors));
        return record;
    }


    public static FieldSource fromRecord(Record record) {
        FieldSource source = new FieldSource();
        List<FieldSelector> selectors = Lists.newArrayList();
        for(Record selectorRecord : record.getRecordList("selectors")) {
            source.selectors.add(FieldPathSelector.fromRecord(selectorRecord));
        }
        return source;
    }

}
