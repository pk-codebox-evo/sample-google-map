package com.company.cubamapexample.web.screens.mapscreen;

import com.company.cubamapexample.entity.SalesPerson;
import com.company.cubamapexample.web.style.MapViewerHelper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.GeoPoint;
import com.haulmont.charts.gui.map.model.Marker;
import com.haulmont.charts.gui.map.model.Polygon;
import com.haulmont.charts.gui.map.model.base.MarkerImage;
import com.haulmont.cuba.core.global.Configuration;
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
    private Configuration configuration;

    @Inject
    protected CollectionDatasource<SalesPerson, UUID> salesPersonsDs;

    @Inject
    protected MapViewer map;

    protected Map<Polygon, SalesPerson> polygonSalesPersonMap = new HashMap<>();

    protected BiMap<Marker, SalesPerson> salesPersonMarkers = HashBiMap.create();

    @Override
    public void ready() {
        addSalesPersonMarkersOnMap();
        initMarkerClickListener();
        initPolygonClickListener();
        super.ready();
    }

    private void addSalesPersonMarkersOnMap() {
        salesPersonsDs.refresh();
        Collection<SalesPerson> salesPersonList = salesPersonsDs.getItems();
        for (SalesPerson salesPerson : salesPersonList) {
            Marker personMarker = createMarkerForSalesPerson(salesPerson);
            map.addMarker(personMarker);
            salesPersonMarkers.put(personMarker, salesPerson);
        }
    }

    private Marker createMarkerForSalesPerson(SalesPerson salesPerson) {
        GeoPoint geoPoint = map.createGeoPoint(salesPerson.getLatitude(), salesPerson.getLongitude());
        Marker marker = map.createMarker("", geoPoint, false);
        marker.setIcon(getMarkerImageForPerson(salesPerson));
        return marker;
    }

    private void initMarkerClickListener() {
        map.addMarkerClickListener(event -> {
            SalesPerson salesPerson = salesPersonMarkers.get(event.getMarker());
            if (salesPerson.getTerritory() != null) {
                if (polygonSalesPersonMap.containsValue(salesPerson)) {
                    removePolygon(salesPerson);
                } else {
                    drawPolygon(salesPerson);
                }
            }
        });
    }

    private void initPolygonClickListener() {
        map.addPolygonClickListener(event -> {
            Polygon polygon = event.getPolygon();
            SalesPerson salesPerson = polygonSalesPersonMap.get(polygon);
            this.openWindow("salesorderchartwindow", WindowManager.OpenType.DIALOG, ParamsMap.of("salesPerson", salesPerson));
        });
    }

    private Polygon getPolygonBySalesPerson(SalesPerson salesPerson) {
        for (Map.Entry<Polygon, SalesPerson> entry : polygonSalesPersonMap.entrySet()) {
            if (entry.getValue() == salesPerson) {
                return entry.getKey();
            }
        }
        return null;
    }

    private MarkerImage getMarkerImageForPerson(SalesPerson salesPerson) {
        GlobalConfig config = configuration.getConfig(GlobalConfig.class);
        MarkerImage markerImage = map.createMarkerImage();
        markerImage.setUrl(config.getDispatcherBaseUrl() + "/getPhoto/" + salesPerson.getId() + '-' + salesPerson.getVersion() + ".png");
        markerImage.setScaledSize(map.createSize(48, 48));
        return markerImage;
    }

    private void removePolygon(SalesPerson entity) {
        Polygon polygon = getPolygonBySalesPerson(entity);
        map.removePolygonOverlay(polygon);
        polygonSalesPersonMap.remove(polygon);
    }

    private Polygon drawPolygon(SalesPerson salesPerson) {
        String polygon = salesPerson.getTerritory().getPolygonGeometry();
        Polygon personTerritoryPolygon;
        personTerritoryPolygon = MapViewerHelper.WKTPolygonToMapPolygon(map, polygon);
        polygonSalesPersonMap.put(personTerritoryPolygon, salesPerson);
        if (personTerritoryPolygon != null) {
            personTerritoryPolygon.setFillColor(salesPerson.getPolygonColor());
            personTerritoryPolygon.setFillOpacity(0.5);
            personTerritoryPolygon.setEditable(false);
            map.addPolygonOverlay(personTerritoryPolygon);
        }
        return personTerritoryPolygon;
    }
}