package com.company.cubamapexample.web.screens.salesrorderchartwindow;

import com.company.cubamapexample.entity.SalesPerson;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Embedded;
import com.haulmont.cuba.gui.components.Label;

import javax.inject.Inject;
import java.util.Map;

public class SalesOrderChartWindow extends AbstractWindow {

    @WindowParam
    protected SalesPerson salesPerson;

    @Inject
    private Configuration configuration;

    @Inject
    private Label nameValue;
    @Inject
    private Label phoneValue;
    @Inject
    private Embedded personPhoto;

    @Override
    public void init(Map<String, Object> params) {
        initWindowParams();
        super.init(params);
    }

    private void initWindowParams() {
        initPhoto();
        initSalesPersonData();
    }

    private void initSalesPersonData() {
        nameValue.setValue(salesPerson.getName());
        phoneValue.setValue(salesPerson.getPhone());
    }

    private void initPhoto() {
        if (salesPerson != null) {
            GlobalConfig config = configuration.getConfig(GlobalConfig.class);
            String photoURL = config.getDispatcherBaseUrl() + "/getPhoto/" + salesPerson.getId() + '-' + salesPerson.getVersion() + ".png";
            personPhoto.setSource(photoURL);
            personPhoto.setType(Embedded.Type.IMAGE);
        }
    }
}