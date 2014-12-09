package org.activityinfo.server.external.akvo.flow;

final public class QuestionGroup {
    static final public class Array {
        public QuestionGroup question_groups[];
        public Object meta;
    }

    static final public class Single {
        public QuestionGroup question_group;
    }

    public String name;
    public String path;
    public String displayName;
    public String description;
    public String code;
    public String questionMap;
    public Integer surveyId;
    public Integer order;
    public Integer keyId;
    public String sourceId;
}
