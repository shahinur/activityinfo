package org.activityinfo.load;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class RequestStats {

    private DateTime startTime;
    public List<Request> requests = Lists.newArrayList();

    public RequestStats(DateTime startTime, List<Request> requests) {
        this.startTime = startTime;
        this.requests = requests;
    }

    public void writeCsv() throws IOException {
        try(PrintWriter writer = new PrintWriter(getFile())) {
            writer.println(Joiner.on(",").join("start.time", "start.time.ms", "status", "latency"));
            for (Request request : requests) {
                writer.println(Joiner.on(",")
                        .join(request.getStartTime(),
                                relativeStartTime(request.getStartTime()),
                                request.getStatus(),
                                request.getLatency()));

            }
        }

    }

    private long relativeStartTime(DateTime requestStartTime) {
        return requestStartTime.getMillis() - startTime.getMillis();
    }

    private File getFile() {
        return new File("load_test" + new DateTime().toString("YYYY_MM_dd_HH_mm_ss") + ".csv");
    }



    public void printSummary() {
        System.out.println("===== Responses ===========");
        printStat("Successful", count(Request.Outcome.SUCCESS));
        printStat("Timed out", count(Request.Outcome.TIMEOUT));
        printStat("Error", count(Request.Outcome.ERROR));
        printErrorCounts();
        printStat("TOTAL", requests.size());
    }

    private void printStat(String name, long value) {
        printStat(name, value, "");
    }

    private void printStat(String name, long value, String description) {
        System.out.println(Strings.padEnd(name, 15, ' ') + Strings.padStart(Long.toString(value), 8, ' ')
                           + "   " + description);
    }


    private long count(Request.Outcome outcome) {
        int count = 0;
        for(Request response : requests) {
            if(response.getOutcome() == outcome) {
                count++;
            }
        }
        return count;
    }

    public void printErrorCounts() {

        Map<RequestError, Integer> counts = Maps.newHashMap();

        for(Request request : requests) {
            if(request.getOutcome() == Request.Outcome.ERROR) {
                RequestError error = request.getRequestError();
                if(counts.containsKey(error)) {
                    counts.put(error, counts.get(error) + 1);
                } else {
                    counts.put(error, 1);
                }
            }
        }

        for(RequestError error : counts.keySet()) {
            printStat(String.format("  %s (%d)", error.getReasonPhrase(), error.getStatusCode()),
                    counts.get(error),
                    error.getMessage());
        }
    }
}
