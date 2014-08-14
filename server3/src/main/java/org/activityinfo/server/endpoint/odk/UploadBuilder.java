package org.activityinfo.server.endpoint.odk;

import org.activityinfo.server.endpoint.odk.xform.PresentationElement;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

class UploadBuilder implements OdkFormFieldBuilder {
    final private String mediatype;

    UploadBuilder(String mediatype) {
        this.mediatype = mediatype;
    }

    @Override
    public String getModelBindType() {
        return "binary";
    }

    @Override
    public JAXBElement<PresentationElement> createPresentationElement(String ref, String label, String hint) {
        PresentationElement presentationElement = new PresentationElement();

        presentationElement.ref = ref;
        presentationElement.mediatype = mediatype;
        presentationElement.label = label;
        presentationElement.hint = hint;

        QName qName = new QName("http://www.w3.org/2002/xforms", "upload");
        return new JAXBElement<>(qName, PresentationElement.class, presentationElement);
    }
}
