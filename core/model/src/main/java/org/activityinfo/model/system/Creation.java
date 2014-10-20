package org.activityinfo.model.system;

import org.activityinfo.model.annotation.RecordBean;
import org.activityinfo.model.type.time.Instant;

/**
 * Metadata describing the creation of a resource
 */
@RecordBean(classId = "_creation")
public class Creation {

    private Instant creationTime;
    private String creatorName;
    private String creatorEmail;

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }
}
