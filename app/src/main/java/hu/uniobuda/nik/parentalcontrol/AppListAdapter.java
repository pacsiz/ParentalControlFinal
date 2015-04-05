package hu.uniobuda.nik.parentalcontrol;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton;

public class AppListAdapter extends ArrayAdapter<AppInfo> //implements CompoundButton.OnCheckedChangeListener
{

    SparseBooleanArray mCheckStates;

    private List<AppInfo> appList;
    private Context context;
    ArrayList<String> checkedApps;
    boolean[] itemChecked;

    public AppListAdapter(Context ctx, int layerSourceId, List<AppInfo> appList, ArrayList<String> checkedApps) {
        super(ctx, layerSourceId, appList);
        this.appList = appList;
        this.context = ctx;
        this.checkedApps = checkedApps;
        //mCheckStates = new SparseBooleanArray(appList.size());
        itemChecked = new boolean[appList.size()];

        for (int i = 0; i < appList.size(); i++) {
            if(checkedApps.contains(appList.get(i).pName))
                itemChecked[i]= true;
        }


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final AppInfoHolder holder;

        if (convertView == null) {
            holder = new AppInfoHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.applist_layout, parent, false);

            ImageView appIcon = (ImageView) v.findViewById(R.id.appIcon);
            TextView appName = (TextView) v.findViewById(R.id.appName);
            CheckBox appChk = (CheckBox)v.findViewById(R.id.appChk);

            holder.appName = appName;
            holder.appIcon = appIcon;
            holder.appChk =appChk;

            v.setTag(holder);
        }
        else{
            holder = (AppInfoHolder)v.getTag();
        }

        AppInfo appinfo = appList.get(position);
        holder.appName.setText(appinfo.appName);
        holder.appIcon.setImageDrawable(appinfo.appIcon);

        if (itemChecked[position])
        {
            holder.appChk.setChecked(true);
        }
        else
            holder.appChk.setChecked(false);

        holder.appChk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(holder.appChk.isChecked()){
                    Log.d("adapter", "listenerTrue");
                    itemChecked[position] = true;
                }
                else{
                    Log.d("adapter", "listenerFalse");
                    itemChecked[position] = false;
                }
            }
        });

        return v;
    }

    private static class AppInfoHolder {
        public TextView appName;
        public ImageView appIcon;
        public CheckBox appChk;
    }
}


