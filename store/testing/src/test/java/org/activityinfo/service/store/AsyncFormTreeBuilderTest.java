package org.activityinfo.service.store;

import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;

import static org.activityinfo.promise.PromiseMatchers.assertResolves;
import static org.activityinfo.store.test.TestResourceStore.createLocator;
import static org.junit.Assert.assertThat;


@SuppressWarnings("GwtClientClassFromNonInheritedModule")
public class AsyncFormTreeBuilderTest {

    @Test
    public void treeResolver() throws IOException {
        ResourceLocator locator = createLocator("/dbunit/sites-simple1.json") ;

        AsyncFormTreeBuilder treeBuilder = new AsyncFormTreeBuilder(locator);
        ResourceId formClassId = CuidAdapter.activityFormClass(1);
        FormTree tree = assertResolves(treeBuilder.apply(formClassId));

        System.out.println(tree);

        assertThat(tree.getRootFormClasses().keySet(), Matchers.hasItems(formClassId));
    }

}
