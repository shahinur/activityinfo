package org.activityinfo.load;

import com.google.common.base.Supplier;
import com.sun.jersey.api.client.ClientResponse;
import org.activityinfo.client.ActivityInfoClient;
import org.activityinfo.client.XFormInstance;
import org.activityinfo.model.resource.ResourceId;

import java.util.concurrent.Future;

public class FormSubmitter implements Supplier<Future<ClientResponse>> {

    private RandomText text = new RandomText();
    private final ActivityInfoClient client;

    public FormSubmitter(ActivityInfoClient client) {
        this.client = client;
    }

    @Override
    public Future<ClientResponse> get() {
        XFormInstance instance = new XFormInstance("jGxWlW/l");
        instance.addFieldValue(ResourceId.valueOf("chz3y5vxl1"), text.sampleLabel());
        instance.addFieldValue(ResourceId.valueOf("chz3y64oj2"), text.sampleLabel());
        instance.addFieldValue(ResourceId.valueOf("chz3y6cq63"), "42");

        return client.submitXFormAsync(instance);
    }
}
