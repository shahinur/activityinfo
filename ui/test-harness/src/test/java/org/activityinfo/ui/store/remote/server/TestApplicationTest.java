package org.activityinfo.ui.store.remote.server;

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

public class TestApplicationTest {


    @Test
    public void test() throws IOException, InterruptedException {
        Closeable server = TestApplication.start(8999);
        server.close();
    }
}
