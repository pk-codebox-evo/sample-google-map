package com.company.cubamapexample.entity;

import com.company.cubamapexample.geometryutils.HasCoordinates;
import com.company.cubamapexample.geometryutils.datatypes.GeoCoordinateDatatype;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import java.util.Set;

@NamePattern("%s|name")
@Table(name = "CUBAMAPEXAMPLE_SALES_PERSON")
@Entity(name = "cubamapexample$SalesPerson")
public class SalesPerson extends StandardEntity implements HasCoordinates {
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
    protected SalesTerritory territory;

    @OneToMany(mappedBy = "salesPerson")
    protected Set<SalesOrder> salesOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PHOTO_ID")
    protected FileDescriptor photo;

    @Column(name = "POLYGON_COLOR", length = 7)
    protected String polygonColor;

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

    public void setPhoto(FileDescriptor photo) {
        this.photo = photo;
    }

    public FileDescriptor getPhoto() {
        return photo;
    }

    public Set<SalesOrder> getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(Set<SalesOrder> salesOrder) {
        this.salesOrder = salesOrder;
    }

    public void setTerritory(SalesTerritory territory) {
        this.territory = territory;
    }

    public SalesTerritory getTerritory() {
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

    @Override
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}