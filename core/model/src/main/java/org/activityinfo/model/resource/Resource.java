package org.activityinfo.model.resource;


import com.google.common.base.Joiner;

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
public final class Resource extends PropertyBag<Resource> {

    private ResourceId id;
    private ResourceId ownerId;
    private ResourceId workspaceId;
    private long version;

    Resource() {
    }

    public Resource copy() {
        Resource copy = new Resource();
        copy.id = this.id;
        copy.ownerId = this.ownerId;
        copy.getProperties().putAll(this.getProperties());
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

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "{" + id.asString() + ": " + Joiner.on(", ").withKeyValueSeparator("=").join(getProperties()) + "}";
    }
}
