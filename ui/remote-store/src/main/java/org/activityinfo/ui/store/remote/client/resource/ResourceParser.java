package org.activityinfo.ui.store.remote.client.resource;

import com.google.common.base.Function;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.RecordJsoImpl;
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
        return new RecordJsoImpl(new JSONObject(object));
    }

    public static Resource parseResource(JavaScriptObject object) {
        Resource resource = Resources.createResource();
        parseResourceProperties(resource, object);
        resource.setValue(new RecordJsoImpl(new JSONObject(object)));
        return resource;
    }

    public static Record parseRecord(JavaScriptObject object) {
        return new RecordJsoImpl(new JSONObject(object));
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

                }
            }
        }
    }-*/;

}
