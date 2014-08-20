package org.activityinfo.model.hierarchy;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.ReferenceValue;

import java.util.List;

/**
* Represents a level within a hierarchy.
*/
public class Level {

    private FormClass formClass;

    ResourceId parentId;
    ResourceId parentFieldId;
    ResourceId labelFieldId;

    Level parent;
    List<Level> children = Lists.newArrayList();

    Level(FormClass formClass) {
        this.formClass = formClass;
        for(FormField field : formClass.getFields()) {
            if(field.isSubPropertyOf(ApplicationProperties.PARENT_PROPERTY)) {
                ReferenceType type = (ReferenceType) field.getType();
                assert type.getRange().size() == 1;
                parentId = type.getRange().iterator().next();
                parentFieldId = field.getId();
            } else if(field.isSubPropertyOf(ApplicationProperties.LABEL_PROPERTY)) {
                labelFieldId = field.getId();
            }
        }
    }

    public ResourceId getClassId() {
        return formClass.getId();
    }

    public String getLabel() {
        return formClass.getLabel();
    }

    public FormClass getFormClass() {
        return formClass;
    }

    public Level getParent() {
        return parent;
    }

    public ResourceId getParentFieldId() {
        return parentFieldId;
    }

    public boolean isRoot() {
        return parentId == null;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public List<Level> getChildren() {
        return children;
    }

    public Node createNode(Resource resource) {
        if(isRoot()) {
            return new Node(resource.getId(), resource.getString(labelFieldId.asString()));
        } else {

            ReferenceValue parent = ReferenceValue.fromRecord(resource.getRecord(parentFieldId.asString()));

            return new Node(
                    resource.getId(),
                    parent.getResourceId(),
                    resource.getString(labelFieldId.asString()));
        }
    }
}
