package org.activityinfo.service.tasks;

import org.activityinfo.model.record.IsRecord;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.Records;

/**
 * Describes the status of a background task being run on behalf of the user.
 */
public class UserTask implements IsRecord {

    private String id;
    private double timeStarted;
    private double timeCompleted;
    private Record taskModel;
    private UserTaskStatus status;
    private String errorMessage;

    public String getId() {
        return id;
    }

    public double getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(double timeStarted) {
        this.timeStarted = timeStarted;
    }

    public UserTaskStatus getStatus() {
        return status;
    }

    public void setStatus(UserTaskStatus status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Record getTaskModel() {
        return taskModel;
    }

    public void setTaskModel(Record taskModel) {
        this.taskModel = taskModel;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public double getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(double timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    @Override
    public Record asRecord() {
        return Records.builder()
            .set("id", id)
            .set("timeStarted", timeStarted)
            .set("status", status.name())
            .set("taskModel", taskModel)
            .set("errorMessage", errorMessage)
            .build();
    }

    public static UserTask fromRecord(Record record) {
        UserTask task = new UserTask();
        task.setId(record.getString("id"));
        task.setTimeStarted(record.getDouble("timeStarted"));
        task.setStatus(UserTaskStatus.valueOf(record.getString("status")));
        task.setTaskModel(record.isRecord("taskModel"));
        task.setErrorMessage(record.isString("errorMessage"));
        return task;
    }

    @Override
    public String toString() {
        return "UserTask{" +
            "id='" + id + '\'' +
            ", timeStarted=" + timeStarted +
            ", status=" + status +
            '}';
    }
}
