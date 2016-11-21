package com.company.sample.web.style;

import com.company.sample.geometryutils.HasCoordinates;
import com.haulmont.cuba.core.entity.Entity;

import java.util.UUID;

public interface GeometryStyleProvider<T extends Entity<UUID> & HasCoordinates> {
    GeometryStyle getGeometryStyle(T entity);
}
