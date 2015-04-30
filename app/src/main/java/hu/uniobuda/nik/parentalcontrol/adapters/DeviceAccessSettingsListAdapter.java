package hu.uniobuda.nik.parentalcontrol.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import hu.uniobuda.nik.parentalcontrol.R;

public class DeviceAccessSettingsListAdapter extends ArrayAdapter<String> {
    Context context;
    String[] days;
    ArrayList<String> foreignDays;
    boolean[] itemChecked;

    public DeviceAccessSettingsListAdapter(Context context, int resource, String[] days, ArrayList<String> foreignDays, ArrayList<String> checkedDays) {
        super(context, resource, days);
        this.context = context;
        this.days = days;
        this.foreignDays = foreignDays;
        itemChecked = new boolean[days.length];

        for (int i = 0; i < days.length; i++) {
            if (checkedDays.contains(days[i]))
                itemChecked[i] = true;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final InfoHolder holder;

        if (convertView == null) {
            holder = new InfoHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.checkboxlist_layout, parent, false);

            TextView url = (TextView) v.findViewById(R.id.url);
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.urlCheckBox);

            holder.url = url;
            holder.checkBox = checkBox;

            v.setTag(holder);
        } else {
            holder = (InfoHolder) v.getTag();
        }

        holder.url.setText(foreignDays.get(position));

        if (itemChecked[position]) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {
                    itemChecked[position] = true;
                } else {
                    itemChecked[position] = false;
                }
            }
        });

        return v;
    }

    private static class InfoHolder {
        public TextView url;
        public CheckBox checkBox;
    }
}
