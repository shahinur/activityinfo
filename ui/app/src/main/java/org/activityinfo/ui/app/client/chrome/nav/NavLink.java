package org.activityinfo.ui.app.client.chrome.nav;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class NavLink extends VThunk {

    public static SafeUri DEFAULT_URL = UriUtils.fromSafeConstant("#");

    private String label;
    private Icon icon;
    private SafeUri url = DEFAULT_URL;
    private boolean active;

    public NavLink() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public SafeUri getUrl() {
        return url;
    }

    public void setUrl(SafeUri url) {
        assert url != null;
        this.url = url;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    protected VTree render() {
        return li(listItemStyle(), link(url, icon.render(), space(), span(label)));
    }

    private PropMap listItemStyle() {
        if(active) {
            return PropMap.withClasses(BaseStyles.NAV_ACTIVE, BaseStyles.ACTIVE);
        } else {
            return PropMap.EMPTY;
        }
    }
}
