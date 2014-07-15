package org.activityinfo.ui.client.pageView;

import com.google.gwt.user.client.ui.IsWidget;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.widget.DisplayWidget;

/**
 * A UI component that renders a page for a given class of {@code FormInstance}
 */
public interface InstancePageView extends IsWidget, DisplayWidget<InstanceViewModel> {

}
