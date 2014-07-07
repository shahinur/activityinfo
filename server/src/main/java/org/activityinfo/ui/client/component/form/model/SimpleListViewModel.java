package org.activityinfo.ui.client.component.form.model;

import com.google.common.base.Function;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.promise.Promise;

import java.util.List;

/**
 * View model of a reference field's range
 */
public class SimpleListViewModel implements FieldViewModel {

    private int count;
    private List<FormInstance> instances;
    private ReferenceType fieldType;

    public SimpleListViewModel(ReferenceType fieldType, List<FormInstance> instances) {
        this.fieldType = fieldType;
        this.instances = instances;
        this.count = instances.size();
    }

    @Override
    public ResourceId getFieldId() {
        return null;
    }

    public int getCount() {
        return count;
    }

    public List<FormInstance> getInstances() {
        return instances;
    }

    public Cardinality getCardinality() {
        return fieldType.getCardinality();
    }

    public static Promise<FieldViewModel> build(ResourceLocator resourceLocator, final FormTree.Node node) {
        return resourceLocator
        .queryInstances(node.getRange())
        .then(new Function<List<FormInstance>, FieldViewModel>() {
            @Override
            public FieldViewModel apply(List<FormInstance> input) {
                return new SimpleListViewModel((ReferenceType) node.getField().getType(), input);
            }
        });
    }

    @Override
    public String toString() {
        return "RangeViewModel{" +
                "count=" + count +
                ", instances=" + instances +
                ", type=" + fieldType +
                '}';
    }
}

