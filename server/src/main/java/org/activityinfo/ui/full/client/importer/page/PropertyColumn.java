package org.activityinfo.ui.full.client.importer.page;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.ui.full.client.importer.binding.DraftModel;
import org.activityinfo.ui.full.client.importer.ont.PropertyPath;

public class PropertyColumn<C> extends Column<DraftModel, C> {

    private final PropertyPath property;

    public PropertyColumn(PropertyPath property, Cell<C> cell) {
        super(cell);
        this.property = property;
    }

    @Override
    public C getValue(DraftModel object) {
        C value = (C) object.getValue(property.getKey());
        return value;
    }

    @Override
    public String getCellStyleNames(Context context, DraftModel object) {
        if (object == null) {
            return null;
        }
        if (object.getValue(property.getKey()) == null) {
            return "danger";
        }
        return null;
    }
}