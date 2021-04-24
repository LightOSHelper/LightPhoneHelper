package com.jhayes.lightosminilauncher;

import android.content.Context;

import java.util.concurrent.Callable;

public class AddActionActionModel extends ActionModel {

    private final Callable clickFunc;
    private final String appnName;

    public AddActionActionModel(String appName, Callable func) {
        super(appName);
        this.appnName = appName;
        this.clickFunc = func;
    }

    @Override
    void executeAction(Context context) throws Exception {
        clickFunc.call();
    }

    public String getAppnName() {
        return appnName;
    }
}
