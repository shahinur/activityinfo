package org.activityinfo.service.blob;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Constructs the policy document that governs what the user may upload
 *
 * @see <a href="https://developers.google.com/storage/docs/reference-methods?csw=1#policydocument">Policy Document</a>
 */
public class GcsPolicyBuilder {


    private final JsonArray conditions;
    private final JsonObject document;

    public GcsPolicyBuilder() {

        conditions = new JsonArray();
        document = new JsonObject();
        document.add("conditions", conditions);
    }

    public GcsPolicyBuilder expiresAfter(Duration duration) {
        DateTime now = new DateTime();
        DateTime expiration = now.plus(duration);
        document.addProperty("expiration", expiration.toString(ISODateTimeFormat.dateTime()));
        return this;
    }

    /**
     * Defines a condition that the key of the object to be uploaded is equal to {@code name}
     *
     */
    public GcsPolicyBuilder keyMustEqual(String name) {
        return addExactCondition("key", name);
    }

    public GcsPolicyBuilder bucketNameMustEqual(String name) {
        return addExactCondition("bucket", name);
    }

    public GcsPolicyBuilder contentTypeMustStartWith(String contentTypePrefix) {
        return addStartsWithCondition("Content-Type", contentTypePrefix);
    }

    public GcsPolicyBuilder contentLengthMustBeBetween(long min, long max) {
       // ["content-length-range", <min_range>, <max_range>].
        JsonArray condition = new JsonArray();
        condition.add(new JsonPrimitive("content-length-range"));
        condition.add(new JsonPrimitive(min));
        condition.add(new JsonPrimitive(max));
        conditions.add(condition);
        return this;
    }

    private GcsPolicyBuilder addExactCondition(String field, String name) {
        JsonObject condition = new JsonObject();
        condition.addProperty(field, name);
        conditions.add(condition);
        return this;
    }

    private GcsPolicyBuilder addStartsWithCondition(String field, String prefix) {
        JsonArray condition = new JsonArray();
        condition.add(new JsonPrimitive("starts-with"));
        condition.add(new JsonPrimitive(field));
        condition.add(new JsonPrimitive(prefix));
        conditions.add(condition);
        return this;
    }

    @Override
    public String toString() {
        return document.toString();
    }

    public byte[] toJson() {
        return document.toString().getBytes(Charsets.UTF_8);
    }
}
