package com.company.cubamapexample.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import java.util.Date;

@Table(name = "CUBAMAPEXAMPLE_SALES_ORDER")
@Entity(name = "cubamapexample$SalesOrder")
public class SalesOrder extends StandardEntity {
    private static final long serialVersionUID = 6089706337274003039L;

    @Column(name = "AMOUNT")
    protected Double amount;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_")
    protected Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALES_PERSON_ID")
    protected SalesPerson salesPerson;

    public void setSalesPerson(SalesPerson salesPerson) {
        this.salesPerson = salesPerson;
    }

    public SalesPerson getSalesPerson() {
        return salesPerson;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}