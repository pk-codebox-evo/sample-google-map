package com.company.sample.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import java.util.Set;

@NamePattern("%s|name")
@Table(name = "SAMPLE_SALESPERSON")
@Entity(name = "sample$Salesperson")
public class Salesperson extends StandardEntity {
    private static final long serialVersionUID = 7616264237995894675L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "PHONE")
    protected String phone;

    @Column(name = "LATITUDE")
    @MetaProperty(datatype = GeoCoordinateDatatype.NAME)
    protected Double latitude;

    @Column(name = "LONGITUDE")
    @MetaProperty(datatype = GeoCoordinateDatatype.NAME)
    protected Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TERRITORY_ID")
    protected Territory territory;

    @OneToMany(mappedBy = "salesperson")
    protected Set<Order> order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PHOTO_ID")
    protected FileDescriptor photo;

    @Column(name = "POLYGON_COLOR", length = 7)
    protected String polygonColor;

    public FileDescriptor getPhoto() {
        return photo;
    }

    public void setPhoto(FileDescriptor photo) {
        this.photo = photo;
    }



    public void setPolygonColor(String polygonColor) {
        if (polygonColor.startsWith("#")) {
            this.polygonColor = polygonColor;
        } else {
            this.polygonColor = "#" + polygonColor;
        }
    }

    public String getPolygonColor() {
        return polygonColor;
    }



    public Set<Order> getOrder() {
        return order;
    }

    public void setOrder(Set<Order> order) {
        this.order = order;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }

    public Territory getTerritory() {
        return territory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}