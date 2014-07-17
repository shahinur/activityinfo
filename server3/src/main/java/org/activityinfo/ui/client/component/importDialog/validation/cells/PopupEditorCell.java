package org.activityinfo.ui.client.component.importDialog.validation.cells;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

public class PopupEditorCell extends AbstractSafeHtmlCell<String> {

    private static final int ESCAPE = 27;

    private final CellPopup popupWidget;
    private int offsetX = 10;
    private int offsetY = 10;
    private Element lastParent;
    private PopupPanel panel;


    /**
     * Constructs a new DatePickerCell that uses the given date/time format and
     * {@link SafeHtmlRenderer}.
     *
     * @param format   a {@link DateTimeFormat} instance
     * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
     */
    public PopupEditorCell(CellPopup popupWidget) {
        super(SimpleSafeHtmlRenderer.getInstance(), CLICK, KEYDOWN);
        this.popupWidget = popupWidget;
        this.panel = new PopupPanel(true, true) {
            @Override
            protected void onPreviewNativeEvent(NativePreviewEvent event) {
                if (Event.ONKEYUP == event.getTypeInt()) {
                    if (event.getNativeEvent().getKeyCode() == ESCAPE) {
                        // Dismiss when escape is pressed
                        panel.hide();
                    }
                }
            }
        };
        panel.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> event) {
                if (lastParent != null && !event.isAutoClosed()) {
                    // Refocus on the containing cell after the user selects a value, but
                    // not if the popup is auto closed.
                    lastParent.focus();
                }
                lastParent = null;
            }
        });
        panel.add(popupWidget);

        // Hide the panel and call valueUpdater.update when a date is selected
//		datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
//			public void onValueChange(ValueChangeEvent<Date> event) {
//				// Remember the values before hiding the popup.
//				Element cellParent = lastParent;
//				Date oldValue = lastValue;
//				Object key = lastKey;
//				int index = lastIndex;
//				int column = lastColumn;
//				panel.hide();
//
//				// Update the cell and value updater.
//				Date date = event.getValue();
//				setViewData(key, date);
//				setValue(new Context(index, column, key), cellParent, oldValue);
//				if (valueUpdater != null) {
//					valueUpdater.update(date);
//				}
//			}
//		});
    }


    @Override
    public void onBrowserEvent(Context context, Element parent, String value,
                               NativeEvent event, ValueUpdater<String> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if (CLICK.equals(event.getType())) {
            showPopup(parent, value);
        }
    }

    @Override
    protected void onEnterKeyDown(Context context, Element parent, String value,
                                  NativeEvent event, ValueUpdater<String> valueUpdater) {

        showPopup(parent, value);
    }

    private void showPopup(Element parent, String value) {
        this.lastParent = parent;

        popupWidget.prepare(value);

        panel.setPopupPositionAndShow(new PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                panel.setPopupPosition(lastParent.getAbsoluteLeft() + offsetX,
                        lastParent.getAbsoluteTop() + offsetY);
            }
        });
    }

    @Override
    protected void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
        if (value != null) {
            sb.append(value);
        }
    }
}

