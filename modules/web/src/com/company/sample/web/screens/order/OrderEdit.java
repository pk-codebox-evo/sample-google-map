package com.company.sample.web.screens.order;

import com.company.sample.entity.Order;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupPickerField;

import javax.inject.Named;
import java.util.Map;

public class OrderEdit extends AbstractEditor<Order> {

    @Named("fieldGroup.salesperson")
    private LookupPickerField salespersonField;

    @Override
    public void init(Map<String, Object> params) {
        salespersonField.addLookupAction();
    }
}