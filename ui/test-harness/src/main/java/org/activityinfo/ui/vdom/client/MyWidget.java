package org.activityinfo.ui.vdom.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import org.activityinfo.ui.vdom.shared.tree.VWidget;

public class MyWidget extends VWidget {
    @Override
    public IsWidget createWidget() {
        Button button = new Button("Click me");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert("clicked!");
            }
        });
        return button;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MyWidget;
    }
}
