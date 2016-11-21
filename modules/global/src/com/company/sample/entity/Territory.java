package com.company.sample.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@NamePattern("%s|name")
@Table(name = "SAMPLE_TERRITORY")
@Entity(name = "sample$Territory")
public class Territory extends StandardEntity {
    private static final long serialVersionUID = -8150713728215605172L;

    @Column(name = "NAME")
    protected String name;

    @Lob
    @Column(name = "POLYGON_GEOMETRY")
    protected String polygonGeometry;

    public String getPolygonGeometry() {
        return polygonGeometry;
    }

    public void setPolygonGeometry(String polygonGeometry) {
        this.polygonGeometry = polygonGeometry;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}