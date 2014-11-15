package org.activityinfo.server.endpoint.odk.formList;

import com.google.common.collect.Lists;
import org.activityinfo.server.endpoint.odk.xform.Namespaces;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "xforms", namespace = Namespaces.XFORM_LIST)
public class XFormList {

    private final List<XFormListItem> items = Lists.newArrayList();

    @XmlElement(name = "xform")
    public List<XFormListItem> getItems() {
        return items;
    }
}
