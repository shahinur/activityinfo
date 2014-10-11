package org.activityinfo.ui.component.importDialog.mapping;

import com.google.gwt.core.client.GWT;
import org.activityinfo.ui.style.BaseStyles;

/**
 * Placeholder until we have a better way to integrate LESS and gwt styles
 */
//@Source("ColumnMapping.less")
public class ColumnMappingStyles {

    public static final ColumnMappingStyles INSTANCE = GWT.create(ColumnMappingStyles.class);

    public String grid() { return BaseStyles.CM_DATAGRID.getClassNames(); }

    public String sourceColumnHeader() { return BaseStyles.SOURCE_COLUMN.getClassNames(); }

    public String mappingHeader() { return BaseStyles.MAPPING.getClassNames(); }

    public String stateIgnored() { return BaseStyles.STATE_IGNORED.getClassNames(); }

    public String stateBound() { return BaseStyles.STATE_BOUND.getClassNames(); }

    public String stateUnset() { return BaseStyles.STATE_UNSET.getClassNames(); }

    public String selected() { return BaseStyles.SELECTED.getClassNames(); }

    public String fieldSelector() { return BaseStyles.CM_FIELD_SELECTOR.getClassNames(); }

    public String incomplete() { return BaseStyles.INCOMPLETE.getClassNames(); }

    public String typeMatched() { return BaseStyles.TYPE_MATCHED.getClassNames(); }

    public String typeNotMatched() { return BaseStyles.TYPE_NOT_MATCHED.getClassNames(); }

}
