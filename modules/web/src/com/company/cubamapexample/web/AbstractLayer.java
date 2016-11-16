/*
 * TODO Copyright
 */

package com.company.cubamapexample.web;

import com.company.cubamapexample.GeometryUtils.HasCoordinates;
import com.company.cubamapexample.web.style.GeometryStyle;
import com.company.cubamapexample.web.style.GeometryStyleProvider;
import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractLayer<T extends Entity<UUID> & HasCoordinates> implements MapLayer {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Map<Class<?>, GeometryStyle> staticStyles = new HashMap<>();
    protected Map<Class<?>, GeometryStyleProvider<T>> styleProviders = new HashMap<>();
    protected CollectionDatasource<T, UUID> ds;
    protected MapViewer map;
    protected boolean added = false;

    public AbstractLayer(MapViewer map, CollectionDatasource<T, UUID> ds) {
        this.map = map;
        this.ds = ds;
    }

    public void addStaticStyle(Class<?> mapGeometryClass, GeometryStyle style) {
        staticStyles.put(mapGeometryClass, style);
    }

    public void addStyleProvider(Class<?> mapGeometryClass, GeometryStyleProvider<T> styleProvider) {
        styleProviders.put(mapGeometryClass, styleProvider);
    }

    public CollectionDatasource<T, UUID> getDs() {
        return ds;
    }

    public void setDs(CollectionDatasource<T, UUID> ds) {
        this.ds = ds;
    }

    public interface EntityClickListener<T> extends Serializable {
        void clicked(T entity);
    }
}
