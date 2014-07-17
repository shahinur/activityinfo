package org.activityinfo.legacy.shared.impl;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.legacy.shared.command.GetFormViewModel;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;


public class GetFormViewModelHandler implements CommandHandlerAsync<GetFormViewModel, ActivityDTO> {

    @Override
    public void execute(final GetFormViewModel command, ExecutionContext context, final AsyncCallback<ActivityDTO> callback) {
        context.execute(new GetSchema(), new AsyncCallback<SchemaDTO>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(SchemaDTO schema) {
                ActivityDTO activity = schema.getActivityById(command.getActivityId());
                callback.onSuccess(activity);
            }
        });
    }
}
