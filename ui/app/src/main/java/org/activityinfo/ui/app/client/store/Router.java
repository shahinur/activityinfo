package org.activityinfo.ui.app.client.store;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import org.activityinfo.ui.app.client.page.PageStore;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.PlaceMapper;
import org.activityinfo.ui.app.client.page.create.NewWorkspacePage;
import org.activityinfo.ui.app.client.page.create.NewWorkspacePlace;
import org.activityinfo.ui.app.client.page.folder.FolderPlace;
import org.activityinfo.ui.app.client.page.home.HomePage;
import org.activityinfo.ui.app.client.page.home.HomePlace;
import org.activityinfo.ui.flux.store.Store;

/**
 * Tracks the current "route" or internal URL within the application,
 * and manages the starting and stopping of "stores" associated with
 * current activity.
 *
 */
public class Router implements Store {

    private PlaceMapper placeMapper = new PlaceMapper();

    private Place currentPlace = new HomePlace();

    private PageStore activePage = new HomePage();

    private Application application;

    public Router(Application application) {
        this.application = application;
    }

    public void updatePath(String path) {
        Place place = placeMapper.parse(path);
        if(!place.equals(currentPlace)) {
            navigateAndFire(place);
        }
    }

    private void navigateAndFire(Place path) {
        this.currentPlace = path;
        if(path instanceof HomePlace) {
            navigate(new HomePage());

        } else if(path instanceof NewWorkspacePlace) {
            navigate(new NewWorkspacePage(application));

        } else if(path instanceof FolderPlace) {

        }
        application.getStoreEventBus().fireChange(this);
    }

    private void navigate(PageStore homePage) {
        if(activePage != null) {
            activePage.stop();
        }
        activePage = homePage;
        activePage.start();
    }

    public PageStore getActivePage() {
        return activePage;
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
