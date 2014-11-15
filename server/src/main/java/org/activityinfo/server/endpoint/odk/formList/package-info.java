/**
 * JAXB Mapping of the OpenRosa Form List Schema
 *
 * @see <a href="https://bitbucket.org/javarosa/javarosa/wiki/FormListAPI">OpenRosa FormList API</a>
 */
@XmlSchema(
        elementFormDefault = XmlNsForm.QUALIFIED,
        namespace = Namespaces.XFORM_LIST,
        xmlns = {
                @XmlNs(prefix = "", namespaceURI = Namespaces.XFORM_LIST),
        })
package org.activityinfo.server.endpoint.odk.formList;

import org.activityinfo.server.endpoint.odk.xform.Namespaces;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;