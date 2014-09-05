package org.activityinfo.ui.vdom.shared.diff.component;

import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.div;

public class MainPage extends VComponent<MainPage> {

    private final State state;
    private final FormComponent form;

    private boolean visible = true;

    public MainPage(State state) {
        this.state = state;
        this.form = new FormComponent(state);
    }

    public void pretendAChangeWasTriggered() {
        refresh();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        refresh();
    }

    @Override
    protected VTree render() {
        if(visible) {
            return form;
        } else {
            return div("no form");
        }
    }
}
