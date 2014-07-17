package org.activityinfo.core.client;

import com.google.gwt.view.client.ProvidesKey;
import org.activityinfo.core.shared.Projection;

/**
 * Provides keys for Projections
 */
public class ProjectionKeyProvider implements ProvidesKey<Projection> {
    @Override
    public String getKey(Projection item) {
        return item.getRootInstanceId().asString();
    }
}
