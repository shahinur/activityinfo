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

import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.request.FetchResource;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import javax.annotation.Nullable;

/**
 * Renames resource label. Dialog identifies resource by id, resource must be in ResourceStore.
 *
 * @author yuriyz on 10/3/14.
 */
public class RenameResourceDialog extends VComponent {

    private final EditLabelDialog editLabelDialog = new EditLabelDialog();
    private final Application application;
    private final ResourceId resourceId;

    public RenameResourceDialog(Application application, ResourceId resourceId) {
        this.application = application;
        this.resourceId = resourceId;
        this.editLabelDialog.setOkClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                String newName = editLabelDialog.getInputControl().getValueAsString();
                onRename(newName);
            }
        });
    }

    private void onRename(final String newName) {
        if (application.getResourceStore().get(resourceId).isAvailable()) {
            rename(newName, application.getResourceStore().get(resourceId).get().getResource());

        } else {

            application.getRequestDispatcher().execute(new FetchResource(resourceId)).then(new Function<UserResource, Object>() {
                @Nullable
                @Override
                public Object apply(@Nullable UserResource input) {
                    rename(newName, input.getResource());
                    return null;
                }
            });
        }

    }

    private void rename(String newName, Resource resource) {
        RecordBuilder updated = Records.buildCopyOf(resource.getValue());
        updated.set(FolderClass.LABEL_FIELD_ID.asString(), newName);
        resource.setValue(updated.build());

        application.getRequestDispatcher().execute(new SaveRequest(resource)).then(new AsyncCallback<UpdateResult>() {
            @Override
            public void onFailure(Throwable caught) {
                editLabelDialog.failedToEditLabel();
            }

            @Override
            public void onSuccess(UpdateResult result) {
                editLabelDialog.setVisible(false);
            }
        });
    }

    @Override
    protected VTree render() {
        return editLabelDialog;
    }

    public void setLabel(String label) {
        editLabelDialog.setLabel(label);
    }

    public void setVisible(boolean visible) {
        editLabelDialog.setVisible(visible);
    }

    public VTree createLinkButton() {
        return editLabelDialog.createLinkButton();
    }
}
