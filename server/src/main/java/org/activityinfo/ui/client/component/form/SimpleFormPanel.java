package org.activityinfo.ui.client.component.form;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.form.*;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.promise.Promise;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.form.FormElement;
import org.activityinfo.model.form.FormElementContainer;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormSection;
import org.activityinfo.ui.client.component.form.field.FormFieldWidget;
import org.activityinfo.ui.client.component.form.field.FormFieldWidgetFactory;
import org.activityinfo.ui.client.component.form.model.FormViewModel;
import org.activityinfo.ui.client.widget.DisplayWidget;

import java.util.Map;
import java.util.Objects;

/**
 * Displays a simple view of the form, where users can edit instances
 */
public class SimpleFormPanel implements DisplayWidget<FormViewModel> {

    private final VerticalFieldContainer.Factory containerFactory;
    private final FormFieldWidgetFactory widgetFactory;

    private final FlowPanel panel;
    private final ScrollPanel scrollPanel;

    private final Map<ResourceId, FieldContainer> containers = Maps.newHashMap();

    /**
     * The original, unmodified instance
     */
    private FormInstance instance;

    /**
     * A new version of the instance, being updated by the user
     */
    private FormInstance workingInstance;

    private FormViewModel viewModel;

    public SimpleFormPanel(VerticalFieldContainer.Factory containerFactory, FormFieldWidgetFactory widgetFactory) {
        FormPanelStyles.INSTANCE.ensureInjected();

        this.containerFactory = containerFactory;
        this.widgetFactory = widgetFactory;

        panel = new FlowPanel();
        panel.setStyleName(FormPanelStyles.INSTANCE.formPanel());
        scrollPanel = new ScrollPanel(panel);
    }

    public FormInstance getInstance() {
        return workingInstance;
    }

    @Override
    public Promise<Void> show(FormViewModel viewModel) {
        this.viewModel = viewModel;

        try {
            addFormElements(viewModel.getFormClass(), 0);
            setValue(viewModel.getInstance());

            return Promise.done();

        } catch(Throwable caught) {
            return Promise.rejected(caught);
        }
    }

    private void setValue(FormInstance instance) {
        this.instance = instance;
        this.workingInstance = viewModel.getInstance().copy();

        for(Map.Entry<ResourceId, FieldContainer> entry : containers.entrySet()) {
            FieldContainer container = entry.getValue();
            FormFieldWidget fieldWidget = container.getFieldWidget();
            fieldWidget.setValue(workingInstance.get(entry.getKey()));
            container.setValid();
        }
    }

    private void addFormElements(FormElementContainer container, int depth) {
        for(FormElement element : container.getElements()) {
            if(element instanceof FormSection) {
                panel.add(createHeader(depth, ((FormSection) element)));
                addFormElements((FormElementContainer) element, depth + 1);
            } else if(element instanceof FormField) {
                addField((FormField)element);
            }
        }
    }

    private void addField(final FormField field) {
        FormTree.Node node = viewModel.getFormTree().getRootField(field.getId());
        FormFieldWidget widget = widgetFactory.createWidget(viewModel, node, new ValueUpdater() {
            @Override
            public void update(Object value) {
                onFieldUpdated(field, value);
            }
        });
        FieldContainer container = containerFactory.createContainer(field, widget);
        containers.put(field.getId(), container);
        panel.add(container);
    }

    private void onFieldUpdated(FormField field, Object newValue) {
        if(!Objects.equals(workingInstance.get(field.getId()), newValue)) {
            workingInstance.set(field.getId(), newValue);
            validate(field);
        }
    }

    private void validate(FormField field) {
        String value = (String)workingInstance.get(field.getId());
        FieldContainer container = containers.get(field.getId());
        if(field.isRequired() && Strings.isNullOrEmpty(value)) {
            container.setInvalid(I18N.CONSTANTS.requiredFieldMessage());
        } else {
            container.setValid();
        }
    }

    private Widget createHeader(int depth, FormSection section) {
        StringBuilder html = new StringBuilder();
        String hn = "h" + (3+depth);
        html.append("<").append(hn).append(">")
            .append(SafeHtmlUtils.htmlEscape(section.getLabel()))
            .append("</").append(hn).append(">");
        return new HTML(html.toString());
    }

    @Override
    public Widget asWidget() {
        return scrollPanel;
    }

}
