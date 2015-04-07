package hu.uniobuda.nik.parentalcontrol;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class URLLIstAdapter extends ArrayAdapter<String> //implements CompoundButton.OnCheckedChangeListener
{
    Context context;
    ArrayList<String> urls;
    boolean[] itemChecked;

    public URLLIstAdapter(Context context, int resource, ArrayList<String> urls) {
        super(context, resource, urls);
        this.context = context;
        this.urls = urls;
        itemChecked = new boolean[urls.size()];

    }

    public void updateItemCheckedSize(int size)
    {
        itemChecked = new boolean[size];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final URLInfoHolder holder;

        if (convertView == null) {
            holder = new URLInfoHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.urllist_layout, parent, false);

            TextView url = (TextView) v.findViewById(R.id.url);
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.urlCheckBox);

            holder.url = url;
            holder.checkBox = checkBox;

            v.setTag(holder);
        } else {
            holder = (URLInfoHolder) v.getTag();
        }

        holder.url.setText(urls.get(position));

        if (itemChecked[position]) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()){
                    itemChecked[position] = true;
                }
                else{
                    itemChecked[position] = false;
                }
            }
        });

        return v;
    }

    private static class URLInfoHolder {
        public TextView url;
        public CheckBox checkBox;
    }
}
