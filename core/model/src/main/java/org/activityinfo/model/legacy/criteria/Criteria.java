package org.activityinfo.model.legacy.criteria;


import org.activityinfo.model.legacy.Projection;
import org.activityinfo.model.form.FormInstance;

import javax.annotation.Nonnull;

/**
 * Superclass of {@code Criteria} that are used to select
 * {@code FormInstance}s
 */
public interface Criteria {

    void accept(CriteriaVisitor visitor);

    boolean apply(@Nonnull FormInstance instance);

    boolean apply(@Nonnull Projection projection);


}
