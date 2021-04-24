package com.jhayes.lightosminilauncher;

import android.content.Context;

import java.util.concurrent.Callable;

public class RemoveAppAction extends ActionModel {
    private final OpenAppActionModel openAction;
    private Callable func;

    public RemoveAppAction(OpenAppActionModel action, Callable func) {
        super(action.getTitle());
        this.openAction = action;
        this.func = func;
    }

    @Override
    void executeAction(Context context) throws Exception {
        func.call();
    }
}
