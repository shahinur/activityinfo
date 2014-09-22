package org.activityinfo.ui.store.remote.client.resource;

import com.google.common.base.Function;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import org.activityinfo.model.resource.PropertyBag;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

import java.util.ArrayList;
import java.util.List;

public class ResourceParser implements Function<Response, Resource> {

    @Override
    public Resource apply(Response input) {
        return parse(input.getText());
    }

    public static Resource parse(String json) {
        JavaScriptObject object = JsonUtils.safeEval(json);
        return parseResource(object);
    }

    public static Record parseRecord(String json) {
        JavaScriptObject object = JsonUtils.safeEval(json);
        return parseRecord(object);
    }

    public static Resource parseResource(JavaScriptObject object) {
        Resource resource = Resources.createResource();
        parseResourceProperties(resource, object);
        return resource;
    }

    public static Record parseRecord(JavaScriptObject object) {
        Record record = new Record();
        parseRecordProperties(record, object);
        return record;
    }

    private static List<Record> parseArray(JsArray array) {
        List<Record> list = new ArrayList<>();
        for(int i=0;i!=array.length();++i) {
            list.add(parseRecord(array.get(i)));
        }
        return list;
    }

    private static native void parseResourceProperties(Resource resource, JavaScriptObject object) /*-{
        for(var key in object) {
            if(object.hasOwnProperty(key)) {
                if(key === '@id') {
                    resource.@org.activityinfo.model.resource.Resource::setId(Lorg/activityinfo/model/resource/ResourceId;)(
                        @org.activityinfo.model.resource.ResourceId::valueOf(Ljava/lang/String;)(object['@id']));
                } else if(key === '@owner') {
                    resource.@org.activityinfo.model.resource.Resource::setOwnerId(Lorg/activityinfo/model/resource/ResourceId;)(
                        @org.activityinfo.model.resource.ResourceId::valueOf(Ljava/lang/String;)(object['@owner']));

                } else if(key === '@version') {
                    resource.@org.activityinfo.model.resource.Resource::setVersionInt(I)(object['@version'])

                } else {
                    @org.activityinfo.ui.store.remote.client.resource.ResourceParser::setProperty(Lorg/activityinfo/model/resource/PropertyBag;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(resource, key, object);
                }
            }
        }
    }-*/;

    private static native void parseRecordProperties(PropertyBag bag, JavaScriptObject object) /*-{
        for(var key in object) {
            if (object.hasOwnProperty(key)) {  // 64 = @
                @org.activityinfo.ui.store.remote.client.resource.ResourceParser::setProperty(Lorg/activityinfo/model/resource/PropertyBag;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(bag, key, object);
            }
        }
    }-*/;

    private static native void setProperty(PropertyBag bag, String key, JavaScriptObject object) /*-{
        var value = object[key];
        if(typeof value === "string") {
            bag.@org.activityinfo.model.resource.PropertyBag::set(Ljava/lang/String;Ljava/lang/String;)(key, value);
        } else if(typeof value === "number") {
            bag.@org.activityinfo.model.resource.PropertyBag::set(Ljava/lang/String;D)(key, value);
        } else if(typeof value === "boolean") {
            bag.@org.activityinfo.model.resource.PropertyBag::set(Ljava/lang/String;Z)(key, value);
        } else if(typeof value === "object") {
            if(value instanceof Array || value instanceof $wnd.Array) {
                var list = @org.activityinfo.ui.store.remote.client.resource.ResourceParser::parseArray(Lcom/google/gwt/core/client/JsArray;)(value);
                bag.@org.activityinfo.model.resource.PropertyBag::set(Ljava/lang/String;Ljava/util/List;)(key, list);
            } else {
                var record = @org.activityinfo.ui.store.remote.client.resource.ResourceParser::parseRecord(Lcom/google/gwt/core/client/JavaScriptObject;)(value);
                bag.@org.activityinfo.model.resource.PropertyBag::set(Ljava/lang/String;Lorg/activityinfo/model/resource/Record;)(key, record);
            }
        }
    }-*/;



}
