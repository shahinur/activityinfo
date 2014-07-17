package org.activityinfo.core.shared.importing.strategy;

import org.activityinfo.core.shared.importing.source.SourceRow;

/**
 * Interface to a column's value, abstracting away differences between
 * columns in imported tables, values provided by users during import and
 * (ultimately) calculated values
 */
public interface ColumnAccessor {

    String getHeading();

    String getValue(SourceRow row);

    boolean isMissing(SourceRow row);



}
