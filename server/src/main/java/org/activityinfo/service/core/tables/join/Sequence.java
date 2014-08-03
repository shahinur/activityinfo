package org.activityinfo.service.core.tables.join;

import com.google.common.collect.UnmodifiableIterator;

import java.util.Iterator;

public class Sequence implements Iterable<Integer> {

    private int fromIndex;
    private int toIndex;

    public Sequence(int fromIndex, int toIndex) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new UnmodifiableIterator<Integer>() {

            private int index = fromIndex;

            @Override
            public boolean hasNext() {
                return (index+1) < toIndex;
            }

            @Override
            public Integer next() {
                return (index++);
            }
        };
    }
}
