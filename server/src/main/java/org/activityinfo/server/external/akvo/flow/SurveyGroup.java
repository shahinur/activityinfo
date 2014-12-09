package org.activityinfo.server.external.akvo.flow;

import java.util.List;

final public class SurveyGroup {
    static final public class Array {
        public SurveyGroup survey_groups[];
    }

    static final public class Single {
        public SurveyGroup survey_group;
    }

    public String name;
    public String displayName;
    public String description;
    public String code;
    public Boolean monitoringGroup;
    public List<Integer> surveyList;
    public Long createdDateTime;
    public Long lastUpdateDateTime;
    public Integer newLocaleSurveyId;
    public Integer keyId;
}
