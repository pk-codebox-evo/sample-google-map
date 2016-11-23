package com.company.sample.web.screens.map;

import com.company.sample.entity.Salesperson;
import com.company.sample.web.MapViewerHelper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.GeoPoint;
import com.haulmont.charts.gui.map.model.Marker;
import com.haulmont.charts.gui.map.model.Polygon;
import com.haulmont.charts.gui.map.model.base.MarkerImage;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapScreen extends AbstractWindow {

    @Inject
    private GlobalConfig config;

    @Inject
    private CollectionDatasource<Salesperson, UUID> salespersonsDs;

    @Inject
    private MapViewer map;

    private Map<Polygon, Salesperson> polygonSalespersonMap = new HashMap<>();

    private BiMap<Marker, Salesperson> salespersonMarkers = HashBiMap.create();

    @Override
    public void ready() {
        addSalesPersonMarkersOnMap();
        initMarkerClickListener();
        initPolygonClickListener();
        super.ready();
    }

    private void addSalesPersonMarkersOnMap() {
        salespersonsDs.refresh();
        Collection<Salesperson> salespersonList = salespersonsDs.getItems();
        for (Salesperson salesperson : salespersonList) {
            Marker personMarker = createMarkerForSalesPerson(salesperson);
            map.addMarker(personMarker);
            salespersonMarkers.put(personMarker, salesperson);
        }
    }

    private Marker createMarkerForSalesPerson(Salesperson salesperson) {
        GeoPoint geoPoint = map.createGeoPoint(salesperson.getLatitude(), salesperson.getLongitude());
        Marker marker = map.createMarker("", geoPoint, false);
        marker.setIcon(getMarkerImageForPerson(salesperson));
        return marker;
    }

    private void initMarkerClickListener() {
        map.addMarkerClickListener(event -> {
            Salesperson salesperson = salespersonMarkers.get(event.getMarker());
            if (salesperson.getTerritory() != null) {
                if (polygonSalespersonMap.containsValue(salesperson)) {
                    removePolygon(salesperson);
                } else {
                    drawPolygon(salesperson);
                }
            }
        });
    }

    private void initPolygonClickListener() {
        map.addPolygonClickListener(event -> {
            Polygon polygon = event.getPolygon();
            Salesperson salesperson = polygonSalespersonMap.get(polygon);
            this.openWindow("chartScreen", WindowManager.OpenType.DIALOG, ParamsMap.of("salesperson", salesperson));
        });
    }

    private Polygon getPolygonBySalesPerson(Salesperson salesperson) {
        for (Map.Entry<Polygon, Salesperson> entry : polygonSalespersonMap.entrySet()) {
            if (entry.getValue() == salesperson) {
                return entry.getKey();
            }
        }
        return null;
    }

    private MarkerImage getMarkerImageForPerson(Salesperson salesperson) {
        MarkerImage markerImage = map.createMarkerImage();
        markerImage.setUrl(config.getDispatcherBaseUrl() + "/getPhoto/" + salesperson.getId() + '-' + salesperson.getVersion() + ".png");
        markerImage.setScaledSize(map.createSize(48, 48));
        return markerImage;
    }

    private void removePolygon(Salesperson entity) {
        Polygon polygon = getPolygonBySalesPerson(entity);
        map.removePolygonOverlay(polygon);
        polygonSalespersonMap.remove(polygon);
    }

    private Polygon drawPolygon(Salesperson salesperson) {
        String polygon = salesperson.getTerritory().getPolygonGeometry();
        Polygon personTerritoryPolygon;
        personTerritoryPolygon = MapViewerHelper.WKTPolygonToMapPolygon(map, polygon);
        polygonSalespersonMap.put(personTerritoryPolygon, salesperson);
        if (personTerritoryPolygon != null) {
            personTerritoryPolygon.setFillColor(salesperson.getPolygonColor());
            personTerritoryPolygon.setFillOpacity(0.5);
            personTerritoryPolygon.setEditable(false);
            map.addPolygonOverlay(personTerritoryPolygon);
        }
        return personTerritoryPolygon;
    }
}