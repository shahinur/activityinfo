package org.activityinfo.ui.app.client.page.pivot;

import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ListFieldValue;
import org.activityinfo.model.type.SubFormValue;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.form.store.FieldState;
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

    private ListFieldValue currentValue;
    private FieldChooser fieldChooser;
    private final Button addButton;

    public MeasureList(Application application, FieldState state) {
        fieldChooser = new FieldChooser(application);
        addButton = new Button(ButtonStyle.PRIMARY, FontAwesome.PLUS.render(), t("Add Measure"));
        addButton.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                fieldChooser.showFormSelection();
                fieldChooser.setVisible(true);
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
        if(currentValue == null) {
            return new VTree[0];
        } else {
            return map(currentValue.getElements(), new Render<FieldValue>() {
                @Override
                public VTree render(FieldValue item) {
                    return renderMeasure(item);
                }
            });
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
}
