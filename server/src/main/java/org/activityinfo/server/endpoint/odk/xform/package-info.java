@XmlSchema(
        elementFormDefault = XmlNsForm.QUALIFIED,
        namespace = "http://www.w3.org/2002/xforms",
        xmlns = {
                @XmlNs(prefix = "", namespaceURI = "http://www.w3.org/2002/xforms"),
                @XmlNs(prefix = "h", namespaceURI = "http://www.w3.org/1999/xhtml"),
                @XmlNs(prefix = "jr", namespaceURI = "http://openrosa.org/javarosa")
        })
package org.activityinfo.server.endpoint.odk.xform;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
