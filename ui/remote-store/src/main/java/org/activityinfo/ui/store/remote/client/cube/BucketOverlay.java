package org.activityinfo.ui.store.remote.client.cube;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import org.activityinfo.model.table.Bucket;

import java.util.List;

public final class BucketOverlay extends JavaScriptObject implements Bucket {

    protected BucketOverlay() {
    }

    @Override
    public native double getValue() /*-{
        return +(this.value);
    }-*/;

    @Override
    public native String getDimensionValue(String measureId) /*-{
        return this[value];
    }-*/;

    public static List<Bucket> parse(String json) {
        JsArray<BucketOverlay> bucketArray = JsonUtils.safeEval(json);
        List<Bucket> buckets = Lists.newArrayList();
        for(int i=0;i!=bucketArray.length();++i) {
            buckets.add(bucketArray.get(i));
        }
        return buckets;
    }
}
