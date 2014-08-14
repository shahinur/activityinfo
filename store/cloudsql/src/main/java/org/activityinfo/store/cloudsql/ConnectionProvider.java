package org.activityinfo.store.cloudsql;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.activityinfo.service.DeploymentConfiguration;

import javax.inject.Provider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Provides a JDBC connection. Connections are reused for the duration of the request
 * and closed upon the request's completion.
 */
@Singleton
public class ConnectionProvider implements Provider<Connection> {

    private static final Logger LOGGER = Logger.getLogger(ConnectionProvider.class.getName());

    private static final String DRIVER_PROPERTY = "hibernate.connection.driver_class";
    private static final String CONNECTION_URL_PROPERTY = "hibernate.connection.url";
    private static final String CONNECTION_USERNAME = "hibernate.connection.username";
    private static final String CONNECTION_PASSWORD = "hibernate.connection.password";


    private final String connectionUrl;
    private final String username;
    private final String password;
    private final String driverClassName;

    /**
     * Avoid loading driver class eagerly to reduce startup time of new instances.
     */
    private boolean driverLoaded = false;

    private final ThreadLocal<Connection> connectionForRequest = new ThreadLocal<>();


    @Inject
    public ConnectionProvider(DeploymentConfiguration configuration) {
        connectionUrl = configuration.getProperty(CONNECTION_URL_PROPERTY);
        username = configuration.getProperty(CONNECTION_USERNAME);
        password = Strings.emptyToNull(configuration.getProperty(CONNECTION_PASSWORD));
        driverClassName = configuration.getProperty(DRIVER_PROPERTY);

        LOGGER.info("connectionUrl: " + connectionUrl);
        LOGGER.info("username: " + (username == null ? "Not provided" : username));
        LOGGER.info("password: " + (password == null ? "NO" : "YES"));
        LOGGER.info("driverClassName: " + driverClassName);
    }

    @Override
    public Connection get() {

        Connection connection = connectionForRequest.get();

        try {
            if(connection == null || connection.isClosed()) {
                connection = openConnection();
                connectionForRequest.set(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new LeasedConnection(connection);
    }

    private Connection openConnection() {

        ensureJdbcDriverIsLoaded();

        Connection connection = null;
        try {
            if(Strings.isNullOrEmpty(username)) {
                connection = DriverManager.getConnection(connectionUrl);
            } else {
                connection = DriverManager.getConnection(connectionUrl, username, password);
            }
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            if(connection != null) {
                Connections.close(connection, true);
            }
            throw new RuntimeException("Failed to open SQL connection using URL [" + connectionUrl + "]");
        }
    }


    private void ensureJdbcDriverIsLoaded() {
        if(!driverLoaded) {
            // ensure the JDBC driver is loaded
            try {
                Class.forName(driverClassName);
                driverLoaded = true;
            } catch (Exception e) {
                throw new RuntimeException("Failed to load JDBC Driver Class '" + driverClassName + "'");
            }
        }
    }

    /**
     *
     * Cleans up any connection that was opened during the request.
     *
     * @param swallowException true if this method is called from an exception handler and subsequent
     *                         exceptions should only be logged and not thrown to avoid loosing track
     *                         of the original exception
     */
    public void cleanupAfterRequestFinishes(boolean swallowException) {
        Connection connection = connectionForRequest.get();
        connectionForRequest.remove();

        if(connection != null) {
            Connections.close(connection, swallowException);
        }
    }
}
