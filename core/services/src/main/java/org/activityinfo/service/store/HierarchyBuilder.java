package org.activityinfo.service.store;

import com.google.common.base.Function;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.hierarchy.Hierarchy;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.promise.Promise;

import javax.annotation.Nullable;
import java.util.List;

public class HierarchyBuilder {

    public static Promise<Hierarchy> get(final ResourceLocator resourceLocator, ReferenceType type) {
        return Promise.map(type.getRange(), new Function<ResourceId, Promise<FormClass>>() {
            @Override
            public Promise<FormClass> apply(@Nullable ResourceId input) {
                return resourceLocator.getFormClass(input);
            }
        }).then(new Function<List<FormClass>, Hierarchy>() {
            @Nullable
            @Override
            public Hierarchy apply(@Nullable List<FormClass> input) {
                return new Hierarchy(input);
            }
        });
    }
}
