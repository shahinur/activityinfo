package org.activityinfo.load;

import com.google.common.collect.Lists;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import java.util.Arrays;
import java.util.List;

public class StringDistribution {
    private List<String> strings;
    private UniformIntegerDistribution distribution;

    public StringDistribution(List<String> strings) {
        this.strings = strings;
        this.distribution = new UniformIntegerDistribution(0, this.strings.size()-1);
    }

    public StringDistribution(String... strings) {
        this(Arrays.asList(strings));
    }

    public StringDistribution(char[] chars) {
        this(toStringList(chars));
    }

    private static List<String> toStringList(char[] chars) {
        List<String> strings = Lists.newArrayList();
        for(int i=0;i!=chars.length;++i) {
            strings.add(String.valueOf(chars[i]));
        }
        return strings;
    }

    public String sample() {
        int index = distribution.sample();
        return strings.get(index);
    }

}
