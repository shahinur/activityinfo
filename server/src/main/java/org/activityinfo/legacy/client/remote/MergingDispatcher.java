package org.activityinfo.legacy.client.remote;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.api.client.util.Maps;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.exception.CommandTimeoutException;
import org.activityinfo.legacy.shared.exception.RetryCountExceedsLimitException;
import org.activityinfo.legacy.shared.util.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An implementation of {@link org.activityinfo.legacy.client.Dispatcher} that
 * merges equivalent commands independently executed during the same event loop.
 * <p/>
 * <p/>
 * This is critical when we have multiple, independent components that all
 * request GetSchema() or other basic information as they are loaded.
 */
public class MergingDispatcher extends AbstractDispatcher {

    /**
     * In case CommandTimeOutException occurs (may happen if Advisory lock wasn't obtains during configurable time),
     * dispatcher automatically retry command execution. With this constant it's possible to limit number of retry calls.
     */
    private static final int RETRY_COUNT_LIMIT_ON_TIMEOUT = 3;

    private Dispatcher dispatcher;

    /**
     * Pending commands have been requested but not yet sent to the server
     */
    private List<CommandRequest> pendingCommands = new ArrayList<CommandRequest>();

    /**
     * Executing commands have been sent to the server but for which we have not
     * yet received a response.
     */
    private List<CommandRequest> executingCommands = new ArrayList<CommandRequest>();

    private Map<CommandRequest, Integer> retries = Maps.newHashMap();

    @Inject
    public MergingDispatcher(Dispatcher dispatcher, Scheduler scheduler) {
        this.dispatcher = dispatcher;

        scheduler.scheduleFinally(new RepeatingCommand() {

            @Override
            public boolean execute() {
                try {
                    if (!pendingCommands.isEmpty()) {
                        dispatchPending();
                    }
                } catch (Exception e) {
                    Log.error("Uncaught exception while dispatching in MergingDispatcher", e);
                }
                return true;
            }
        });
    }

    @Override
    public <T extends CommandResult> void execute(Command<T> command, AsyncCallback<T> callback) {

        CommandRequest request = new CommandRequest(command, callback);

        if (Commands.hasMutatingCommand(command)) {
            // mutating requests get queued immediately, don't try to merge them
            // into any pending/executing commands, it wouldn't be correct

            queue(request);
        } else {
            if (!request.mergeSuccessfulInto(pendingCommands) && !request.mergeSuccessfulInto(executingCommands)) {

                queue(request);

                Log.debug("MergingDispatcher: Scheduled " + command.toString() + ", now " +
                        pendingCommands.size() + " command(s) pending");
            }
        }
    }

    private void queue(CommandRequest request) {
        pendingCommands.add(request);
    }

    private void dispatchPending() {
        Log.debug("MergingDispatcher: sending " + pendingCommands.size() + " to server.");

        final List<CommandRequest> sent = new ArrayList<CommandRequest>(pendingCommands);
        executingCommands.addAll(sent);
        pendingCommands.clear();

        if (!sent.isEmpty()) {
            for (final CommandRequest request : sent) {
                executeCommand(request);
            }
        }
    }

    private void executeCommand(final CommandRequest request) {
        dispatcher.execute(request.getCommand(), new AsyncCallback() {

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof CommandTimeoutException) {
                    // todo do we want to let client know about each retry or timeout or both? via exception ?
//                    request.onFailure(caught); // report about timeout
//                    request.onFailure(new RetryCommandException()); // report about retry

                    Log.debug("Request timed out, retring...");
                    retry(request);
                    return;
                }
                executingCommands.remove(request);
                request.onFailure(caught);
            }

            @Override
            public void onSuccess(Object result) {
                executingCommands.remove(request);
                request.onSuccess(result);
            }
        });
    }

    private void retry(final CommandRequest request) {
        try {
            incrementRetryCounter(request);
            executeCommand(request); // execute retry
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
            request.onFailure(e);
        }
    }

    /**
     * Increments current retry count and returns it.
     *
     * @param request command request
     * @return current retry count
     */
    private int incrementRetryCounter(CommandRequest request) {
        if (!retries.containsKey(request)) {
            retries.put(request, 1);
            return 1;
        }
        Integer count = retries.get(request);
        count++;
        if (count > RETRY_COUNT_LIMIT_ON_TIMEOUT) {
            throw new RetryCountExceedsLimitException(); // we don't want to continue retry cycles
        }
        retries.put(request, count);
        return count;
    }
}
