package com.company.cubamapexample.web.salesterritory;

import com.company.cubamapexample.entity.SalesTerritory;
import com.company.cubamapexample.web.style.MapViewerHelper;
import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.GeoPoint;
import com.haulmont.charts.gui.map.model.Polygon;
import com.haulmont.charts.gui.map.model.drawing.*;
import com.haulmont.cuba.gui.components.AbstractEditor;

import javax.inject.Inject;
import java.util.Collections;

public class SalesTerritoryEdit extends AbstractEditor<SalesTerritory> {

    protected final static String POLYGON_COLOR = "#ffe133";
    protected final static Double POLYGON_OPACITY = 0.5;

    @Inject
    private MapViewer map;

    @Override
    public void ready() {
        addExistingPolygonOverlay();
        setMapCenter();
        super.ready();
    }

    private void addExistingPolygonOverlay() {
        com.haulmont.charts.gui.map.model.Polygon polygon = MapViewerHelper.WKTPolygonToMapPolygon(map, getItem().getPolygonGeometry());
        if (polygon != null) {
            polygon.setFillColor(POLYGON_COLOR);
            polygon.setFillOpacity(POLYGON_OPACITY);
            polygon.setEditable(true);
            map.addPolygonOverlay(polygon);
            addPolygonEditListener();
        }
    }

    private void setMapCenter() {
        if (getItem().getPolygonGeometry() != null) {
            Polygon polygon = MapViewerHelper.WKTPolygonToMapPolygon(map, getItem().getPolygonGeometry());
            double[] center = MapViewerHelper.getCenter(polygon);
            GeoPoint centerPoint = map.createGeoPoint(center[0], center[1]);
            map.setCenter(centerPoint);
        }
    }

    @Override
    protected void initNewItem(SalesTerritory item) {
        initDrawingMode();
        super.initNewItem(item);
    }

    private void initDrawingMode() {
        DrawingOptions drawingOptions = new DrawingOptions();
        PolygonOptions polygonOptions = new PolygonOptions(false, true, POLYGON_COLOR,
                POLYGON_OPACITY);
        polygonOptions.setStrokeWeight(1);
        CircleOptions circleOptions = new CircleOptions();
        ControlOptions controlOptions = new ControlOptions(Position.TOP_CENTER, Collections.singletonList(OverlayType.POLYGON));
        drawingOptions.setPolygonOptions(polygonOptions);
        drawingOptions.setCircleOptions(circleOptions);
        drawingOptions.setEnableDrawingControl(true);
        drawingOptions.setDrawingControlOptions(controlOptions);
        map.setDrawingOptions(drawingOptions);
        map.addPolygonCompleteListener(event ->
                getItem().setPolygonGeometry(MapViewerHelper.mapPolygonToWKTPolygon(event.getPolygon())));
        addPolygonEditListener();
    }


    private void addPolygonEditListener() {
        map.addPolygonEditListener(polygonEditEvent -> getItem().
                setPolygonGeometry(MapViewerHelper.mapPolygonToWKTPolygon(polygonEditEvent.getPolygon())));
    }
}