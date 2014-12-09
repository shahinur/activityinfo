package org.activityinfo.server.external.akvo.flow;

import java.util.List;

final public class Question {
    static final public class Array {
        public Question questions[];
        public Object questionOptions;
        public Object meta;
    }

    static final public class Single {
        public Question question;
    }

    public String type;
    public String displayName;
    public String questionTypeString;
    public Integer questionGroupId;
    public Double maxVal;
    public String dependentQuestionAnswer;
    public Boolean dependentQuestionId;
    public Boolean dependentFlag;
    public Boolean dependentMultipleFlag;
    public Boolean allowMultipleFlag;
    public Boolean allowOtherFlag;
    public Boolean mandatoryFlag;
    public String tip;
    public Boolean geoLocked;
    public Boolean localeNameFlag;
    public Boolean requireDoubleEntry;
    public Boolean localeLocationFlag;
    public Boolean allowDecimal;
    public Boolean allowSign;
    public Double minVal;
    public List<Integer> questionOptions;
    public Integer surveyId;
    public Integer order;
    public String path;
    public String immutable;
    public String questionDependency;
    public String sourceId;
    public String optionList;
    public String collapseable;
    public String allowExternalSources;
    public String isName;
    public String metricId;
    public String questionId;
    public String translationMap;
    public String optionContainerDto;
    public String questionHelpList;
    public String text;
    public String keyId;
}
