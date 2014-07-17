package org.activityinfo.legacy.shared.adapter;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.common.base.Function;
import org.activityinfo.legacy.shared.command.result.ListResult;

/**
 * Extracts a unique result from a ListResult object.
 */
public class SingleListResultAdapter<T extends ModelData> implements Function<ListResult<T>, T> {

    @Override
    public T apply(ListResult<T> input) {
        if (input.getData().size() != 1) {
            throw new IllegalStateException("Expected single result, got size=" + input.getData().size());
        }
        return input.getData().get(0);
    }
}
