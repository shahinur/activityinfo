package org.activityinfo.ui.client.component.form.field.suggest;

import com.google.gwt.user.client.ui.SuggestOracle;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.core.shared.form.FormInstanceLabeler;

public class InstanceSuggestion implements SuggestOracle.Suggestion {

    private final ResourceId id;
    private final String label;

    public InstanceSuggestion(ResourceId id, String label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public String getDisplayString() {
        return getReplacementString();
    }

    @Override
    public String getReplacementString() {
        return label;
    }

    public ResourceId getInstanceId() {
        return id;
    }
}
