package org.activityinfo.server.endpoint.odk.xform;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.ReferenceType;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.List;

import static org.activityinfo.model.type.Cardinality.SINGLE;

public class ReferenceTypeAdapterImpl implements OdkTypeAdapter {
    final private Cardinality cardinality;
    final private List<Item> item;

    ReferenceTypeAdapterImpl(ReferenceType referenceType) {
        cardinality = referenceType.getCardinality();
        item = Lists.newArrayListWithCapacity(referenceType.getRange().size());
        for (ResourceId resourceId : referenceType.getRange()) {
            Item item = new Item();
            item.label = "";    //TODO Set label to correct value
            item.value = resourceId.asString();
            this.item.add(item);
        }
    }

    @Override
    public String getModelBindType() {
        return "string";
    }

    public JAXBElement<PresentationElement> createPresentationElement(String ref, String label, String hint) {
        PresentationElement presentationElement = new PresentationElement();

        presentationElement.ref = ref;
        presentationElement.label = label;
        presentationElement.item = item;
        presentationElement.hint = hint;

        QName qName = new QName("http://www.w3.org/2002/xforms", SINGLE.equals(cardinality) ? "select1" : "select");
        return new JAXBElement<>(qName, PresentationElement.class, presentationElement);
    }
}
