package org.activityinfo.ui.app.client.page.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.PageFrame;
import org.activityinfo.ui.app.client.form.control.HorizontalFormView;
import org.activityinfo.ui.app.client.page.PageView;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.folder.FolderPlace;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.app.client.store.InstanceState;
import org.activityinfo.ui.style.*;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class NewWorkspacePage extends PageView {

    private final Application application;
    private final SavePanel savePanel;
    private InstanceState workspaceDraft;

    public NewWorkspacePage(Application application) {
        this.application = application;
        this.workspaceDraft = application.getDraftStore().getWorkspaceDraft();
        this.savePanel = new SavePanel();
        this.savePanel.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                createWorkspace();
            }
        });
    }

    @Override
    public boolean accepts(Place place) {
        return place == NewWorkspacePlace.INSTANCE;
    }

    @Override
    protected VTree render() {
        return new PageFrame(FontAwesome.TH_LARGE, I18N.CONSTANTS.newWorkspace(), content());
    }

    @Override
    public void componentWillMount() {

    }

    @Override
    public void componentDidMount() {
    }

    @Override
    protected void componentWillUnmount() {
        GWT.log("new workspace will unmount!");
    }

    private VTree content() {
        return div(BaseStyles.CONTENTPANEL,
            Grid.row(
                Grid.column(12,
                    introPanel(),
                    blankWorkspacePanel(),
                    templateWorkspace())));
    }

    private VTree introPanel() {
        return new Alert(AlertStyle.INFO,
            p(t("Great! Creating a workspace is the first step in creating an information system for" +
                "your organization, or for organizing your own projects. More info here and here " +
                "and lots of great explanations.")));
    }


    private VTree blankWorkspacePanel() {

        Panel panel = new Panel();
        panel.setTitle(t(I18N.CONSTANTS.createEmptyWorkspace()));
        panel.setIntroParagraph(t("Create an empty workspace in which you can import existing data, " +
            "create your own data collection forms, and conduct analysis."));
        panel.setContent(new HorizontalFormView(application, workspaceDraft));
        panel.setFooter(savePanel);

        return panel;
    }

    private VTree templateWorkspace() {

        Panel panel = new Panel();
        panel.setTitle(t(I18N.CONSTANTS.createWorkspaceFromTemplate()));
        panel.setIntroParagraph(t("Jump start your project by choosing from an existing template."));
        panel.setContent(p("Todo"));

        return panel;
    }


    private void createWorkspace() {
        // Note that the promise returned by the request dispatcher constitutes local state
        // for this component.

        if(workspaceDraft.isValid()) {

            final Promise<UpdateResult> request = application.getRequestDispatcher().execute(
                new SaveRequest(workspaceDraft.getUpdatedResource()));

            savePanel.updateRequestState(request.getState());

            request.then(new AsyncCallback<UpdateResult>() {
                @Override
                public void onFailure(Throwable caught) {
                    savePanel.updateRequestState(request.getState());
                }

                @Override
                public void onSuccess(UpdateResult result) {
                    new FolderPlace(workspaceDraft.getInstanceId()).navigateTo(application);
                }
            });
        }
    }}
