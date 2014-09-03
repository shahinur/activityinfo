package org.activityinfo.ui.app.client.form.control;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.form.FormField;
import org.activityinfo.ui.app.client.form.store.InstanceStore;
import org.activityinfo.ui.app.client.form.store.PersistAction;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.*;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.activityinfo.ui.vdom.shared.html.H.t;

public class HorizontalFormView extends VThunk<HorizontalFormView> implements StoreChangeListener {

    private Dispatcher dispatcher;
    private final InstanceStore store;

    public HorizontalFormView(Dispatcher dispatcher, InstanceStore store) {
        this.dispatcher = dispatcher;
        this.store = store;
    }

//    @Override
//    public boolean shouldUpdate(HorizontalFormView previousProperties) {
//        // only update if we set the dirty flag ourselves
//        return false;
//    }

    @Override
    protected VTree render() {

        Button createButton = new Button(ButtonStyle.PRIMARY);
        switch(store.getLoadingState()) {
            case LOADED:
                createButton.setEnabled(true);
                createButton.setContent(t(I18N.CONSTANTS.createWorkspace()));
                break;

            case SAVING:
                createButton.setEnabled(false);
                createButton.setContent(Spinners.spinner().render(), t(I18N.CONSTANTS.saving()));
                break;

            case SAVING_FAILED:
                createButton.setEnabled(true);
                createButton.setContent(t(I18N.CONSTANTS.retry()));
                break;
        }

        createButton.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                dispatcher.dispatch(new PersistAction(store.getInstanceId()));
            }
        });

        Panel panel = new Panel();
        panel.setTitle(t(I18N.CONSTANTS.createEmptyWorkspace()));
        panel.setIntroParagraph(t("Create an empty workspace in which you can import existing data, " +
            "create your own data collection forms, and conduct analysis."));
        panel.setContent(renderForm());
        panel.setFooter(footerPanel(createButton));

        return panel;
    }

    private VTree renderForm() {
        if(store.getLoadingState() == InstanceStore.LoadingState.LOADING) {
            return t("Loading...");
        } else {
            switch (store.getLoadingState()) {
                case LOADED:
                case SAVING:
                case SAVING_FAILED:

            }
            HorizontalForm form = new HorizontalForm();
            FieldControlFactory factory = new FieldControlFactory(dispatcher);

            for (FormField field : store.getFormClass().getFields()) {
                form.addGroup(new HorizontalFieldGroup(store, field.getId(), factory.create(field)));
            }
            return form;
        }
    }

    private VTree footerPanel(Button createButton) {
        return div(BaseStyles.ROW, div("col-sm-6 col-sm-offset-3", createButton));
    }

    @Override
    public void onStoreChanged(Store store) {
        if(store == this.store) {
            forceUpdate();
        }
    }
}
