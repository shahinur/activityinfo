package org.activityinfo.server.endpoint.odk.xform;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.server.endpoint.odk.OdkField;
import org.activityinfo.server.endpoint.odk.OdkFormFieldBuilder;
import org.activityinfo.server.endpoint.odk.OdkFormFieldBuilderFactory;

import java.util.*;

import static org.activityinfo.model.legacy.CuidAdapter.*;
import static org.activityinfo.model.legacy.CuidAdapter.field;

/**
 * Constructs an XForm from a FormClass
 */
public class XFormBuilder {
    private OdkFormFieldBuilderFactory factory;
    private String userId;
    private FormClass formClass;
    private List<OdkField> fields;
    private ResourceId startDateFieldId;
    private ResourceId endDateFieldId;
    private Set<ResourceId> dateFields;
    private XPathBuilder xPathBuilder;
    private XForm xform;

    public XFormBuilder(OdkFormFieldBuilderFactory factory) {
        this.factory = factory;
    }

    public XFormBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public XForm build(FormClass formClass) {
        this.formClass = formClass;

        startDateFieldId = field(formClass.getId(), START_DATE_FIELD);
        endDateFieldId = field(formClass.getId(), END_DATE_FIELD);
        dateFields = Sets.newHashSet(startDateFieldId, endDateFieldId);

        fields = createFieldBuilders(formClass);
        xPathBuilder = new XPathBuilder(fields);
        xform = new XForm();
        xform.getHead().setTitle(formClass.getLabel());
        xform.getHead().setModel(createModel());
        xform.setBody(createBody());

        return xform;
    }

    private List<OdkField> createFieldBuilders(FormClass formClass) {
        fields = new ArrayList<>();
        for (FormField field : formClass.getFields()) {
            if(field.isVisible()) {
                OdkFormFieldBuilder builder = factory.get(field.getType());
                if (builder != null) {
                    fields.add(new OdkField(field, builder));
                }
            }
        }
        return fields;
    }

    private Model createModel() {
        Model model = new Model();
        model.getItext().getTranslations().add(Translation.defaultTranslation());
        model.setInstance(createInstance());
        model.getBindings().addAll(createBindings());
        return model;
    }

    private Instance createInstance() {

        InstanceElement data = new InstanceElement("data");
        data.setId(formClass.getId().asString());
        data.addChild(
                new InstanceElement("meta",
                        new InstanceElement("instanceID"),
                        new InstanceElement("userID", userId)));

        for (OdkField field : fields) {
            data.addChild(new InstanceElement(field.getRelativeFieldName()));
        }

        return new Instance(data);
    }


    private Collection<Bind> createBindings() {
        List<Bind> bindings = Lists.newArrayList();
        bindings.add(instanceIdBinding());
        bindings.add(startDate(formClass.getId()));
        bindings.add(endDate(formClass.getId()));

        for (OdkField field : fields) {
            // As a transitional hack, populate the startDate and endDate of the "activity"
            // with the start/end date of interview
            if(field.getModel().getId().equals(startDateFieldId)) {
                bindings.add(startDate(formClass.getId()));

            } else if(field.getModel().getId().equals(endDateFieldId)) {
                bindings.add(endDate(formClass.getId()));

            } else {
                Bind bind = new Bind();
                bind.setNodeSet(field.getAbsoluteFieldName());
                bind.setType(field.getBuilder().getModelBindType());
                if (field.getModel().isReadOnly()) {
                    bind.setReadonly(XPathBuilder.TRUE);
                }
                //TODO Fix this
                //bind.calculate = formField.getExpression();
                bind.setRelevant(xPathBuilder.build(field.getModel().getRelevanceConditionExpression()));
                if (field.getModel().isRequired()) {
                    bind.setRequired(XPathBuilder.TRUE);
                }
                bindings.add(bind);
            }
        }
        return bindings;
    }

    private Body createBody() {
        Body body = new Body();
        for (OdkField field : fields) {
            if (field.getModel().isVisible() && !dateFields.contains(field.getModel().getId())) {
                body.addElement(createPresentationElement(field));
            }
        }
        return body;
    }

    private BodyElement createPresentationElement(OdkField formField) {
        return formField.getBuilder().createBodyElement(
                formField.getAbsoluteFieldName(),
                formField.getModel().getLabel(),
                formField.getModel().getDescription());
    }

    private Bind instanceIdBinding() {
        Bind bind = new Bind();
        bind.setNodeSet("/data/meta/instanceID");
        bind.setType(BindingType.STRING);
        bind.setReadonly(XPathBuilder.TRUE);
        bind.setCalculate("concat('uuid:',uuid())");
        return bind;
    }

    private Bind startDate(ResourceId classId) {
        Bind bind = new Bind();
        bind.setNodeSet("/data/field_" + field(classId, START_DATE_FIELD));
        bind.setType(BindingType.DATETIME);
        bind.setPreload("timestamp");
        bind.setPreloadParams("start");
        return bind;
    }

    private Bind endDate(ResourceId classId) {
        Bind bind = new Bind();
        bind.setNodeSet("/data/field_" + field(classId, END_DATE_FIELD));
        bind.setReadonly(XPathBuilder.TRUE);
        bind.setPreload("timestamp");
        bind.setPreloadParams("end");
        return bind;
    }

}
