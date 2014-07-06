package org.activityinfo.ui.client.page.entry.place;
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

import com.google.common.base.Strings;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.Cuids;
import org.activityinfo.ui.client.page.PageStateParser;

/**
 * #form/a34234/s2343
 * #form/{defId}/{siteId}
 *
 * @author yuriyz on 2/3/14.
 */
public class UserFormPlaceParser implements PageStateParser {

    public static String serialize(UserFormPlace place) {
        final StringBuilder fragment = new StringBuilder();
        if (place.getUserFormId() != null) {
            fragment.append(normalizeIri(place.getUserFormId())).append("/");
        }
        if (place.getUserFormInstanceId() != null) {
            fragment.append(normalizeIri(place.getUserFormInstanceId())).append("/");
        }
        return fragment.toString();
    }

    public static String normalizeIri(ResourceId iri) {
        final String iriAsString = iri.asString();
        if (iriAsString.contains(Cuids.IRI_PREFIX)) {
            return iriAsString.substring(Cuids.IRI_PREFIX.length());
        }
        return iriAsString;
    }

    public static UserFormPlace parseToken(String token) {
        final UserFormPlace place = new UserFormPlace();
        if (!Strings.isNullOrEmpty(token)) {
            final String searchString = UserFormPlace.PAGE_ID.getId() + "/";
            final int indexOf = token.indexOf(searchString);
            if (indexOf != -1) {
                token = token.substring(indexOf + searchString.length());
            }

            final String[] parts = token.split("\\/");
            if (parts.length == 2) {
                place.setUserFormId(ResourceId.create(parts[0]));
                place.setUserFormInstanceId(ResourceId.create(parts[1]));
            }
        }
        return place;
    }

    @Override
    public UserFormPlace parse(String token) {
        return parseToken(token);
    }
}
