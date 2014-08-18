package org.activityinfo.ui.vdom.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VHook;

import java.util.Collections;
import java.util.Map;

public class Properties {


    public static void applyProperties(Element node, PropMap props, PropMap previous) {
        for (String propName : props.keys()) {
            Object propValue = props.get(propName);
            if (propValue == null) {
                removeProperty(node, props, previous, propName);

            } else if (propValue instanceof VHook) {
                VHook hook = (VHook) propValue;
                hook.hook(node, propName, previousValueIfAny(previous, propName));

            } else {
                if (PropMap.isObject(propValue)) {
                    patchObject(node, props, previous, propName, propValue);

                } else {
                    node.setPropertyString(propName, (String) propValue);
                }
            }
        }
    }

    private static Object previousValueIfAny(PropMap previous, String propName) {
        return (previous != null) ? previous.get(propName) : null;
    }

    private static void removeProperty(Element element, PropMap props, PropMap previous, String propName) {
        if (previous != null) {
            Object previousValue = previous.get(propName);
            if (!VHook.isHook(previousValue)) {
                if (propName.equals("attributes")) {
                    for (String attrName : keys(previousValue)) {
                        element.removeAttribute(attrName);
                    }
                } else if (propName.equals("style")) {
                    for (String name : keys(previousValue)) {
                        element.getStyle().clearProperty(name);
                    }
                } else if (previousValue instanceof String) {
                    element.setPropertyString(propName, "");
                } else {
                    element.setPropertyString(propName, null);
                }
            }
        }
    }

    private static Iterable<String> keys(Object object) {
        if(object instanceof PropMap) {
            return ((PropMap) object).keys();
        } else {
            return Collections.emptySet();
        }
    }

    private static Iterable<Map.Entry<String, Object>> entries(Object object) {
        if(object instanceof PropMap) {
            return ((PropMap)object).entrySet();
        } else {
            return Collections.emptySet();
        }
    }

    public static void patchObject(Element node, PropMap props, PropMap previous, String propName, Object propValue) {
        Object previousValue = previousValueIfAny(previous, propName);
        // Set attributes
        if (propName.equals("attributes")) {
            for (Map.Entry<String, Object> attr : entries(propValue)) {
                if (attr.getValue() == null) {
                    node.removeAttribute(attr.getKey());
                } else {
                    node.setAttribute(attr.getKey(), (String) attr.getValue());
                }
            }
            return;
        }

        // TODO(AB) not sure what this about ...
//        if (previous != null && PropMap.isObject(previousValue) /* && */
//          /* getPrototype(previousValue) !== getPrototype(propValue) */) {
//            node.setPropertyObject(propName, propValue);
//            return;
//        }
//
//        if (!nodeValueIsObject(node, propName)) {
//            node.setPropertyJSO(propName, JavaScriptObject.createObject());
//        }

        if (propName.equals("style")) {
            Style style = node.getStyle();
            for (Map.Entry<String, Object> prop : entries(propValue)) {
                style.setProperty(prop.getKey(), (String) prop.getValue());
            }
        } else {
            throw new UnsupportedOperationException(propName);
        }
    }

    private static native boolean nodeValueIsObject(Node node, String propName) /*-{
        var x = node[propName];
        return typeof x === "object" && x !== null;
    }-*/;
}
