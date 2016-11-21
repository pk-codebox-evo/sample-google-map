package com.company.sample.web.style;

import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class MapViewerHelper {
    public static String mapPolygonToWKTPolygon(com.haulmont.charts.gui.map.model.Polygon polygon) {
        String polygonString = "POLYGON ((";
        String firstVertex = "";
        List<GeoPoint> coordinates = polygon.getCoordinates();
        int count = 0;
        for (GeoPoint geoPoint : coordinates) {
            polygonString = polygonString + geoPoint.getLongitude().toString() + " " +
                    geoPoint.getLatitude().toString();
            if (count == 0) {
                firstVertex = geoPoint.getLongitude().toString() + " " +
                        geoPoint.getLatitude().toString();
            }
            count++;
            polygonString = polygonString + ", ";
        }
        polygonString = polygonString + firstVertex;
        polygonString = polygonString + "))";
        return polygonString;
    }

    public static com.haulmont.charts.gui.map.model.Polygon WKTPolygonToMapPolygon(MapViewer map, String wktPolygon) {
        if (wktPolygon == null || map == null) {
            return null;
        }
        String coordinateString = wktPolygon.substring(10, wktPolygon.length() - 2);
        String[] coordinatePairs = coordinateString.split(",");
        List<GeoPoint> geoPoints = new ArrayList<>(coordinatePairs.length);
        for (String pair : coordinatePairs) {
            String[] lonLat = pair.trim().split(" ");
            geoPoints.add(map.createGeoPoint(Double.valueOf(lonLat[1]), Double.valueOf(lonLat[0])));
        }
        return map.createPolygon(geoPoints);
    }

    public static double[] getCenter(com.haulmont.charts.gui.map.model.Polygon polygonGeometry) {
        double lat = 0;
        double lon = 0;
        List<GeoPoint> geoPoints = polygonGeometry.getCoordinates();
        for (GeoPoint geoPoint : geoPoints) {
            lat = lat + geoPoint.getLatitude();
            lon = lon + geoPoint.getLongitude();
        }
        return new double[]{lat / geoPoints.size(), lon / geoPoints.size()};
    }
}
