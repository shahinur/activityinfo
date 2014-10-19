package org.activityinfo.server.command.handler;

import com.google.inject.util.Providers;
import org.activityinfo.legacy.shared.command.GetSiteHistory;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.store.test.TestFormClass;
import org.activityinfo.store.test.TestResourceStore;
import org.junit.Test;

import javax.persistence.EntityManager;

import static org.easymock.EasyMock.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class GetSiteHistoryHandlerTest {

    private TestResourceStore store;

    @Test
    public void test() {
        store = new TestResourceStore();
        store.setUp();

        AuthenticatedUser user = new AuthenticatedUser(1);
        User userEntity = new User();
        userEntity.setId(user.getId());

        User committer = new User();
        committer.setId(1);
        committer.setName("Test User");
        committer.setEmail("test@example.com");

        EntityManager em = createMock(EntityManager.class);
        expect(em.find(User.class, 1)).andReturn(committer).anyTimes();
        replay(em);

        FormInstance workspace = new FormInstance(Resources.generateId(), FolderClass.CLASS_ID);
        workspace.setOwnerId(Resources.ROOT_ID);
        workspace.set(FolderClass.LABEL_FIELD_ID, "Workspace!");
        store.create(user, workspace.asResource());

        TestFormClass form = new TestFormClass(workspace.getId());
        store.create(user, form.formClass.asResource());

        Resource instance = form.instances(1).iterator().next();
        store.create(user, instance);

        GetSiteHistoryHandler historyHandler = new GetSiteHistoryHandler(store, Providers.of(em));
        GetSiteHistory.GetSiteHistoryResult history = (GetSiteHistory.GetSiteHistoryResult) historyHandler.execute(
                new GetSiteHistory(instance.getId()), userEntity);

        assertThat(history.getSiteHistories(), hasSize(1));

        System.out.println(history.getSiteHistories().get(0).getProperties());

    }
}