package org.activityinfo.ui.client.component.form.model;


import org.activityinfo.model.resource.ResourceId;

/**
 * Marker interface for view models of fields. The view
 * model should contain any information required to render or
 * populate the form; we should not be fetching anything asynchronously
 * during the form rendering process.
 */
public interface FieldViewModel {

    ResourceId getFieldId();
}
