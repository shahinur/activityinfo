package org.activityinfo.server.endpoint.odk;

import com.google.inject.Inject;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.server.command.ResourceLocatorSync;

public class OdkFieldValueParserFactory {
    final private FieldValueParserFactory fieldValueParserFactory;

    @Inject
    public OdkFieldValueParserFactory(ResourceLocatorSync table) {
        fieldValueParserFactory = new FieldValueParserFactory(table);
    }

    public OdkFieldValueParser fromFieldType(FieldType fieldType) {
        return new OdkFieldValueParser(fieldValueParserFactory.fromFieldType(fieldType));
    }
}
