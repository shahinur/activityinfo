package org.activityinfo.ui.component.importDialog;

import com.google.common.base.Function;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.AsyncFormTreeBuilder;
import org.activityinfo.service.store.ResourceLocator;

public class ImportLauncher {

    public static Promise<ImportPresenter> showPresenter(ResourceId activityId, final ResourceLocator resourceLocator) {
        AsyncFormTreeBuilder treeBuilder = new AsyncFormTreeBuilder(resourceLocator);
        return treeBuilder.apply(activityId).then(new Function<FormTree, ImportPresenter>() {
            @Override
            public ImportPresenter apply(FormTree input) {
                return new ImportPresenter(resourceLocator, input);
            }
        });
    }
}
