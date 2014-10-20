package org.activityinfo.ui.client.page.entry.form;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import org.activityinfo.ui.client.page.entry.form.resources.SiteFormResources;

public class FieldGroup extends LayoutContainer {

    public FieldGroup(Component... components) {
        addStyleName(SiteFormResources.INSTANCE.style().fieldGroup());
        for (int i = 0; i < components.length; i++) {
            add(components[i]);
        }
    }

}
