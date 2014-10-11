package org.activityinfo.ui.widget.loading;

import com.google.gwt.user.client.ui.IsWidget;
import org.activityinfo.promise.Promise;

/**
 * Marker interface for a widget that displays a value
 * of a certain type
 */
public interface DisplayWidget<V> extends IsWidget {

    Promise<Void> show(V value);
}
