package org.activityinfo.load;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class RequestStats {

    private DateTime startTime;
    public List<Request> requests = Lists.newArrayList();

    public RequestStats(DateTime startTime, List<Request> requests) {
        this.startTime = startTime;
        this.requests = requests;
    }

    public void printSummary() {
        printStat("Num requests", requests.size());
        printStat("Successful", pctSuccessful());
    }

    private void printStat(String name, long value) {
        System.out.println(Strings.padEnd(name, 20, ' ') + Strings.padStart(Long.toString(value), 10, ' '));
    }


    public void writeCsv() throws IOException {
        PrintWriter writer = new PrintWriter(getFile());
        writer.println(Joiner.on(",").join("start.time", "start.time.ms", "status", "latency"));
        for(Request request : requests) {
            writer.println(
                    Joiner.on(",").join(
                            request.getStartTime(),
                            relativeStartTime(request.getStartTime()),
                            request.getStatus(),
                            request.getLatency()));

        }

    }

    private long relativeStartTime(DateTime requestStartTime) {
        return requestStartTime.getMillis() - startTime.getMillis();
    }

    private File getFile() {
        return new File("load_test" + new DateTime().toString("YYYY_MM_dd_HH_mm_ss") + ".csv");
    }


    private long pctSuccessful() {
        double numSuccessful = 0;
        for(Request response : requests) {
            if(response.isSuccess()) {
                numSuccessful++;
            }
        }
        return Math.round(numSuccessful / requests.size() * 100d);
    }
}
