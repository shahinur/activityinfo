package org.activityinfo.service.cubes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.activityinfo.model.analysis.cube.AttributeLoading;
import org.activityinfo.model.analysis.cube.AttributeMapping;
import org.activityinfo.model.analysis.cube.MeasureMapping;
import org.activityinfo.model.analysis.cube.SourceMapping;
import org.activityinfo.service.tables.RowSetBuilder;

import java.util.List;

public class AttributeReaderSet {

    private final List<AttributeReader> readers = Lists.newArrayList();

    public AttributeReaderSet(CubeContext context, SourceMapping source, MeasureMapping measureMapping) {
        // add dimensions associated with the form
        Iterable<AttributeLoading> loadings = Iterables.concat(
            source.getAttributeLoadings(),
            measureMapping.getLoadings());

        for(AttributeLoading loading : loadings) {
            int attrIndex = context.getAttributeIndex(loading.getAttributeId());
            LoadedAttributeReader reader = new LoadedAttributeReader(attrIndex, loading);
            this.readers.add(reader);
        }

        for(AttributeMapping mapping : source.getAttributeMappings()) {
            MappedAttributeReader attribute = new MappedAttributeReader(mapping,
                context.getAttributeIndex(mapping.getAttributeId()));
            this.readers.add(attribute);
        }
    }



    public void scheduleRequests(RowSetBuilder rowSetBuilder) {
        for(AttributeReader reader : readers) {
            reader.scheduleRequests(rowSetBuilder);
        }
    }

    public Multimap<Integer, String> read(int rowIndex) {
        Multimap<Integer, String> tuple = HashMultimap.create();
        for(AttributeReader reader : readers) {
            double dimFactor = reader.factor(rowIndex);
            if(!Double.isNaN(dimFactor) && dimFactor != 0.0) {
                String label = reader.member(rowIndex);
                if(dimFactor != 1.0) {
                    label += "*" + String.format("%.02f", dimFactor);
                }
                tuple.put(reader.getAttributeIndex(), label);
            }
        }
        return tuple;
    }

    public void start() {
        for(AttributeReader reader : readers) {
            reader.start();
        }
    }
}
