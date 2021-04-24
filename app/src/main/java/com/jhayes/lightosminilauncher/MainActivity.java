package com.jhayes.lightosminilauncher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.view.Window;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {
    ListView actionsListView;
    List<ActionModel> actions;
    private final String actionsListFile = "actions.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
            getSupportActionBar().hide(); //hide the title bar

            setContentView(R.layout.activity_main);
            actionsListView = findViewById(R.id.actionlistView);

            resetActionList();

        } catch (Exception ex) {
            String a = "";
        }
    }

    private Object SelectNewAction(ApplicationInfo info, String name) {
        OpenAppActionModel addAppAction = new OpenAppActionModel(name, info.packageName, null, UUID.randomUUID());
        actions.add(addAppAction);
        AddActionToFile(addAppAction);
        return null;
    }

    private void AddActionToFile(OpenAppActionModel openAppActionModel) {
        ActionsList currentActions = getActionsListFromFile();
        if(currentActions == null) {
            currentActions = new ActionsList();
        }
        currentActions.getActions().add(openAppActionModel);
        updateActionsInFile(currentActions);
    }

    private void updateActionsInFile(ActionsList currentActions) {
        Gson gson = new Gson();
        try (FileOutputStream fos = openFileOutput(actionsListFile, MODE_PRIVATE)) {
            String fileContents = gson.toJson(currentActions);
            fos.write(fileContents.getBytes());
            ResetAdapter(BuildActionsList());
        } catch (Exception ex) {
            //todo error handling
        }
    }

    private ActionsList getActionsListFromFile() {
        try {
            File file = new File(getFilesDir(), actionsListFile);
            Gson gson = new Gson();
            StringBuilder text = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();

            return gson.fromJson(text.toString(), ActionsList.class);
        }
        catch (IOException ioException) {
            //todo error handling
        }
        return null;
    }

    private void resetActionList() {
        actions = new ArrayList<>();
        actions.add(new OpenAppActionModel("Service Menu", "com.arima.servicemenu", "SerivceMainActivity", null));
        actions.add(new OpenAppActionModel("LightOS", "com.lightos", null, null));
        actions.add(new OpenAppActionModel("Settings", "com.android.settings", "Settings", null));

        ActionsList userAddedActions = getActionsListFromFile();
        if(userAddedActions!=null && userAddedActions.getActions().size() > 0){
            actions.addAll(userAddedActions.getActions());
        }
        ResetAdapter(BuildActionsList());
    }

    private void ResetAdapter(List<ActionModel> appActions) {
        ActionAdapter adapter = new ActionAdapter(this, appActions);
        actionsListView.setAdapter(adapter);
    }

    public String GetAppName(String ApkPackageName){

        String Name = "";

        ApplicationInfo applicationInfo;

        PackageManager packageManager = getPackageManager();

        try {

            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0);

            if(applicationInfo!=null){

                Name = (String)packageManager.getApplicationLabel(applicationInfo);
            }

        }catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
        return Name;
    }

    protected List<ActionModel> BuildActionsList() {
        List<ActionModel> actionsToDisplay = new ArrayList<>(actions);
        actionsToDisplay.add(getAddActionActionModel());
        actionsToDisplay.add(getRemoveActionMode());
        return actionsToDisplay;
    }

    private ActionModel getRemoveActionMode() {
        return new AddActionActionModel("Remove (-)",() -> {
            SetRemoveActionMode();
            return null;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected ActionModel getAddActionActionModel() {
        return new AddActionActionModel("Add (+)",() -> {
            SetAddActionMode();
            return null;
        });
    }

    private void SetRemoveActionMode() {
        List<ActionModel> removeActions = new ArrayList<>();
        ActionsList currentUserActions = getActionsListFromFile();
        removeActions.add(getCancel());
        if(currentUserActions != null){
            for (OpenAppActionModel action : currentUserActions.getActions()) {
                removeActions.add(new RemoveAppAction(action, () -> {
                    RemoveAction(action.getUuid());
                    return null;
                }));
            }
        }
        ResetAdapter(removeActions);
    }

    private AddActionActionModel getCancel() {
        return new AddActionActionModel("Cancel", () -> {
            resetActionList();
            return null;
        });
    }

    private void RemoveAction(UUID uuid) {
        ActionsList currentUserActions = getActionsListFromFile();
        List<OpenAppActionModel> userActions = currentUserActions.getActions();
        OpenAppActionModel actionToRemove = null;
        for (OpenAppActionModel action : userActions) {
            if (action.getUuid().equals(uuid)) {
                actionToRemove = action;
            }
        }
        if (actionToRemove != null) {
            userActions.remove(actionToRemove);
        }
        updateActionsInFile(currentUserActions);
        resetActionList();
    }

    protected void SetAddActionMode() {
        List<ActionModel> appActions = new ArrayList<>();
        UserManager userManager = (UserManager)getSystemService(Context.USER_SERVICE);
        LauncherApps launcher = (LauncherApps)getSystemService(Context.LAUNCHER_APPS_SERVICE);
        for (LauncherActivityInfo activityInfo:launcher.getActivityList(null, userManager.getUserProfiles().get(0))) {
            if(!activityInfo.getApplicationInfo().enabled) { continue; }
            String name = GetAppName(activityInfo.getApplicationInfo().packageName);
            appActions.add(new AddActionActionModel(name, () -> SelectNewAction(activityInfo.getApplicationInfo(), name)));
        }
        appActions.sort((o1, o2) -> o1.getTitle().compareTo(o2.getTitle()));
        appActions.add(0,getCancel());
        ResetAdapter(appActions);
    }
}