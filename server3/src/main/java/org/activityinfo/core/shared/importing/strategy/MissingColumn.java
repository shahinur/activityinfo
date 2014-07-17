package org.activityinfo.core.shared.importing.strategy;

import org.activityinfo.core.shared.importing.source.SourceRow;

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
