package org.activityinfo.promise;

import com.google.common.base.Function;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


public class PromiseTest {

    @Test
    public void forEach() {

        List<Integer> numbers = Arrays.asList(1,2,3);
        Promise<Void> result = Promise.forEach(numbers, new Function<Integer, Promise<Void>>() {
            @Nullable
            @Override
            public Promise<Void> apply(@Nullable Integer input) {
                return Promise.rejected(new UnsupportedOperationException());
            }
        });

        assertThat(result.getState(), equalTo(Promise.State.REJECTED));
    }
}
