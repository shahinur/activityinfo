package org.activityinfo.legacy.shared.command;

import org.activityinfo.legacy.shared.model.ActivityDTO;

/**
 * Retrieves a FormClass (ActivityDTO for now)
 *
 */
public class GetFormViewModel implements Command<ActivityDTO> {

    private int activityId;

    public GetFormViewModel(int activityId) {
        this.activityId = activityId;
    }

    public GetFormViewModel() {
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }
}
