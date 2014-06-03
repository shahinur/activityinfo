package org.activityinfo.core.shared.table.provider;
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

import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.table.ColumnView;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.client.callback.SuccessCallback;
import org.activityinfo.ui.client.component.table.FieldColumn;

/**
 * @author yuriyz on 5/29/14.
 */
public class MainColumnViewProvider implements ColumnViewProvider {

    private final ResourceLocator resourceLocator;
    private final CachedColumnViewProvider cache;
    private final ColumnViewProviderBuilder providerBuilder;

    public MainColumnViewProvider(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        this.cache = new CachedColumnViewProvider(resourceLocator);
        this.providerBuilder = new ColumnViewProviderBuilder(resourceLocator, this);
    }

    public CachedColumnViewProvider getCache() {
        return cache;
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    @Override
    public Promise<? extends ColumnView> view(final FieldColumn column, final FormClass formClass) {
        Promise<? extends ColumnView> cachedView = cache.view(column, formClass);
        if (cachedView != null && cachedView.get() != null) {
            return cachedView;
        } else {
            Promise<? extends ColumnView> view = providerBuilder.build(column).view(column, formClass);
            view.then(new SuccessCallback<ColumnView>() {
                @Override
                public void onSuccess(ColumnView result) {
                    cache.put(column.getFieldPaths().get(0), formClass.getId(), result, result.getFormClassCacheId());
                }
            });

            return view;
        }
    }
}
