package org.activityinfo.io.importing;

import com.google.common.collect.Lists;
import org.activityinfo.io.importing.model.ImportModel;
import org.activityinfo.io.importing.strategy.FieldImportStrategies;
import org.activityinfo.io.importing.strategy.FieldImportStrategy;
import org.activityinfo.io.importing.strategy.ImportTarget;
import org.activityinfo.io.importing.validation.ValidatedRowTable;
import org.activityinfo.io.importing.validation.ValidationResult;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.ResourceLocator;

import java.util.List;


public class Importer {

    private ResourceLocator resourceLocator;

    static class TargetField {
        FormTree.Node node;
        FieldImportStrategy strategy;

        private TargetField(FormTree.Node node, FieldImportStrategy strategy) {
            this.node = node;
            this.strategy = strategy;
        }

        @Override
        public String toString() {
            return node.toString();
        }
    }

    private List<TargetField> fields = Lists.newArrayList();

    public Importer(ResourceLocator resourceLocator, FormTree formTree, FieldImportStrategies fieldImportStrategies) {
        this.resourceLocator = resourceLocator;
        for(FormTree.Node rootField : formTree.getRootFields()) {
            fields.add(new TargetField(rootField, fieldImportStrategies.forField(rootField)));
        }
    }

    public List<ImportTarget> getImportTargets() {
        List<ImportTarget> targets = Lists.newArrayList();
        for(TargetField binding : fields) {
            targets.addAll(binding.strategy.getImportSites(binding.node));
        }
        return targets;
    }

    public Promise<ValidatedRowTable> validateRows(final ImportModel model) {
        final ImportCommandExecutor modeller = new ImportCommandExecutor(model, fields, resourceLocator);
        return modeller.execute(new ValidateRowsImportCommand());
    }

    public Promise<List<ValidationResult>> validateClass(final ImportModel model) {
        final ImportCommandExecutor modeller = new ImportCommandExecutor(model, fields, resourceLocator);
        return modeller.execute(new ValidateClassImportCommand());
    }

    public Promise<Void> persist(final ImportModel model) {
        final ImportCommandExecutor modeller = new ImportCommandExecutor(model, fields, resourceLocator);
        return modeller.execute(new PersistImportCommand());
    }
}
