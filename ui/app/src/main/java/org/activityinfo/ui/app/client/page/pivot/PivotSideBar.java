package org.activityinfo.ui.app.client.page.pivot;

import com.google.common.annotations.VisibleForTesting;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.analysis.DimensionModel;
import org.activityinfo.model.analysis.MeasureModel;
import org.activityinfo.model.analysis.MeasurementType;
import org.activityinfo.model.expr.SymbolExpr;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.SubFormValue;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.form.store.AddListItemAction;
import org.activityinfo.ui.app.client.page.pivot.tree.FieldChooser;
import org.activityinfo.ui.app.client.page.pivot.tree.FieldSelectHandler;
import org.activityinfo.ui.app.client.store.InstanceState;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Button;
import org.activityinfo.ui.style.ButtonStyle;
import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.activityinfo.ui.vdom.shared.html.H.t;

public class PivotSideBar extends VComponent implements StoreChangeListener {

    private FieldChooser fieldChooser;

    private Application application;
    private InstanceState draft;

    private Button saveButton;
    private ListPanel measurePanel;
    private ListPanel dimensionPanel;

    public PivotSideBar(Application application, InstanceState draft) {
        assert draft != null;
        this.application = application;
        this.draft = draft;
        fieldChooser = new FieldChooser(application);

        saveButton = new Button(ButtonStyle.PRIMARY, t(I18N.CONSTANTS.save()));
        saveButton.setBlock(true);

        measurePanel = new ListPanel(draft.getState(ResourceId.valueOf("measures")));
        measurePanel.getAddButton().setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                fieldChooser.showFormSelection();
                fieldChooser.setVisible(true);
                fieldChooser.setAcceptHandler(new FieldSelectHandler() {
                    @Override
                    public void onAccepted(FormClass formClass, FormField value) {
                        onMeasureSelected(formClass, value);
                    }
                });
            }
        });

        dimensionPanel = new ListPanel(draft.getState(ResourceId.valueOf("dimensions")));
        dimensionPanel.getAddButton().setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                fieldChooser.showFormSelection();
                fieldChooser.setVisible(true);
                fieldChooser.setAcceptHandler(new FieldSelectHandler() {
                    @Override
                    public void onAccepted(FormClass formClass, FormField value) {
                        onDimensionSelected(formClass, value);
                    }
                });
            }
        });
    }

    @Override
    protected void componentWillMount() {
        draft.addChangeListener(this);
    }

    @Override
    protected VTree render() {
        return div(BaseStyles.FM_SIDEBAR,
            saveButton,
            measurePanel,
            dimensionPanel,
            fieldChooser);
    }

    public Button getSaveButton() {
        return saveButton;
    }



    @VisibleForTesting
    void onMeasureSelected(FormClass formClass, FormField value) {

        MeasureModel measure = new MeasureModel();
        measure.setId(Resources.generateId().asString());
        measure.setSource(formClass.getId());
        measure.setLabel(value.getLabel());
        measure.setValueExpression(new SymbolExpr(value.getId()).asExpression());
        measure.setMeasurementType(MeasurementType.FLOW);

        SubFormValue fieldValue = new SubFormValue(MeasureModel.CLASS_ID, measure.asRecord());

        application.getDispatcher().dispatch(
            new AddListItemAction(draft.getInstanceId(), ResourceId.valueOf("measures"), fieldValue));
    }


    @VisibleForTesting
    void onDimensionSelected(FormClass formClass, FormField value) {

        DimensionModel dimension = new DimensionModel();
        dimension.setId(Resources.generateId().asString());
        dimension.setLabel(value.getLabel());

        SubFormValue fieldValue = new SubFormValue(DimensionModel.CLASS_ID, dimension.asRecord());

        application.getDispatcher().dispatch(
            new AddListItemAction(draft.getInstanceId(), ResourceId.valueOf("dimensions"), fieldValue));
    }

    @Override
    public void onStoreChanged(Store store) {
        refresh();
        measurePanel.refresh();
        dimensionPanel.refresh();
    }
}
