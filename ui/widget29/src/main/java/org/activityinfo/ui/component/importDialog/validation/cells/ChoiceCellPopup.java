package org.activityinfo.ui.component.importDialog.validation.cells;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.formTree.FieldPath;

/**
 * A {@code CellPopup} that provides a list of actions to the user
 * based on the column's available choices.
 *
 * @param <T>
 */
public class ChoiceCellPopup<T> implements CellPopup {

    private MenuBar menuBar = new MenuBar();
    private final FieldPath property;
    private UpdateCommandFactory<T> commandFactory;

    public ChoiceCellPopup(FieldPath property, UpdateCommandFactory<T> commandFactory) {
        super();
        this.property = property;
        this.commandFactory = commandFactory;
    }

    @Override
    public Widget asWidget() {
        return menuBar;
    }

    @Override
    public void prepare(String cellValue) {
//		menuBar.clearItems();
//		
//		for(String suggestion : property.getSuggestionOracle().apply(cellValue)) {
//			menuBar.addItem(I18N.MESSAGES.updateAllRowsTo(suggestion), 
//					commandFactory.setColumnValue(property, suggestion));
//		}
    }
}
