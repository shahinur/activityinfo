package org.activityinfo.service.store;

import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.legacy.criteria.FormClassSet;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;

import java.util.Collections;
import java.util.logging.Logger;

/**
 * Builds a {@link org.activityinfo.model.formTree.FormTree}
 */
public class AsyncFormTreeBuilder implements Function<ResourceId, Promise<FormTree>> {

    private static final Logger LOGGER = Logger.getLogger(AsyncFormTreeBuilder.class.getName());

    private final ResourceLocator locator;

    public AsyncFormTreeBuilder(ResourceLocator locator) {
        this.locator = locator;
    }

    @Override
    public Promise<FormTree> apply(ResourceId formClassId) {
        return execute(Collections.singleton(formClassId));
    }

    public Promise<FormTree> execute(Iterable<ResourceId> formClasses) {
        Promise<FormTree> result = new Promise<>();
        new Resolver(formClasses, result);
        return result;
    }

    public class Resolver {

        private AsyncCallback<? super FormTree> callback;
        private FormTree tree;
        private int outstandingRequests = 0;

        public Resolver(final Iterable<ResourceId> classIds, final AsyncCallback<FormTree> callback) {
            this.callback = callback;
            this.tree = new FormTree();
            for(ResourceId formClass : classIds) {
                requestFormClassForNode(null, formClass);
            }
        }

        private void requestFormClassForNode(final FormTree.Node node, final ResourceId formClassId) {

            LOGGER.fine("Requesting form class for " + node);

            outstandingRequests++;
            locator.getFormClass(formClassId).then(new AsyncCallback<FormClass>() {
                @Override
                public void onFailure(Throwable caught) {
                    Resolver.this.callback.onFailure(caught);
                }

                @Override
                public void onSuccess(FormClass formClass) {
                    addChildrenToNode(node, formClass);

                    outstandingRequests--;
                    if(outstandingRequests == 0) {
                        callback.onSuccess(tree);
                    }
                }
            });
        }

        /**
         * Now that we have the actual FormClass model that corresponds to this node's
         * formClassId, add it's children.
         *
         */
        private void addChildrenToNode(FormTree.Node node, FormClass formClass) {
            for(FormField field : formClass.getFields()) {
                FormTree.Node childNode;
                if(node == null) {
                    childNode = tree.addRootField(formClass, field);
                } else {
                    childNode = node.addChild(formClass, field);
                }
                if(childNode.isReference()) {
                    queueNextRequests(childNode);
                }
            }
        }

        private void queueNextRequests(FormTree.Node child) {
            FormClassSet classSet = FormClassSet.of(child.getRange());
            assert classSet.isClosed() : "trees for open class ranges not yet implemented";
            for(ResourceId classId : classSet.getElements()) {
                requestFormClassForNode(child, classId);
            }
        }
    }
}
