package org.activityinfo.ui.style.tree;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SingleSelectionModel implements SelectionModel {

    private String selectedKey = null;
    private Set<SelectionChangeListener> listeners;


    @Override
    public boolean isSelected(String nodeKey) {
        return nodeKey.equals(selectedKey);
    }

    @Override
    public void select(String nodeKey) {
        if(!Objects.equals(selectedKey, nodeKey)) {
            selectedKey = nodeKey;
            fireChange();
        }
    }

    private void fireChange() {
        if(listeners != null) {
            for(SelectionChangeListener listener : listeners) {
                listener.onSelectionChanged(this);
            }
        }
    }

    @Override
    public void addChangeListener(SelectionChangeListener selectionModel) {
        if(listeners == null) {
            listeners = new HashSet<>();
        }
        listeners.add(selectionModel);
    }

    @Override
    public void removeChangeListener(SelectionChangeListener selectionModel) {
        listeners.remove(selectionModel);
    }

    public String getSelectedKey() {
        return selectedKey;
    }

    public boolean hasSelection() {
        return selectedKey != null;
    }
}
