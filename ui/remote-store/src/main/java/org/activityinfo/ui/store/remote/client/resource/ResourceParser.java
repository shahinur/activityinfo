package org.activityinfo.ui.store.remote.client.resource;

import com.google.common.base.Function;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import org.activityinfo.model.resource.PropertyBag;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;

public class ResourceParser implements Function<Response, Resource> {

    @Override
    public Resource apply(Response input) {
        return parse(input.getText());
    }

    public static Resource parse(String json) {
        JavaScriptObject object = JsonUtils.safeEval(json);
        return parseResource(object);
    }

    public static native Resource parseResource(JavaScriptObject object) /*-{
      var resource = @org.activityinfo.model.resource.Resources::createResource()();
      resource.@org.activityinfo.model.resource.Resource::setId(Lorg/activityinfo/model/resource/ResourceId;)(
          @org.activityinfo.model.resource.ResourceId::valueOf(Ljava/lang/String;)(object['@id']));
      resource.@org.activityinfo.model.resource.Resource::setOwnerId(Lorg/activityinfo/model/resource/ResourceId;)(
          @org.activityinfo.model.resource.ResourceId::valueOf(Ljava/lang/String;)(object['@owner']));

      @org.activityinfo.ui.store.remote.client.resource.ResourceParser::parseProperties(Lorg/activityinfo/model/resource/PropertyBag;Lcom/google/gwt/core/client/JavaScriptObject;)(resource, object);

      return resource;
    }-*/;

    private static Record parseRecord(JavaScriptObject object) {
        Record record = new Record();
        parseProperties(record, object);
        return record;
    }

    private static native void parseProperties(PropertyBag bag, JavaScriptObject object) /*-{
      for(var key in object) {
        if(object.hasOwnProperty(key) && key.charCodeAt(0) != 64) {  // 64 = @
          var value = object[key];
          if(typeof value === "string") {
            bag.@org.activityinfo.model.resource.PropertyBag::set(Ljava/lang/String;Ljava/lang/String;)(key, value);
          } else if(typeof value === "number") {
            bag.@org.activityinfo.model.resource.PropertyBag::set(Ljava/lang/String;D)(key, value);
          } else if(typeof value === "boolean") {
            bag.@org.activityinfo.model.resource.PropertyBag::set(Ljava/lang/String;Z)(key, value);
          } else if(typeof value === "object") {
            var record = @org.activityinfo.ui.store.remote.client.resource.ResourceParser::parseRecord(Lcom/google/gwt/core/client/JavaScriptObject;)(value);
            bag.@org.activityinfo.model.resource.PropertyBag::set(Ljava/lang/String;Lorg/activityinfo/model/resource/Record;)(key, record);
          }
        }
      }

    }-*/;

}
