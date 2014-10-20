package org.activityinfo.ui.client.page.entry;

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

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.client.type.IndicatorNumberFormat;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.ui.client.page.entry.form.SiteRenderer;

public class DetailTab extends TabItem {

    private final ContentPanel panel;
    private final Html content;
    private final Dispatcher dispatcher;

    private ToggleButton hideEmptyFieldsToggle;

    private SiteDTO site;
    private SchemaDTO schema;

    public DetailTab(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        setText(I18N.CONSTANTS.details());
        setLayout(new FitLayout());

        hideEmptyFieldsToggle = new ToggleButton(I18N.CONSTANTS.hideEmptyFields());
        hideEmptyFieldsToggle.toggle(false);
        hideEmptyFieldsToggle.setStateful(true);
        hideEmptyFieldsToggle.setStateId("detailTab.hideEmptyFields");
        hideEmptyFieldsToggle.addListener(Events.Toggle, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                render();
            }
        });

        ToolBar toolBar = new ToolBar();
        toolBar.add(hideEmptyFieldsToggle);

        content = new Html();
        content.setStyleName("details");

        panel = new ContentPanel();
        panel.setHeaderVisible(false);
        panel.setTopComponent(toolBar);
        panel.add(content);
        panel.setScrollMode(Scroll.AUTOY);
        add(panel);
    }

    public void setSite(final SiteDTO site) {
        this.site = site;
        content.setHtml(I18N.CONSTANTS.loading());
        dispatcher.execute(new GetSchema(), new AsyncCallback<SchemaDTO>() {


            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(SchemaDTO result) {
                schema = result;
                render();
            }
        });
    }

    private void render() {
        if(site == null || schema == null) {
            content.setHtml(SafeHtmlUtils.EMPTY_SAFE_HTML.asString());
        } else {
            SiteRenderer renderer = new SiteRenderer(new IndicatorNumberFormat());
            renderer.setHideEmptyValues(hideEmptyFieldsToggle.isPressed());
            content.setHtml(renderer.renderSite(site, schema.getActivityById(site.getActivityId()), true));
        }
    }
}
