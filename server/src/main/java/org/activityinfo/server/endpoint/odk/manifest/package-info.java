/**
 * JAXB Mapping of the OpenRosa Manifest Document.
 *
 * @see <a href="https://bitbucket.org/javarosa/javarosa/wiki/FormListAPI">OpenRosa FormList API</a>
 */
@XmlSchema(
        elementFormDefault = XmlNsForm.QUALIFIED,
        namespace = Namespaces.XFORM_MANIFEST,
        xmlns = {
                @XmlNs(prefix = "", namespaceURI = Namespaces.XFORM_MANIFEST),
        })
package org.activityinfo.server.endpoint.odk.manifest;

import org.activityinfo.server.endpoint.odk.xform.Namespaces;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;