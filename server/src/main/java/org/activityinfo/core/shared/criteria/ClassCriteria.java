package org.activityinfo.core.shared.criteria;

import com.google.common.collect.Lists;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.form.FormInstance;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * Spike for instance criteria. Defines the criteria
 * for the query as FormInstances that are classes of the
 * given Iri.
 */
public class ClassCriteria implements Criteria {

    private Cuid classId;


    public ClassCriteria(Cuid cuid) {
        this.classId = cuid;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitClassCriteria(this);
    }


    @Override
    public boolean apply(FormInstance input) {
        return classId.equals(input.getClassId());
    }

    @Override
    public boolean apply(@Nonnull Projection projection) {
        return classId.equals(projection.getRootClassId());
    }

    public static Criteria union(Set<Cuid> range) {
        if(range.size() == 1) {
            return new ClassCriteria(range.iterator().next());
        } else {
            List<ClassCriteria> criteriaList = Lists.newArrayList();
            for(Cuid classCuid : range) {
                criteriaList.add(new ClassCriteria(classCuid));
            }
            return new CriteriaUnion(criteriaList);
        }
    }

    public Cuid getClassId() {
        return classId;
    }
}
