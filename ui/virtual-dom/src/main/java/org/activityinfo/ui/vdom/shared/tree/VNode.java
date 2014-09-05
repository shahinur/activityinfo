package org.activityinfo.ui.vdom.shared.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class VNode extends VTree {

    /**
     * Singleton instance for an empty child list.
     *
     */
    public static final VTree[] NO_CHILDREN = new VTree[0];


    public final Tag tag;
    public final PropMap properties;
    public final VTree[] children;

    @Nullable
    public final String key;

    @Nullable
    public final String namespace;


    private int count;
    private boolean hasComponents;
    private boolean hooks = false;
    private int descendants = 0;
    private boolean descendantHooks = false;

    public VNode(Tag tag, VTree... children) {
        this(tag, null, children, null, null);
    }

    public VNode(Tag tag, PropMap propMap) {
        this(tag, propMap, null, null, null);
    }

    public VNode(Tag tag, PropMap properties, VTree child) {
        this(tag, properties, new VTree[] { child });
    }

    public VNode(Tag tag, PropMap properties, VTree... children) {
        this(tag, properties, children, null, null);
    }

    public VNode(@Nonnull Tag tag,
                 @Nullable PropMap properties,
                 @Nullable VTree[] children,
                 @Nullable String key,
                 @Nullable String namespace) {

        this.tag = tag;
        this.properties = properties == null ? PropMap.EMPTY : properties;
        this.children = children == null ? NO_CHILDREN : children;
        this.key = key;
        this.namespace = namespace;

        int count = this.children.length;
        int descendants = 0;
        boolean hasWidgets = false;

        Map<String, VHook> hooks = null;

        if(properties != null) {
            for (Map.Entry<String, Object> prop : properties.entrySet()) {
                if (prop.getValue() instanceof VHook) {
                    if (hooks == null) {
                        hooks = new HashMap<>();
                    }
                    hooks.put(prop.getKey(), (VHook) prop.getValue());
                }
            }
        }

        for (int i = 0; i < count; ++i) {
            VTree child = children[i];
            if (child instanceof VNode) {
                VNode childNode = (VNode) child;
                descendants += childNode.count;

                if (!hasWidgets && childNode.hasComponents) {
                    hasWidgets = true;
                }
                if (!descendantHooks && (childNode.hooks || childNode.descendantHooks)) {
                    descendantHooks = true;
                }
            } else if (!hasWidgets && child instanceof VComponent) {
                hasWidgets = true;
            }
        }

        this.count = count + descendants;
        this.descendants = descendants;
        this.hasComponents = hasWidgets;
    }


    @Override
    public boolean hasComponents() {
        return hasComponents;
    }

    @Override
    public VTree[] children() {
        return children;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public PropMap properties() {
        return properties;
    }

    public PropMap hooks() {
        return null;
    }

    @Override
    public void accept(VTreeVisitor visitor) {
        visitor.visitNode(this);
    }

    @Override
    public String toString() {
        String tag = this.tag.name().toLowerCase();
        if(children.length == 1 && children[0] instanceof VText) {
            return "<" + tag + ">" + children[0].text() + "</" + tag + "/>";
        } else if(children.length > 0) {
            return "<" + tag + "> ... </" + tag + ">";
        } else {
            return "<" + tag + "/>";
        }
    }
}
