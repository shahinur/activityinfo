package org.activityinfo.ui.component.importDialog.validation;

import com.google.common.base.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.io.importing.model.ImportModel;
import org.activityinfo.io.importing.validation.ValidatedRowTable;
import org.activityinfo.promise.Promise;
import org.activityinfo.promise.PromiseMonitor;
import org.activityinfo.ui.component.importDialog.ImportPage;
import org.activityinfo.io.importing.Importer;

/**
 * Presents the result of the matching to the user and provides
 * and opportunity to resolve conversion problems or ambiguities
 * in reference instances.
 */
public class ValidationPage extends Composite implements PromiseMonitor, ImportPage {

    private static ValidationPageUiBinder uiBinder = GWT
            .create(ValidationPageUiBinder.class);


    interface ValidationPageUiBinder extends UiBinder<Widget, ValidationPage> {
    }

    private ImportModel model;
    private Importer importer;

    @UiField(provided = true)
    ValidationGrid dataGrid;

    @UiField
    Element loadingElement;
    @UiField
    Element loadingErrorElement;

    public ValidationPage(ImportModel model, Importer importer) {
        this.model = model;
        this.importer = importer;

        dataGrid = new ValidationGrid();

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void start() {
        importer.validateRows(model)
                .withMonitor(this)
                .then(new Function<ValidatedRowTable, Void>() {
                    @Override
                    public Void apply(ValidatedRowTable input) {
                        dataGrid.refresh(input);
                        return null;
                    }
                });
    }

    @Override
    public void fireStateChanged() {
    }

    public int getInvalidRowsCount() {
        return dataGrid.getInvalidRowsCount();
    }

    @Override
    public void onPromiseStateChanged(Promise.State state) {
        this.loadingElement.getStyle().setDisplay(  state == Promise.State.PENDING ?
                Style.Display.BLOCK : Style.Display.NONE );
        this.loadingErrorElement.getStyle().setDisplay(  state == Promise.State.REJECTED ?
                Style.Display.BLOCK : Style.Display.NONE );
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean hasNextStep() {
        return false;
    }

    @Override
    public boolean hasPreviousStep() {
        return false;
    }

    @Override
    public void nextStep() {

    }

    @Override
    public void previousStep() {

    }

}
