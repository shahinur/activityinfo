package org.activityinfo.store.cloudsql;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IdemPotentCreationTest {


    @Test
    public void duplicateSubmissionIsHappilyIgnored() {

        Resource resource = Resources.createResource();
        resource.setId(ResourceId.generateId());
        resource.setOwnerId(ResourceId.ROOT_ID);
        resource.set("label", "Hello world");

    }
}
