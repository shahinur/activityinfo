package org.activityinfo.core.shared.importing.match;
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
import com.google.common.collect.Maps;
import org.activityinfo.model.form.FormFieldType;
import org.activityinfo.core.shared.type.converter.Converter;
import org.activityinfo.core.shared.type.converter.ConverterFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Guesser use converters to guess column type.
 * <p/>
 * Note: If we can NOT convert value to particular type then we
 * don't have other choice then consider it as string.
 *
 * @author yuriyz on 5/9/14.
 */
public class ColumnTypeGuesser {

    private final Map<FormFieldType, Integer> typeMap = newFieldMap();

    private final List<String> columnValues;
    private final ConverterFactory converterFactory;

    public ColumnTypeGuesser(List<String> columnValues, ConverterFactory converterFactory) {
        this.columnValues = columnValues;
        this.converterFactory = converterFactory;
    }

    public FormFieldType guessType() {
        calculateTypeScores();

        final List<Map.Entry<FormFieldType, Integer>> copyEntrySet = Lists.newArrayList(typeMap.entrySet());
        Collections.sort(copyEntrySet, new Comparator<Map.Entry<FormFieldType, Integer>>() {
            @Override
            public int compare(Map.Entry<FormFieldType, Integer> o1, Map.Entry<FormFieldType, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        return copyEntrySet.get(copyEntrySet.size() - 1).getKey();
    }

    private void calculateTypeScores() {
        for (String value : columnValues) {
            final Map<FormFieldType, Integer> copyMap = Maps.newHashMap(typeMap);

            // we don't need to iterate over string types because input is always string
            copyMap.remove(FormFieldType.FREE_TEXT);
            copyMap.remove(FormFieldType.NARRATIVE);

            boolean hasMatch = false;
            for (Map.Entry<FormFieldType, Integer> entry : copyMap.entrySet()) {
                try {
                    final Converter stringConverter = converterFactory.createStringConverter(entry.getKey());
                    final Object convertedValue = stringConverter.convert(value);

                    // analyze converted value
                    if (convertedValue != null) {
                        increaseValue(entry.getKey());
                        hasMatch = true;
                        break;
                    }
                } catch (Exception e) {
                    // ignore
                }
            }

            // if no match then we fallback to string type
            if (!hasMatch) {
                final int length = value.length();
                if (length < FormFieldType.FREE_TEXT_LENGTH) {
                    increaseValue(FormFieldType.FREE_TEXT);
                } else {
                    increaseValue(FormFieldType.NARRATIVE);
                }
            }
        }
    }

    private void increaseValue(FormFieldType type) {
        typeMap.put(type, typeMap.get(type) + 1);
    }

    private static Map<FormFieldType, Integer> newFieldMap() {
        Map<FormFieldType, Integer> typeMap = Maps.newHashMap();
        for (FormFieldType type : FormFieldType.values()) {
            typeMap.put(type, 0);
        }
        return typeMap;
    }

}
