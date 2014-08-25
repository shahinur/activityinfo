package org.activityinfo.service.store;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Preconditions;
import org.activityinfo.model.resource.ResourceId;

public final class UpdateResult {

    private ResourceId resourceId;

    private CommitStatus status;

    private long newVersion;

    @JsonCreator
    private UpdateResult() {
    }

    private UpdateResult(CommitStatus status,
                         ResourceId resourceId,
                         long newVersion) {
        this.status = status;
        this.resourceId = resourceId;
        this.newVersion = newVersion;
    }

    private UpdateResult(CommitStatus status) {
        this.status = status;
    }

    public static UpdateResult committed(ResourceId id, long newVersion) {
        Preconditions.checkNotNull(id, "id");

        return new UpdateResult(CommitStatus.COMMITTED, id, newVersion);
    }

    public static UpdateResult rejected() {
        return new UpdateResult(CommitStatus.REJECTED);
    }

    public static UpdateResult pending() {
        return new UpdateResult(CommitStatus.PENDING);
    }

    public CommitStatus getStatus() {
        return status;
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public long getNewVersion() {
        return newVersion;
    }

    @Override
    public String toString() {
        if(status == CommitStatus.COMMITTED) {
            return "<Committed as version " + newVersion + ">";
        } else {
            return "<" + status.name() + ">";
        }
    }

}
