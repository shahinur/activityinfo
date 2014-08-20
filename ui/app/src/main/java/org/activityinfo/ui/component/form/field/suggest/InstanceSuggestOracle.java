package org.activityinfo.ui.component.form.field.suggest;

import com.google.gwt.user.client.ui.SuggestOracle;
import org.activityinfo.io.importing.match.names.LatinPlaceNameScorer;
import org.activityinfo.model.table.InstanceLabelTable;

import java.util.ArrayList;
import java.util.List;

public class InstanceSuggestOracle extends SuggestOracle {

    private InstanceLabelTable instances;
    private LatinPlaceNameScorer scorer = new LatinPlaceNameScorer();

    public InstanceSuggestOracle(InstanceLabelTable range) {
        this.instances = range;
    }

    @Override
    public void requestSuggestions(Request request, Callback callback) {
        List<Suggestion> suggestions = new ArrayList<>();
        for(int i=0;i!=instances.getNumRows();++i) {
            if(scorer.score(request.getQuery(), instances.getLabel(i)) > 0.5) {
                suggestions.add(new InstanceSuggestion(instances.getId(i), instances.getLabel(i)));
            }
        }
        callback.onSuggestionsReady(request, new Response(suggestions));
    }
}
