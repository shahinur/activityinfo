package org.activityinfo.server.external.akvo.flow;

import java.util.List;

final public class SurveyedLocale {
    static final public class Array {
        public SurveyedLocale surveyed_locales[];
    }

    static final public class Single {
        public SurveyedLocale surveyed_locale;
    }

    public Integer id;
    public String displayName;
    public String identifier;
    public List<Integer> surveyInstances;
    public Long lastUpdateDateTime;
    public Double lat;
    public Double lon;
    public Integer surveyGroupId;
    public Integer keyId;
}
