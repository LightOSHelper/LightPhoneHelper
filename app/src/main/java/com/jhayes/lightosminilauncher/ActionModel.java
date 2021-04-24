package com.jhayes.lightosminilauncher;

import android.content.Context;

public abstract class ActionModel {

    public ActionModel(String title){
        Title = title;
    }

    private String Title;
    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }

    abstract void executeAction(Context context) throws Exception;
}

