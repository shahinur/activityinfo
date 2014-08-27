package org.activityinfo.ui.client.component.form.field.hierarchy;

import com.google.common.base.Function;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.component.form.field.ReferenceFieldWidget;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Widget for Reference fields which presents multi-level combo boxes
 *
 */
public class HierarchyFieldWidget implements ReferenceFieldWidget {

    private final FlowPanel panel;
    private final Map<ResourceId, LevelView> widgets = new HashMap<>();
    private final Presenter presenter;

    public HierarchyFieldWidget(ResourceLocator locator, Hierarchy tree,
                                ValueUpdater valueUpdater) {

        this.panel = new FlowPanel();
        for(Level level : tree.getLevels()) {
            LevelWidget widget = new LevelWidget(level.getLabel());
            widgets.put(level.getClassId(), widget);
            this.panel.add(widget);
        }

        this.presenter = new Presenter(locator, tree, widgets, valueUpdater);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        for(LevelView widget : widgets.values()) {
            widget.setReadOnly(readOnly);
        }
    }

    @Override
    public Promise<Void> setValue(ReferenceValue value) {
        return presenter.setInitialSelection(value.getResourceIds());
    }

    @Override
    public void clearValue() {
        presenter.setInitialSelection(Collections.<ResourceId>emptySet());
    }

    @Override
    public void setType(FieldType type) {

    }

    @Override
    public Widget asWidget() {
        return panel;
    }


    public static Promise<HierarchyFieldWidget> create(final ResourceLocator locator,
                                                 final ReferenceType type,
                                                 final ValueUpdater valueUpdater) {

        return Promise.map(type.getRange(), new Function<ResourceId, Promise<FormClass>>() {
            @Nullable
            @Override
            public Promise<FormClass> apply(@Nullable ResourceId input) {
                return locator.getFormClass(input);
            }
        }).then(new Function<List<FormClass>, HierarchyFieldWidget>() {
            @Nullable
            @Override
            public HierarchyFieldWidget apply(@Nullable List<FormClass> input) {
                return new HierarchyFieldWidget(locator, new Hierarchy(input), valueUpdater);
            }
        });
    }

    @Override
    public List<FormInstance> getRange() {
        throw new UnsupportedOperationException();
    }
}

