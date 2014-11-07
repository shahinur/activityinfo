package org.activityinfo.legacy.shared.command;

import org.activityinfo.legacy.shared.model.ActivityDTO;

/**
 * Retrieves a FormClass (ActivityDTO for now)
 *
 */
public class GetActivity implements Command<ActivityDTO> {

    private int activityId;

    public GetActivity(int activityId) {
        this.activityId = activityId;
    }

    public GetActivity() {
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }
}
