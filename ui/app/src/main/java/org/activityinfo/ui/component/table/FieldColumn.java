package org.activityinfo.ui.component.table;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.model.legacy.Projection;
import org.activityinfo.model.legacy.criteria.Criteria;
import org.activityinfo.model.formTree.FieldPath;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.ui.component.table.renderer.RendererFactory;
import org.activityinfo.ui.component.table.renderer.ValueRenderer;

import java.util.List;

/**
 * Column that displays the value of a given field
 */
public class FieldColumn extends Column<Projection, String> {

    public static final String NON_BREAKING_SPACE = "\u00A0";
    private FormTree.Node node;
    private List<FieldPath> fieldPaths;
    private String header;
    private Criteria criteria;

    public FieldColumn(FormTree.Node node) {
        super(new TextCell());
        this.node = node;
        this.header = composeHeader(node);
        this.fieldPaths = Lists.newArrayList(node.getPath());
    }

    public FieldColumn(FieldPath fieldPath, String header) {
        super(new TextCell());
        this.header = header;
        this.fieldPaths = Lists.newArrayList(fieldPath);
    }

    public Object getValueAsObject(Projection projection) {
        for (FieldPath path : fieldPaths) {
            final Object value = projection.getValue(path);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String getValue(Projection projection) {
        final Object valueAsObject = getValueAsObject(projection);
        if (valueAsObject != null) {
            final ValueRenderer valueRenderer = RendererFactory.create(getNode().getTypeClass());
            return valueRenderer.asString(valueAsObject);
        }

        return NON_BREAKING_SPACE;
    }

    public void addFieldPath(FieldPath path) {
        fieldPaths.add(path);
    }

    public FormTree.Node getNode() {
        return node;
    }

    public List<FieldPath> getFieldPaths() {
        return fieldPaths;
    }

    public String getHeader() {
        return header;
    }

    private String composeHeader(FormTree.Node node) {
        if (node.getPath().isNested()) {
            return node.getDefiningFormClass().getLabel() + " " + node.getField().getLabel();
        } else {
            return node.getField().getLabel();
        }
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return "FieldColumn{" +
                "header='" + header + '\'' +
                ", criteria=" + criteria +
                '}';
    }
}
