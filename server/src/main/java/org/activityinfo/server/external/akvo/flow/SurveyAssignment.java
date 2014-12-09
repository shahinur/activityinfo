package org.activityinfo.server.external.akvo.flow;

import java.util.List;

final public class SurveyAssignment {
    static final public class Array {
        public SurveyAssignment survey_assignments[];
    }

    static final public class Single {
        public SurveyAssignment survey_assignment;
    }

    public String name;
    public String language;
    public List<Integer> devices;
    public List<Integer> surveys;
    public Long startDate;
    public Long endDate;
    public Integer keyId;
}
