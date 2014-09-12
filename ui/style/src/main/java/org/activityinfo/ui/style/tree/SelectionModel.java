package org.activityinfo.ui.style.tree;

public interface SelectionModel {

    boolean isSelected(String nodeKey);

    void select(String nodeKey);

    void addChangeListener(SelectionChangeListener selectionModel);

    void removeChangeListener(SelectionChangeListener selectionModel);
}
