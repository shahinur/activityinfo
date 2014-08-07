package org.activityinfo.service.store;

import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.activityinfo.model.resource.*;
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
    public Iterator<Resource> openCursor(ResourceId formClassId) {
        final Iterator iterator = session.get()
                .createSQLQuery("select json from resource where classId = ?")
                .setString(0, formClassId.asString())
                .setReadOnly(true)
                .list().iterator();

        return new UnmodifiableIterator<Resource>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Resource next() {
                String json = (String) iterator.next();
                return Resources.fromJson(json);
            }
        };
    }

    @Override
    public ResourceTree queryTree(ResourceTreeRequest request) {
        TreeBuilder builder = new TreeBuilder(session.get());
        return builder.build(request.getRootId());
    }
}
