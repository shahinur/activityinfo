package org.activityinfo.store.hrd;

import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.junit.Test;

public class IdemPotentCreationTest {


    @Test
    public void duplicateSubmissionIsHappilyIgnored() {

        Resource resource = Resources.createResource();
        resource.setId(Resources.generateId());
        resource.setOwnerId(Resources.ROOT_ID);
        resource.setValue(Records.builder().set("label", "Hello world").build());

    }
}
