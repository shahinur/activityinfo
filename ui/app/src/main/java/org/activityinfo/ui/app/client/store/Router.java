package org.activityinfo.ui.app.client.store;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import org.activityinfo.ui.app.client.action.NavigationHandler;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.home.HomePlace;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores the current "route" or internal URL within the application
 *
 */
public class Router extends AbstractStore implements NavigationHandler {

    private static final Logger LOGGER = Logger.getLogger(Router.class.getName());

    private Place currentPlace = HomePlace.INSTANCE;

    public Router(Dispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void navigate(Place place) {
        if(!place.equals(currentPlace)) {
            this.currentPlace = place;
            LOGGER.log(Level.INFO, "Router.currentPlace = " + place);
            fireChange();
        }
    }

    public <X extends Place> X getCurrentPlace() {
        return (X)currentPlace;
    }

    public static SafeUri uri(Place place) {
        StringBuilder sb = new StringBuilder("#");
        String[] tokens = place.getPath();
        for(int i=0;i!=tokens.length;++i) {
            if(i > 0) {
                sb.append("/");
            }
            sb.append(tokens[i]);
        }
        return UriUtils.fromTrustedString(sb.toString());
    }

}
