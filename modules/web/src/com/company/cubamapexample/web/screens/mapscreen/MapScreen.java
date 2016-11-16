package com.company.cubamapexample.web.screens.mapscreen;

import com.company.cubamapexample.entity.SalesPerson;
import com.company.cubamapexample.web.PointLayer;
import com.company.cubamapexample.web.style.MapViewerHelper;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.Marker;
import com.haulmont.charts.gui.map.model.Polygon;
import com.haulmont.charts.gui.map.model.base.MarkerImage;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapScreen extends AbstractWindow {
    @Inject
    protected CollectionDatasource<SalesPerson, UUID> salesPersonsDs;
    @Inject
    protected MapViewer map;
    @Inject
    private Configuration configuration;
    protected PointLayer<SalesPerson> pointLayer;
    protected Map<Polygon, SalesPerson> polygonSalesPersonHashMap = new HashMap<>();

    @Override
    public void ready() {
        addPointLayer();
        initPolygonClickListener();
        super.ready();
    }

    private void initPolygonClickListener() {
        map.addPolygonClickListener(event -> {
            Polygon polygon = event.getPolygon();
            SalesPerson salesPerson = polygonSalesPersonHashMap.get(polygon);
            this.openWindow("salesorderchartwindow", WindowManager.OpenType.DIALOG, ParamsMap.of("salesPerson", salesPerson));
        });

    }

    private Polygon getPolygonBySalesPerson(SalesPerson salesPerson) {
        for (Map.Entry<Polygon, SalesPerson> entry : polygonSalesPersonHashMap.entrySet()) {
            if (entry.getValue() == salesPerson) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void addPointLayer() {
        pointLayer = new PointLayer<SalesPerson>(map, salesPersonsDs) {
            @Override
            protected void onMarkerClick(SalesPerson entity, Marker marker) {
                if (entity.getTerritory() != null) {
                    if (polygonSalesPersonHashMap.containsValue(entity)) {
                        Polygon polygon = getPolygonBySalesPerson(entity);
                        map.removePolygonOverlay(polygon);
                        polygonSalesPersonHashMap.remove(polygon);
                    } else {
                        drawPersonTerritoryPolygon(entity);
                    }
                }
                super.onMarkerClick(entity, marker);
            }
        };
        pointLayer.setMarkerIconImageProvider(salesPerson -> {
            GlobalConfig config = configuration.getConfig(GlobalConfig.class);
            String urlString = config.getWebAppUrl();
            MarkerImage markerImage = map.createMarkerImage();
            markerImage.setUrl(urlString + "/dispatch/getPhoto/" + salesPerson.getId() + '-' + salesPerson.getVersion() + ".png");
            markerImage.setScaledSize(map.createSize(48, 48));
            return markerImage;
        });
        pointLayer.refresh();
    }

    private Polygon drawPersonTerritoryPolygon(SalesPerson salesPerson) {
        String polygon = salesPerson.getTerritory().getPolygonGeometry();
        Polygon personTerritoryPolygon;
        personTerritoryPolygon = MapViewerHelper.WKTPolygonToMapPolygon(map, polygon);
        polygonSalesPersonHashMap.put(personTerritoryPolygon, salesPerson);
        if (personTerritoryPolygon != null) {
            personTerritoryPolygon.setFillColor(salesPerson.getPolygonColor());
            personTerritoryPolygon.setFillOpacity(0.5);
            personTerritoryPolygon.setEditable(false);
            map.addPolygonOverlay(personTerritoryPolygon);
        }
        return personTerritoryPolygon;
    }
}