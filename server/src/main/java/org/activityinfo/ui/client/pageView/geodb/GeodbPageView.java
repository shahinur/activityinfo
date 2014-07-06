package org.activityinfo.ui.client.pageView.geodb;

import com.google.common.base.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.application.ApplicationProperties;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.page.InstanceUri;
import org.activityinfo.ui.client.pageView.InstancePageView;

import javax.annotation.Nullable;
import java.util.List;

public class GeodbPageView implements InstancePageView {


    private final HTMLPanel panel;

    interface GeodbPageViewUiBinder extends UiBinder<HTMLPanel, GeodbPageView> {
    }

    private static GeodbPageViewUiBinder ourUiBinder = GWT.create(GeodbPageViewUiBinder.class);

    public interface Templates extends SafeHtmlTemplates {

        @Template("<li><a href='{0}'>{1}</a></li>")
        SafeHtml country(SafeUri id, String name);

    }

    public static final Templates TEMPLATES = GWT.create(Templates.class);

    private final ResourceLocator locator;
    @UiField
    UListElement countryList;

    public GeodbPageView(ResourceLocator locator) {
        this.locator = locator;
        panel = ourUiBinder.createAndBindUi(this);
    }

    @Override
    public Promise<Void> show(FormInstance instance) {
        return locator.queryInstances(new ClassCriteria(ApplicationProperties.CLASS_PROPERTY))
                .then(new Function<List<FormInstance>, Void>() {

                    @Nullable
                    @Override
                    public Void apply(@Nullable List<FormInstance> input) {
                        renderCountryList(input);
                        return null;
                    }
                });
    }


    private void renderCountryList(List<FormInstance> countries) {
        SafeHtmlBuilder list = new SafeHtmlBuilder();
        for(FormInstance country : countries) {
            list.append(
                    TEMPLATES.country(InstanceUri.of(country),
                    country.getString(ApplicationProperties.COUNTRY_NAME_FIELD)));

        }
        countryList.setInnerSafeHtml(list.toSafeHtml());
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}