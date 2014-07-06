package org.activityinfo.ui.client.component.form.model;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.inject.Provider;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.application.ApplicationProperties;
import org.activityinfo.core.shared.criteria.IdCriteria;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.promise.Promise;

import java.util.List;

/**
 * Asynchronously builds a form model. We don't want to display
 * a partially rendered form in the event that one of the requests
 * fails so we build it all ahead of time.
 */
public class FormViewModelProvider implements Provider<Promise<FormViewModel>> {

    private final ResourceLocator resourceLocator;
    private ResourceId classId;
    private ResourceId instanceId;
    private FormViewModel viewModel = new FormViewModel();

    public FormViewModelProvider(ResourceLocator resourceLocator, ResourceId classId, ResourceId instanceId) {
        this.resourceLocator = resourceLocator;
        this.classId = classId;
        this.instanceId = instanceId;
    }

    @Override
    public Promise<FormViewModel> get() {
        final Promise<FormTree> formTree = new AsyncFormTreeBuilder(resourceLocator).apply(classId);
        final Promise<List<FormInstance>> instance = resourceLocator.queryInstances(new IdCriteria(instanceId));
        return Promise.waitAll(formTree, instance).join(new Supplier<Promise<FormViewModel>>() {

            @Override
            public Promise<FormViewModel> get() {
                viewModel.formTree = formTree.get();
                if(instance.get().isEmpty()) {
                    viewModel.instance = new FormInstance(instanceId, classId);
                } else {
                    viewModel.instance = instance.get().get(0);
                }

                final List<Promise<FieldViewModel>> fieldModels = buildFieldViewModels();
                return Promise.waitAll(fieldModels).then(new Supplier<FormViewModel>() {
                    @Override
                    public FormViewModel get() {
                        for(Promise<FieldViewModel> model : fieldModels) {
                            viewModel.fields.put(model.get().getFieldId(), model.get());
                        }
                        return viewModel;
                    }
                });
            }
        });
    }

    private List<Promise<FieldViewModel>> buildFieldViewModels() {
        List<Promise<FieldViewModel>> models = Lists.newArrayList();
        for(FormTree.Node node : viewModel.formTree.getRootFields()) {
            if(node.isReference()) {
                models.add(buildFieldViewModel(node));
            }
        }
        return models;
    }

    private Promise<FieldViewModel> buildFieldViewModel(FormTree.Node node) {
        Object fieldValue = viewModel.getInstance().get(node.getFieldId());

        if(node.getField().isSubPropertyOf(ApplicationProperties.HIERARCHIAL)) {
            return HierarchyViewModel.build(resourceLocator, node, fieldValue);
        } else {
            return SimpleListViewModel.build(resourceLocator, node);
        }
    }
}
