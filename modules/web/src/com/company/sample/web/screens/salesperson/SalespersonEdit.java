package com.company.sample.web.screens.salesperson;

import com.company.sample.entity.Salesperson;
import com.company.sample.entity.Territory;
import com.company.sample.web.MapViewerHelper;
import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.GeoPoint;
import com.haulmont.charts.gui.map.model.Marker;
import com.haulmont.charts.gui.map.model.Polygon;
import com.haulmont.cuba.core.app.FileStorageService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.vaadin.shared.ui.colorpicker.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.util.Map;

public class SalespersonEdit extends AbstractEditor<Salesperson> {

    protected static final Color DEFAULT_POLYGON_COLOR = Color.BLUE;

    private static final Logger log = LoggerFactory.getLogger(SalespersonEdit.class);

    @Inject
    private Configuration configuration;
    @Inject
    private FileStorageService fileStorageService;
    @Inject
    protected MapViewer map;

    @Inject
    protected ColorPicker colorPicker;
    @Inject
    private Embedded personPhoto;
    @Inject
    private Datasource<Salesperson> salespersonDs;

    @Named("fieldGroup.photo")
    private FileUploadField photoField;
    @Named("fieldGroup.territory")
    private PickerField territoryField;
    @Named("fieldGroup.latitude")
    private TextField latitudeTextField;
    @Named("fieldGroup.longitude")
    private TextField longitudeTextField;

    protected Marker salespersonLocationMarker;
    protected Polygon territoryPolygon;

    @Override
    public void init(Map<String, Object> params) {
        addMarkerDragListener();
        addTerritoryFieldChangeListener();
        super.init(params);
    }

    @Override
    protected void initNewItem(Salesperson item) {
        double lat = map.getCenter().getLatitude();
        double lon = map.getCenter().getLongitude();
        item.setLatitude(lat);
        item.setLongitude(lon);
        setDefaultColorPickerValue(item);
        super.initNewItem(item);
    }

    @Override
    public void ready() {
        addExistingMarkerOnMap();
        setMapCenter();
        setPersonPhoto();
        addPhotoChangeListener();
        setColorPickerValue();
        addColorPickerChangeListener();
        displayPhoto();
        super.ready();
    }

    private void setColorPickerValue() {
        if (getItem() != null && getItem().getPolygonColor() != null) {
            colorPicker.setValue(hex2Rgb(getItem().getPolygonColor()));
        }
    }

    private void addTerritoryFieldChangeListener() {
        territoryField.addValueChangeListener(e -> {
            if (territoryPolygon != null) {
                map.removePolygonOverlay(territoryPolygon);
            }
            if (e.getValue() != null) {
                Territory st = (Territory) e.getValue();
                territoryPolygon = MapViewerHelper.WKTPolygonToMapPolygon(map, st.getPolygonGeometry());
                if (territoryPolygon != null) {
                    setPolygonColor(territoryPolygon);
                    territoryPolygon.setFillOpacity(0.5);
                    map.addPolygonOverlay(territoryPolygon);
                }
            }
        });
    }

    private void setPolygonColor(Polygon territoryPolygon) {
        if (getItem() != null && getItem().getPolygonColor() != null) {
            territoryPolygon.setFillColor(getItem().getPolygonColor());
        }
    }

    private void addPhotoChangeListener() {
        photoField.addFileUploadSucceedListener(e -> {
            getItem().setPhoto(photoField.getValue());
            displayPhoto();
        });
        salespersonDs.addItemPropertyChangeListener(e -> {
            if ("photo".equals(e.getProperty())) {
                if (e.getValue() == null) {
                    personPhoto.setVisible(false);
                }
            }
        });
    }

    private void displayPhoto() {
        byte[] bytes = null;
        FileDescriptor photo = getItem().getPhoto();
        if (photo != null) {
            try {
                bytes = fileStorageService.loadFile(photo);
            } catch (FileStorageException e) {
                log.error("Unable to load image file", e);
                showNotification("Unable to load image file", NotificationType.HUMANIZED);
            }
        }
        if (bytes != null) {
            personPhoto.setSource(photo.getName(), new ByteArrayInputStream(bytes));
            personPhoto.setType(Embedded.Type.IMAGE);
            personPhoto.setVisible(false);
            personPhoto.setVisible(true);
        } else {
            personPhoto.setVisible(false);
        }
    }

    private void setMapCenter() {
        Salesperson salesperson = getItem();
        if (salesperson.getLatitude() != null && salesperson.getLongitude() != null) {
            GeoPoint center = map.createGeoPoint(salesperson.getLatitude(), salesperson.getLongitude());
            map.setCenter(center);
        }
    }

    private void addColorPickerChangeListener() {
        colorPicker.addValueChangeListener(e -> {
            getItem().setPolygonColor((String) e.getValue());
            redrawTerritoryPolygon();
        });
    }

    private void redrawTerritoryPolygon() {
        if (territoryPolygon != null) {
            map.removePolygonOverlay(territoryPolygon);
            setPolygonColor(territoryPolygon);
            territoryPolygon.setFillOpacity(0.5);
            map.addPolygonOverlay(territoryPolygon);
        }
    }

    private void setPersonPhoto() {
        Salesperson salesperson = getItem();
        if (salesperson.getPhoto() != null) {
            GlobalConfig config = configuration.getConfig(GlobalConfig.class);
            personPhoto.setSource(config.getDispatcherBaseUrl() + "/getPhoto/" + salesperson.getId() + '-' + salesperson.getVersion() + ".png");
            personPhoto.setType(Embedded.Type.IMAGE);
        } else {
            personPhoto.setVisible(false);
        }
    }

    private void addMarkerDragListener() {
        map.addMarkerDragListener(event -> {
            GeoPoint gp = event.getMarker().getPosition();
            setLatLonTextFieldValues(gp.getLatitude(), gp.getLongitude());
        });
    }

    private void setDefaultColorPickerValue(Salesperson item) {
        colorPicker.setValue(DEFAULT_POLYGON_COLOR);
        item.setPolygonColor(DEFAULT_POLYGON_COLOR.getCSS());
    }

    private Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }

    private void setLatLonTextFieldValues(double lat, double lon) {
        longitudeTextField.setValue(lon);
        latitudeTextField.setValue(lat);
    }

    private void addExistingMarkerOnMap() {
        if (getItem().getLatitude() != null && getItem().getLongitude() != null) {
            double lat = getItem().getLatitude();
            double lon = getItem().getLongitude();
            createMarkerByCoordinates(lat, lon);
            setLatLonTextFieldValues(lat, lon);
        }
    }

    private void createMarkerByCoordinates(double lat, double lon) {
        if (salespersonLocationMarker == null) {
            salespersonLocationMarker = map.createMarker();
            salespersonLocationMarker.setDraggable(true);
            GeoPoint position = map.createGeoPoint(lat, lon);
            salespersonLocationMarker.setPosition(position);
            map.addMarker(salespersonLocationMarker);
            setLatLonTextFieldValues(lat, lon);
        }
    }
}