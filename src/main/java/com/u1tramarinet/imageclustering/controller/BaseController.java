package com.u1tramarinet.imageclustering.controller;

import com.u1tramarinet.imageclustering.BaseApplication;
import javafx.fxml.Initializable;

public abstract class BaseController<A extends BaseApplication> implements Initializable {
    protected A application;

    public final void setApplication(A application) {
        this.application = application;
    }
}
