package org.activityinfo.ui.client.page.home;

import com.google.common.collect.Lists;
import org.activityinfo.ui.client.page.PageId;
import org.activityinfo.ui.client.page.PageState;
import org.activityinfo.ui.client.page.PageStateParser;
import org.activityinfo.ui.client.page.app.Section;

import java.util.List;

/**
 *
 */
public class HomePlace implements PageState {
    @Override
    public String serializeAsHistoryToken() {
        return null;
    }

    @Override
    public PageId getPageId() {
        return HomePage.PAGE_ID;
    }

    @Override
    public List<PageId> getEnclosingFrames() {
        return Lists.newArrayList(HomePage.PAGE_ID);
    }

    @Override
    public Section getSection() {
        return null;
    }

    public static class Parser implements PageStateParser {

        @Override
        public PageState parse(String token) {
            return new HomePlace();
        }
    }
}
