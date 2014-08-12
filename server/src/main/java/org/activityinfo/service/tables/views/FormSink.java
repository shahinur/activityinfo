package org.activityinfo.service.tables.views;


import org.activityinfo.core.shared.expr.eval.FormEvalContext;
import org.activityinfo.model.resource.Resource;

/**
 * An object which can receive a stream of {@code Resource}s
 */
public interface FormSink {

    void accept(FormEvalContext resource);

}
