package org.activityinfo.model.table;

import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.IsRecord;

import java.util.List;

/**
 * Selects the source of a field
 */
public interface FieldSelector extends IsRecord {


    /**
     * Selects matching nodes from the FormTree
     */
    List<FormTree.Node> select(FormTree tree);
}
