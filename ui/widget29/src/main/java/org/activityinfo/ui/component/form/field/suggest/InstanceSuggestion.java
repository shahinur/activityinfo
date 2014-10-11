package org.activityinfo.ui.component.form.field.suggest;

import com.google.gwt.user.client.ui.SuggestOracle;
import org.activityinfo.model.resource.ResourceId;

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
