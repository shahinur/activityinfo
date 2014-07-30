package org.activityinfo.server.store;

import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fixtures.Modules;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.server.util.locale.LocaleModule;
import org.activityinfo.service.ResourceLocatorSync;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@Modules({ ResourceStoreModule.class, LocaleModule.class })
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class ResourceStoreModuleTest extends CommandTestCase2 {

    @Inject
    private ResourceLocatorSync locatorSync;

    @Before
    public void before() {
        LocaleProxy.initialize();
        LocaleProxy.setLocale(Locale.ENGLISH);
    }

    @Test
    public void syncLocator() {
        Resource form = locatorSync.getResource(CuidAdapter.activityFormClass(1));
        assertThat(form.getString("label"), equalTo("NFI"));
    }
}
