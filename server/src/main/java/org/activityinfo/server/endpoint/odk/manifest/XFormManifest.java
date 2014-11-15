package org.activityinfo.server.endpoint.odk.manifest;

import com.google.common.collect.Lists;
import org.activityinfo.server.endpoint.odk.xform.Namespaces;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(namespace = Namespaces.XFORM_MANIFEST, name = "manifest")
public class XFormManifest {

    private final List<MediaFile> mediaFiles = Lists.newArrayList();

    @XmlElement(name = "mediaFile")
    public List<MediaFile> getMediaFiles() {
        return mediaFiles;
    }
}
