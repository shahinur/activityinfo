package org.activityinfo.ui.app.client.page.pivot;

import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ListFieldValue;
import org.activityinfo.ui.app.client.form.store.FieldState;
import org.activityinfo.ui.style.*;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.*;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class ListPanel extends VComponent {

    private FieldState state;
    private Button addButton;

    public ListPanel(FieldState state) {
        this.state = state;
        this.addButton = new Button(ButtonStyle.DEFAULT, t("Add"));
    }

    public Button getAddButton() {
        return addButton;
    }

    @Override
    protected VTree render() {
        Panel panel = new Panel();
        panel.setStyle(PanelStyle.INFO);
        panel.setTitle(t(state.getField().getLabel()));
        panel.setContent(
            new VNode(HtmlTag.DIV,
                ul(BaseStyles.FOLDER_LIST, renderList()),
                addButton));

        return div(PropMap.withStyle(new Style().set("marginTop", "30px")), panel);
    }

    private VTree[] renderList() {
        if(state.getValue() instanceof ListFieldValue) {

            ListFieldValue currentValue = (ListFieldValue) state.getValue();
            return map(currentValue.getElements(), new H.Render<FieldValue>() {
                @Override
                public VTree render(FieldValue item) {
                    return renderItem(item);
                }
            });
        } else {
            return new VTree[0];
        }
    }

    private VTree renderItem(FieldValue item) {
        if(item instanceof Record) {
            Record subForm = (Record) item;
            CloseButton deleteButton = new CloseButton(FloatStyle.RIGHT);
            return li(t(subForm.getString("label")), deleteButton);
        } else {
            return li("error");
        }
    }
}
