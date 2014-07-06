package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.criteria.*;

import java.util.List;

/**
 * Helper functions for evaluating criteria
 */
public class CriteriaEvaluation {

    /**
     * Partially evaluates a Criteria against a {@code FormClass} id
     *
     * @return a Predicate which returns true if {@code FormInstances} of this class
     * <strong>might</strong> be included in the result.
     */
    public static Predicate<ResourceId> evaluatePartiallyOnClassId(Criteria criteria) {
        ClassIdEvaluator evaluator = new ClassIdEvaluator();
        criteria.accept(evaluator);
        return evaluator.getPredicate();
    }

    private static class ClassIdEvaluator extends CriteriaVisitor {

        private List<Predicate<ResourceId>> classPredicates = Lists.newArrayList();
        private boolean hasCriteriaIndependentOfClassId = false;

        @Override
        public void visitInstanceIdCriteria(IdCriteria criteria) {
            hasCriteriaIndependentOfClassId = true;
        }

        @Override
        public void visitFieldCriteria(FieldCriteria criteria) {
            hasCriteriaIndependentOfClassId = true;
        }

        @Override
        public void visitClassCriteria(ClassCriteria criteria) {
            classPredicates.add(Predicates.equalTo(criteria.getClassId()));
        }

        @Override
        public void visitIntersection(CriteriaIntersection intersection) {
            ClassIdEvaluator visitor = evaluateSet(intersection);
            if (visitor.hasCriteriaIndependentOfClassId) {
                hasCriteriaIndependentOfClassId = true;
            }
            classPredicates.add(Predicates.and(visitor.classPredicates));
        }

        @Override
        public void visitUnion(CriteriaUnion union) {
            ClassIdEvaluator visitor = evaluateSet(union.getElements());
            if (visitor.hasCriteriaIndependentOfClassId) {
                hasCriteriaIndependentOfClassId = true;
            } else {
                classPredicates.add(Predicates.or(visitor.classPredicates));
            }
        }

        private ClassIdEvaluator evaluateSet(Iterable<Criteria> set) {
            ClassIdEvaluator visitor = new ClassIdEvaluator();
            for (Criteria criteria : set) {
                criteria.accept(visitor);
            }
            return visitor;
        }

        public Predicate<ResourceId> getPredicate() {
            if (classPredicates.isEmpty()) {
                return Predicates.alwaysTrue();
            } else {
                Preconditions.checkState(classPredicates.size() == 1);
                return classPredicates.get(0);
            }
        }
    }
}
