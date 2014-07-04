package org.activityinfo.ui.client.pageView.folder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.client.Resources;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.application.ApplicationProperties;
import org.activityinfo.core.shared.application.FolderClass;
import org.activityinfo.core.shared.criteria.ParentCriteria;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.ui.client.component.list.InstanceList;
import org.activityinfo.ui.client.page.instance.BreadCrumbBuilder;
import org.activityinfo.ui.client.pageView.IconStyleProvider;
import org.activityinfo.ui.client.pageView.InstancePageView;
import org.activityinfo.ui.client.widget.LoadingPanel;

import java.util.List;

/**
 * View for Folder instances
 */
public class FolderPageView implements InstancePageView {


    private FormInstance instance;
    private Resources resources;

    interface FolderViewUiBinder extends UiBinder<HTMLPanel, FolderPageView> {
    }

    private static FolderViewUiBinder ourUiBinder = GWT.create(FolderViewUiBinder.class);

    private final HTMLPanel rootElement;

    @UiField
    Element folderNameElement;

    @UiField
    Element pageIcon;

    @UiField
    Element folderDescriptionElement;

    @UiField
    Element breadCrumbElement;

//    @UiField
//    Button newFormButton;
//
//    @UiField
//    Button newFolderButton;

    @UiField
    LoadingPanel<List<Projection>> instanceList;

    private BreadCrumbBuilder breadCrumb;

    public FolderPageView(ResourceLocator resourceLocator) {
        this.resources = new Resources(resourceLocator);
        rootElement = ourUiBinder.createAndBindUi(this);
        breadCrumb = new BreadCrumbBuilder(resourceLocator, breadCrumbElement);
    }

    public Promise<Void> show(FormInstance folderInstance) {
        this.instance = folderInstance;
        folderNameElement.setInnerText(instance.getString(FolderClass.LABEL_FIELD_ID));
        folderDescriptionElement.setInnerText(instance.getString(FolderClass.DESCRIPTION_FIELD_ID));
        pageIcon.setClassName(IconStyleProvider.getIconStyleForFormClass(instance.getClassId()));

        breadCrumb.show(folderInstance);

        instanceList.setDisplayWidget(new InstanceList());
        return instanceList.show(resources.query(), childrenQuery());
    }

    private InstanceQuery childrenQuery() {
        return InstanceQuery
            .select(
                    ApplicationProperties.LABEL_PROPERTY,
                    ApplicationProperties.DESCRIPTION_PROPERTY)
            .where(ParentCriteria.isChildOf(instance.getId())).build();
    }
//
//    @UiHandler("newFormButton")
//    public void onNewFormButton(ClickEvent event) {
//        final FormClass formClass = new FormClass(CuidGenerator.INSTANCE.nextCuid());
//        formClass.setLabel(new LocalizedString("New Form"));
//        formClass.setParentId(instance.getId());
//        resources.resourceLocator.persist(formClass).then(new AsyncCallback<Void>() {
//            @Override
//            public void onFailure(Throwable caught) {
//                Window.alert(caught.getMessage());
//            }
//
//            @Override
//            public void onSuccess(Void result) {
//                Window.Location.replace(Window.Location.createUrlBuilder()
//                        .setHash("#i/" + formClass.getId().asString() + "/design")
//                        .buildString());
//            }
//        });
//    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

}