package org.activityinfo.ui.app.client.action;

import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.flux.action.Action;
import org.activityinfo.ui.flux.store.Store;

/**
 * Action triggered by navigation to a new url, for example from
 * www.activityinfo.org/ to activityinfo.org/#home
 *
 */
public class UpdatePlace implements Action {

    private Place place;

    public UpdatePlace(Place place) {
        this.place = place;
    }

    @Override
    public void accept(Store listener) {
        if(listener instanceof NavigationHandler) {
            ((NavigationHandler) listener).navigate(place);
        }
    }
}
