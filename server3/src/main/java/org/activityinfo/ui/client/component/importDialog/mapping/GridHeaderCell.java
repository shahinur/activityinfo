package org.activityinfo.ui.client.component.importDialog.mapping;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.activityinfo.core.shared.importing.model.*;
import org.activityinfo.core.shared.importing.source.SourceColumn;
import org.activityinfo.i18n.shared.I18N;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

/**
 * Handles click events on header cells
 */
class GridHeaderCell extends AbstractCell<SourceColumn> {

    private ImportModel model;

    public GridHeaderCell(ImportModel model) {
        super(CLICK);
        this.model = model;
    }

    @Override
    public void render(Context context, SourceColumn column, SafeHtmlBuilder sb) {
        if(context.getIndex() == ColumnMappingGrid.SOURCE_COLUMN_HEADER_ROW) {
            sb.appendEscaped(column.getHeader());
        } else {
            ColumnAction action = model.getColumnAction(column);
            if(action == null) {
                sb.appendHtmlConstant(I18N.CONSTANTS.chooseFieldHeading());
            } else if(action == IgnoreAction.INSTANCE) {
                sb.appendEscaped(I18N.CONSTANTS.ignoreColumnAction());
            } else if(action instanceof MapExistingAction) {
                sb.appendEscaped(((MapExistingAction) action).getTarget().getLabel());
            }
        }
    }
}
