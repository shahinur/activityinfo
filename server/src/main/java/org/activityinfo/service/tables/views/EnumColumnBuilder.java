package org.activityinfo.service.tables.views;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.columns.DiscreteStringColumnView;
import org.activityinfo.model.type.FieldValues;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;

import java.util.List;
import java.util.Map;

public class EnumColumnBuilder implements ColumnViewBuilder {

    private final String fieldName;
    private final Map<ResourceId, Integer> labelIndexMap = Maps.newHashMap();
    private final String[] labels;
    private List<Integer> values = Lists.newArrayList();

    private DiscreteStringColumnView result = null;

    public EnumColumnBuilder(ResourceId fieldId, EnumType enumType) {
        this.fieldName = fieldId.asString();

        int labelIndex = 0;
        this.labels = new String[enumType.getValues().size()];
        for(EnumValue item : enumType.getValues()) {
            this.labels[labelIndex] = item.getLabel();
            this.labelIndexMap.put(item.getId(), labelIndex);
            labelIndex++;
        }
    }

    @Override
    public void putResource(Resource resource) {
        EnumFieldValue fieldValue = FieldValues.readFieldValueIfType(resource, fieldName, EnumType.TYPE_CLASS);
        if(fieldValue.getValueIds().size() == 1) {
            Integer index = labelIndexMap.get(fieldValue.getValueId());
            if(index == null) {
                values.add(-1);
            } else {
                values.add(index);
            }
        }
    }

    @Override
    public void finalizeView() {
        this.result = new DiscreteStringColumnView(labels, createIndexArray());
    }

    private int[] createIndexArray() {
        int indexes[] = new int[values.size()];
        for(int i=0;i!=indexes.length;++i) {
            indexes[i] = values.get(i);
        }
        return indexes;
    }

    @Override
    public ColumnView get() {
        if(this.result == null) {
            throw new IllegalStateException();
        } else {
            return result;
        }
    }
}
