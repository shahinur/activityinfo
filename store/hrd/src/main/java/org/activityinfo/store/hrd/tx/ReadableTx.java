package org.activityinfo.store.hrd.tx;

import com.google.common.base.Optional;

public interface ReadableTx {

    <T extends IsEntity> T getOrThrow(IsKey<T> key);

    <T extends IsEntity> Optional<T> getIfExists(IsKey<T> key);

}
