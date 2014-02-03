package org.activityinfo.ui.full.client.page.entry;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.activityinfo.ui.full.client.page.*;
import org.activityinfo.ui.full.client.page.entry.place.SiteFormPlace;

/**
 * @author yuriyz on 1/31/14.
 */
public class SiteFormLoader implements PageLoader {

    private final Provider<SiteFormPage> dataEntryPageProvider;

    @Inject
    public SiteFormLoader(
            NavigationHandler pageManager,
            PageStateSerializer placeSerializer,
            Provider<SiteFormPage> dataEntryPageProvider) {

        this.dataEntryPageProvider = dataEntryPageProvider;

        pageManager.registerPageLoader(SiteFormPage.PAGE_ID, this);
        placeSerializer.registerStatelessPlace(SiteFormPage.PAGE_ID, new SiteFormPlace());
    }

    @Override
    public void load(final PageId pageId, final PageState pageState,
                     final AsyncCallback<Page> callback) {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onSuccess() {
                final SiteFormPage siteFormPage = dataEntryPageProvider.get();
                siteFormPage.navigate(pageState);
                callback.onSuccess(siteFormPage);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }
}
