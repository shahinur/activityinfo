package org.activityinfo.server.endpoint.odk.xform;


import com.google.common.collect.Lists;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class IText {
    private final List<Translation> translation = Lists.newArrayList();

    public IText() {
    }

    @XmlElement(name = "translation")
    public List<Translation> getTranslations() {
        return translation;
    }
}
