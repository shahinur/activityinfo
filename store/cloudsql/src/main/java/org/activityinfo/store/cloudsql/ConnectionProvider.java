package org.activityinfo.store.cloudsql;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.activityinfo.service.DeploymentConfiguration;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.joda.time.Duration;

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

    private DataSource connectionPool = null;


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
        try {
            ensureConnectionPoolIsCreated();

            return connectionPool.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection openConnection() throws SQLException {

        ensureConnectionPoolIsCreated();

        Connection connection = null;
        try {
            if(Strings.isNullOrEmpty(username)) {
                connection = DriverManager.getConnection(connectionUrl);
            } else {
                connection = DriverManager.getConnection(connectionUrl, username, password);
            }
            connection.setAutoCommit(false);

            LOGGER.info("Opened MySQL connection for request.");

            return connection;
        } catch (SQLException e) {
            if(connection != null) {
                Connections.close(connection, true);
            }
            throw new RuntimeException("Failed to open SQL connection using URL [" + connectionUrl + "]");
        }
    }


    private void ensureConnectionPoolIsCreated() throws SQLException {
        if(connectionPool == null) {
            initConnectionPool();
        }
    }

    private synchronized void initConnectionPool() {
        if(connectionPool == null) {
            PoolProperties p = new PoolProperties();
            p.setUrl(connectionUrl);
            p.setDriverClassName(driverClassName);
            p.setUsername(username);
            p.setPassword(password);

            p.setJmxEnabled(false);

            p.setTestOnBorrow(true);
            p.setTestOnReturn(false);
            p.setValidationQuery("SELECT 1");
            p.setValidationInterval(Duration.standardMinutes(5).getMillis());

            // Keep the number of active connections low as there
            // may be a large number of other application servers running
            p.setMaxActive(4);
            p.setInitialSize(0);
            p.setMinIdle(0);

            // Don't wait longer than 5 seconds for a connection to be available
            p.setMaxWait(5000);

            // CloudSQL connections time out after 15 minutes
            p.setMaxAge(Duration.standardMinutes(10).getMillis());

            // Turn off the async queue which will not run on AppEngine
            p.setTestWhileIdle(false);
            p.setFairQueue(false);
            p.setTimeBetweenEvictionRunsMillis(-1);

            p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                                  "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
            connectionPool = new DataSource();
            connectionPool.setPoolProperties(p);
        }
    }
}
