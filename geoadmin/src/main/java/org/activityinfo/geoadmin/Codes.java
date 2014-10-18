package org.activityinfo.geoadmin;

public class Codes {

    public static boolean hasCode(Object[] attributeValues, String code) {
        if (code.matches("\\d+")) {
            return hasIntCode(attributeValues, Integer.parseInt(code));
        } else {
            // TODO
            return false;
        }
    }

    private static boolean hasIntCode(Object[] attributeValues, int code) {
        for (Object value : attributeValues) {
            try {
                if (value instanceof Number) {
                    if (((Number) value).intValue() == code) {
                        return true;
                    }
                } else if (value instanceof String) {
                    if (Integer.parseInt((String) value) == code) {
                        return true;
                    }
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

}
