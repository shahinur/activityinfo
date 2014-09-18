package org.activityinfo.ui.app.client.page.pivot.tree;

import com.google.common.base.Function;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormElement;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormSection;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.page.form.FieldIconProvider;
import org.activityinfo.ui.app.client.request.FetchResource;
import org.activityinfo.ui.app.client.store.ResourceStore;
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.style.tree.TreeModel;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.util.Collections;
import java.util.List;

public class FieldSelectionTree implements TreeModel<FormElement> {

    private final Application application;
    private final ResourceStore resourceStore;
    private final ResourceId formClassId;

    public FieldSelectionTree(Application application, ResourceId formClassId) {
        this.application = application;
        this.formClassId = formClassId;
        this.resourceStore = application.getResourceStore();
    }

    @Override
    public boolean isLeaf(FormElement node) {
        return node instanceof FormField;
    }

    @Override
    public Status<List<FormElement>> getRootNodes() {
        return resourceStore.getFormClass(formClassId).join(new Function<FormClass, List<FormElement>>() {
            @Override
            public List<FormElement> apply(FormClass input) {
                return input.getElements();
            }
        });
    }

    @Override
    public Status<List<FormElement>> getChildren(FormElement parent) {
        if(parent instanceof FormSection) {
            FormSection section = (FormSection) parent;
            return Status.cache(section.getElements());
        }
        return Status.cache(Collections.<FormElement>emptyList());
    }

    @Override
    public String getLabel(FormElement node) {
        return node.getLabel();
    }

    @Override
    public Icon getIcon(FormElement node, boolean expanded) {
        if(node instanceof FormField) {
            return FieldIconProvider.get((FormField)node);
        } else if(node instanceof FormSection) {
            if(expanded) {
                return FontAwesome.FOLDER;
            } else {
                return FontAwesome.FOLDER_OPEN;
            }
        } else {
            return FontAwesome.QUESTION;
        }
    }

    @Override
    public String getKey(FormElement node) {
        return node.getId().asString();
    }

    @Override
    public void requestRootNodes() {
        application.getRequestDispatcher().execute(new FetchResource(formClassId));
    }

    @Override
    public void requestChildren(FormElement node) {

    }

    @Override
    public void addChangeListener(StoreChangeListener listener) {
        resourceStore.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(StoreChangeListener listener) {
        resourceStore.removeChangeListener(listener);
    }
}
