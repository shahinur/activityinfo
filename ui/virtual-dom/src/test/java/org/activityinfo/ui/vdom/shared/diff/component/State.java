package org.activityinfo.ui.vdom.shared.diff.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class State {

    private List<String> names = new ArrayList<>();

    public StateListener listener;

    public State(String... names) {
        this.names = Arrays.asList(names);
    }

    public void addListener(StateListener listener) {
        assert this.listener == null : "listener already attached";
        this.listener = listener;
    }

    public void removeListener(StateListener listener) {
        assert this.listener == listener : "lister not attached";
        this.listener = null;
    }

    public void update(int index, String name) {
        names.set(index, name);
        if(this.listener != null ) {
            listener.onChanged();
        }
    }

    public List<String> getNames() {
        return names;
    }
}
