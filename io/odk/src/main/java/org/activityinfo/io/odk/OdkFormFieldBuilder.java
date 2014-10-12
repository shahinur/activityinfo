package org.activityinfo.io.odk;

import org.activityinfo.io.odk.xform.PresentationElement;

import javax.xml.bind.JAXBElement;

public interface OdkFormFieldBuilder {
    public String getModelBindType();

    public JAXBElement<PresentationElement> createPresentationElement(String ref, String label, String hint);
}
