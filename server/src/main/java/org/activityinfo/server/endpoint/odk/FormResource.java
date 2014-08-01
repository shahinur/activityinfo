package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.AttributeGroupDTO;
import org.activityinfo.legacy.shared.model.IsFormField;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.server.endpoint.odk.xform.*;
import org.activityinfo.service.ResourceLocatorSync;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Path("/activityForm")
public class FormResource extends ODKResource {

    private ResourceLocatorSync locator;

    @Inject
    public FormResource(ResourceLocatorSync locator) {
        this.locator = locator;
    }

    @GET @Produces(MediaType.TEXT_XML)
    public Response form(@QueryParam("id") int id) throws Exception {
        if (enforceAuthorization()) {
            return askAuthentication();
        }
        LOGGER.finer("ODK activity form " + id + " requested by " +
                     getUser().getEmail() + " (" + getUser().getId() + ")");

        //TODO This is still not done and needs major refactoring, but we're getting there
        Resource resource = locator.getResource(CuidAdapter.activityFormClass(id));
        FormClass formClass = FormClass.fromResource(resource);
        List<FormField> formFields = formClass.getFields();

        Html html = new Html();
        html.head = new Head();
        html.head.title = formClass.getLabel();
        html.head.model = new Model();
        html.head.model.instance = new Instance();
        html.head.model.instance.data = new Data();
        html.head.model.instance.data.meta = new Meta();
        html.head.model.instance.data.meta.instanceID = new InstanceId();
        html.head.model.instance.data.jaxbElement = Lists.newArrayListWithCapacity(formFields.size());
        for (FormField formField : formFields) {
            QName qName = new QName("http://www.w3.org/2002/xforms", formField.getId().asString());
            html.head.model.instance.data.jaxbElement.add(new JAXBElement<>(qName, String.class, ""));
        }
        html.head.model.bind = Lists.newArrayListWithCapacity(formFields.size() + 1);
        Bind bind = new Bind();
        bind.nodeset = "/data/meta/instanceID";
        bind.type = "string";
        bind.readonly = "true()";
        bind.calculate = "concat('uuid:',uuid())";
        html.head.model.bind.add(bind);
        for (FormField formField : formFields) {
            OdkTypeAdapter odkTypeAdapter = new OdkTypeAdapter(formField.getType());
            bind = new Bind();
            bind.nodeset = "/data/" + formField.getId().asString();
            bind.type = odkTypeAdapter.getModelBindType();
            if (formField.isReadOnly()) bind.readonly = "true()";
            bind.calculate = formField.getCalculation();
            if (formField.isRequired()) bind.required = "true()";
            html.head.model.bind.add(bind);
        }
        html.body = new Body();
        html.body.jaxbElement = Lists.newArrayListWithCapacity(formFields.size());
        for (FormField formField : formFields) {
            OdkTypeAdapter odkTypeAdapter = new OdkTypeAdapter(formField.getType());
            PresentationElement presentationElement;
            presentationElement = new PresentationElement();
            presentationElement.ref = "/data/" + formField.getId().asString();
            String label = formField.getLabel();
            if (odkTypeAdapter.isQuantity()) {
                QuantityType quantityType = (QuantityType) formField.getType();
                String units = quantityType.getUnits();
                if (units != null) {
                    if (label == null) label = units;
                    else label += " [" + units + ']';
                }
            }
            presentationElement.label = label;
            if (odkTypeAdapter.isBoolean()) {
                Item no = new Item();
                no.label = "no";
                no.value = "false";
                Item yes = new Item();
                yes.label = "yes";
                yes.value = "true";
                presentationElement.item = Lists.newArrayList(yes, no);
            } else if (odkTypeAdapter.isEnum()) {
                EnumType enumType = (EnumType) formField.getType();
                presentationElement.item = Lists.newArrayListWithCapacity(enumType.getValues().size());
                for (EnumValue enumValue : enumType.getValues()) {
                    Item item = new Item();
                    item.label = enumValue.getLabel();
                    item.value = enumValue.getId().asString();
                    presentationElement.item.add(item);
                }
                //FIXME Temporary hack to work around FormClass.fromResource() apparently being complete
                if (presentationElement.item.size() < 1) continue;
            } else if (odkTypeAdapter.isReference()) {
                ReferenceType referenceType = (ReferenceType) formField.getType();
                presentationElement.item = Lists.newArrayListWithCapacity(referenceType.getRange().size());
                for (ResourceId resourceId : referenceType.getRange()) {
                    Item item = new Item();
                    item.label = "";    //TODO Set label to correct value
                    item.value = resourceId.asString();
                    presentationElement.item.add(item);
                }
            }
            presentationElement.hint = formField.getDescription();
            QName qName = new QName("http://www.w3.org/2002/xforms", odkTypeAdapter.getPresentationElement());
            html.body.jaxbElement.add(new JAXBElement<>(qName, PresentationElement.class, presentationElement));
        }
        return Response.ok(html).build();
    }

    private List<IsFormField> sortFieldsTogether(ActivityDTO activity) {
        List<IsFormField> fields = Lists.newArrayList();

        // add only attribute groups with at least one attribute
        for(AttributeGroupDTO group : activity.getAttributeGroups()) {
            if(group.getAttributes().size() > 0) {
                fields.add(group);
            }
        }

        // add indicators if this nto monthly reporting
        if(activity.getReportingFrequency() == 0) {
            fields.addAll(activity.getIndicators());
        }

        Collections.sort(fields, new Comparator<IsFormField>() {
            @Override
            public int compare(IsFormField o1, IsFormField o2) {
                return o1.getSortOrder() - o2.getSortOrder();
            }
        });
        return fields;
    }
}
