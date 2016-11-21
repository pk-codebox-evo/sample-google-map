package com.company.sample.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import java.util.Date;

@Table(name = "SAMPLE_ORDER")
@Entity(name = "sample$Order")
public class Order extends StandardEntity {
    private static final long serialVersionUID = 6089706337274003039L;

    @Column(name = "AMOUNT")
    protected Double amount;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_")
    protected Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALESPERSON_ID")
    protected Salesperson salesperson;

    public void setSalesperson(Salesperson salesperson) {
        this.salesperson = salesperson;
    }

    public Salesperson getSalesperson() {
        return salesperson;
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