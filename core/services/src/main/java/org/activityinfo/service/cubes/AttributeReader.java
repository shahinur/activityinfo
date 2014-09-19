package org.activityinfo.service.cubes;

import org.activityinfo.service.tables.RowSetBuilder;

public interface AttributeReader {

    int getAttributeIndex();

    void start();

    double factor(int rowIndex);

    String member(int rowIndex);

    void scheduleRequests(RowSetBuilder rowSetBuilder);
}
