package org.activityinfo.ui.app.client.chrome.tasks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.service.tasks.UserTaskStatus;
import org.activityinfo.ui.app.client.action.RemoteUpdateHandler;
import org.activityinfo.ui.app.client.request.ImportRequest;
import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;

import java.util.List;
import java.util.Map;

/**
 * Maintains a list of tasks running in the background on behalf of the current user.
 */
public class TaskStore extends AbstractStore implements RemoteUpdateHandler {

    private Map<String, UserTask> tasks = Maps.newHashMap();

    public TaskStore(Dispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void requestStarted(Request request) {

    }

    @Override
    public void requestFailed(Request request, Exception e) {

    }

    @Override
    public <R> void processUpdate(Request<R> request, R response) {
        if(request instanceof ImportRequest) {
            UserTask task = (UserTask) response;
            tasks.put(task.getId(), task);
            fireChange();
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
}
