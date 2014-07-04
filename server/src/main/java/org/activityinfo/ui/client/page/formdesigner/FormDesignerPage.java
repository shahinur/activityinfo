package org.activityinfo.ui.client.page.formdesigner;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.ui.client.component.formdesigner.FormDesignerDialog;
import org.activityinfo.ui.client.page.NavigationCallback;
import org.activityinfo.ui.client.page.Page;
import org.activityinfo.ui.client.page.PageId;
import org.activityinfo.ui.client.page.PageState;

/**
 * @author yuriyz on 7/4/14.
 */
public class FormDesignerPage implements Page {

    public static final PageId PAGE_ID = new PageId("formdesigner");

    interface PageUiBinder extends UiBinder<HTMLPanel, FormDesignerPage> {
    }

    private static PageUiBinder uiBinder = GWT.create(PageUiBinder.class);

    private final HTMLPanel panel;

    public FormDesignerPage(ResourceLocator resourceLocator) {
        panel = uiBinder.createAndBindUi(this);
    }

    @UiHandler(value = "createForm")
    public void onCreateForm(ClickEvent event) {
        FormDesignerDialog dialog = new FormDesignerDialog();
        dialog.show();
    }

    @Override
    public PageId getPageId() {
        return PAGE_ID;
    }

    @Override
    public Object getWidget() {
        return panel;
    }

    @Override
    public void requestToNavigateAway(PageState place, NavigationCallback callback) {
        callback.onDecided(true);
    }

    @Override
    public String beforeWindowCloses() {
        return null;
    }

    @Override
    public boolean navigate(PageState place) {
        return false;
    }

    @Override
    public void shutdown() {
    }
}
