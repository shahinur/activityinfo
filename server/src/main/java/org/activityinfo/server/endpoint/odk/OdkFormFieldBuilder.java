package org.activityinfo.server.endpoint.odk;

import org.activityinfo.server.endpoint.odk.xform.BindingType;
import org.activityinfo.server.endpoint.odk.xform.BodyElement;

public interface OdkFormFieldBuilder {

    public BindingType getModelBindType();

    public BodyElement createBodyElement(String ref, String label, String hint);
}
