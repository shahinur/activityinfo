package org.activityinfo.service.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.hibernate.Session;

import javax.inject.Provider;
import java.util.Iterator;

@Singleton
public class MySqlResourceStore implements ResourceStore {

    private final Provider<Session> session;

    @Inject
    public MySqlResourceStore(Provider<Session> session) {
        this.session = session;
    }

    @Override
    public Resource get(final ResourceId resourceId) {

        String json = (String)session.get()
                .createSQLQuery("select json from resource where id = ?")
                .setParameter(0, resourceId.asString())
                .uniqueResult();

        return Resources.fromJson(json);
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) {
        final Iterator iterator = session.get()
                .createSQLQuery("select json from resource where classId = ?")
                .setString(0, formClassId.asString())
                .setReadOnly(true)
                .list().iterator();

        return new ResourceCursor() {

            private Resource current;

            @Override
            public boolean next() {
                if(iterator.hasNext()) {
                    String json = (String)iterator.next();
                    current = Resources.fromJson(json);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public Resource getResource() {
                return current;
            }
        };
    }
}
