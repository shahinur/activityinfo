package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import org.activityinfo.legacy.shared.command.result.ResourceResult;
import org.activityinfo.model.form.FormClass;

import javax.annotation.Nullable;

class FormClassDeserializer implements Function<ResourceResult, FormClass> {

    @Nullable
    @Override
    public FormClass apply(@Nullable ResourceResult result) {
        return FormClass.fromResource(result.parseResource());
    }
}
