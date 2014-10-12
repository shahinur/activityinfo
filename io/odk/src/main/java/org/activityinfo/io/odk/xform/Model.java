package org.activityinfo.io.odk.xform;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Model {
    @XmlElement
    public Instance instance;

    @XmlElement
    public List<Bind> bind;
}
