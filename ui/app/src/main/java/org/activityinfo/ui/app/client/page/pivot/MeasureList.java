package org.activityinfo.ui.app.client.page.pivot;

import com.google.common.annotations.VisibleForTesting;
import org.activityinfo.model.analysis.AggregationFunction;
import org.activityinfo.model.analysis.MeasureModel;
import org.activityinfo.model.analysis.MeasurementType;
import org.activityinfo.model.expr.SymbolExpr;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ListFieldValue;
import org.activityinfo.model.type.SubFormValue;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.form.store.AddListItemAction;
import org.activityinfo.ui.app.client.form.store.FieldState;
import org.activityinfo.ui.app.client.page.pivot.tree.AcceptHandler;
import org.activityinfo.ui.app.client.page.pivot.tree.FieldChooser;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Button;
import org.activityinfo.ui.style.ButtonStyle;
import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class MeasureList extends VComponent {

    private FieldChooser fieldChooser;
    private final Button addButton;
    private Application application;
    private FieldState state;

    public MeasureList(Application application, FieldState state) {
        assert state != null;
        this.application = application;
        this.state = state;
        fieldChooser = new FieldChooser(application);
        addButton = new Button(ButtonStyle.PRIMARY, FontAwesome.PLUS.render(), t(" "), t("Add Measure"));
        addButton.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                fieldChooser.showFormSelection();
                fieldChooser.setVisible(true);
                fieldChooser.setAcceptHandler(new AcceptHandler<FormField>() {
                    @Override
                    public void onAccepted(FormField value) {
                        onMeasureSelected(value);
                    }
                });
            }
        });
    }

    @Override
    protected VTree render() {
        return div(PropMap.EMPTY,
            subtitle(),
            ul(measureList()),
            addButton,
            fieldChooser);
    }

    private VTree[] measureList() {
        if(state.getValue() instanceof ListFieldValue) {
            ListFieldValue currentValue = (ListFieldValue) state.getValue();
            return map(currentValue.getElements(), new Render<FieldValue>() {
                @Override
                public VTree render(FieldValue item) {
                    return renderMeasure(item);
                }
            });
        } else {
            return new VTree[0];
        }
    }


    private VNode subtitle() {
        return h4(PropMap.withClasses(BaseStyles.SUBTITLE), t("Measures"));
    }

    private VTree renderMeasure(FieldValue item) {
        if(item instanceof SubFormValue) {
            SubFormValue subForm = (SubFormValue) item;
            return li(subForm.getString("label"));
        } else {
            return li("error");
        }
    }

    @VisibleForTesting
    void onMeasureSelected(FormField value) {

        MeasureModel measure = new MeasureModel();
        measure.setLabel(value.getLabel());
        measure.setValueExpression(new SymbolExpr(value.getId()).asExpression());
        measure.setAggregationFunction(AggregationFunction.SUM);
        measure.setMeasurementType(MeasurementType.FLOW);

        SubFormValue fieldValue = new SubFormValue(MeasureModel.CLASS_ID, measure.asRecord());

        application.getDispatcher().dispatch(
            new AddListItemAction(state.getInstanceId(), state.getFieldId(), fieldValue));
    }

}
