package com.company.cubamapexample.web;

import com.company.cubamapexample.GeometryUtils.HasCoordinates;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.InfoWindow;
import com.haulmont.charts.gui.map.model.Marker;
import com.haulmont.charts.gui.map.model.base.MarkerImage;
import com.haulmont.charts.gui.map.model.listeners.InfoWindowClosedListener;
import com.haulmont.charts.gui.map.model.listeners.click.MarkerClickListener;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.vividsolutions.jts.geom.Point;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class PointLayer<T extends Entity<UUID> & HasCoordinates> extends AbstractLayer<T> {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected BiMap<UUID, Marker> entityMarkers;
    protected BiMap<Marker, InfoWindow> markerInfoWindows;
    protected Function<T, String> infoWindowContentProvider;
    protected Function<T, String> markerIconUrlProvider;
    protected Function<T, MarkerImage> markerIconImageProvider;
    protected Function<T, String> markerCaptionProvider;
    protected BiFunction<T, MapViewer, InfoWindow> infoWindowProvider;
    protected Predicate<T> filter = t -> true;

    protected MetaClass metaClass;
    protected String pointPropertyName;
    protected CollectionDatasource.CollectionChangeListener<T, UUID> refreshLayerListener;
    protected InfoWindowClosedListener infoWindowClosedListener;

    public PointLayer(final MapViewer map, final CollectionDatasource<T, UUID> ds) {
        super(map, ds);
        entityMarkers = HashBiMap.create();
        markerInfoWindows = HashBiMap.create();
        metaClass = ds.getMetaClass();
        initDefaultProviders();
        initListeners(map, ds);
    }

    public PointLayer(final MapViewer map, final CollectionDatasource<T, UUID> ds, String pointPropertyName) {
        this(map, ds);

        MetaProperty pointProperty;
        if (pointPropertyName != null && pointPropertyName.indexOf('.') != -1) {
            MetaPropertyPath pointPropertyPath = metaClass.getPropertyPath(pointPropertyName);
            if (pointPropertyPath == null) {
                throw new RuntimeException("Property path is not valid: " + pointPropertyName);
            }

            pointProperty = pointPropertyPath.getMetaProperty();
        } else {
            pointProperty = metaClass.getProperty(pointPropertyName);
        }

        if (pointProperty == null) {
            throw new IllegalArgumentException("Property " + pointPropertyName + " is not found in entity "
                    + metaClass.getName());
        } else if (!isPoint(pointProperty)) {
            throw new IllegalArgumentException("Property " + pointPropertyName + " of entity "
                    + metaClass.getName() + " should be of Point class");
        }

        this.pointPropertyName = pointPropertyName;
    }

    protected void initListeners(MapViewer map, CollectionDatasource<T, UUID> ds) {
        map.addMarkerClickListener(getEntityMarkerListener(map));
        refreshLayerListener = e -> refresh(false);
        ds.addCollectionChangeListener(refreshLayerListener);
        infoWindowClosedListener = event -> {
            InfoWindow iw = event.getInfoWindow();
            if (markerInfoWindows.containsValue(iw)) {
                markerInfoWindows.remove(markerInfoWindows.inverse().get(iw));
            }
        };
    }

    protected void initDefaultProviders() {
        infoWindowProvider = (T entity, MapViewer map) -> {
            String infoWindowContent = formatInfoWindowContent(entity);
            if (StringUtils.isBlank(infoWindowContent)) {
                return null;
            }
            InfoWindow infoWindow = map.createInfoWindow();
            infoWindow.setContent(infoWindowContent);
            infoWindow.setAutoPanDisabled(true);
            infoWindow.setWidth("300px");
            return infoWindow;
        };
    }

    private boolean isPoint(MetaProperty pointProperty) {
        return Point.class.isAssignableFrom(pointProperty.getJavaType());
    }

    public Predicate<T> getFilter() {
        return filter;
    }

    public void setFilter(Predicate<T> filter) {
        this.filter = filter;
    }

    public BiFunction<T, MapViewer, InfoWindow> getInfoWindowProvider() {
        return infoWindowProvider;
    }

    public void setInfoWindowProvider(BiFunction<T, MapViewer, InfoWindow> infoWindowProvider) {
        this.infoWindowProvider = infoWindowProvider;
    }

    public Function<T, String> getMarkerCaptionProvider() {
        return markerCaptionProvider;
    }

    public void setMarkerCaptionProvider(Function<T, String> markerCaptionProvider) {
        this.markerCaptionProvider = markerCaptionProvider;
    }

    public Function<T, MarkerImage> getMarkerIconImageProvider() {
        return markerIconImageProvider;
    }

    public void setMarkerIconImageProvider(Function<T, MarkerImage> markerIconImageProvider) {
        this.markerIconImageProvider = markerIconImageProvider;
    }

    public Function<T, String> getInfoWindowContentProvider() {
        return infoWindowContentProvider;
    }

    public void setInfoWindowContentProvider(Function<T, String> infoWindowContentProvider) {
        this.infoWindowContentProvider = infoWindowContentProvider;
    }

    public Function<T, String> getMarkerIconUrlProvider() {
        return markerIconUrlProvider;
    }

    public void setMarkerIconUrlProvider(Function<T, String> markerIconUrlProvider) {
        this.markerIconUrlProvider = markerIconUrlProvider;
    }

    protected MarkerClickListener getEntityMarkerListener(final MapViewer map) {
        return event -> {
            Marker marker = event.getMarker();
            if (markerInfoWindows.containsKey(marker)) {
                return;
            }

            UUID entityId = entityMarkers.inverse().get(marker);
            if (entityId == null) {
                log.warn("Marker clicked but no related entity was found");
                return;
            }

            T entity = ds.getItem(entityId);
            onMarkerClick(entity, marker);
        };
    }

    protected void beforeMarkerClicked(Marker marker, T entity) {
    }

    protected void afterMarkerClicked(Marker marker, T entity) {
    }

    public void onEntityClick(T entity) {
        if (entity == null) {
            return;
        }
        Marker marker = entityMarkers.get(entity.getId());
        if (marker != null) {
            onMarkerClick(entity, marker);
        }
    }

    protected void onMarkerClick(T entity, Marker marker) {
        beforeMarkerClicked(marker, entity);
        InfoWindow infoWindow = createInfoWindow(entity, marker);
        if (infoWindow != null) {
            markerInfoWindows.put(marker, infoWindow);
            map.openInfoWindow(infoWindow);
        }
        afterMarkerClicked(marker, entity);
    }

    private InfoWindow createInfoWindow(T entity, Marker marker) {
        if (entity == null || marker == null || infoWindowProvider == null) {
            return null;
        }

        InfoWindow infoWindow = infoWindowProvider.apply(entity, map);
        if (infoWindow != null) {
            infoWindow.setAnchorMarker(marker);
        }

        return infoWindow;
    }

    protected String formatInfoWindowContent(T entity) {
        return infoWindowContentProvider != null ? infoWindowContentProvider.apply(entity) : null;
    }

    protected Marker createMarker(T entity) {
        if (entity == null || entity.getLatitude() == null || entity.getLongitude() == null) {
            return null;
        }

        double latitude = entity.getLatitude();
        double longitude = entity.getLongitude();

        Marker marker = map.createMarker();
        marker.setPosition(map.createGeoPoint(latitude, longitude));

        if (markerCaptionProvider != null) {
            marker.setCaption(markerCaptionProvider.apply(entity));
        }

        if (markerIconImageProvider != null) {
            marker.setIcon(markerIconImageProvider.apply(entity));
        } else if (markerIconUrlProvider != null) {
            marker.setIconUrl(markerIconUrlProvider.apply(entity));
        }

        marker.setAnimationEnabled(false);

        return marker;
    }

    @Override
    public void refresh() {
        refresh(true);
    }

    @Override
    public void refresh(boolean refreshDs) {
        if (refreshDs) {
            ds.refresh();
        }

        Set<UUID> onMap = new HashSet<>();
        addNewMarkers(onMap);
        removeExtraMarkers(onMap);

        if (!added) {
            ds.addCollectionChangeListener(refreshLayerListener);
            map.addInfoWindowClosedListener(infoWindowClosedListener);
        }
        added = true;
    }

    private void removeExtraMarkers(Set<UUID> onMap) {
        Iterator it = entityMarkers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            if (!onMap.contains((UUID) e.getKey())) {
                map.removeMarker((Marker) e.getValue());
                it.remove();
            }
        }
    }

    private void addNewMarkers(Set<UUID> onMap) {
        for (T entity : ds.getItems()) {
            if (!filter.test(entity)) {
                continue;
            }

            UUID id = entity.getId();
            if (!entityMarkers.containsKey(id)) {
                Marker marker = createMarker(entity);
                if (marker == null) {
                    continue;
                }
                map.addMarker(marker);
                entityMarkers.put(id, marker);
            } else if (!added) {
                map.addMarker(entityMarkers.get(id));
            }
            onMap.add(id);
        }
    }

    @Override
    public void remove() {
        for (Marker marker : entityMarkers.values()) {
            map.removeMarker(marker);
        }
        ds.removeCollectionChangeListener(refreshLayerListener);
        map.removeInfoWindowClosedListener(infoWindowClosedListener);
        added = false;
    }

    @Override
    public void add() {
        if (!added) {
            for (Marker marker : entityMarkers.values()) {
                map.addMarker(marker);
            }
            ds.addCollectionChangeListener(refreshLayerListener);
            map.addInfoWindowClosedListener(infoWindowClosedListener);
            added = true;
        } else {
            refresh();
        }
    }

    @Override
    public void setDs(CollectionDatasource<T, UUID> ds) {
        super.setDs(ds);
        remove();
        refresh(true);
    }
}
