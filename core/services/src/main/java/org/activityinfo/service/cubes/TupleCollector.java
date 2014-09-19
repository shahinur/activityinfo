package org.activityinfo.service.cubes;

import com.google.common.collect.Multimap;
import org.activityinfo.model.resource.ResourceId;

public interface TupleCollector {
    void add(double value, ResourceId measureId, Multimap<Integer, String> dimensionValues);
}
