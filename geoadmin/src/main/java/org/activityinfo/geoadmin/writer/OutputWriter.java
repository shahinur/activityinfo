package org.activityinfo.geoadmin.writer;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.feature.FeatureCollection;

import java.io.IOException;

public interface OutputWriter {

    void start(FeatureCollection features) throws IOException;

    void write(int adminEntityId, Geometry geometry) throws IOException;

    void close() throws IOException;
}
