package org.activityinfo.ui.client.component.form.field.hierarchy;

import com.google.common.base.Supplier;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.application.ApplicationProperties;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.criteria.CriteriaIntersection;
import org.activityinfo.core.shared.criteria.FieldCriteria;
import org.activityinfo.promise.Promise;

import java.util.List;

/**
* Created by alex on 4/2/14.
*/
public class ChoiceProvider implements Supplier<Promise<List<Projection>>> {
    private final Level level;
    private final ResourceLocator locator;
    private Presenter selectionModel;

    public ChoiceProvider(ResourceLocator locator, Level level, Presenter selectionModel) {
        this.level = level;
        this.locator = locator;
        this.selectionModel = selectionModel;
    }

    @Override
    public Promise<List<Projection>> get() {
        InstanceQuery.Builder query = InstanceQuery
                .select(ApplicationProperties.LABEL_PROPERTY, ApplicationProperties.PARENT_PROPERTY);

        if(level.isRoot()) {
            query.where(new ClassCriteria(level.getClassId()));
        } else {
            Projection selectedParent = selectionModel.getSelection(level.getParent());

            query.where(new CriteriaIntersection(
                    new ClassCriteria(level.getClassId()),
                    new FieldCriteria(level.getParentFieldId(), selectedParent.getRootInstanceId())));
        }

        return locator.query(query.build());
    }
}
