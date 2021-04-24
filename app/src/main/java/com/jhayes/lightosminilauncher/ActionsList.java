package com.jhayes.lightosminilauncher;

import java.util.ArrayList;
import java.util.List;

public class ActionsList {
    private List<OpenAppActionModel> actions;

    public ActionsList(){
        actions = new ArrayList<>();
    }

    public List<OpenAppActionModel> getActions() {
        return actions;
    }

    public void setActions(List<OpenAppActionModel> actions) {
        this.actions = actions;
    }
}
