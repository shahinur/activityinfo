package org.activityinfo.store.cloudsql;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.store.ResourceCursor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlResourceCursor implements ResourceCursor {

    private final ResultSet resultSet;

    private Resource next;

    MySqlResourceCursor(ResultSet resultSet) throws SQLException {
        this.resultSet = resultSet;
        fetchNext();
    }

    private void fetchNext() {
        try {
            if (resultSet.next()) {
                next = Resources.fromJson(resultSet.getString(2));
                next.setVersion(resultSet.getLong(1));
            } else {
                next = null;
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Resource next() {
        Resource toReturn = next;
        fetchNext();
        return toReturn;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws Exception {
        resultSet.close();
    }
}
