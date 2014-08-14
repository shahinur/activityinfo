package org.activityinfo.store.cloudsql;

import com.google.common.collect.Lists;

import javax.inject.Provider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TestingConnectionProvider implements Provider<Connection> {

    private static final String PASSWORD_PROPERTY = "testDatabasePassword";
    private static final String USERNAME_PROPERTY = "testDatabaseUsername";
    private static final String URL_PROPERTY = "testDatabaseUrl";

    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "root";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost/activityinfo-test?useUnicode=true&characterEncoding=UTF-8";


    private final String username;
    private final String connectionUrl;
    private final String password;

    private final List<Connection> leasedConnections = Lists.newArrayList();

    private final List<String> tableNames = Arrays.asList("resource", "resource_version", "pending_commits");

    public TestingConnectionProvider() throws IOException {
        Properties activityinfoProperties = new Properties();
        File propertiesFile = new File(System.getProperty("user.home"), "activityinfo.properties");
        if (propertiesFile.exists()) {
            activityinfoProperties.load(new FileInputStream(propertiesFile));
        }

        String urlProperty = activityinfoProperties.getProperty(URL_PROPERTY);
        this.connectionUrl = urlProperty != null ? urlProperty : System.getProperty(URL_PROPERTY, DEFAULT_URL);

        String usernameProperty = activityinfoProperties.getProperty(USERNAME_PROPERTY);
        this.username = usernameProperty != null ? usernameProperty :
                System.getProperty(USERNAME_PROPERTY, DEFAULT_USERNAME);

        String passwordProperty = activityinfoProperties.getProperty(PASSWORD_PROPERTY);
        this.password = passwordProperty != null ? passwordProperty :
                System.getProperty(PASSWORD_PROPERTY, DEFAULT_PASSWORD);
    }

    @Override
    public Connection get() {
        System.out.println("Opening test database at " + connectionUrl);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(connectionUrl, username, password);
            connection.setAutoCommit(false);

            leasedConnections.add(connection);

            return connection;

        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clearTables() throws SQLException {
        try(Connection connection = get()) {
            Statement statement = connection.createStatement();
            for(String tableName : tableNames) {
                statement.executeUpdate("TRUNCATE TABLE " + tableName);
            }
            statement.executeUpdate("UPDATE global_version SET current_version = 0");
        }
    }



    public void assertThatAllConnectionsHaveBeenClosed() throws SQLException {
        int openCount = 0;
        for(Connection connection : leasedConnections) {
            if(!connection.isClosed()) {
                openCount++;
            }
        }
        if(openCount > 0) {
            throw new AssertionError(openCount + " of " + leasedConnections.size() + " were left open!");
        } else {
            System.out.println("All leased connections were closed. Netjes!");
        }
    }

}
