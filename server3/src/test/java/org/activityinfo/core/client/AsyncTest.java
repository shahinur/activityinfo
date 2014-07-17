package org.activityinfo.core.client;

import com.google.common.base.Function;
import org.activityinfo.promise.Promise;
import org.junit.Test;

import javax.annotation.Nullable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;


public class AsyncTest {


    @Test
    public void normallyResolved() {

        Promise<Integer> promise = new Promise<Integer>();
        assertFalse(promise.isSettled());
        assertThat(promise.getState(), equalTo(Promise.State.PENDING));

        promise.resolve(64);

        assertThat(promise.getState(), equalTo(Promise.State.FULFILLED));
        assertThat(promise, PromiseMatchers.resolvesTo(equalTo(64)));

        Function<Integer, Double> takeSquareRoot = new Function<Integer, Double>() {

            @Nullable
            @Override
            public Double apply(@Nullable Integer integer) {
                return Math.sqrt(integer);
            }
        };

        assertThat(promise.then(takeSquareRoot), PromiseMatchers.resolvesTo(equalTo(8.0)));
    }

    @Test
    public void retryable() {




    }
}
