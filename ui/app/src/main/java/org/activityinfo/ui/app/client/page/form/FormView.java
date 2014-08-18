package org.activityinfo.ui.app.client.page.form;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.TextBox;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Grid;
import org.activityinfo.ui.vdom.shared.tree.VTree;
import org.activityinfo.ui.vdom.shared.tree.VWidget;

import static org.activityinfo.ui.vdom.shared.html.H.div;

public class FormView {

    public static VTree render(FormPage page) {

        return div(BaseStyles.CONTENTPANEL,
                    new ViewSelector(page),
                    Grid.row(new MyWidget())
                );


    }

    private static class MyWidget extends VWidget {

        @Override
        public Element init() {
            TextBox box = new TextBox();
            box.setText("hello world");
            return box.getElement();
        }
    }

}
