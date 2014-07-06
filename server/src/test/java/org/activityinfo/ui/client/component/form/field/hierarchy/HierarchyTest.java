package org.activityinfo.ui.client.component.form.field.hierarchy;

import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import org.activityinfo.core.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.application.ApplicationProperties;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.promise.Promise;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.adapter.LocationClassAdapter;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.client.component.form.model.HierarchyViewModel;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.activityinfo.legacy.shared.adapter.CuidAdapter.entity;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;


@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/jordan-admin.db.xml")
public class HierarchyTest extends CommandTestCase2 {

    public static final ResourceId CAMP_CLASS = CuidAdapter.locationFormClass(50505);

    public static final ResourceId CAMP_DISTRICT_CLASS = CuidAdapter.adminLevelFormClass(1528);

    public static final ResourceId REGION = CuidAdapter.adminLevelFormClass(1520);

    public static final ResourceId GOVERNORATE_ID = CuidAdapter.adminLevelFormClass(1360);

    private Map<ResourceId, MockLevelWidget> widgets;

    @Test
    public void buildViewModelTest() {
        ResourceLocatorAdaptor resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        FormTree tree = assertResolves(new AsyncFormTreeBuilder(resourceLocator).apply(CAMP_CLASS));

        FormTree.Node adminNode = tree.getRootField(LocationClassAdapter.getAdminFieldId(CAMP_CLASS));

        Set<ResourceId> fieldValue = Collections.singleton(entity(325703));

        HierarchyViewModel viewModel = (HierarchyViewModel) assertResolves(HierarchyViewModel
                .build(resourceLocator, adminNode, fieldValue));

        prettyPrintTree(viewModel);
        assertThat(viewModel.getTree().getRoots(), hasSize(1));

        createWidgets(viewModel);

        Presenter presenter = new Presenter(resourceLocator, viewModel.getTree(), widgets, new ValueUpdater() {
            @Override
            public void update(Object value) {
                System.out.println("VALUE = " + value);
            }
        });
        presenter.setInitialSelection(viewModel.getSelection());

        assertThat(presenter.getSelectionLabel(CAMP_DISTRICT_CLASS), equalTo("District 5"));

        // now try to get options for the root level
        List<Projection> choices = assertResolves(widgets.get(REGION).choices.get());
        System.out.println(choices);

        assertThat(choices, hasSize(3));

        // if we change the root item, then all descendants should be cleared
        widgets.get(REGION).setSelection("South");

        prettyPrintWidgets(viewModel.getTree());

        assertThat(widgets.get(CAMP_DISTRICT_CLASS).selection, isEmptyOrNullString());

        assertThat(widgets.get(GOVERNORATE_ID).choices, Matchers.notNullValue());

        List<Projection> governorateChoices = assertResolves(widgets.get(GOVERNORATE_ID).choices.get());
        System.out.println(governorateChoices);
        assertThat(governorateChoices, hasSize(4));
    }

    private void prettyPrintWidgets(Hierarchy tree) {
        for(Level level : tree.getLevels()) {
            System.out.println(widgets.get(level.getClassId()));
        }
    }

    private void createWidgets(HierarchyViewModel viewModel) {
        Map<ResourceId, MockLevelWidget> levels = new HashMap<>();
        for(Level level : viewModel.getTree().getLevels()) {
            levels.put(level.getClassId(), new MockLevelWidget(level.getLabel()));
        }
        this.widgets = levels;
    }


    private void prettyPrintTree(HierarchyViewModel model) {
        for(Level level : model.getTree().getRoots()) {
            printTree(0, model, level);
        }
    }

    private void printTree(int indent, HierarchyViewModel model, Level parent) {
        System.out.println(Strings.repeat(" ", indent) + parent.getLabel() +
                " = " + model.getSelection().get(parent.getClassId()));
        for(Level child : parent.getChildren()) {
            printTree(indent+1, model, child);
        }
    }

    private static class MockLevelWidget implements LevelView {

        private SimpleEventBus eventBus = new SimpleEventBus();
        private String selection;
        private String label;

        private MockLevelWidget(String label) {
            this.label = label;
        }

        private Supplier<Promise<List<Projection>>> choices;

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
        public void setSelection(Projection selection) {
            this.selection = selection.getStringValue(ApplicationProperties.LABEL_PROPERTY);
        }

        @Override
        public void setChoices(Supplier<Promise<List<Projection>>> choices) {
            this.choices = choices;
        }

        public void setSelection(String label) {
            List<Projection> choices = assertResolves(this.choices.get());
            for(Projection projection : choices) {
                if(Objects.equals(projection.getStringValue(ApplicationProperties.LABEL_PROPERTY), label)) {
                    this.selection = label;
                    SelectionEvent.fire(this, projection);
                    return;
                }
            }
            throw new AssertionError("No item with label '" + label + "', we have: " + choices);
        }

        @Override
        public HandlerRegistration addSelectionHandler(SelectionHandler<Projection> handler) {
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
