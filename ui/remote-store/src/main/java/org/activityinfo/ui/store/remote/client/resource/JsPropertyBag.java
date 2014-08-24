package org.activityinfo.ui.store.remote.client.resource;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import org.activityinfo.model.resource.Record;

import java.util.Collection;
import java.util.Map;

public final class JsPropertyBag extends JavaScriptObject {


    protected JsPropertyBag() {
    }

    public static final native JsPropertyBag create() /*-{
      return {};
    }-*/;

    public final native void setString(String propertyName, String value) /*-{
      this[propertyName] = value;
    }-*/;

    public final native void setBoolean(String propertyName, boolean value) /*-{
      this[propertyName] = value;
    }-*/;

    public final native void setNumber(String propertyName, double number) /*-{
      this[propertyName] = number;
    }-*/;

    public final native void setObject(String propertyName, JavaScriptObject object) /*-{
      this[propertyName] = object;
    }-*/;


    public final void set(String propertyName, Record record) {
        set(propertyName, toBag(record));
    }

    private JsPropertyBag toBag(Record record) {
        JsPropertyBag bag = create();
        for (Map.Entry<String, Object> entry : record.getProperties().entrySet()) {
            bag.set(entry.getKey(), entry.getValue());
        }
        return bag;
    }

    public void set(String key, Object value) {
        if(value instanceof String) {
            setString(key, (String)value);
        } else if(value instanceof Boolean) {
            setBoolean(key, value == Boolean.TRUE);
        } else if(value instanceof Number) {
            setNumber(key, ((Number)value).doubleValue());
        } else if(value instanceof Collection) {
            setObject(key, toJsonArray((Collection<Record>) value));
        } else if(value instanceof Record) {
            setObject(key, toBag((Record) value));
        } else {
            throw new IllegalArgumentException("value: " + value);
        }
    }

    private JavaScriptObject toJsonArray(Collection<Record> value) {
        JsArray<JsPropertyBag> array = JsArray.<JsPropertyBag>createArray().cast();
        for(Record record : value) {
            array.push(toBag(record));
        }
        return array;
    }
}
