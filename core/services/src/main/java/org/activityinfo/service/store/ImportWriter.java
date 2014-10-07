package org.activityinfo.service.store;

import org.activityinfo.model.form.FormClass;

public interface ImportWriter {

    InstanceWriter createFormClass(FormClass formClass);

}
