package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.div;

public class DropdownButton extends VComponent {

    private final DropdownToggle toggle;
    private DropdownMenu menu;
    private boolean open = false;

    public DropdownButton() {
        this.toggle = new DropdownToggle(new ClickHandler() {
            @Override
            public void onClicked() {
                toggleMenu();
            }
        });
        this.menu = new DropdownMenu();
    }

    public DropdownToggle getToggle() {
        return toggle;
    }

    public DropdownMenu getMenu() {
        return menu;
    }

    private void toggleMenu() {
        open = !open;
        refresh();
    }

    @Override
    protected VTree render() {
        return div(groupProps(), toggle, menu);
    }

    private PropMap groupProps() {
        if (open) {
            return PropMap.withClasses(BaseStyles.BTN_GROUP, BaseStyles.OPEN);
        } else {
            return PropMap.withClasses(BaseStyles.BTN_GROUP);
        }
    }
}
