package org.activityinfo.load;

import org.activityinfo.client.ActivityInfoClient;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class OdkLoadTest {


    public static void main(String[] args) throws InterruptedException, IOException {

        URI devServer = UriBuilder.fromPath("https://ai-production.appspot.com/").build();
        ActivityInfoClient client = new ActivityInfoClient(devServer, "test@test.org", "testing123");


    //    run(new FormSubmitter(client));

        run(new FormSubmitter(client, "871k.png"), LoadProfiles.rush10());

    }

    private static void run(FormSubmitter submitter, LoadProfile profile) throws InterruptedException, IOException {
        AsyncLoadTester tester = new AsyncLoadTester(submitter, profile);
        RequestStats run = tester.run();
        run.printSummary();
        run.writeCsv();
    }


}
