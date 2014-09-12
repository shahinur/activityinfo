package org.activityinfo.ui.app.client.page.pivot.tree;

import com.google.common.collect.Maps;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.request.FetchResource;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.Button;
import org.activityinfo.ui.style.ButtonStyle;
import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.style.Modal;
import org.activityinfo.ui.style.tree.SingleSelectionModel;
import org.activityinfo.ui.style.tree.TreeComponent;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.Map;

import static org.activityinfo.ui.vdom.shared.html.H.t;

public class FieldChooser extends VComponent implements StoreChangeListener {

    public enum Step {
        FORM,
        FIELD
    }

    private Application application;
    private SingleSelectionModel formSelection = new SingleSelectionModel();
    private SingleSelectionModel fieldSelection = new SingleSelectionModel();

    private final Modal modal;

    private final Button addButton;

    private final FormSelectionTree formTree;
    private final Map<ResourceId, FieldSelectionTree> fieldTrees = Maps.newHashMap();

    private Step currentStep;

    private AcceptHandler<FormField> acceptHandler;

    public FieldChooser(Application application) {
        this.application = application;
        this.formTree = new FormSelectionTree(application);

        this.modal = new Modal();
        this.modal.setTitle(t("Choose Form"));

        Button cancelButton = new Button(ButtonStyle.DEFAULT, t(I18N.CONSTANTS.cancel()));
        cancelButton.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                modal.setVisible(false);
            }
        });
        this.addButton = new Button(ButtonStyle.PRIMARY, t(I18N.CONSTANTS.add()));
        this.addButton.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                onAccepted();
            }
        });
        this.modal.setFooter(cancelButton, addButton);

        showFormSelection();
    }

    public void showFormSelection() {
        currentStep = Step.FORM;

        modal.setTitle(t("Choose form"));
        modal.setBody(new TreeComponent<>(formTree, formSelection));
        addButton.setContent(t(I18N.CONSTANTS.nextButton()));
        modal.refresh();
    }

    private void showFieldSelection(ResourceId selection) {
        currentStep = Step.FIELD;

        application.getRequestDispatcher().execute(new FetchResource(selection));

        FieldSelectionTree fieldTree =  new FieldSelectionTree(application, selection);

        modal.setTitle(t("Choose field"));
        modal.setBody(new TreeComponent<>(fieldTree, fieldSelection));
        modal.refresh();
    }

    @Override
    protected void componentDidMount() {
        formTree.addChangeListener(this);
    }

    @Override
    protected void componentWillUnmount() {
        formTree.removeChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        addButton.setEnabled(formSelection.hasSelection());
    }

    public void setVisible(boolean visible) {
        modal.setVisible(visible);
    }

    private void onAccepted() {
        switch(currentStep) {
            case FORM:
                if(formSelection.hasSelection()) {
                    showFieldSelection(ResourceId.valueOf(formSelection.getSelectedKey()));
                }
                break;
            case FIELD:
                if(fieldSelection.hasSelection()) {
                    fireSelected();
                }
                break;
        }
    }

    private void fireSelected() {
        FormClass formClass = application.getResourceStore().getFormClass(
            ResourceId.valueOf(formSelection.getSelectedKey())).get();

        FormField field = formClass.getField(ResourceId.valueOf(fieldSelection.getSelectedKey()));
        if(acceptHandler != null) {
            acceptHandler.onAccepted(field);
        }
        setVisible(false);
    }

    @Override
    protected VTree render() {
        return modal;
    }
}
