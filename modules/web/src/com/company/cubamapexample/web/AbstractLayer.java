/*
 * TODO Copyright
 */

package com.company.cubamapexample.web;

import com.company.cubamapexample.geometryutils.HasCoordinates;
import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import java.util.UUID;

public abstract class AbstractLayer<T extends Entity<UUID> & HasCoordinates> implements MapLayer {

    protected CollectionDatasource<T, UUID> ds;
    protected MapViewer map;
    protected boolean added = false;

    public AbstractLayer(MapViewer map, CollectionDatasource<T, UUID> ds) {
        this.map = map;
        this.ds = ds;
    }

    public void setDs(CollectionDatasource<T, UUID> ds) {
        this.ds = ds;
    }
}
