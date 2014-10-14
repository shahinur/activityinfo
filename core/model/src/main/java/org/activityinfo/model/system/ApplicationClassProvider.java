package org.activityinfo.model.system;

import com.google.common.collect.Maps;
import org.activityinfo.model.analysis.PivotTableModelClass;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.ParametrizedFieldTypeClass;
import org.activityinfo.model.type.TypeRegistry;
import org.activityinfo.model.type.time.LocalDateIntervalClass;

import java.util.Collections;
import java.util.Map;

/**
 * Provides application-level form classes
 */
public class ApplicationClassProvider {

    private Map<ResourceId, FormClass> classMap = Maps.newHashMap();

    public ApplicationClassProvider() {

        classMap.put(FormClass.CLASS_ID, createFormClassClass());
        classMap.put(FolderClass.CLASS_ID, FolderClass.INSTANCE.get());
        classMap.put(PivotTableModelClass.CLASS_ID, PivotTableModelClass.INSTANCE.get());
        classMap.put(LocalDateIntervalClass.CLASS_ID, LocalDateIntervalClass.INSTANCE.get());

        for (FieldTypeClass fieldTypeClass : TypeRegistry.get().getTypeClasses()) {
            if(fieldTypeClass instanceof ParametrizedFieldTypeClass) {
                FormClass parameterFormClass = ((ParametrizedFieldTypeClass)fieldTypeClass).getParameterFormClass();
                classMap.put(parameterFormClass.getId(), parameterFormClass);
            }
        }
    }

    public Map<ResourceId, FormClass> asMap() {
        return Collections.unmodifiableMap(classMap);
    }

    private FormClass createFormClassClass() {
        FormField labelField = new FormField(ResourceId.valueOf(FormClass.LABEL_FIELD_ID));

        FormClass formClass = new FormClass(FormClass.CLASS_ID);
        formClass.addElement(labelField);

        return formClass;
    }

    public FormClass get(ResourceId classId) {
        FormClass formClass = classMap.get(classId);
        if(formClass == null) {
            throw new IllegalArgumentException("No such system class: " + classId);
        }
        return formClass;
    }

    public boolean isApplicationFormClass(ResourceId id) {
        return id.asString().startsWith("_");
    }
}
