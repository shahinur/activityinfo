package org.activityinfo.ui.style;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.Style;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static com.google.gwt.dom.client.Style.Display.BLOCK;
import static com.google.gwt.dom.client.Style.Display.NONE;
import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.activityinfo.ui.vdom.shared.html.H.h4;
import static org.activityinfo.ui.vdom.shared.tree.PropMap.withClasses;

/**
 * Modal Dialog Component
 *
 * <p>Port of the Bootstrap/Bracket Modal component.</p>
 */
public class Modal extends VComponent {

    private final CloseButton closeButton;

    private VTree title;
    private VTree body;
    private VTree[] footer;

    private boolean visible;

    public Modal() {
        this.closeButton = new CloseButton();
        this.closeButton.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                setVisible(false);
            }
        });
    }

    public VTree[] getFooter() {
        return footer;
    }

    public void setFooter(VTree... footer) {
        this.footer = footer;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if(this.visible != visible) {
            this.visible = visible;
            refresh();
        }
    }

    public VTree getTitle() {
        return title;
    }

    public void setTitle(VTree title) {
        if(this.title != title) {
            this.title = title;
            refresh();
        }
    }

    public VTree getBody() {
        return body;
    }

    public void setBody(VTree body) {
        this.body = body;
    }

    @Override
    protected VTree render() {

        PropMap props = new PropMap();
        props.setClass(BaseStyles.MODAL);
        props.set("tabIndex", "-1");

        if(visible) {
            props.setStyle(new Style().display(BLOCK));
            props.set("className", "modal fade in");
        } else {
            props.setStyle(new Style().display(NONE));
            props.set("className", "modal");
        }

        return div(props,
                div(MODAL_DIALOG,
                    div(MODAL_CONTENT,
                        div(MODAL_HEADER,
                            closeButton,
                            h4(withClasses(MODAL_TITLE), title)),
                        div(MODAL_BODY, body),
                        div(MODAL_FOOTER, footer))));
    }

    @Override
    public int getEventMask() {
        return Event.KEYEVENTS;
    }

    @Override
    public void onBrowserEvent(Event event) {
        if(event.getTypeInt() == Event.ONKEYPRESS) {
            if(event.getKeyCode() == KeyCodes.KEY_ESCAPE) {
                visible = false;
                refresh();
                event.preventDefault();
            }
        }
    }
}