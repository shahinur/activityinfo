package org.activityinfo.service.store;

import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.client.AsyncFormTreeBuilder;
import org.activityinfo.client.ResourceLocator;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.test.TestResourceStore;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.activityinfo.promise.PromiseMatchers.assertResolves;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;


@SuppressWarnings("GwtClientClassFromNonInheritedModule")
public class AsyncFormTreeBuilderTest {

    @Rule
    public TestResourceStore store = new TestResourceStore();

    @BeforeClass
    public static final void setupLocale() {
        LocaleProxy.initialize();
    }

    @Test
    public void treeResolver() throws IOException {
        ResourceLocator locator = store.load("sites-simple1.json").createLocator();

        AsyncFormTreeBuilder treeBuilder = new AsyncFormTreeBuilder(locator);
        ResourceId formClassId = CuidAdapter.activityFormClass(1);
        FormTree tree = assertResolves(treeBuilder.apply(formClassId));

        System.out.println(tree);

        assertThat(tree.getRootFormClasses().keySet(), hasItems(formClassId));
    }

}
