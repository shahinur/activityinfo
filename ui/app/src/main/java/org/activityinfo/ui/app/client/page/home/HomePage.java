package org.activityinfo.ui.app.client.page.home;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.PageFrame;
import org.activityinfo.ui.app.client.page.PageView;
import org.activityinfo.ui.app.client.page.PageViewFactory;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.style.Alert;
import org.activityinfo.ui.style.AlertStyle;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Grid;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class HomePage extends PageView {

    public static class Factory implements PageViewFactory<HomePlace> {

        private final Application application;
        private HomePage instance;

        public Factory(Application application) {
            this.application = application;
        }

        @Override
        public boolean accepts(Place place) {
            return place instanceof HomePlace;
        }

        @Override
        public PageView create(HomePlace place) {
            if(instance == null) {
                instance = new HomePage(application);
            }
            return instance;
        }
    }

    private final Application application;

    public HomePage(Application application) {
        this.application = application;
    }

    @Override
    protected VTree render() {
        return new PageFrame(
            FontAwesome.HOME, I18N.CONSTANTS.home(),
            div(BaseStyles.CONTENTPANEL, Grid.row(announcement())));
    }

    public static VTree announcement() {
        return new Alert(AlertStyle.INFO,
            h4("Welcome to Activity 3.0 Beta!"),
            p(strong("Please note"),
                t(" that not all functionality is yet available in this beta; we look forward " +
                    " to your feedback and are working to completing the migration as soon as possible.")));

    }

}
