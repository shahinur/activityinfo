package org.activityinfo.model.type.formatter;
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

import com.google.gwt.core.shared.GwtIncompatible;
import org.activityinfo.model.type.time.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author yuriyz on 3/10/14.
 */
@GwtIncompatible
public class JavaDateFormatterFactory implements DateFormatterFactory {

    private static final SimpleDateFormat JAVA_FORMAT = new SimpleDateFormat(FORMAT);

    @Override
    public DateFormatter create() {
        return new DateFormatter() {
            @Override
            public String format(LocalDate value) {
                return JAVA_FORMAT.format(value);
            }

            @Override
            public LocalDate parse(String valueAsString) {
                try {
                    return new LocalDate(JAVA_FORMAT.parse(valueAsString));
                } catch (ParseException e) {
                    e.printStackTrace(); // todo log
                    return null;
                }
            }
        };
    }
}
