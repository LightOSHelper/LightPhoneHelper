package com.jhayes.lightosminilauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.UUID;

public class OpenAppActionModel extends ActionModel {
    private final String cls;
    private final String pkg;
    private final UUID uuid;

    public OpenAppActionModel(String title, String pkg, String cls, UUID uuid) {
        super(title);
        this.pkg = pkg;
        this.cls = cls;
        this.uuid = uuid;
    }

    public void executeAction(Context context) {
        Intent intent;
        if(cls != null){
            String fullIntentAction = pkg+"."+cls;
            intent = new Intent(fullIntentAction);
            intent.setPackage(this.pkg);
            ComponentName name = new ComponentName(pkg,
                    fullIntentAction);
            intent.setComponent(name);
        }
        else {
            intent = context.getPackageManager().getLaunchIntentForPackage(pkg);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    public UUID getUuid() {
        return uuid;
    }
}
