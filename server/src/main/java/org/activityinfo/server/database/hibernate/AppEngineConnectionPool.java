package org.activityinfo.server.database.hibernate;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import org.activityinfo.server.util.config.ConfigModule;
import org.activityinfo.server.util.config.DeploymentConfiguration;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.joda.time.Duration;

import java.sql.Connection;
import java.sql.SQLException;


public class AppEngineConnectionPool implements ConnectionProvider {

    private final DataSource connectionPool;

    @Inject
    public AppEngineConnectionPool() {

        DeploymentConfiguration config = ConfigModule.DEPLOYMENT_CONFIG;
        Preconditions.checkState(config != null, "DeploymentConfig has not yet been loaded.");

        PoolProperties p = new PoolProperties();
        p.setUrl(config.getProperty("hibernate.connection.url"));
        p.setDriverClassName(config.getProperty("hibernate.connection.driver_class"));
        if(config.hasProperty("hibernate.connection.username")) {
            p.setUsername(config.getProperty("hibernate.connection.username"));
        }
        if(config.hasProperty("hibernate.connection.password")) {
            p.setPassword(config.getProperty("hibernate.connection.password"));
        }

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

        // Turn auto-commit off
        p.setDefaultAutoCommit(false);

        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        connectionPool = new DataSource();
        connectionPool.setPoolProperties(p);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        try {
            return (T)this;
        } catch(ClassCastException e) {
            throw new UnknownUnwrapTypeException(unwrapType);
        }
    }
}
