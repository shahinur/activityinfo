package org.activityinfo.load;

import com.google.common.base.Charsets;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static com.google.common.io.Resources.readLines;

public class RandomText {

    private final ExponentialDistribution wordCountDistribution = new ExponentialDistribution(3);
    private final UniformIntegerDistribution wordTypeDistribution = new UniformIntegerDistribution(0, 5);
    private final StringDistribution problemWords;

    private final StringDistribution characterDistribution = new StringDistribution(
            "ABCDEFGHIJKLMNabcdefghzjklmnq23455425!@#$%^&*()=".toCharArray());

    private final ExponentialDistribution wordLengthDistribution = new ExponentialDistribution(5);
    private final StringDistribution commonWords = new StringDistribution("a", "people", "foo");

    public RandomText()  {
        try {
            problemWords = new StringDistribution(
                    readLines(getResource(RandomText.class, "problem-words.txt"), Charsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String sampleLabel() {
        int numWords = 1 + (int)wordCountDistribution.sample();
        StringBuilder label = new StringBuilder();
        for(int i=0;i<numWords;++i) {
            if(i > 0) {
                label.append(" ");
            }
            appendRandomWord(label);
        }
        return label.toString();
    }

    private void appendRandomWord(StringBuilder label) {
        switch(wordTypeDistribution.sample()) {
            case 0:
                appendProblemWord(label);
                break;
            case 1:
                appendCommonWord(label);
                break;
            default:
                appendAlphaNum(label);
                break;
        }
    }

    private void appendProblemWord(StringBuilder label) {
        label.append(problemWords.sample());
    }

    private void appendCommonWord(StringBuilder label) {
        label.append(commonWords.sample());
    }

    private void appendAlphaNum(StringBuilder label) {
        int wordLength = 1+(int) wordLengthDistribution.sample();
        for(int i=0;i!=wordLength;++i) {
            label.append(characterDistribution.sample());
        }
    }

}
