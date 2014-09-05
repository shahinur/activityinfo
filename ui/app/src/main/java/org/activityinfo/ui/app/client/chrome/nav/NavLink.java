package org.activityinfo.ui.app.client.chrome.nav;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.store.Router;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.Objects;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class NavLink extends VComponent implements StoreChangeListener {

    public static SafeUri DEFAULT_URL = UriUtils.fromSafeConstant("#");

    private final Router router;

    private String label;
    private Icon icon;
    private Place target;

    public NavLink(Router router) {
        this.router = router;
    }

    @Override
    protected void componentDidMount() {
        router.addChangeListener(this);
    }

    @Override
    protected void componentWillUnmount() {
        router.removeChangeListener(this);
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

    public Place getTarget() {
        return target;
    }

    public void setTarget(Place target) {
        this.target = target;
    }

    public boolean isActive() {
        return Objects.equals(router.getCurrentPlace(), target);
    }

    @Override
    protected VTree render() {
        SafeUri uri = target == null ? DEFAULT_URL : Router.uri(target);

        return li(listItemStyle(), link(uri, icon.render(), space(), span(label)));
    }

    private PropMap listItemStyle() {
        if(isActive()) {
            return PropMap.withClasses(BaseStyles.NAV_ACTIVE, BaseStyles.ACTIVE);
        } else {
            return PropMap.EMPTY;
        }
    }

    @Override
    public void onStoreChanged(Store store) {
        refresh();
    }

    @Override
    public String getPropertiesForDebugging() {
        return "label=" + label;
    }
}
