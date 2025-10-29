package com.ermr.eromoro.location;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class GeoHelper {

    private final GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);

    public Point toPoint(double latitude, double longitude) {
        Point p = gf.createPoint(new Coordinate(longitude, latitude));
        p.setSRID(4326);
        return p;
    }
}
