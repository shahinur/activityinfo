package org.activityinfo.core.shared.form.tree;

import com.google.common.collect.Lists;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.application.ApplicationProperties;
import org.activityinfo.core.shared.criteria.FormClassSet;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormField;

import java.util.List;

/**
* Represents a level within a hierarchy.
*/
public class Level {

    private FormClass formClass;

    Cuid parentId;
    Cuid parentFieldId;
    Level parent;
    List<Level> children = Lists.newArrayList();

    Level(FormClass formClass) {
        this.formClass = formClass;
        for(FormField field : formClass.getFields()) {
            if(field.isSubPropertyOf(ApplicationProperties.PARENT_PROPERTY)) {
                parentId = FormClassSet.of(field.getRange()).unique();
                parentFieldId = field.getId();
            }
        }
    }

    public Cuid getClassId() {
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

    public Cuid getParentFieldId() {
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

}
