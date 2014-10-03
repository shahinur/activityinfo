package org.activityinfo.ui.app.client.page.folder;

import com.google.common.collect.Lists;
import com.google.gwt.safehtml.shared.SafeUri;
import org.activityinfo.model.analysis.PivotTableModelClass;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.PageFrame;
import org.activityinfo.ui.app.client.chrome.PageFrameConfig;
import org.activityinfo.ui.app.client.dialogs.ConfirmDialog;
import org.activityinfo.ui.app.client.dialogs.DeleteResourceAction;
import org.activityinfo.ui.app.client.dialogs.RenameResourceDialog;
import org.activityinfo.ui.app.client.page.PagePreLoader;
import org.activityinfo.ui.app.client.page.PageView;
import org.activityinfo.ui.app.client.page.PageViewFactory;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.ResourcePlace;
import org.activityinfo.ui.app.client.page.folder.task.TasksPanel;
import org.activityinfo.ui.app.client.page.form.FormPlace;
import org.activityinfo.ui.app.client.page.form.FormViewType;
import org.activityinfo.ui.app.client.store.Router;
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.*;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Children;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.Style;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;
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

    public FolderPage(Application application, ResourceId folderId) {
        this.application = application;
        this.folderId = folderId;
        this.tasksPanel = new TasksPanel(application, folderId);
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

            ResourceNode rootNode = folder.get().getRootNode();

            LOGGER.info("Folder id = " + rootNode.getId() + ", label = " + rootNode.getLabel());

            return new PageFrame(PAGE_ICON,
                    rootNode.getLabel(), pageFrameConfig(rootNode, true),
                    renderContents(rootNode));
        }
    }

    private PageFrameConfig pageFrameConfig(ResourceNode node, boolean changePlaceAfterDeletion) {
        final PageFrameConfig config = new PageFrameConfig()
                .setEditAllowed(node.isEditAllowed());

        if (node.isEditAllowed()) {
            config.setEnableRename(new RenameResourceDialog(application, node.getId()));
            config.setEnableDeletion(new DeleteResourceAction(application, node.getId(), node.getLabel(), changePlaceAfterDeletion));
        }
        return config;
    }

    private VTree renderContents(ResourceNode folder) {
        return
                div(BaseStyles.ROW,
                        listColumn(folder),
                        timelineColumn(),
                        helpColumn(folder));
    }

    private VTree listColumn(ResourceNode page) {
        return Grid.column(5, childTable(page));
    }

    private VTree childTable(ResourceNode page) {
        return new Panel(
                div(className(BaseStyles.WIDGET_BLOGLIST), map(page.getChildren(),
                        new H.Render<ResourceNode>() {

                            @Override
                            public VTree render(ResourceNode item) {
                                return media(item);
                            }
                        })));
    }

    private VTree media(ResourceNode child) {

        return Media.media(childIcon(child),
                link(child),
                div("", t(child.getLabel()), buttons(child)), description(child));
    }

    private VTree buttons(final ResourceNode node) {
        final PageFrameConfig config = pageFrameConfig(node, false);
        if (!config.isEditAllowed()) {
            return H.space();
        }

        final List<VTree> content = Lists.newArrayList();

        if (config.getEnableRename() != null) {
            config.getEnableRename().setLabel(node.getLabel());
            content.add(config.getEnableRename());

            Button renameButton = new Button(ButtonStyle.LINK, FontAwesome.EDIT.render());
            renameButton.setClickHandler(new ClickHandler() {
                @Override
                public void onClicked() {
                    config.getEnableRename().setVisible(true);
                }
            });
            content.add(renameButton);
        }

        if (config.enableDeletion() != null) {
            final ConfirmDialog confirmDialog = ConfirmDialog.confirm(config.enableDeletion());
            content.add(confirmDialog);

            Button deleteButton = new Button(ButtonStyle.LINK, FontAwesome.TRASH_O.render());
            deleteButton.setClickHandler(new ClickHandler() {
                @Override
                public void onClicked() {
                    confirmDialog.setVisible(true);
                }
            });
            content.add(deleteButton);
        }

        return div(BaseStyles.PULL_RIGHT, Children.toArray(content));
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

        } else if (PivotTableModelClass.CLASS_ID.equals(child.getClassId())) {
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
