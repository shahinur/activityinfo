package org.activityinfo.model.analysis.cube;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Arrays;

/**
 * A tuple is used to define a slice of data from a cube; it is composed of an ordered collection of one
 * member from one or more dimensions. A tuple is used to identify specific sections of multidimensional
 * data from a cube; a tuple composed of one member from each dimension in a cube completely describes
 * a cell value. Put another way, a tuple is a vector of members; think of a tuple as one or more
 * records in the underlying database whose value in these columns falls under these categories.
 *
 */
public class TupleBuilder {

    private double value;
    private Multimap<Integer, String> members;

    private TupleBuilder() {
    }

    public static TupleBuilder withDimensionality(int numDimensions) {
        TupleBuilder tupleBuilder = new TupleBuilder();
        tupleBuilder.members = HashMultimap.create();
        return tupleBuilder;
    }

    public void addMembers(int dimensionIndex, String... members) {
        this.members.putAll(dimensionIndex, Arrays.asList(members));
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Tuple build() {
        throw new UnsupportedOperationException();
    }


}
