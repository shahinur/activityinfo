package org.activityinfo.geoadmin.merge;

public abstract class TreeColumn {
    private String name;

    public TreeColumn(String name) {
        super();
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public abstract Object getValue(MergeNode node);

    public void setValue(MergeNode node, Object value) {
        throw new UnsupportedOperationException(name);
    }
}
