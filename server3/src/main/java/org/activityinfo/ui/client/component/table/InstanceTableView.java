package org.activityinfo.ui.client.component.table;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.widget.AlertPanel;
import org.activityinfo.ui.client.widget.Templates;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Displays the this classes' FormInstances in a table format
 */
public class InstanceTableView implements IsWidget, RequiresResize {

    private static final int DEFAULT_MAX_COLUMN_COUNT = 5;
    private static final Logger LOGGER = Logger.getLogger(InstanceTableView.class.getName());

    private final ResourceLocator resourceLocator;
    private final HTMLPanel panel;
    private List<FieldColumn> columns;
    private List<FieldColumn> selectedColumns;
    private Collection<FormClass> rootFormClasses;

    @UiField
    DivElement emRuler;
    @UiField
    AlertPanel columnAlert;
    @UiField(provided = true)
    InstanceTable table;
    @UiField
    AlertPanel errorMessages;
    @UiField
    Button loadMoreButton;
    @UiField
    HTML loadFailureMessageContainer;

    interface InstanceTableViewUiBinder extends UiBinder<HTMLPanel, InstanceTableView> {
    }

    private static InstanceTableViewUiBinder ourUiBinder = GWT.create(InstanceTableViewUiBinder.class);

    public InstanceTableView(ResourceLocator resourceLocator) {
        InstanceTableStyle.INSTANCE.ensureInjected();
        this.resourceLocator = resourceLocator;
        this.table = new InstanceTable(this);
        this.panel = ourUiBinder.createAndBindUi(this);

        addLoadMoreButtonHandler();
    }

    private void addLoadMoreButtonHandler() {
        table.getTable().getEventBus().addHandler(InstanceTableDataLoader.DataLoadEvent.TYPE, new InstanceTableDataLoader.DataLoadHandler() {
            @Override
            public void onLoad(final InstanceTableDataLoader.DataLoadEvent event) {
                if (event.isFailed()) {
                    // Show failure message only after a short fixed delay to ensure that
                    // the progress stage is displayed. Otherwise if we have a synchronous error, clicking
                    // the retry button will look like it's not working.
                    Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
                        @Override
                        public boolean execute() {
                            handleLoadMoreButton(event);
                            return false;
                        }
                    }, 500);
                } else {
                    handleLoadMoreButton(event);
                }
            }
        });
    }

    private void handleLoadMoreButton(final InstanceTableDataLoader.DataLoadEvent event) {
        loadFailureMessageContainer.setVisible(event.isFailed());
        loadMoreButton.setText(event.isFailed() ? I18N.CONSTANTS.retryLoading() : I18N.CONSTANTS.loadMore());
        final int totalCount = event.getTotalCount();
        final int loadedDataCount = event.getLoadedDataCount();
        loadMoreButton.setEnabled(loadedDataCount < totalCount);
    }

    public void setCriteria(Criteria criteria) {
        table.setCriteria(criteria);
    }

    public void setColumns(final List<FieldColumn> columns) {
        this.columns = columns;
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                calculateSelectedColumns();
            }
        });
    }

    public void setSelectedColumns(final List<FieldColumn> selectedColumns) {
        this.selectedColumns = selectedColumns;
        table.setColumns(selectedColumns);
        final int allColumns = columns.size();
        final int visibleColumns = selectedColumns.size();
        if (visibleColumns < allColumns) {
            columnAlert.showMessages(I18N.MESSAGES.notAllColumnsAreShown(visibleColumns, allColumns, I18N.CONSTANTS.chooseColumns()));
        } else {
            columnAlert.setVisible(false);
        }
    }

    private void calculateSelectedColumns() {
        if (columns.size() <= getMaxNumberOfColumns()) {
            setSelectedColumns(Lists.newArrayList(columns));
        } else {
            setSelectedColumns(Lists.newArrayList(columns.subList(0, getMaxNumberOfColumns())));
        }
    }

    public int getMaxNumberOfColumns() {
        double emSizeInPixels = ((double) emRuler.getOffsetWidth()) / 100d;

        LOGGER.log(Level.FINE, "emSizeInPixels = " + emSizeInPixels);

        double columnWidthInPixels = InstanceTable.COLUMN_WIDTH * emSizeInPixels;

        int columnLimit = (int) Math.floor(panel.getElement().getClientWidth() / columnWidthInPixels);
        LOGGER.log(Level.FINE, "columnLimit = " + columnLimit);
        if (columnLimit <= 0) { // fallback : yuriyz: check calculations above
            columnLimit = DEFAULT_MAX_COLUMN_COUNT;
        }
        return columnLimit;
    }

    public InstanceTable getTable() {
        return table;
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void onResize() {
    }

    @UiHandler("loadMoreButton")
    public void onLoadMore(ClickEvent event) {
        loadMoreButton.setHTML(Templates.OK_BTN_TEMPLATE.html(I18N.CONSTANTS.loading()));
        loadMoreButton.setEnabled(false);
        loadFailureMessageContainer.setVisible(false);
        table.loadMore();
    }

    public String getFormClassLabel() {
        if (rootFormClasses != null && !rootFormClasses.isEmpty()) {
            final FormClass formClass = rootFormClasses.iterator().next();
            return formClass.getLabel();
        }
        return "";
    }

    public List<FieldColumn> getColumns() {
        if (columns == null) {
            columns = Lists.newArrayList();
        }
        return columns;
    }

    public List<FieldColumn> getSelectedColumns() {
        if (selectedColumns == null) {
            selectedColumns = Lists.newArrayList();
        }
        return selectedColumns;
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    public void setRootFormClasses(Collection<FormClass> rootFormClasses) {
        this.rootFormClasses = rootFormClasses;
        if (rootFormClasses != null && !rootFormClasses.isEmpty()) {
            table.setRootFormClass(rootFormClasses.iterator().next());
        }
    }
}