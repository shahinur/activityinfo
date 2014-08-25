package org.activityinfo.ui.store.remote.client.resource;

import com.google.common.base.Function;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.UpdateResult;

public class UpdateResultParser implements Function<Response, UpdateResult> {
    @Override
    public UpdateResult apply(Response input) {
        return parse(input);
    }

    public static UpdateResult parse(Response input) {
        JavaScriptObject object = JsonUtils.safeEval(input.getText());
        String status = getString(object, "status");
        switch(status) {
            case "COMMITTED":
                return UpdateResult.committed(
                        ResourceId.valueOf(getString(object, "id")),
                        Long.parseLong(getString(object, "newVersion")));
            case "PENDING":
                return UpdateResult.pending();
        }
        throw new IllegalArgumentException("unknown status: " + status);
    }

    private static native String getString(JavaScriptObject object, String key) /*-{
      return object[key];
    }-*/;
}
