package org.activityinfo.store.hrd.dao;

import java.util.Date;

public class ConstantClock implements Clock {
    private final long time;

    public ConstantClock(Date date) {
        this.time = date.getTime();
    }

    @Override
    public long getTime() {
        return time;
    }
}
