package org.activityinfo.server.endpoint.odk.xform;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Body {
    @XmlAnyElement
    public List<JAXBElement<PresentationElement>> jaxbElement;

    // This ugly hack is necessary to prevent JAXB serialization failures, but a saner solution would be very welcome...
    @XmlElement
    final static PresentationElement dummy = null;
}
