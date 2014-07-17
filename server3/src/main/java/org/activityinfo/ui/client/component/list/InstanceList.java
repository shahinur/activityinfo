package org.activityinfo.ui.client.component.list;

import com.bedatadriven.rebar.style.client.Source;
import com.bedatadriven.rebar.style.client.Stylesheet;
import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.page.instance.InstancePlace;
import org.activityinfo.ui.client.pageView.IconStyleProvider;
import org.activityinfo.ui.client.widget.DisplayWidget;

import java.util.List;

import static org.activityinfo.core.shared.application.ApplicationProperties.DESCRIPTION_PROPERTY;
import static org.activityinfo.core.shared.application.ApplicationProperties.LABEL_PROPERTY;

/**
 * Displays a list of instances
 */
public class InstanceList extends HTML implements DisplayWidget<List<Projection>> {

    private final ListItemRenderer renderer;

    @Source("InstanceList.less")
    public interface InstanceListStylesheet extends Stylesheet {

    }

    public static final InstanceListStylesheet STYLESHEET = GWT.create(InstanceListStylesheet.class);

    public InstanceList() {
        STYLESHEET.ensureInjected();
        this.renderer = GWT.create(ListItemRenderer.class);
    }

    @Override
    public Promise<Void> show(List<Projection> projections) {
        SafeHtmlBuilder html = new SafeHtmlBuilder();
        for(Projection projection : projections) {
            String label = projection.getStringValue(LABEL_PROPERTY);
            String description = Strings.nullToEmpty(projection.getStringValue(DESCRIPTION_PROPERTY));
            renderer.render(html,
                    IconStyleProvider.getIconStyleForFormClass(projection.getRootClassId()),
                    label, description,
                    InstancePlace.safeUri(projection.getRootInstanceId()).asString());
        }
        setHTML(html.toSafeHtml());
        return Promise.done();
    }
}