package org.activityinfo.ui.client.page.report;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.common.base.Strings;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.AsyncMonitor;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.client.monitor.MaskingAsyncMonitor;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.legacy.shared.command.CreateReport;
import org.activityinfo.legacy.shared.command.GetReportModel;
import org.activityinfo.legacy.shared.command.RenderElement.Format;
import org.activityinfo.legacy.shared.command.UpdateReportModel;
import org.activityinfo.legacy.shared.command.UpdateReportSubscription;
import org.activityinfo.legacy.shared.command.result.CreateResult;
import org.activityinfo.legacy.shared.command.result.VoidResult;
import org.activityinfo.legacy.shared.model.ReportDTO;
import org.activityinfo.legacy.shared.model.ReportMetadataDTO;
import org.activityinfo.legacy.shared.reports.model.Report;
import org.activityinfo.ui.client.EventBus;
import org.activityinfo.ui.client.page.*;
import org.activityinfo.ui.client.page.common.dialog.SaveChangesCallback;
import org.activityinfo.ui.client.page.common.dialog.SavePromptMessageBox;
import org.activityinfo.ui.client.page.common.toolbar.ExportCallback;
import org.activityinfo.ui.client.page.report.editor.CompositeEditor2;
import org.activityinfo.ui.client.page.report.editor.EditorProvider;
import org.activityinfo.ui.client.page.report.editor.ReportElementEditor;
import org.activityinfo.ui.client.page.report.resources.ReportResources;

import java.util.Date;

public class ReportDesignPage extends ContentPanel implements Page, ExportCallback {

    private class SaveCallback implements AsyncCallback<VoidResult> {
        @Override
        public void onSuccess(final VoidResult result) {
            Info.display(I18N.CONSTANTS.saved(), I18N.MESSAGES.reportSaved(currentModel.getTitle()));
            onSaved();
        }

        @Override
        public final void onFailure(final Throwable caught) {
            MessageBox.alert(I18N.CONSTANTS.serverError(), caught.getMessage(), null);
        }

        public void onSaved() {
        }
    }

    public static final PageId PAGE_ID = new PageId("report");

    private final EventBus eventBus;
    private final Dispatcher dispatcher;
    private final EditorProvider editorProvider;

    private boolean reportEdited;
    private ReportBar reportBar;

    private boolean dirty = false;

    /**
     * The model being edited on this page
     */
    private Report currentModel;
    private ReportMetadataDTO currentMetadata;

    /**
     * The editor for the model
     */
    private ReportElementEditor currentEditor;

    @Inject
    public ReportDesignPage(final EventBus eventBus, final Dispatcher service, final EditorProvider editorProvider) {
        this.eventBus = eventBus;
        this.dispatcher = service;
        this.editorProvider = editorProvider;

        ReportResources.INSTANCE.style().ensureInjected();

        setLayout(new BorderLayout());
        setHeaderVisible(false);

        createToolbar();

        eventBus.addListener(ReportChangeEvent.TYPE, new Listener<ReportChangeEvent>() {

            @Override
            public void handleEvent(final ReportChangeEvent event) {
                if (event.getModel() == currentModel || currentModel.getElements().contains(event.getModel())) {
                    Log.debug("marking report as dirty");
                    dirty = true;
                }
            }
        });
    }

    public void createToolbar() {
        reportBar = new ReportBar();
        BorderLayoutData reportBarLayout = new BorderLayoutData(LayoutRegion.NORTH);
        reportBarLayout.setSize(35);
        add(reportBar, reportBarLayout);

        reportBar.getExportButton().setCallback(this);

        reportBar.addTitleEditCompleteListener(new Listener<EditorEvent>() {
            @Override
            public void handleEvent(final EditorEvent be) {
                String newTitle = (String) be.getValue();
                if (newTitle != null && !newTitle.equals(currentModel.getTitle())) {
                    currentModel.setTitle(newTitle);
                    reportBar.setReportTitle(newTitle);
                    save(new SaveCallback());
                }
            }
        });

        reportBar.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(final ButtonEvent ce) {
                saveTitled(null, new SaveCallback());
            }
        });

        reportBar.getShareButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(final ButtonEvent ce) {
                showShareForm();
            }

        });

        reportBar.getDashboardButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(final ButtonEvent ce) {
                pinToDashboard(reportBar.getDashboardButton().isPressed());
            }
        });

        reportBar.getSwitchViewButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(final ButtonEvent ce) {
                switchView();
            }
        });
    }

    @Override
    public boolean navigate(final PageState place) {
        if (place instanceof ReportDesignPageState) {
            go(((ReportDesignPageState) place).getReportId());
            return true;
        }
        return false;
    }

    public void go(final int reportId) {
        loadReport(reportId);
    }

    private void loadReport(final int reportId) {
        dispatcher.execute(new GetReportModel(reportId, true),
                new MaskingAsyncMonitor(this, I18N.CONSTANTS.loading()),
                new AsyncCallback<ReportDTO>() {
                    @Override
                    public void onFailure(final Throwable caught) {
                    }

                    @Override
                    public void onSuccess(final ReportDTO result) {
                        onModelLoaded(result);
                    }
                });
    }

    private void onModelLoaded(final ReportDTO result) {
        this.currentModel = result.getReport();
        this.currentMetadata = result.getReportMetadataDTO();

        reportBar.setReportTitle(currentModel.getTitle());
        reportBar.getDashboardButton().toggle(currentMetadata.isDashboard());

        if (currentModel.getElements().size() == 1) {
            ReportElementEditor editor = editorProvider.create(currentModel.getElement(0));
            editor.bind(currentModel.getElement(0));
            installEditor(editor);
            reportBar.getSwitchViewButton().setVisible(true);
        } else {
            installCompositeEditor();
        }
    }

    private void installCompositeEditor() {
        CompositeEditor2 editor = (CompositeEditor2) editorProvider.create(currentModel);
        editor.bind(currentModel);
        installEditor(editor);

        reportBar.getSwitchViewButton().setVisible(false);
    }

    protected void switchView() {
        installCompositeEditor();
    }

    private void installEditor(final ReportElementEditor editor) {
        if (currentEditor != null) {
            remove(currentEditor.getWidget());
        }

        reportBar.getExportButton().setFormats(editor.getExportFormats());

        add(editor.getWidget(), new BorderLayoutData(LayoutRegion.CENTER));
        this.currentEditor = editor;
        layout();
    }

    private void pinToDashboard(final boolean pressed) {
        saveTitled(new SaveCallback() {
            @Override
            public void onSaved() {
                final UpdateReportSubscription update = new UpdateReportSubscription();
                update.setReportId(currentModel.getId());
                update.setPinnedToDashboard(pressed);

                dispatcher.execute(update, new SaveCallback() {
                    @Override
                    public void onSuccess(final VoidResult result) {
                        if (update.getPinnedToDashboard()) {
                            Info.display(I18N.CONSTANTS.saved(),
                                    I18N.MESSAGES.addedToDashboard(currentModel.getTitle()));
                        } else {
                            Info.display(I18N.CONSTANTS.saved(),
                                    I18N.MESSAGES.removedFromDashboard(currentModel.getTitle()));
                        }
                    }

                });
            }
        });
    }

    private void saveTitled(final AsyncMonitor monitor, final SaveCallback callback) {
        if (untitled()) {
            promptForTitle(callback);
        } else {
            save(monitor, callback);
        }
    }

    private void saveTitled(final SaveCallback callback) {
        if (untitled()) {
            promptForTitle(callback);
        } else {
            callback.onSaved();
        }
    }

    private void promptForTitle(final AsyncCallback<VoidResult> callback) {
        MessageBox.prompt(I18N.CONSTANTS.save(), I18N.CONSTANTS.chooseReportTitle(), new Listener<MessageBoxEvent>() {

            @Override
            public void handleEvent(final MessageBoxEvent be) {
                String newTitle = be.getMessageBox().getTextBox().getValue();
                if (!Strings.isNullOrEmpty(newTitle)) {
                    currentModel.setTitle(newTitle);
                    reportBar.setReportTitle(newTitle);
                    save(callback);
                }
            }
        });
    }

    private void save(final AsyncCallback<VoidResult> callback) {
        save(null, callback);
    }

    private void save(final AsyncMonitor monitor, final AsyncCallback<VoidResult> callback) {
        if (currentMetadata.isEditAllowed()) {
            performUpdate(monitor, callback);
        } else {
            confirmCreate(monitor, callback);
        }
    }

    private void performUpdate(final AsyncMonitor monitor, final AsyncCallback<VoidResult> callback) {
        UpdateReportModel updateReport = new UpdateReportModel();
        updateReport.setModel(currentModel);

        dispatcher.execute(updateReport, monitor, new AsyncCallback<VoidResult>() {
            @Override
            public void onFailure(final Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(final VoidResult result) {
                dirty = false;
                callback.onSuccess(result);
            }
        });
    }

    private void confirmCreate(final AsyncMonitor monitor, final AsyncCallback<VoidResult> callback) {
        MessageBox.confirm(I18N.CONSTANTS.save(), I18N.MESSAGES.confirmSaveCopy(), new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(final MessageBoxEvent be) {
                Button btn = be.getButtonClicked();
                if (Dialog.YES.equalsIgnoreCase(btn.getItemId())) {
                    performCreate();
                }
            }
        });
    }

    private void performCreate() {
        currentModel.setTitle(currentModel.getTitle() + " (" + I18N.CONSTANTS.copy() + ")");

        dispatcher.execute(new CreateReport(currentModel), new AsyncCallback<CreateResult>() {
            @Override
            public void onFailure(final Throwable caught) {
            }

            @Override
            public void onSuccess(final CreateResult created) {
                eventBus.fireEvent(new NavigationEvent(NavigationHandler.NAVIGATION_REQUESTED,
                        new ReportDesignPageState(created.getNewId())));
            }
        });
    }

    public void setReportEdited(final boolean edited) {
        reportEdited = edited;
    }

    public boolean reportEdited() {
        return reportEdited;
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }

    @Override
    public PageId getPageId() {
        return PAGE_ID;
    }

    @Override
    public Object getWidget() {
        return this;
    }

    @Override
    public void requestToNavigateAway(final PageState place, final NavigationCallback callback) {
        if (!dirty) {
            callback.onDecided(true);
        } else {
            SavePromptMessageBox box = new SavePromptMessageBox();
            box.show(new SaveChangesCallback() {

                @Override
                public void save(final AsyncMonitor monitor) {
                    saveTitled(monitor, new SaveCallback() {

                        @Override
                        public void onSaved() {
                            callback.onDecided(true);
                        }
                    });
                }

                @Override
                public void discard() {
                    callback.onDecided(true);
                }

                @Override
                public void cancel() {
                    callback.onDecided(false);
                }
            });

        }
    }

    @Override
    public String beforeWindowCloses() {
        // TODO Auto-generated method stub
        return null;
    }

    public void showShareForm() {
        saveTitled(new SaveCallback() {

            @Override
            public void onSaved() {
                final ShareReportDialog dialog = new ShareReportDialog(dispatcher);
                // form.updateForm(currentReportId);
                dialog.show(currentModel);
            }
        });
    }

    @Override
    public void export(final Format format) {
        StringBuilder fileName = new StringBuilder();
        fileName.append(untitled() ? I18N.CONSTANTS.untitledReport() : currentModel.getTitle());
        fileName.append(" ");
        fileName.append(DateTimeFormat.getFormat("yyyyMMdd_HHmm").format(new Date()));

        ExportDialog dialog = new ExportDialog(dispatcher);
        dialog.export(fileName.toString(), currentEditor.getModel(), format);
    }

    private boolean untitled() {
        return currentModel.getTitle() == null;
    }
}
