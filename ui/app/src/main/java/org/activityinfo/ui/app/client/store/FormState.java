package org.activityinfo.ui.app.client.store;
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

import org.activityinfo.model.form.FormClass;
import org.activityinfo.ui.app.client.form.store.FormChangeHandler;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;

import javax.annotation.Nonnull;

/**
 * @author yuriyz on 9/18/14.
 */
public class FormState extends AbstractStore implements FormChangeHandler {

    private FormClass formClass;

    public FormState(@Nonnull Dispatcher dispatcher, @Nonnull FormClass formClass) {
        super(dispatcher);
        this.formClass = formClass;
    }

    public FormClass getFormClass() {
        return formClass;
    }
}
