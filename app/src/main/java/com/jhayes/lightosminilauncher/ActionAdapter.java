package com.jhayes.lightosminilauncher;

import android.app.Notification;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jhayes.lightosminilauncher.ActionModel;
import java.util.List;


public class ActionAdapter extends BaseAdapter implements View.OnClickListener {
    private List<ActionModel> actionList;
    private Context context;

    public ActionAdapter(Context context, List<ActionModel> list) {
        this.context = context;
        actionList = list;
    }

    @Override
    public int getCount() {
        return actionList.size();
    }

    @Override
    public Object getItem(int pos) {
        return actionList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }


    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        try {
            // get selected entry
            ActionModel action = actionList.get(pos);

            // inflating list view layout if null
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_view_item, parent, false);
            }
            convertView.setTag(pos);
            TextView titleView = (TextView) convertView.findViewById(R.id.actionTitle);
            titleView.setText(action.getTitle());
            convertView.setOnClickListener(this);
        } catch (Exception ex) {
            String a = "";
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        ActionModel action = actionList.get(position);
        try {
            action.executeAction(context);
        } catch (Exception e) {
            Exception ex = e;// todo handle exceptions
        }
    }
}