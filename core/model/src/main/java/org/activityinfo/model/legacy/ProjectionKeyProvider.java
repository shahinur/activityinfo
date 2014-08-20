package org.activityinfo.model.legacy;

import com.google.gwt.view.client.ProvidesKey;

/**
 * Provides keys for Projections
 */
public class ProjectionKeyProvider implements ProvidesKey<Projection> {
    @Override
    public String getKey(Projection item) {
        return item.getRootInstanceId().asString();
    }
}
