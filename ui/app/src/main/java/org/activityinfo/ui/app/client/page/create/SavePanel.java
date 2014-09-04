package org.activityinfo.ui.app.client.page.create;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.style.*;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.activityinfo.ui.vdom.shared.html.H.t;

public class SavePanel extends VComponent {

    private final Button button;

    private Promise.State requestState = Promise.State.FULFILLED;

    public SavePanel() {
        button = new Button(ButtonStyle.PRIMARY);
    }

    public void setClickHandler(ClickHandler clickHandler) {
        button.setClickHandler(clickHandler);
    }

    @Override
    protected VTree render() {

        switch (requestState) {
            case FULFILLED:
                button.setEnabled(true);
                button.setContent(t(I18N.CONSTANTS.createWorkspace()));
                break;

            case PENDING:
                button.setEnabled(false);
                button.setContent(Spinners.spinner().render(), t(I18N.CONSTANTS.saving()));
                break;

            case REJECTED:
                button.setEnabled(true);
                button.setContent(Spinners.spinner().render(), t(I18N.CONSTANTS.retry()));
                break;
        }

        return div(BaseStyles.ROW, div("col-sm-6 col-sm-offset-3", button));
    }

    public void updateRequestState(Promise.State state) {
        this.requestState = state;
        button.refresh();
    }
}
