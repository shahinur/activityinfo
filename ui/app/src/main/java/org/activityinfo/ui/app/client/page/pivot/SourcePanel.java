package org.activityinfo.ui.app.client.page.pivot;

import org.activityinfo.ui.style.*;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VText;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class SourcePanel extends VComponent {

    private Button addButton;
    private Modal modal;

    public SourcePanel() {
        addButton = new Button(ButtonStyle.PRIMARY, t("Add Source"));
        modal = new Modal();
    }

    @Override
    protected VTree render() {
        Panel panel = new Panel("Source Forms", formList());
        panel.setIntroParagraph(helpText());
        panel.setFooter(div(PropMap.EMPTY, addButton));
        return panel;
    }

    private VTree formList() {
        return p("No sources added yet!");
    }

    private VText helpText() {
        return t("Choose the forms that you want to include in this cube.");
    }

    public Button getAddButton() {
        return addButton;
    }
}
