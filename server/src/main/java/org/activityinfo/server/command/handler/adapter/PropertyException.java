package org.activityinfo.server.command.handler.adapter;

public class PropertyException extends RuntimeException {

    private PropertyException(String message) {
        super(message);
    }

    public static PropertyException missing(String name) {
        return new PropertyException("Required property " + name + " is missing");
    }

    public static PropertyException invalidType(String name, String expectedType, Object value) {
        return new PropertyException("Expect property '" + name + "' to have type '" + name + "'," +
                " found: '" + value + "'");
    }
}
