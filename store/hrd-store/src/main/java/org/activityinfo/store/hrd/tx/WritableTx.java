package org.activityinfo.store.hrd.tx;

public interface WritableTx extends ReadableTx {

    void put(IsEntity entity);

    void commit();

    void rollback();
}
