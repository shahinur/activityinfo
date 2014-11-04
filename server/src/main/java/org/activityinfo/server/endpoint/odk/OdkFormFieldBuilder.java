package org.activityinfo.server.endpoint.odk;

import org.activityinfo.server.endpoint.odk.xform.PresentationElement;

import javax.xml.bind.JAXBElement;

public interface OdkFormFieldBuilder {
    public String getModelBindType();

    public JAXBElement<PresentationElement> createPresentationElement(String ref, String label, String hint);
}
