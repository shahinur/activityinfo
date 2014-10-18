package org.activityinfo.geoadmin.merge;

import com.google.common.collect.Lists;
import org.activityinfo.geoadmin.ImportSource;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import javax.swing.tree.TreePath;
import java.util.List;

public class MergeTreeTableModel extends DefaultTreeTableModel {

    public static final int ACTION_COLUMN = 2;

    private List<TreeColumn> columns = Lists.newArrayList();

    public MergeTreeTableModel(MergeNode root, final ImportSource source) {
        super(root);

        columns.add(new TreeColumn("Entity") {

            @Override
            public Object getValue(MergeNode node) {
                if (node.getEntity() == null) {
                    return null;
                }
                return node.getEntity().getName();
            }
        });
        columns.add(new TreeColumn("Entity Code") {

            @Override
            public Object getValue(MergeNode node) {
                if (node.getEntity() == null) {
                    return null;
                }
                return node.getEntity().getCode();
            }
        });

        columns.add(new TreeColumn("Action") {

            @Override
            public Object getValue(MergeNode node) {
                return node.getAction();
            }

            @Override
            public void setValue(MergeNode node, Object value) {
                node.setAction((MergeAction) value);
            }
        });

        for (int ai = 0; ai != source.getAttributeCount(); ++ai) {
            final int attributeIndex = ai;
            columns.add(new TreeColumn(source.getAttributeNames()[ai]) {

                @Override
                public Object getValue(MergeNode node) {
                    if (node.getFeature() == null) {
                        return null;
                    } else {
                        return node.getFeature().getAttributeValue(
                            attributeIndex);
                    }
                }
            });
        }
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).getName();
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        MergeNode mergeNode = (MergeNode) node;
        return mergeNode.isLeaf() && column == ACTION_COLUMN;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        return columns.get(column).getValue((MergeNode) node);
    }

    @Override
    public void setValueAt(Object value, Object node, int column) {

        columns.get(column).setValue((MergeNode) node, value);

        super.setValueAt(value, node, column);
    }

    public void fireNodeChanged(MergeNode node) {
        modelSupport.firePathChanged(new TreePath(getPathToRoot(node)));
    }

}
