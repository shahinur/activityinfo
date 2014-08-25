package org.activityinfo.client.xform;

import com.google.common.collect.Lists;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "formList")
public class XFormList {

    private List<XFormItem> forms = Lists.newArrayList();

    @XmlElement(name = "form")
    public List<XFormItem> getForms() {
        return forms;
    }
}
