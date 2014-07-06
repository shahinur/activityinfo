package org.activityinfo.core.shared.expr;
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

import junit.framework.Assert;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.junit.Test;

/**
 * @author yuriyz on 2/4/14.
 */
public class NamespaceTest {

    @Test
    public void siteId() {
        final int activityId = 34;
        final ResourceId resourceId = CuidAdapter.activityFormClass(34);
        Assert.assertEquals(activityId, CuidAdapter.getLegacyIdFromCuid(resourceId));
    }

}
