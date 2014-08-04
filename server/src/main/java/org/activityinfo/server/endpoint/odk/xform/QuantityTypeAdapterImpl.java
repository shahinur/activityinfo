package org.activityinfo.server.endpoint.odk.xform;

import org.activityinfo.model.type.number.QuantityType;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class QuantityTypeAdapterImpl implements OdkTypeAdapter {
    final private String units;

    QuantityTypeAdapterImpl(QuantityType quantityType) {
        units = quantityType.getUnits();
    }

    @Override
    public String getModelBindType() {
        return "decimal";
    }

    @Override
    public JAXBElement<PresentationElement> createPresentationElement(String ref, String label, String hint) {
        PresentationElement presentationElement = new PresentationElement();

        presentationElement.ref = ref;
        if (units == null) presentationElement.label = label;
        else if (label == null) presentationElement.label = units;
        else presentationElement.label = label + " [" + units + ']';
        presentationElement.hint = hint;

        QName qName = new QName("http://www.w3.org/2002/xforms", "input");
        return new JAXBElement<>(qName, PresentationElement.class, presentationElement);
    }
}
