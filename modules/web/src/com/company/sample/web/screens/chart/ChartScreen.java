package com.company.sample.web.screens.chart;

import com.company.sample.entity.Salesperson;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Embedded;
import com.haulmont.cuba.gui.components.Label;

import javax.inject.Inject;
import java.util.Map;

public class ChartScreen extends AbstractWindow {

    @WindowParam(required = true)
    protected Salesperson salesperson;

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
        initSalespersonData();
    }

    private void initSalespersonData() {
        nameValue.setValue(salesperson.getName());
        phoneValue.setValue(salesperson.getPhone());
    }

    private void initPhoto() {
        if (salesperson != null) {
            GlobalConfig config = configuration.getConfig(GlobalConfig.class);
            String photoURL = config.getDispatcherBaseUrl() + "/getPhoto/" + salesperson.getId() + '-' + salesperson.getVersion() + ".png";
            personPhoto.setSource(photoURL);
            personPhoto.setType(Embedded.Type.IMAGE);
        }
    }
}