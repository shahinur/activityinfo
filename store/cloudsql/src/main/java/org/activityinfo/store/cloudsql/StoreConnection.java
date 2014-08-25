package org.activityinfo.store.cloudsql;

import com.google.common.base.Optional;

import javax.inject.Provider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Simplified wrapper of {@link java.sql.Connection} and
 * {@link com.google.appengine.api.memcache.MemcacheService} with a few convenience methods.
 */
public class StoreConnection implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(StoreConnection.class.getName());

    private final Provider<Connection> connectionProvider;

    private Connection connection;

    StoreConnection(Provider<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }


    private Connection getConnection() throws SQLException {
        if(connection == null) {
            connection = connectionProvider.get();
        }
        return connection;
    }

    /**
     * Executes the given SQL statement and returns the number of affected rows.
     */
    public int executeUpdate(String sql) throws SQLException {
        try(Statement statement = getConnection().createStatement()) {
            return statement.executeUpdate(sql);
        }
    }

    public Optional<Integer> queryInteger(String sql) throws SQLException {
        try(Statement statement = getConnection().createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                if(!rs.next()) {
                    return Optional.absent();
                } else {
                    return Optional.of(rs.getInt(1));
                }
            }
        }
    }

    public Optional<Long> queryLong(String sql) throws SQLException {
        try(Statement statement = getConnection().createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                if(!rs.next()) {
                    return Optional.absent();
                } else {
                    return Optional.of(rs.getLong(1));
                }
            }
        }
    }


    public PreparedStatement prepareStatement(String statement) throws SQLException {
        return getConnection().prepareStatement(statement);
    }

    public Statement createStatement() throws SQLException {
        return getConnection().createStatement();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if(connection != null) {
            connection.setAutoCommit(autoCommit);
        }
    }

    public void commit() throws SQLException {
        if(connection != null) {
            connection.commit();
        }
    }

    /**
     * Closes a {@link java.sql.Connection}, with control over whether an {@code SQLException} may be thrown.
     * This is primarily useful in a finally block, where a thrown exception needs to be logged but
     * not propagated (otherwise the original exception will be lost).
     *
     * <p>If {@code swallowException} is true then we never throw {@code SQLException} but merely log
     * it.
     */
    public void close(boolean swallowException) {
        Connections.close(connection, swallowException);
        connection = null;
    }


    @Override
    public void close() {
        close(true);
    }
}
