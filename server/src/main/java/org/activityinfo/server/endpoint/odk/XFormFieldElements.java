package org.activityinfo.server.endpoint.odk;

import org.activityinfo.server.endpoint.odk.xform.Bind;
import org.activityinfo.server.endpoint.odk.xform.BodyElement;
import org.activityinfo.server.endpoint.odk.xform.InstanceElement;

import java.util.List;


public class XFormFieldElements {

    private InstanceElement instanceElement;
    private List<Bind> bindings;
    private List<BodyElement> bodyElements;

    public XFormFieldElements(InstanceElement instanceElement, List<Bind> bindings, List<BodyElement> bodyElements) {
        this.instanceElement = instanceElement;
        this.bindings = bindings;
        this.bodyElements = bodyElements;
    }
}
