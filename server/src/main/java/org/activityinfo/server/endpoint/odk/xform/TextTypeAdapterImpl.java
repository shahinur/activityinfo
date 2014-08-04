package org.activityinfo.server.endpoint.odk.xform;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class TextTypeAdapterImpl implements OdkTypeAdapter {
    TextTypeAdapterImpl() {
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
        presentationElement.hint = hint;

        QName qName = new QName("http://www.w3.org/2002/xforms", "input");
        return new JAXBElement<>(qName, PresentationElement.class, presentationElement);
    }
}
