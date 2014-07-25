package org.activityinfo.ui.client.component.formdesigner.skip;
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.activityinfo.ui.client.component.formdesigner.container.FieldWidgetContainer;

/**
 * @author yuriyz on 7/24/14.
 */
public class SkipPanelPresenter {

    private final FieldWidgetContainer fieldWidgetContainer;
    private final SkipPanel view = new SkipPanel();

    public SkipPanelPresenter(final FieldWidgetContainer fieldWidgetContainer) {
        this.fieldWidgetContainer = fieldWidgetContainer;

        addRow(fieldWidgetContainer);
    }

    private void addRow(final FieldWidgetContainer fieldWidgetContainer) {
        final SkipRowPresenter skipRowPresenter = new SkipRowPresenter(fieldWidgetContainer);
        view.getRootPanel().add(skipRowPresenter.getView());
        skipRowPresenter.getView().getAddButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addRow(fieldWidgetContainer);
            }
        });
        skipRowPresenter.getView().getRemoveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                view.getRootPanel().add(skipRowPresenter.getView());
            }
        });
    }

    public SkipPanel getView() {
        return view;
    }

    public void updateFormField() {
        fieldWidgetContainer.getFormField().setSkipExpression(buildSkipExpression());
    }

    private String buildSkipExpression() {
        return "skip expression build is under construction"; // todo
    }
}
