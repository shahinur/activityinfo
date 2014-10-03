package org.activityinfo.ui.style;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Event;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VText;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class DropdownMenuItem extends VComponent {


    private boolean newItem;
    private VTree thumbnail = VText.NO_BREAK_SPACE;
    private VTree name = VText.NO_BREAK_SPACE;
    private VTree message = VText.NO_BREAK_SPACE;
    private SafeUri uri = UriUtils.fromSafeConstant("#");
    private ClickHandler clickHandler;

    public boolean isNewItem() {
        return newItem;
    }

    public void setNewItem(boolean newItem) {
        this.newItem = newItem;
    }

    public VTree getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(VTree thumbnail) {
        this.thumbnail = thumbnail;
    }

    public VTree getName() {
        return name;
    }

    public void setName(VTree name) {
        this.name = name;
    }

    public VTree getMessage() {
        return message;
    }

    public void setMessage(VTree message) {
        this.message = message;
    }

    public void setClickHandler(ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    protected VTree render() {

        return li(
            a(href(uri),
                span(BaseStyles.THUMB, thumbnail),
                span(BaseStyles.DESC,
                    span(BaseStyles.NAME, name,
                        newBadge()),
                    span(BaseStyles.MSG, message)
                )
            )
        );
    }

    private VTree newBadge() {
        if (isNewItem()) {
            return Badges.success(I18N.CONSTANTS.newBadgeText());
        } else {
            return VText.EMPTY_TEXT;
        }
    }

    @Override
    public int getEventMask() {
        return Event.ONCLICK;
    }

    @Override
    public void onBrowserEvent(DomEvent event) {
        if(clickHandler != null) {
            clickHandler.onClicked();
        }
        event.preventDefault();
    }
}
