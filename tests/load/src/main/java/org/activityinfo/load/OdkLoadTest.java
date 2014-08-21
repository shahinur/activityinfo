package org.activityinfo.load;

import org.activityinfo.client.ActivityInfoClient;
import org.joda.time.Duration;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class OdkLoadTest {


    public static void main(String[] args) throws InterruptedException, IOException {

        URI devServer = UriBuilder.fromPath("https://ai-production.appspot.com/").build();
        ActivityInfoClient client = new ActivityInfoClient(devServer, "test@test.org", "testing123");

        FormSubmitter submitter = new FormSubmitter(client);

        AsyncLoadTester tester = new AsyncLoadTester(submitter);
        tester.setDuration(Duration.standardMinutes(3));
        tester.setGrowthFunction(new GrowthFunction(Duration.standardSeconds(60), 50));
        RequestStats run = tester.run();

        run.printSummary();
        run.writeCsv();
    }


}
