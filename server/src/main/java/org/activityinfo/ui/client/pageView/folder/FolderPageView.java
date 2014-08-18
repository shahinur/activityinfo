package org.activityinfo.ui.client.pageView.folder;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.ResourceTree;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.chrome.PageHeader;
import org.activityinfo.ui.client.page.instance.InstancePlace;
import org.activityinfo.ui.client.pageView.InstancePageView;
import org.activityinfo.ui.client.pageView.InstanceViewModel;

import javax.annotation.Nullable;
import java.util.List;

/**
 * View for Folder instances
 */
public class FolderPageView implements InstancePageView {


    public static final String NON_BREAKING_SPACE = "\u00A0";
    private final ResourceLocator resourceLocator;
    private FormInstance instance;

    interface FolderViewUiBinder extends UiBinder<HTMLPanel, FolderPageView> {
    }

    public interface Templates extends SafeHtmlTemplates {

        @Template("<li><a href='{0}'><i class='fa fa-folder-o'></i> {1}</a></li>")
        SafeHtml folder(String link, String name);
    }

    private static FolderViewStylesheet stylesheet = GWT.create(FolderViewStylesheet.class);

    private static FolderViewUiBinder ourUiBinder = GWT.create(FolderViewUiBinder.class);

    private static Templates templates = GWT.create(Templates.class);

    private final HTMLPanel rootElement;

    @UiField PageHeader pageHeader;

    @UiField UListElement folderList;
    @UiField DivElement formListBody;

    public FolderPageView(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        rootElement = ourUiBinder.createAndBindUi(this);
        stylesheet.ensureInjected();
    }

    public Promise<Void> show(InstanceViewModel view) {
        this.instance = view.getInstance();
        pageHeader.setPageTitle(instance.getString(FolderClass.LABEL_FIELD_ID));
        pageHeader.setIconStyle("glyphicon glyphicon-folder-open");

        return resourceLocator.getTree(view.getInstance().getId()).then(new Function<ResourceTree, Void>() {
            @Nullable
            @Override
            public Void apply(@Nullable ResourceTree tree) {
                renderFolders(tree.getRootNode().getChildren());
                renderForms(tree.getRootNode().getChildren());
                return null;
            }
        });
    }

    private void renderFolders(List<ResourceNode> projections) {
        SafeHtmlBuilder html = new SafeHtmlBuilder();
        for(ResourceNode projection : projections) {
            if(projection.getClassId().equals(FolderClass.CLASS_ID)) {
                html.append(templates.folder(
                        InstancePlace.safeUri(projection.getClassId()).asString(),
                        projection.getLabel()));
            }
        }
        folderList.setInnerSafeHtml(html.toSafeHtml());
    }

    private void renderForms(List<ResourceNode> projections) {

        FormItemRenderer renderer = GWT.create(FormItemRenderer.class);

        SafeHtmlBuilder html = new SafeHtmlBuilder();
        for(ResourceNode projection : projections) {
            if(projection.getClassId().equals(FormClass.CLASS_ID)) {
                renderer.render(html, projection.getLabel(),
                        Objects.firstNonNull( null /*projection.getStringValue(DESCRIPTION_PROPERTY) */, NON_BREAKING_SPACE),
                        InstancePlace.safeUri(projection.getId()).asString());
            }
        }
        formListBody.setInnerSafeHtml(html.toSafeHtml());
    }


    @Override
    public Widget asWidget() {
        return rootElement;
    }

}