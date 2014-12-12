package org.activityinfo.model.resource;


import org.activityinfo.model.record.Record;

import static org.activityinfo.model.record.Records.deepEquals;

/**
 * ActivityInfo is, at it's core, concerned with the management of users' `Resource`s.
 *
 * Our users need to manage a variety of `Resource`s, from form definitions (`FormClass`es) to results
 * submitted by other users (`FormInstance`s), to a diverse set of report models, access control rules, etc.
 *
 * All these "things" we will consider to be "resources", which have a stable, globally unique
 * identity, a version, and owner.
 *
 * The owner of a resource is another may be an individual user, a group of users, or another resource from
 * which access control rules will be inherited. Users and user groups may be also be modelled as
 * resources, so we can say that every `Resource` is owned by another `Resource`; except the root resource.
 *
 * As a `Resource` can have exactly one owner, resources form a tree structure that we will
 * present to the user as a sort-of folder structure.
 *
 * Resources have zero or more, named properties.
 *
 */
public final class Resource {

    private ResourceId id;
    private ResourceId ownerId;
    private long version;
    private Record value;

    Resource() {
    }

    public Resource copy() {
        Resource copy = new Resource();
        copy.id = this.id;
        copy.ownerId = this.ownerId;
        copy.version = this.version;
        copy.value = this.value;
        return copy;
    }

    /**
     * Returns the Resource's globally-unique ID.
     *
     */
    public ResourceId getId() {
        return id;
    }

    public Resource setId(ResourceId id) {
        if(id == null) {
            throw new NullPointerException("id");
        }
        this.id = id;
        return this;
    }

    /**
     * Returns the id of the {@code Resource} which owns this {@code Resource}
     *
     */
    public ResourceId getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the owner of this {@code Resource}
     *
     * @param owningResourceId the id of the {@code Resource} that owns this
     *     resource
     */
    public Resource setOwnerId(ResourceId owningResourceId) {
        if(owningResourceId == null) {
            throw new NullPointerException("owner");
        }
        this.ownerId = owningResourceId;
        return this;
    }

    public ResourceId getClassId() {
        return value.getClassId();
    }

    public long getVersion() {
        return version;
    }

    public Resource setVersion(long version) {
        this.version = version;
        return this;
    }

    public Resource setVersionInt(int version) {
        this.version = version;
        return this;
    }

    public Record getValue() {
        return value;
    }

    public Resource setValue(Record value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "{" + id.asString() + ": " + value + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        if (version != resource.version) return false;
        if (id != null ? !id.equals(resource.id) : resource.id != null) return false;
        if (ownerId != null ? !ownerId.equals(resource.ownerId) : resource.ownerId != null) return false;
        if (value != null ? resource.value == null || !deepEquals(value, resource.value) : resource.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (ownerId != null ? ownerId.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

}
