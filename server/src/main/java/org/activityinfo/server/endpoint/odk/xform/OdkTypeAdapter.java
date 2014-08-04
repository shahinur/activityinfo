package org.activityinfo.server.endpoint.odk.xform;

import javax.xml.bind.JAXBElement;

public interface OdkTypeAdapter {
    public String getModelBindType();

    public JAXBElement<PresentationElement> createPresentationElement(String ref, String label, String hint);
}
