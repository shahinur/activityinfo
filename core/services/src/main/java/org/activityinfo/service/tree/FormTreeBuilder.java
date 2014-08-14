package org.activityinfo.service.tree;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.auth.AuthenticatedUser;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.service.store.StoreAccessor;

import java.util.logging.Logger;

/**
 * Constructs a tree of related Forms from a given root FormClass.
 */
public class FormTreeBuilder {

    private static final Logger LOGGER = Logger.getLogger(FormTreeBuilder.class.getName());

    private final StoreAccessor store;

    public FormTreeBuilder(StoreAccessor store) {
        this.store = store;
    }


    public FormTree queryTree(ResourceId rootFormClassId) throws Exception {
        FormTree tree = new FormTree();
        FormClass rootClass = fetchFormClass(rootFormClassId);
        for(FormField field : rootClass.getFields()) {
            FormTree.Node node = tree.addRootField(rootClass, field);
            if(field.getType() instanceof ReferenceType) {
                fetchChildren(node);
            }
        }
        return tree;
    }

    /**
     * Now that we have the actual FormClass model that corresponds to this node's
     * formClassId, add it's children.
     *
     */
    private void fetchChildren(FormTree.Node parent) throws Exception {
        ReferenceType type = (ReferenceType) parent.getType();
        for(ResourceId childClassId : type.getRange()) {
            FormClass childClass = fetchFormClass(childClassId);
            for(FormField field : childClass.getFields()) {
                FormTree.Node childNode = parent.addChild(childClass, field);
                if(childNode.isReference()) {
                   fetchChildren(childNode);
                }
            }
        }
    }

    private FormClass fetchFormClass(ResourceId formClassId) throws Exception {
        return FormClass.fromResource(store.get(formClassId));
    }
}
