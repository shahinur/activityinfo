package org.activityinfo.model.legacy.criteria;

import com.google.common.collect.Sets;
import org.activityinfo.model.resource.ResourceId;

import java.util.Set;

/**
 * The set of FormClasses that could be potentially selected by a given
 * criteria
 */
public class FormClassSet {

    public static final FormClassSet OPEN = new FormClassSet();

    private boolean open = true;
    private Set<ResourceId> closed = Sets.newHashSet();

    private FormClassSet() {
    }

    private FormClassSet(ResourceId classId) {
        open = false;
        closed.add(classId);
    }

    public FormClassSet(FormClassSet set) {
        open = false;
        closed.addAll(set.closed);
    }

    public static FormClassSet of(Criteria criteria) {
        if(criteria instanceof ClassCriteria) {
            return new FormClassSet(((ClassCriteria) criteria).getClassId());

        } else if(criteria instanceof CriteriaUnion) {
            return computeUnion((CriteriaUnion) criteria);

        } else if(criteria instanceof CriteriaIntersection) {
            return computeIntersection((CriteriaIntersection)criteria);

        } else {
            return OPEN;
        }
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isClosed() {
        return !open;
    }

    public boolean isEmpty() {
        return !open && closed.isEmpty();
    }

    public boolean isSingleton() {
        return !open && closed.size() == 1;
    }

    public ResourceId unique() {
        assert !open : "The set is open";
        assert !closed.isEmpty() : "The set is empty";
        assert  closed.size() == 1 : "The set includes more than one class";

        return closed.iterator().next();
    }

    public Set<ResourceId> getElements() {
        assert isClosed();
        return closed;
    }

    private static FormClassSet computeUnion(CriteriaUnion criteria) {
        FormClassSet union = new FormClassSet();
        union.open = false;

        for(Criteria element : criteria.getElements()) {
            FormClassSet elementSet = FormClassSet.of(element);
            if(elementSet.open) {
                union.open = true;
                break;
            } else {
                union.closed.addAll(elementSet.closed);
            }
        }
        return union;
    }


    private static FormClassSet computeIntersection(CriteriaIntersection criteria) {
        FormClassSet intersection = OPEN;

        for(Criteria element : criteria.getElements()) {
            FormClassSet elementSet = FormClassSet.of(element);
            if(!elementSet.open) {
                if(intersection == OPEN) {
                    intersection = new FormClassSet(elementSet);
                } else {
                    intersection.closed.retainAll(elementSet.closed);
                }
            }
        }
        return intersection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormClassSet that = (FormClassSet) o;

        if (open != that.open) return false;
        if (!closed.equals(that.closed)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (open ? 1 : 0);
        result = 31 * result + closed.hashCode();
        return result;
    }

    public static FormClassSet of(Set<ResourceId> range) {
        FormClassSet set = new FormClassSet();
        set.open = false;
        set.closed.addAll(range);
        return set;
    }
}
