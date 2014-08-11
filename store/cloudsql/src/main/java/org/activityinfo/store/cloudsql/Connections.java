package org.activityinfo.store.cloudsql;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility methods for working with {@link java.sql.Connection} objects.
 */
public class Connections {

    public static final Logger LOGGER = Logger.getLogger(Connections.class.getName());



    public static void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Swallowing exception caught while rolling back transaction", e);
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
    public static void close(Connection connection, boolean swallowException) {
        if(connection != null) {
            try {
                connection.close();
            } catch (Exception closingException) {
                if (swallowException) {
                    LOGGER.log(Level.SEVERE,
                            "Swallowing exception caught while closing connection in exception handler",
                            closingException);
                } else {
                    throw new RuntimeException(closingException);
                }
            }
        }
    }
}
