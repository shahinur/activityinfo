package org.activityinfo.load;

import com.google.common.base.Supplier;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.sun.jersey.api.client.ClientResponse;
import org.activityinfo.client.ActivityInfoClient;
import org.activityinfo.client.xform.XFormInstanceBuilder;
import org.activityinfo.model.resource.ResourceId;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Future;

public class FormSubmitter implements Supplier<Future<ClientResponse>> {

    private RandomText text = new RandomText();
    private final ActivityInfoClient client;
    private byte[] image;
    private String imageFileName;

    public FormSubmitter(ActivityInfoClient client) {
        this.client = client;
    }

    public FormSubmitter(ActivityInfoClient client, String imageFileName) throws IOException {
        this.client = client;
        this.imageFileName = imageFileName;
        URL resource = Resources.getResource(FormSubmitter.class, imageFileName);

        // we don't want to hit the disk every time we create a request so
        // read the whole image into memory.
        image = Resources.asByteSource(resource).read();
    }

    @Override
    public Future<ClientResponse> get() {
        XFormInstanceBuilder instance = new XFormInstanceBuilder("jGxWlW/l");
        instance.addFieldValue(ResourceId.valueOf("chz3y5vxl1"), text.sampleLabel());
        instance.addFieldValue(ResourceId.valueOf("chz3y64oj2"), text.sampleLabel());
        instance.addFieldValue(ResourceId.valueOf("chz3y6cq63"), "42");

        if(imageFileName != null) {
            try {
                instance.addImageFieldValue(ResourceId.valueOf("chz478k361"), imageFileName, ByteSource.wrap(image));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return client.submitXFormAsync(instance);
    }
}
