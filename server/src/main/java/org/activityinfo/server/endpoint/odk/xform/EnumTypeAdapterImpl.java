package org.activityinfo.server.endpoint.odk.xform;

import com.google.common.collect.Lists;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.List;

import static org.activityinfo.model.type.Cardinality.SINGLE;

public class EnumTypeAdapterImpl implements OdkTypeAdapter {
    final private Cardinality cardinality;
    final private List<Item> item;

    EnumTypeAdapterImpl(EnumType enumType) {
        cardinality = enumType.getCardinality();
        item = Lists.newArrayListWithCapacity(enumType.getValues().size());
        for (EnumValue enumValue : enumType.getValues()) {
            Item item = new Item();
            item.label = enumValue.getLabel();
            item.value = enumValue.getId().asString();
            this.item.add(item);
        }
    }

    @Override
    public String getModelBindType() {
        return "string";
    }

    @Override
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
