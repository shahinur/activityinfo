package org.activityinfo.ui.app.client.page.folder;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.PageFrame;
import org.activityinfo.ui.app.client.chrome.PageFrameConfig;
import org.activityinfo.ui.app.client.dialogs.DeleteResourceAction;
import org.activityinfo.ui.app.client.dialogs.EditLabelDialog;
import org.activityinfo.ui.app.client.page.PagePreLoader;
import org.activityinfo.ui.app.client.page.*;
import org.activityinfo.ui.app.client.page.folder.task.TasksPanel;
import org.activityinfo.ui.app.client.page.form.FormPlace;
import org.activityinfo.ui.app.client.page.form.FormViewType;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.app.client.store.Router;
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.*;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.Style;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.logging.Logger;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class FolderPage extends PageView implements StoreChangeListener {


    public static class Factory implements PageViewFactory<FolderPlace> {

        private final Application application;

        public Factory(Application application) {
            this.application = application;
        }

        @Override
        public boolean accepts(Place place) {
            return place instanceof FolderPlace;
        }

        @Override
        public PageView create(FolderPlace place) {
            return new FolderPage(application, place.getResourceId());
        }
    }

    private static final Logger LOGGER = Logger.getLogger(FolderPage.class.getName());

    public static final Icon PAGE_ICON = FontAwesome.FOLDER_OPEN_O;

    private final Application application;
    private final ResourceId folderId;

    private final TasksPanel tasksPanel;
    private final EditLabelDialog editLabelDialog = new EditLabelDialog();

    public FolderPage(Application application, ResourceId folderId) {
        this.application = application;
        this.folderId = folderId;
        this.tasksPanel = new TasksPanel(application, folderId);
        this.editLabelDialog.setOkClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                String newName = editLabelDialog.getInputControl().getValueAsString();
                onRename(newName);
            }
        });
    }

    private void onRename(String newName) {
        ResourceId id = getFolder().get().getRootNode().getId();
        Resource resource = application.getResourceStore().get(id).get();

        RecordBuilder updated = Records.buildCopyOf(resource.getValue());
        updated.set(FolderClass.LABEL_FIELD_ID.asString(), newName);
        resource.setValue(updated.build());

        application.getRequestDispatcher().execute(new SaveRequest(resource)).then(new AsyncCallback<UpdateResult>() {
            @Override
            public void onFailure(Throwable caught) {
                editLabelDialog.failedToEditLabel();
            }

            @Override
            public void onSuccess(UpdateResult result) {
                editLabelDialog.setVisible(false);
            }
        });
    }

    @Override
    public void componentDidMount() {
        application.getFolderStore().addChangeListener(this);
        application.getRouter().addChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        refresh();
    }

    @Override
    protected void componentWillUnmount() {
        application.getFolderStore().removeChangeListener(this);
        application.getRouter().removeChangeListener(this);
    }


    private Status<FolderProjection> getFolder() {
        return application.getFolderStore().get(folderId);
    }

    @Override
    protected VTree render() {

        Status<FolderProjection> folder = getFolder();

        if (!folder.isAvailable()) {
            return new PagePreLoader();

        } else {

            ResourceId resourceId = folder.get().getRootNode().getId();
            String label = folder.get().getRootNode().getLabel();
            LOGGER.info("Folder id = " + resourceId + ", label = " + label);

            final PageFrameConfig config = new PageFrameConfig().
                    setEnableRename(editLabelDialog).
                    setEnableDeletion(new DeleteResourceAction(application, resourceId, label)).
                    setEditAllowed(folder.get().getRootNode().isEditAllowed());
            return new PageFrame(PAGE_ICON,
                folder.get().getRootNode().getLabel(), config,
                renderContents(folder.get().getRootNode()));
        }
    }

    private VTree renderContents(ResourceNode folder) {
        return
            div(BaseStyles.ROW,
                    listColumn(folder),
                    timelineColumn(),
                    helpColumn(folder));
    }

    private static VTree listColumn(ResourceNode page) {
        return Grid.column(5, childTable(page));
    }

    private static VTree childTable(ResourceNode page) {
        return new Panel(
            div(className(BaseStyles.WIDGET_BLOGLIST), map(page.getChildren(),
                new H.Render<ResourceNode>() {

                @Override
                public VTree render(ResourceNode item) {
                    return media(item);
                }
            })));
    }

    private static VTree media(ResourceNode child) {
        return Media.media(childIcon(child),
            link(child),
            t(child.getLabel()), description(child));
    }

    private static SafeUri link(ResourceNode node) {
        if (node.getClassId().equals(FormClass.CLASS_ID)) {
            return Router.uri(new FormPlace(node.getId(), FormViewType.TABLE));
        } else if (node.getClassId().equals(FolderClass.CLASS_ID)) {
            return Router.uri(new FolderPlace(node.getId()));
        } else {
            return Router.uri(new ResourcePlace(node.getId()));
        }
    }

    private static VTree description(ResourceNode child) {
        return FormClass.CLASS_ID.equals(child.getClassId()) ?
                t("Data Entry Form") :
                t("Folder");
    }

    private static VTree childIcon(ResourceNode child) {
        Icon icon;
        if (FormClass.CLASS_ID.equals(child.getClassId())) {
            icon = FontAwesome.FILE;

        } else if(PivotTableModel.CLASS_ID.equals(child.getClassId())) {
            icon = FontAwesome.TABLE;

        } else {
            icon = FontAwesome.FOLDER_OPEN_O;
        }

        Style iconStyle = new Style().fontSize(50).lineHeight(50);
        PropMap iconProps = new PropMap();
        iconProps.setStyle(iconStyle);
        iconProps.setClass(icon.getCssClass());

        return div(className(BaseStyles.MEDIA_OBJECT), span(iconProps));
    }

    private static VTree timelineColumn() {
        return Grid.column(3,
            new Panel("Recent Activity", p("Todo...")));
    }

    private VTree helpColumn(ResourceNode folder) {
        return Grid.column(3,
            tasksPanel,
            new Panel("Administration", p("Todo...")));
    }
}
