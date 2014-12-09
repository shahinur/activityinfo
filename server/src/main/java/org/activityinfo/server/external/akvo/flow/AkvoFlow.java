package org.activityinfo.server.external.akvo.flow;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;

import static com.google.common.base.Optional.of;
import static java.lang.System.exit;

public class AkvoFlow {
    static final private String ALGORITHM = "HmacSHA1";

    final private String server;
    final private String access;
    final private SecretKeySpec secretKeySpec;
    final private Mac mac;

    public AkvoFlow(String server, String access, String secret) {
        try {
            this.server = server;
            this.access = access;

            secretKeySpec = new SecretKeySpec(secret.getBytes(), ALGORITHM);
            mac = Mac.getInstance(ALGORITHM);

            mac.init(secretKeySpec);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        final String server, access, secret;

        if (args.length == 3) {
            server = args[0];
            access = args[1];
            secret = args[2];
        } else {
            System.err.println("Three command line arguments needed: a server's URL, an access key and its secret key");
            exit(1);
            return;
        }

        AkvoFlow akvoFlow = new AkvoFlow(server, access, secret);
        for (Survey survey : akvoFlow.getSurveys()) {
            SurveyGroup surveyGroup = survey.surveyGroupId != null ?
                    akvoFlow.getSurveyGroup(survey.surveyGroupId) : null;
            QuestionGroup questionGroups[] = survey.keyId != null ?
                    akvoFlow.getQuestionGroups(survey.keyId) : new QuestionGroup[0];
            Question questions[] = survey.keyId != null ?
                    akvoFlow.getQuestions(survey.keyId) : new Question[0];
            SurveyInstance surveyInstances[] = survey.keyId != null ?
                    akvoFlow.getSurveyInstances(survey.keyId) : null;

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

            System.out.println(survey.name);
            System.out.println(survey.path);
            System.out.println(survey.displayName);
            System.out.println(survey.version);
            System.out.println(survey.description);
            System.out.println(survey.code);
            System.out.println(survey.sector);
            System.out.println(survey.pointType);
            System.out.println(survey.defaultLanguageCode);
            System.out.println(survey.requireApproval);
            System.out.println(survey.createdDateTime);
            System.out.println(survey.lastUpdateDateTime);
            System.out.println(survey.sourceId);

            if (surveyGroup != null) {
                SurveyedLocale surveyedLocales[] = surveyGroup.keyId != null ?
                        akvoFlow.getSurveyedLocales(surveyGroup.keyId) : new SurveyedLocale[0];
                System.out.println("\t" + survey.surveyGroupId);
                System.out.println("\t" + surveyGroup.name);
                System.out.println("\t" + surveyGroup.displayName);
                System.out.println("\t" + surveyGroup.description);
                System.out.println("\t" + surveyGroup.code);
                System.out.println("\t" + surveyGroup.monitoringGroup);
                System.out.println("\t" + surveyGroup.surveyList);
                System.out.println("\t" + surveyGroup.createdDateTime);
                System.out.println("\t" + surveyGroup.lastUpdateDateTime);
                System.out.println("\t" + surveyGroup.newLocaleSurveyId);
                System.out.println("\t" + surveyGroup.keyId);

                for (SurveyedLocale surveyedLocale : surveyedLocales) {
                    System.out.println("\t\t\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
                    System.out.println("\t\t" + surveyedLocale.id);
                    System.out.println("\t\t" + surveyedLocale.displayName);
                    System.out.println("\t\t" + surveyedLocale.identifier);
                    System.out.println("\t\t" + surveyedLocale.surveyInstances);
                    System.out.println("\t\t" + surveyedLocale.lastUpdateDateTime);
                    System.out.println("\t\t" + surveyedLocale.lat);
                    System.out.println("\t\t" + surveyedLocale.lon);
                    System.out.println("\t\t" + surveyedLocale.surveyGroupId);
                    System.out.println("\t\t" + surveyedLocale.keyId);
                    System.out.println("\t\t////////////////////////////////////");
                }
            }

            System.out.println(survey.instanceCount);
            System.out.println(survey.keyId);
            System.out.println(survey.status);
            System.out.println(survey.questionGroupList);

            for (QuestionGroup questionGroup : questionGroups) {
                System.out.println("\t(((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((");
                System.out.println("\t" + questionGroup.name);
                System.out.println("\t" + questionGroup.path);
                System.out.println("\t" + questionGroup.displayName);
                System.out.println("\t" + questionGroup.description);
                System.out.println("\t" + questionGroup.code);
                System.out.println("\t" + questionGroup.questionMap);
                System.out.println("\t" + questionGroup.surveyId);
                System.out.println("\t" + questionGroup.order);
                System.out.println("\t" + questionGroup.keyId);
                System.out.println("\t" + questionGroup.sourceId);
                System.out.println("\t)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
            }

            for (Question question : questions) {
                System.out.println("\t{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{");
                System.out.println("\t" + question.type);
                System.out.println("\t" + question.displayName);
                System.out.println("\t" + question.questionTypeString);
                System.out.println("\t" + question.questionGroupId);
                System.out.println("\t" + question.maxVal);
                System.out.println("\t" + question.dependentQuestionAnswer);
                System.out.println("\t" + question.dependentQuestionId);
                System.out.println("\t" + question.dependentFlag);
                System.out.println("\t" + question.allowMultipleFlag);
                System.out.println("\t" + question.allowOtherFlag);
                System.out.println("\t" + question.mandatoryFlag);
                System.out.println("\t" + question.tip);
                System.out.println("\t" + question.geoLocked);
                System.out.println("\t" + question.localeNameFlag);
                System.out.println("\t" + question.requireDoubleEntry);
                System.out.println("\t" + question.localeLocationFlag);
                System.out.println("\t" + question.allowDecimal);
                System.out.println("\t" + question.allowSign);
                System.out.println("\t" + question.minVal);
                System.out.println("\t" + question.questionOptions);
                System.out.println("\t" + question.surveyId);
                System.out.println("\t" + question.order);
                System.out.println("\t" + question.path);
                System.out.println("\t" + question.immutable);
                System.out.println("\t" + question.questionDependency);
                System.out.println("\t" + question.sourceId);
                System.out.println("\t" + question.optionList);
                System.out.println("\t" + question.collapseable);
                System.out.println("\t" + question.allowExternalSources);
                System.out.println("\t" + question.isName);
                System.out.println("\t" + question.metricId);
                System.out.println("\t" + question.questionId);
                System.out.println("\t" + question.translationMap);
                System.out.println("\t" + question.optionContainerDto);
                System.out.println("\t" + question.questionHelpList);
                System.out.println("\t" + question.text);
                System.out.println("\t" + question.keyId);
                System.out.println("\t}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}");
            }

            for (SurveyInstance surveyInstance : surveyInstances) {
                System.out.println("\t(((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((");
                QuestionAnswer questionAnswers[] = surveyInstance.keyId != null ?
                        akvoFlow.getQuestionAnswers(surveyInstance.keyId) : new QuestionAnswer[0];
                System.out.println("\t" + surveyInstance.surveyCode);
                System.out.println("\t" + surveyInstance.surveyId);
                System.out.println("\t" + surveyInstance.submitterName);
                System.out.println("\t" + surveyInstance.deviceIdentifier);
                System.out.println("\t" + surveyInstance.approximateLocationFlag);
                System.out.println("\t" + surveyInstance.approvedFlag);
                System.out.println("\t" + surveyInstance.userID);
                System.out.println("\t" + surveyInstance.collectionDate);
                System.out.println("\t" + surveyInstance.questionAnswersStore);
                System.out.println("\t" + surveyInstance.surveyalTime);
                System.out.println("\t" + surveyInstance.keyId);
                System.out.println("\t" + surveyInstance.surveyedLocaleIdentifier);
                System.out.println("\t" + surveyInstance.surveyedLocaleId);
                System.out.println("\t" + surveyInstance.surveyedLocaleDisplayName);

                for (QuestionAnswer questionAnswer : questionAnswers) {
                    System.out.println("\t\t\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
                    System.out.println("\t\t" + questionAnswer.value);
                    System.out.println("\t\t" + questionAnswer.type);
                    System.out.println("\t\t" + questionAnswer.oldValue);
                    System.out.println("\t\t" + questionAnswer.arbitratyNumber);
                    System.out.println("\t\t" + questionAnswer.surveyId);
                    System.out.println("\t\t" + questionAnswer.questionId);
                    System.out.println("\t\t" + questionAnswer.questionText);
                    System.out.println("\t\t" + questionAnswer.collectionDate);
                    System.out.println("\t\t" + questionAnswer.surveyInstanceId);
                    System.out.println("\t\t" + questionAnswer.keyId);
                    System.out.println("\t\t" + questionAnswer.textualQuestionId);
                    System.out.println("\t\t" + questionAnswer.questionID);
                    System.out.println("\t\t////////////////////////////////////");
                }
                System.out.println("\t)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
            }

            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }

        for (SurveyAssignment surveyAssignment : akvoFlow.getSurveyAssignments()) {
            System.out.println("[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[");

            System.out.println(surveyAssignment.name);
            System.out.println(surveyAssignment.language);
            System.out.println(surveyAssignment.devices);
            System.out.println(surveyAssignment.surveys);
            System.out.println(surveyAssignment.startDate);
            System.out.println(surveyAssignment.endDate);
            System.out.println(surveyAssignment.keyId);

            System.out.println("]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
        }
    }

    private SurveyGroup getSurveyGroup(int id) {
        return get("survey_groups/" + id, Optional.<String>absent(), SurveyGroup.Single.class).survey_group;
    }

    private SurveyGroup[] getSurveyGroups() {
        return get("survey_groups", Optional.<String>absent(), SurveyGroup.Array.class).survey_groups;
    }

    private Survey getSurvey(int id) {
        return get("surveys/" + id, Optional.<String>absent(), Survey.Single.class).survey;
    }

    private Survey[] getSurveys() {
        return get("surveys", Optional.<String>absent(), Survey.Array.class).surveys;
    }

    private QuestionGroup[] getQuestionGroups(int id) {
        return get("question_groups", of("surveyId=" + id), QuestionGroup.Array.class).question_groups;
    }

    private QuestionGroup[] getQuestionGroups() {
        return get("question_groups", Optional.<String>absent(), QuestionGroup.Array.class).question_groups;
    }

    private Question getQuestion(int id) {
        return get("questions/" + id, Optional.<String>absent(), Question.Single.class).question;
    }

    private Question[] getQuestions(int id) {
        return get("questions", of("surveyId=" + id), Question.Array.class).questions;
    }

    private QuestionAnswer[] getQuestionAnswers(int id) {
        return get("question_answers", of("surveyInstanceId=" + id), QuestionAnswer.Array.class).question_answers;
    }

    private SurveyAssignment[] getSurveyAssignments() {
        return get("survey_assignments", Optional.<String>absent(), SurveyAssignment.Array.class).survey_assignments;
    }

    private SurveyedLocale[] getSurveyedLocales(int id) {
        return get("surveyed_locales", of("surveyGroupId=" + id), SurveyedLocale.Array.class).surveyed_locales;
    }

    private SurveyInstance[] getSurveyInstances(int id) {
        return get("survey_instances", of("surveyId=" + id), SurveyInstance.Array.class).survey_instances;
    }

    private <T> T get(String location, Optional<String> parameters, Class<T> type) {
        final DefaultHttpClient client = new DefaultHttpClient();

        try {
            String resource = "/api/v1/" + location;
            String url = "http://" + server + resource;
            String date = String.valueOf(System.currentTimeMillis() / 1000);
            String plaintext = String.format("%s\n%s\n%s", "GET", date, resource);
            String signature = Base64.encodeBase64String(mac.doFinal(plaintext.getBytes()));
            HttpGet request = new HttpGet(parameters.isPresent() ? url + "?" + parameters.get() : url);

            request.addHeader("Date", date);
            request.addHeader("Authorization", String.format("%s:%s", access, signature));

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            try (InputStream inputStream = entity.getContent()) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    return new ObjectMapper().readValue(inputStream, type);
                } else {
                    ByteStreams.copy(inputStream, System.err);
                    throw new RuntimeException(response.getStatusLine().toString());
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}
