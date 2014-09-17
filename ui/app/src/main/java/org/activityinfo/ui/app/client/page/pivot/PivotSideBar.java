package org.activityinfo.ui.app.client.page.pivot;

import com.google.common.annotations.VisibleForTesting;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.analysis.MeasureModel;
import org.activityinfo.model.analysis.MeasurementType;
import org.activityinfo.model.expr.SymbolExpr;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ListFieldValue;
import org.activityinfo.model.type.SubFormValue;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.form.store.AddListItemAction;
import org.activityinfo.ui.app.client.form.store.FieldState;
import org.activityinfo.ui.app.client.page.pivot.tree.FieldChooser;
import org.activityinfo.ui.app.client.page.pivot.tree.FieldSelectHandler;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Button;
import org.activityinfo.ui.style.ButtonStyle;
import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.*;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class PivotSideBar extends VComponent {

    private FieldChooser fieldChooser;
    private final AddLink addButton;
    private Application application;

    private Button saveButton;

    private FieldState state;

    public PivotSideBar(Application application, FieldState state) {
        assert state != null;
        this.application = application;
        this.state = state;
        fieldChooser = new FieldChooser(application);

        saveButton = new Button(ButtonStyle.PRIMARY, t(I18N.CONSTANTS.save()));
        saveButton.setBlock(true);

        addButton = new AddLink(I18N.CONSTANTS.addIndicator(), new ClickHandler() {
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
    }

    @Override
    protected VTree render() {
        return div(BaseStyles.FM_SIDEBAR,
            saveButton,
            subtitle(),
            ul(BaseStyles.FOLDER_LIST, measureList()),
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

    public Button getSaveButton() {
        return saveButton;
    }

    private VNode subtitle() {
        return new VNode(HtmlTag.H5, PropMap
            .withClasses(BaseStyles.SUBTITLE)
            .setStyle(new Style().set("marginTop", "30px")),
            t(I18N.CONSTANTS.indicators()),
            addButton
            );
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
    void onMeasureSelected(FormClass formClass, FormField value) {

        MeasureModel measure = new MeasureModel();
        measure.setId(Resources.generateId().asString());
        measure.setSource(formClass.getId());
        measure.setLabel(value.getLabel());
        measure.setValueExpression(new SymbolExpr(value.getId()).asExpression());
        measure.setMeasurementType(MeasurementType.FLOW);

        SubFormValue fieldValue = new SubFormValue(MeasureModel.CLASS_ID, measure.asRecord());

        application.getDispatcher().dispatch(
            new AddListItemAction(state.getInstanceId(), state.getFieldId(), fieldValue));
    }

}
