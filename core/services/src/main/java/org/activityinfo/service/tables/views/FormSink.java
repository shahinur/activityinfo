package org.activityinfo.service.tables.views;


import org.activityinfo.model.form.FormEvalContext;

/**
 * An object which can receive a stream of {@code Resource}s
 */
public interface FormSink {

    void accept(FormEvalContext resource);

}
