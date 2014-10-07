package org.activityinfo.service.tables;

import com.google.common.base.Supplier;
import org.activityinfo.model.table.ColumnView;

public interface ColumnBinding extends Supplier<ColumnView> {
    void finalizeView();
}
