package org.activityinfo.store.hrd.tx;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.base.Optional;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.LatestVersionKey;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ReadTxTest {


    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setApplyAllHighRepJobPolicy());


    @Before
    public void setUp() throws Exception {
        helper.setUp();
    }

    @After
    public void tearDown() throws Exception {
        helper.tearDown();
    }

    @Test
    public void test() {
        ResourceId workspaceId = Resources.generateId();
        ResourceId resourceId = Resources.generateId();
        ResourceId classId = Resources.generateId();

        try(ReadWriteTx tx = ReadWriteTx.serializedCrossGroup()) {
            WorkspaceEntityGroup group = new WorkspaceEntityGroup(workspaceId);
            Resource resource = Resources.createResource();
            resource.setId(resourceId);
            resource.setOwnerId(group.getWorkspaceId());
            resource.setValue(Records.builder(classId).set("label", "foo").build());
            LatestVersion latestVersion = new LatestVersion(group, resource);
            latestVersion.setVersion(1L);
            tx.put(latestVersion);
            tx.commit();
        }

        try(ReadTx tx = ReadTx.outsideTransaction()) {
            Optional<LatestVersionKey> key = tx.query(LatestVersion.ofResource(resourceId));
            assertThat(key.get().getWorkspace().getWorkspaceId(), equalTo(workspaceId));
        }
    }
}