package org.activityinfo.io.importing.strategy;

import org.activityinfo.io.importing.source.SourceRow;

public class MissingColumn implements ColumnAccessor {

    public final static MissingColumn INSTANCE = new MissingColumn();

    private MissingColumn() {
    }

    @Override
    public String getHeading() {
        return null;
    }

    @Override
    public String getValue(SourceRow row) {
        return null;
    }

    @Override
    public boolean isMissing(SourceRow row) {
        return true;
    }
}
