package org.activityinfo.server.external.akvo.flow;

final public class SurveyInstance {
    static final public class Array {
        public SurveyInstance survey_instances[];
        public Object meta;
    }

    static final public class Single {
        public SurveyInstance survey_instance;
    }

    public String surveyCode;
    public String surveyId;
    public String submitterName;
    public String deviceIdentifier;
    public String approximateLocationFlag;
    public Boolean approvedFlag;
    public String userID;
    public Long collectionDate;
    public Object questionAnswersStore;
    public Integer surveyalTime;
    public Integer keyId;
    public String surveyedLocaleIdentifier;
    public String surveyedLocaleId;
    public String surveyedLocaleDisplayName;
}
