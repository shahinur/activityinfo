package org.activityinfo.model.legacy;

import com.google.common.collect.Sets;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.legacy.criteria.*;

import java.util.Set;

/**
 * Created by alex on 3/15/14.
 */
public class CriteriaAnalysis extends CriteriaVisitor {

    /**
     * Instances must be a subclass of all of these FormClasses
     */
    private final Set<ResourceId> classCriteria = Sets.newHashSet();

    private final Set<ResourceId> parentCriteria = Sets.newHashSet();

    private boolean rootOnly = false;
    private boolean classUnion = true;

    public ResourceId getParentCriteria() {
        return parentCriteria.iterator().next();
    }

    public boolean isRootOnly() {
        return rootOnly;
    }

    @Override
    public void visitClassCriteria(ClassCriteria criteria) {
        classCriteria.add(criteria.getClassId());
    }

    @Override
    public void visitInstanceIdCriteria(IdCriteria criteria) {
    }

    @Override
    public void visitParentCriteria(ParentCriteria criteria) {
        if (criteria.selectsRoot()) {
            rootOnly = true;
        } else {
            parentCriteria.add(criteria.getParentId());
        }
    }

    @Override
    public void visitIntersection(CriteriaIntersection intersection) {
        // A ∩ (B ∩ C) = A ∩ B ∩ C
        for (Criteria criteria : intersection) {
            criteria.accept(this);
        }
    }

    @Override
    public void visitUnion(CriteriaUnion criteriaUnion) {
        classUnion = true; // todo temp fix! - in general wrong approach, will work in flat case only!
        for (Criteria criteria : criteriaUnion.getElements()) {
            if (classUnion && !(criteria instanceof ClassCriteria)) {
                classUnion = false;
            }
            criteria.accept(this);
        }
    }

    public boolean isEmptySet() {
        if (classCriteria.size() > 1 && !classUnion) {
            // a single instance cannot (at this time) be a member of more than one
            // class, so the result of this query is logically the empty set
            return true;
        }

        if (parentCriteria.size() > 1 || (rootOnly && !parentCriteria.isEmpty())) {
            // likewise, a single instance cannot be a child of multiple parents, so
            // the result of this query is logically the empty set
            return true;
        }

        return false;
    }

    public boolean isRestrictedToSingleClass() {
        return classCriteria.size() == 1;
    }

    public ResourceId getClassRestriction() {
        return classCriteria.iterator().next();
    }

    public static CriteriaAnalysis analyze(Criteria criteria) {
        CriteriaAnalysis analysis = new CriteriaAnalysis();
        criteria.accept(analysis);
        return analysis;
    }
}
