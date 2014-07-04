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

import com.google.common.collect.Lists;
import org.activityinfo.ui.client.page.PageId;
import org.activityinfo.ui.client.page.PageState;
import org.activityinfo.ui.client.page.PageStateParser;
import org.activityinfo.ui.client.page.app.Section;

import java.util.List;

/**
 * @author yuriyz on 7/4/14.
 */
public class FormDesignerPlace implements PageState {

    @Override
    public String serializeAsHistoryToken() {
        return null;
    }

    @Override
    public PageId getPageId() {
        return FormDesignerPage.PAGE_ID;
    }

    @Override
    public List<PageId> getEnclosingFrames() {
        return Lists.newArrayList(FormDesignerPage.PAGE_ID);
    }

    @Override
    public Section getSection() {
        return null;
    }

    public static class Parser implements PageStateParser {

        @Override
        public PageState parse(String token) {
            return new FormDesignerPlace();
        }
    }
}
