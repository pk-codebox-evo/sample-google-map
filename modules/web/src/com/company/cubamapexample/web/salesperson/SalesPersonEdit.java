package com.company.cubamapexample.web.salesperson;

import com.company.cubamapexample.entity.SalesPerson;
import com.company.cubamapexample.entity.SalesTerritory;
import com.company.cubamapexample.web.style.MapViewerHelper;
import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.GeoPoint;
import com.haulmont.charts.gui.map.model.Marker;
import com.haulmont.charts.gui.map.model.Polygon;
import com.haulmont.cuba.core.app.FileStorageService;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.vaadin.shared.ui.colorpicker.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

public class SalesPersonEdit extends AbstractEditor<SalesPerson> {

    protected static final String DEFAULT_POLYGON_COLOR = "#0000ff";

    protected static final int IMG_HEIGHT = 115;
    protected static final int IMG_WIDTH = 115;
    private static final Logger log = LoggerFactory.getLogger(SalesPersonEdit.class);
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
    private Datasource<SalesPerson> salesPersonDs;

    @Named("fieldGroup.photo")
    private FileUploadField photoField;
    @Named("fieldGroup.territory")
    private PickerField territoryField;
    @Named("fieldGroup.latitude")
    private TextField latitudeTextField;
    @Named("fieldGroup.longitude")
    private TextField longitudeTextField;

    protected Marker salesPersonLocationMarker;

    protected Polygon territoryPolygon;

    @Override
    public void init(Map<String, Object> params) {
        addMapClickListener();
        addMarkerDragListener();
        addTerritoryFieldChangeListener();
        super.init(params);
    }

    @Override
    protected void initNewItem(SalesPerson item) {
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
                SalesTerritory st = (SalesTerritory) e.getValue();
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
        salesPersonDs.addItemPropertyChangeListener(e -> {
            if ("photo".equals(e.getProperty())) {
                if (e.getValue() == null) {
                    personPhoto.setVisible(false);
                }
            }
        });
    }

    private void displayPhoto() {
        byte[] bytes = null;
        if (getItem().getPhoto() != null) {
            try {
                bytes = fileStorageService.loadFile(getItem().getPhoto());
            } catch (FileStorageException e) {
                log.error("Unable to load image file", e);
                showNotification("Unable to load image file", NotificationType.HUMANIZED);
            }
        }
        if (bytes != null) {
            personPhoto.setSource(getItem().getPhoto().getName(), new ByteArrayInputStream(bytes));
            personPhoto.setType(Embedded.Type.IMAGE);
            BufferedImage image;
            try {
                image = ImageIO.read(new ByteArrayInputStream(bytes));
                int width = image.getWidth();
                int height = image.getHeight();

                if (((double) height / (double) width) > ((double) IMG_HEIGHT / (double) IMG_WIDTH)) {
                    personPhoto.setHeight(String.valueOf(IMG_HEIGHT));
                    personPhoto.setWidth(String.valueOf(width * IMG_HEIGHT / height));
                } else {
                    personPhoto.setWidth(String.valueOf(IMG_WIDTH));
                    personPhoto.setHeight(String.valueOf(height * IMG_WIDTH / width));
                }
            } catch (IOException e) {
                log.error("Unable to resize image", e);
            }
            personPhoto.setVisible(false);
            personPhoto.setVisible(true);
        } else {
            personPhoto.setVisible(false);
        }
    }


    private void setMapCenter() {
        if (getItem().getLatitude() != null && getItem().getLongitude() != null) {
            GeoPoint center = map.createGeoPoint(getItem().getLatitude(), getItem().getLongitude());
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
        if (getItem().getPhoto() != null) {
            GlobalConfig config = configuration.getConfig(GlobalConfig.class);
            String urlString = config.getWebAppUrl();
            personPhoto.setSource(urlString + "/dispatch/getPhoto/" + getItem().getId() + ".png");
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


    private void setDefaultColorPickerValue(SalesPerson item) {
        Color color = hex2Rgb(DEFAULT_POLYGON_COLOR);
        colorPicker.setValue(color);
        item.setPolygonColor(DEFAULT_POLYGON_COLOR);
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
            setMarkerPosition(lat, lon);
            setLatLonTextFieldValues(lat, lon);
        }
    }

    private void addMapClickListener() {
        map.addMapClickListener(event -> {
            setMarkerPosition(event.getPosition().getLatitude(), event.getPosition().getLongitude());
        });
    }

    private void setMarkerPosition(double lat, double lon) {
        if (salesPersonLocationMarker == null) {
            salesPersonLocationMarker = map.createMarker();
            salesPersonLocationMarker.setDraggable(true);
            GeoPoint position = map.createGeoPoint(lat, lon);
            salesPersonLocationMarker.setPosition(position);
            map.addMarker(salesPersonLocationMarker);
            setLatLonTextFieldValues(lat, lon);
        }
    }

}