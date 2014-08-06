package org.activityinfo.server.endpoint.odk;

import org.activityinfo.server.endpoint.odk.xform.PresentationElement;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

class SimpleInputAdapter implements OdkTypeAdapter {
    final private String modelBindType;

    SimpleInputAdapter(String modelBindType) {
        this.modelBindType = modelBindType;
    }

    @Override
    public String getModelBindType() {
        return modelBindType;
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
