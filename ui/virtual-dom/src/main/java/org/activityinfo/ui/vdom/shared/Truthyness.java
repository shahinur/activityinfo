package org.activityinfo.ui.vdom.shared;

import java.util.Collection;
import java.util.Map;

public class Truthyness {
    public static boolean isTrue(Object object) {
        if(object == null) {
            return false;
        }
        if(object instanceof Collection) {
            return !((Collection) object).isEmpty();
        }
        if(object instanceof Map) {
            return !((Map) object).isEmpty();
        }
        return true;
    }

    public static boolean isTrue(int value) {
        return value != 0;
    }
}
