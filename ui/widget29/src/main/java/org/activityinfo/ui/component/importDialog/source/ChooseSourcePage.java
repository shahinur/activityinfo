package org.activityinfo.ui.component.importDialog.source;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.io.importing.model.ImportModel;
import org.activityinfo.model.type.converter.JsConverterFactory;
import org.activityinfo.ui.component.importDialog.ImportPage;
import org.activityinfo.ui.component.importDialog.PageChangedEvent;
import org.activityinfo.io.importing.data.PastedTable;

/**
 * Start page for the table import process that prompts the user
 * for pasted table data
 */
public class ChooseSourcePage extends ResizeComposite implements ImportPage {


    private static ChoosePageUiBinder uiBinder = GWT
            .create(ChoosePageUiBinder.class);

    interface ChoosePageUiBinder extends UiBinder<Widget, ChooseSourcePage> {
    }

    private ImportModel model;
    private final EventBus eventBus;

    @UiField TextArea textArea;

    public ChooseSourcePage(ImportModel model, EventBus eventBus) {
        this.model = model;
        this.eventBus = eventBus;
        initWidget(uiBinder.createAndBindUi(this));
        textArea.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                fireStateChanged();
            }
        });
    }

    public void fireStateChanged() {
        if (isValid()) {
            final PastedTable source = new PastedTable(textArea.getValue());
            source.guessColumnsType(JsConverterFactory.get());
            model.setSource(source);
            eventBus.fireEvent(new PageChangedEvent(true, ""));
        } else {
            eventBus.fireEvent(new PageChangedEvent(false, I18N.CONSTANTS.pleaseProvideCommaSeparatedText()));
        }
    }

    @UiHandler("textArea")
    public void onTextChanged(ChangeEvent event) {
        fireStateChanged();
    }

    @Override
    public boolean isValid() {
        if (!Strings.isNullOrEmpty(textArea.getValue())) {
            try {
                final PastedTable pastedTable = new PastedTable(textArea.getValue());
                return !pastedTable.getRows().isEmpty();
            } catch (Exception e) {
                // ignore : text is not valid
            }
        }
        return false;
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
    public void start() {

    }

    @Override
    public void nextStep() {
    }

    @Override
    public void previousStep() {
    }
}
