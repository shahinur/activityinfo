package org.activityinfo.service.tables.views;

import com.google.common.base.Supplier;
import org.activityinfo.model.table.ColumnView;

public interface ColumnScanner extends InstanceSink, Supplier<ColumnView> {

    void finalizeView();

    void useCached(ColumnView view);
}
