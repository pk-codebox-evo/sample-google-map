/*
 * TODO Copyright
 */

package com.company.cubamapexample.web.style;

import com.company.cubamapexample.GeometryUtils.HasCoordinates;
import com.haulmont.cuba.core.entity.Entity;

import java.util.UUID;

public interface GeometryStyleProvider<T extends Entity<UUID> & HasCoordinates> {
    GeometryStyle getGeometryStyle(T entity);
}
