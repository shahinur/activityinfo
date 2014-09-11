package org.activityinfo.ui.app.client.chrome.nav;

import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.store.Router;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.Objects;

public class NavLink extends VComponent implements StoreChangeListener {

    private final Router router;

    private NavLinkRenderer renderer = new DefaultNavLinkRenderer();

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

    public NavLinkRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(NavLinkRenderer renderer) {
        this.renderer = renderer;
    }

    public boolean isActive() {
        return Objects.equals(router.getCurrentPlace(), target);
    }

    @Override
    protected VTree render() {
        return renderer.render(this);
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
