package org.activityinfo.ui.app.client.chrome.tasks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.client.StatusCodeException;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.service.tasks.UserTaskStatus;
import org.activityinfo.ui.app.client.action.NotificationHandler;
import org.activityinfo.ui.app.client.action.RemoteUpdateHandler;
import org.activityinfo.ui.app.client.page.folder.task.Task;
import org.activityinfo.ui.app.client.request.FetchTaskRequest;
import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.app.client.request.StartTask;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Maintains a list of tasks running in the background on behalf of the current user.
 */
public class TaskStore extends AbstractStore implements RemoteUpdateHandler, NotificationHandler {

    private Map<String, UserTask> tasks = Maps.newHashMap();

    //
    private List<Task> taskList = Lists.newArrayList();

    private Set<String> newlyCompleted = Sets.newHashSet();

    public TaskStore(Dispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void requestStarted(Request request) {
        if(request instanceof StartTask) {
            StartTask startRequest = (StartTask) request;

            UserTask task = new UserTask();
            task.setId(((StartTask) request).getTaskId());
            task.setTaskModel(startRequest.getTaskModelRecord());
            task.setStatus(UserTaskStatus.RUNNING);
            task.setTimeStarted(new Date().getTime());
            tasks.put(task.getId(), task);
            fireChange();
        }
    }

    @Override
    public void requestFailed(Request request, Throwable e) {
        if(request instanceof StartTask) {
            StartTask startRequest = (StartTask) request;

            // Create UserTask
            if(tasks.containsKey(startRequest.getTaskId())) {
                UserTask task = tasks.get(startRequest.getTaskId());
                task.setStatus(UserTaskStatus.FAILED);
                task.setErrorMessage(errorMessage(e));
                fireChange();
            }
        }
    }

    private String errorMessage(Throwable e) {
        if(e instanceof StatusCodeException) {
            int code = ((StatusCodeException) e).getStatusCode();
            switch(code) {
                case 400:
                    return I18N.CONSTANTS.permissionDeniedExportFailure();
                default:
                    return I18N.CONSTANTS.serverError();
            }
        } else {
            return I18N.CONSTANTS.connectionProblem();
        }
    }

    @Override
    public <R> void processUpdate(Request<R> request, R response) {
        if(request instanceof FetchTaskRequest) {
            List<UserTask> result = (List<UserTask>) response;
            boolean changed = false;
            for(UserTask updatedTask : result) {
                UserTask task = tasks.get(updatedTask.getId());
                // consider this task newly complete if we've previously seen this task
                // running
                if(task != null &&
                    task.getStatus() != UserTaskStatus.COMPLETE &&
                    updatedTask.getStatus() == UserTaskStatus.COMPLETE) {
                    newlyCompleted.add(updatedTask.getId());
                    changed = true;
                }
                if(updateTask(task, updatedTask)) {
                    changed = true;
                }
            }
            if(changed) {
                fireChange();
            }
        }
    }

    private boolean updateTask(UserTask task, UserTask updatedTask) {
        if(task.getStatus() != updatedTask.getStatus()) {
            task.setStatus(updatedTask.getStatus());
            return true;
        } else {
            return false;
        }
    }

    public List<UserTask> getTasks() {
        return Lists.newArrayList(tasks.values());
    }

    public int getRunningCount() {
        int count = 0;
        for(UserTask task : tasks.values()) {
            if(task.getStatus() == UserTaskStatus.RUNNING) {
                count++;
            }
        }
        return count;
    }

    public boolean isNewlyCompleted(UserTask task) {
        return newlyCompleted.contains(task.getId());
    }

    public int getNewCount() {
        int count = 0;
        for(UserTask task : tasks.values()) {
            if(newlyCompleted.contains(task.getId())) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void notificationAcknowledged(String id) {
        if(newlyCompleted.remove(id)) {
            fireChange();
        }
    }
}
