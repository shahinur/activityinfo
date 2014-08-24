package org.activityinfo.ui.vdom;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestCase;
import org.activityinfo.ui.vdom.client.DomBuilderTest;

public class GwtVDomTestSuite extends TestCase {

    public static Test suite() {
        GWTTestSuite suite = new GWTTestSuite("VDom integration tests");
        suite.addTestSuite(DomBuilderTest.class);

        return suite;
    }

}
