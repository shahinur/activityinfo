package org.activityinfo.ui.app.client.page;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.action.UpdatePlace;
import org.activityinfo.ui.app.client.store.Router;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class WindowLocationHash implements StoreChangeListener {

    private static final Logger LOGGER = Logger.getLogger(WindowLocationHash.class.getName());

    private final Dispatcher dispatcher;
    private final PlaceMapper placeMapper = new PlaceMapper();
    private final Router router;
    private Application application;

    public WindowLocationHash(Application application) {
        this.application = application;
        this.dispatcher = application.getDispatcher();
        this.router = application.getRouter();
    }

    public Place getCurrentPlace() {
        return placeMapper.parse(History.getToken());
    }

    public void start() {

        dispatcher.dispatch(new UpdatePlace(getCurrentPlace()));

        router.addChangeListener(this);

        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {

                LOGGER.log(Level.INFO, "Triggering navigation to [" + event.getValue() + "]");

                dispatch(event.getValue());
            }
        });
    }

    private void dispatch(String value) {
        Place place = placeMapper.parse(value);
        place.navigateTo(application);
    }

    @Override
    public void onStoreChanged(Store store) {
        if(store == router) {
            String hash = toToken(router.getCurrentPlace());
            if(!hash.equals(History.getToken())) {

                LOGGER.log(Level.INFO, "Updating history to [" + hash + "]");
                History.newItem(hash, false);
            }
        }
    }

    private static String toToken(Place place) {
        StringBuilder sb = new StringBuilder();
        String[] tokens = place.getPath();
        for(int i=0;i!=tokens.length;++i) {
            if(i > 0) {
                sb.append("/");
            }
            sb.append(tokens[i]);
        }
        return sb.toString();
    }
}
