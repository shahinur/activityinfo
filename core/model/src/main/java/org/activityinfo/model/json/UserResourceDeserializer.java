package org.activityinfo.model.json;
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.activityinfo.model.resource.UserResource;

import java.io.IOException;

/**
 * @author yuriyz on 9/26/14.
 */
public class UserResourceDeserializer extends JsonDeserializer<UserResource> {
    @Override
    public UserResource deserialize(JsonParser reader,
                                    DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        UserResource userResource = UserResource.userResource();

        while (reader.nextToken() == JsonToken.FIELD_NAME) {
            String propertyName = reader.getCurrentName();

            // read the value
            reader.nextToken();

            if (propertyName.equals("@editAllowed")) {
                userResource.setEditAllowed(Boolean.valueOf(reader.getText()));

            } else if (propertyName.equals("@owner")) {
                userResource.setOwner(Boolean.valueOf(reader.getText()));

            } else if (propertyName.equals("@resource")) {
                userResource.setResource(ResourceDeserializer.deserialize(reader));
            }
        }
        return userResource;
    }

}
