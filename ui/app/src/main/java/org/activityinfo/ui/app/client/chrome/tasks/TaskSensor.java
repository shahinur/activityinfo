package org.activityinfo.ui.app.client.chrome.tasks;

import com.google.gwt.user.client.Timer;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.request.FetchTaskRequest;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;

/**
 * Polls the server for changes in task status.
 */
public class TaskSensor implements StoreChangeListener {

    private static final int TIMER_INTERVAL = 1000;

    private final Application application;
    private Timer timer;

    public TaskSensor(Application application) {
        this.application = application;
    }

    public void start() {
        this.application.getTaskStore().addChangeListener(this);
        this.application.getRequestDispatcher().execute(new FetchTaskRequest());
    }

    @Override
    public void onStoreChanged(Store store) {
        boolean runningTasks = application.getTaskStore().getRunningCount() > 0;
        if(runningTasks && !isTimerRunning()) {
            startTimer();
        } else if(!runningTasks && isTimerRunning()) {
            stopTimer();
        }
    }

    private void startTimer() {
        if(timer == null) {
            timer = new Timer() {
                @Override
                public void run() {
                    if(application.getConnectivityStore().isOnline()) {
                        application.getRequestDispatcher().execute(new FetchTaskRequest());
                    }
                }
            };
        }
        if(!timer.isRunning()) {
            timer.scheduleRepeating(TIMER_INTERVAL);
        }
    }

    private void stopTimer() {
        if(timer != null) {
            timer.cancel();
        }
    }

    private boolean isTimerRunning() {
        return timer != null && timer.isRunning();
    }
}
