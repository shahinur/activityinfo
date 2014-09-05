package org.activityinfo.ui.app.client.form.control;

import org.activityinfo.model.form.FormField;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.store.InstanceState;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.Button;
import org.activityinfo.ui.style.HorizontalForm;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import javax.annotation.Nonnull;

public class HorizontalFormView extends VComponent<HorizontalFormView> implements StoreChangeListener {

    private final Application application;
    private final Dispatcher dispatcher;
    private final InstanceState instanceState;

    private Button createButton;


    public HorizontalFormView(@Nonnull final Application application, final InstanceState instanceState) {
        this.application = application;
        this.instanceState = instanceState;
        this.dispatcher = application.getDispatcher();
    }

    @Override
    public void componentDidMount() {
        instanceState.addChangeListener(this);
    }

    @Override
    protected void componentWillUnmount() {
        instanceState.removeChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        if(store == instanceState) {
            refresh();
        }
    }

    @Override
    protected VTree render() {

        HorizontalForm form = new HorizontalForm();
        FieldControlFactory factory = new FieldControlFactory(dispatcher);

        for (FormField field : instanceState.getFormClass().getFields()) {
            form.addGroup(new HorizontalFieldGroup(instanceState.getState(field.getId()), factory.create(field)));
        }
        return form;
    }
}
