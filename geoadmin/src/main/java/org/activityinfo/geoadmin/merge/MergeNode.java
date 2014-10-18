package org.activityinfo.geoadmin.merge;

import com.google.common.collect.Lists;
import org.activityinfo.geoadmin.ImportFeature;
import org.activityinfo.geoadmin.model.AdminEntity;
import org.activityinfo.geoadmin.model.AdminLevel;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import java.util.List;

public class MergeNode extends DefaultMutableTreeTableNode {

    private AdminLevel level;
    private AdminEntity entity;
    private ImportFeature feature;
    private MergeAction action;

    public AdminLevel getLevel() {
        return level;
    }

    public void setLevel(AdminLevel level) {
        this.level = level;
    }

    public AdminEntity getEntity() {
        return entity;
    }

    public void setEntity(AdminEntity entity) {
        this.entity = entity;
    }

    public ImportFeature getFeature() {
        return feature;
    }

    public void setFeature(ImportFeature feature) {
        this.feature = feature;
    }

    public boolean isJoined() {
        return entity != null && feature != null;
    }

    public MergeAction getAction() {
        return action;
    }

    public void setAction(MergeAction action) {
        this.action = action;
    }

    public List<MergeNode> getLeaves() {
        if (isLeaf()) {
            return Lists.newArrayList(this);
        } else {
            List<MergeNode> leaves = Lists.newArrayList();
            for (int i = 0; i != getChildCount(); ++i) {
                leaves.addAll(((MergeNode) getChildAt(i)).getLeaves());
            }
            return leaves;
        }
    }

}
