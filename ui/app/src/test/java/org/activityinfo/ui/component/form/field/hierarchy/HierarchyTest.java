package org.activityinfo.ui.component.form.field.hierarchy;

import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.hierarchy.Hierarchy;
import org.activityinfo.model.hierarchy.Level;
import org.activityinfo.model.hierarchy.Node;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.ResourceLocator;
import org.activityinfo.service.store.HierarchyBuilder;
import org.activityinfo.store.test.TestResourceStore;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.activityinfo.model.legacy.CuidAdapter.entity;
import static org.activityinfo.promise.PromiseMatchers.assertResolves;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;


public class HierarchyTest {

    public static final ResourceId CAMP_CLASS = CuidAdapter.locationFormClass(50505);

    public static final ResourceId CAMP_DISTRICT_CLASS = CuidAdapter.adminLevelFormClass(1528);

    public static final ResourceId REGION = CuidAdapter.adminLevelFormClass(1520);

    public static final ResourceId GOVERNORATE_ID = CuidAdapter.adminLevelFormClass(1360);

    private Map<ResourceId, MockLevelWidget> widgets;

    @Test
    public void buildViewModelTest() throws IOException {
        ResourceLocator resourceLocator = new TestResourceStore()
                .load("/dbunit/jordan-admin.json")
                .createLocator();
        FormClass campForm = assertResolves(resourceLocator.getFormClass(CAMP_CLASS));

        FormField adminField = campForm.getField(CuidAdapter.getAdminFieldId(CAMP_CLASS));

        Set<ResourceId> fieldValue = Collections.singleton(entity(325703));

        Hierarchy tree = assertResolves(HierarchyBuilder
                .get(resourceLocator, (ReferenceType) adminField.getType()));

        prettyPrintTree(tree);
        assertThat(tree.getRoots(), hasSize(1));

        createWidgets(tree);

        Presenter presenter = new Presenter(resourceLocator, tree, widgets, new ValueUpdater() {
            @Override
            public void update(Object value) {
                System.out.println("VALUE = " + value);
            }
        });
        assertResolves(presenter.setInitialSelection(fieldValue));

        assertThat(presenter.getSelectionLabel(CAMP_DISTRICT_CLASS), equalTo("District 5"));

        // now try to get options for the root level
        List<Node> choices = assertResolves(widgets.get(REGION).choices.get());
        System.out.println(choices);

        assertThat(choices, hasSize(3));

        // if we change the root item, then all descendants should be cleared
        widgets.get(REGION).setSelection("South");

        prettyPrintWidgets(tree);

        assertThat(widgets.get(CAMP_DISTRICT_CLASS).selection, isEmptyOrNullString());

        assertThat(widgets.get(GOVERNORATE_ID).choices, Matchers.notNullValue());

        List<Node> governorateChoices = assertResolves(widgets.get(GOVERNORATE_ID).choices.get());
        System.out.println(governorateChoices);
        assertThat(governorateChoices, hasSize(4));
    }

    private void prettyPrintWidgets(Hierarchy tree) {
        for(Level level : tree.getLevels()) {
            System.out.println(widgets.get(level.getClassId()));
        }
    }

    private void createWidgets(Hierarchy tree) {
        Map<ResourceId, MockLevelWidget> levels = new HashMap<>();
        for(Level level : tree.getLevels()) {
            levels.put(level.getClassId(), new MockLevelWidget(level.getLabel()));
        }
        this.widgets = levels;
    }


    private void prettyPrintTree(Hierarchy tree) {
        for(Level level : tree.getRoots()) {
            printTree(0, level);
        }
    }

    private void printTree(int indent, Level parent) {
        System.out.println(Strings.repeat(" ", indent) + parent.getLabel());
        for(Level child : parent.getChildren()) {
            printTree(indent+1, child);
        }
    }

    private static class MockLevelWidget implements LevelView {

        private SimpleEventBus eventBus = new SimpleEventBus();
        private String selection;
        private String label;

        private MockLevelWidget(String label) {
            this.label = label;
        }

        private Supplier<Promise<List<Node>>> choices;

        @Override
        public void clearSelection() {
            this.selection = null;
        }

        @Override
        public void setReadOnly(boolean readOnly) {

        }

        @Override
        public void setEnabled(boolean enabled) {

        }

        @Override
        public void setSelection(Node selection) {
            this.selection = selection.getLabel();
        }

        @Override
        public void setChoices(Supplier<Promise<List<Node>>> choices) {
            this.choices = choices;
        }

        public void setSelection(String label) {
            List<Node> choices = assertResolves(this.choices.get());
            for(Node Node : choices) {
                if(Objects.equals(Node.getLabel(), label)) {
                    this.selection = label;
                    SelectionEvent.fire(this, Node);
                    return;
                }
            }
            throw new AssertionError("No item with label '" + label + "', we have: " + choices);
        }

        @Override
        public HandlerRegistration addSelectionHandler(SelectionHandler<Node> handler) {
            return eventBus.addHandler(SelectionEvent.getType(), handler);
        }

        @Override
        public void fireEvent(GwtEvent<?> event) {
            eventBus.fireEvent(event);
        }

        @Override
        public String toString() {
            return Strings.padEnd(label, 20, ' ') + " [" +
                    Strings.padEnd(Strings.nullToEmpty(selection), 30, ' ') + "]";
        }
    }

}
