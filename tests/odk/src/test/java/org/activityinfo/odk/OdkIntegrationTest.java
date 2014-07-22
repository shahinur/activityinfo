package org.activityinfo.odk;

import com.google.common.base.Preconditions;
import org.activityinfo.odk.driver.FormList;
import org.activityinfo.odk.driver.OdkDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

/**
 * Tests ActivityInfo's integration with ODK against a live
 * ActivityInfo server and
 */
public class OdkIntegrationTest {

    private OdkDriver odkDriver;

    @Before
    public void setUp() throws Exception {
        odkDriver = OdkDriver.create();
    }

    @After
    public void tearDown() throws Exception {
        odkDriver.close();
    }

    @Test
    public void blankFormAvailable() {

        odkDriver.openGeneralSettings()
           .setUrl(getRequiredProperty("serverUrl"))
           .setAccountEmail(getRequiredProperty("accountEmail"))
           .setPassword(getRequiredProperty("accountPassword"));

        FormList formList = odkDriver.openFormList().load();

        assertThat(formList.getFormNames(), hasItem("FormsTest / LCCA"));
    }

    private String getRequiredProperty(String name) {
        return Preconditions.checkNotNull(System.getProperty(name), name);
    }
}
