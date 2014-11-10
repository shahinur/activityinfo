package org.activityinfo.server.endpoint.odk;

import org.activityinfo.server.endpoint.odk.xform.BindingType;
import org.activityinfo.server.endpoint.odk.xform.Upload;

class UploadBuilder implements OdkFormFieldBuilder {
    final private String mediaType;

    UploadBuilder(String mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public BindingType getModelBindType() {
        return BindingType.BINARY;
    }

    @Override
    public Upload createPresentationElement(String ref, String label, String hint) {
        Upload upload = new Upload();

        upload.setRef(ref);
        upload.setMediaType(mediaType);
        upload.setLabel(label);
        upload.setHint(hint);

        return upload;
    }
}
