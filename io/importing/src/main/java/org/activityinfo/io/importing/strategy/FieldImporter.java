package org.activityinfo.io.importing.strategy;

import org.activityinfo.io.importing.source.SourceRow;
import org.activityinfo.io.importing.validation.ValidationResult;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.ResourceLocator;

import java.util.List;

/**
 * FieldImporters operate on
 */
public interface FieldImporter {

    Promise<Void> prepare(ResourceLocator locator, List<? extends SourceRow> batch);

    void validateInstance(SourceRow row, List<ValidationResult> results);

    boolean updateInstance(SourceRow row, FormInstance instance);

    List<FieldImporterColumn> getColumns();

}
