package org.activityinfo.legacy.shared.adapter;
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

import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Cuids;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author yuriyz on 2/10/14.
 */
public class CuidAdapterTest {

    @Test
    public void blockSize() {
        assertThat(CuidAdapter.BLOCK_SIZE, equalTo(Integer.toString(Integer.MAX_VALUE, Cuids.RADIX).length()));
    }

    @Test
    public void locationInstance() {
        int id = 998707825;
        final Cuid cuid = CuidAdapter.locationInstanceId(id);
        final int legacyIdFromCuid = CuidAdapter.getLegacyIdFromCuid(cuid);
        Assert.assertEquals(id, legacyIdFromCuid);

        Cuid fieldId = CuidAdapter.field(cuid, CuidAdapter.ADMIN_FIELD);
        assertThat(CuidAdapter.getBlock(fieldId, 0), equalTo(id));
        assertThat(CuidAdapter.getBlock(fieldId, 1), equalTo(CuidAdapter.ADMIN_FIELD));
    }

}
