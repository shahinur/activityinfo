package org.activityinfo.ui.app.client.dialogs;
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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.app.client.chrome.FailureDescription;
import org.activityinfo.ui.style.*;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.t;

/**
 * @author yuriyz on 9/23/14.
 */
public class ConfirmDialog extends VComponent {
    /**
     * Maintain a single instance of this dialog, as there is by definition never more than one
     * modal dialog shown at a time.
     */
    private static ConfirmDialog INSTANCE = null;

    /**
     * @author yuriyz on 4/8/14.
     */
    public static class Messages {
        private String titleText;
        private String messageText;
        private String primaryButtonText;

        public Messages(String titleText, String messageText, String primaryButtonText) {
            this.titleText = titleText;
            this.messageText = messageText;
            this.primaryButtonText = primaryButtonText;
        }

        public String getTitleText() {
            return titleText;
        }

        public String getMessageText() {
            return messageText;
        }

        public String getPrimaryButtonText() {
            return primaryButtonText;
        }
    }

    /**
     * @author yuriyz on 4/7/14.
     */
    public static interface Action {

        Messages getConfirmationMessages();

        Messages getProgressMessages();

        Messages getFailureMessages();

        ButtonStyle getPrimaryButtonStyle();

        /**
         *
         * Invoked when the user has confirmed the action, or is retrying.
         */
        Promise<Void> execute();

        /**
         * Invoked when the action completes successfully
         */
        void onComplete();
    }

    public static enum State {
        CONFIRM, PROGRESS, FAILED
    }

    private final Modal dialog = new Modal();

    // store
    private State state;
    private Throwable throwable = null;
    private Action action;

    private ConfirmDialog() {
    }

    private Messages getMessages() {
        switch (state) {
            case CONFIRM:
                return action.getConfirmationMessages();
            case FAILED:
                return action.getFailureMessages();
            case PROGRESS:
                return action.getProgressMessages();
        }
        throw new IllegalArgumentException("State is not supported, state: " + state);
    }

    @Override
    protected VTree render() {
        Messages messages = getMessages();
        Button cancelButton = new Button(ButtonStyle.DEFAULT, t(I18N.CONSTANTS.cancel()));
        cancelButton.setClickHandler(new org.activityinfo.ui.style.ClickHandler() {
            @Override
            public void onClicked() {
                dialog.setVisible(false);
            }
        });

        Button okButton = new Button(action.getPrimaryButtonStyle(), t(messages.getPrimaryButtonText()));
        okButton.setClickHandler(new org.activityinfo.ui.style.ClickHandler() {
            @Override
            public void onClicked() {
                tryAction();
            }
        });

        dialog.setTitle(t(messages.getTitleText()));
        if (state == State.FAILED) {
            String messageText = messages.getMessageText();
            if (throwable != null) {
                messageText += " " + FailureDescription.of(throwable).getDescription();
            }
            dialog.setBody(new Alert(AlertStyle.DANGER, t(messageText)));
        } else {
            dialog.setBody(t(messages.getMessageText()));
        }
        dialog.setFooter(cancelButton, okButton);
        return dialog;
    }

    /**
     * Shows the confirmation dialog
     */
    public static ConfirmDialog confirm(Action action) {
        if(INSTANCE == null) {
            INSTANCE = new ConfirmDialog();
        }
        INSTANCE.action = action;
        INSTANCE.throwable = null;
        INSTANCE.state = State.CONFIRM;
        return INSTANCE;
    }

    public void setVisible(boolean visible) {
        dialog.setVisible(visible);
    }

    private void tryAction() {
        state = State.PROGRESS;
        throwable = null;
        action.execute().then(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                showFailureDelayed(caught);
            }

            @Override
            public void onSuccess(Void result) {
                ConfirmDialog.this.dialog.setVisible(false);
                action.onComplete();
            }
        });
        refresh();
    }

    private void showFailureDelayed(final Throwable caught) {
        // Show failure message only after a short fixed delay to ensure that
        // the progress stage is displayed. Otherwise if we have a synchronous error, clicking
        // the retry button will look like it's not working.
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                state = State.FAILED;
                throwable = caught;
                refresh();
                return false;
            }
        }, 500);
    }
}
