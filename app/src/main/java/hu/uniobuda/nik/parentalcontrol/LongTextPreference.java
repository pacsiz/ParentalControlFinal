package hu.uniobuda.nik.parentalcontrol;

import android.content.Context;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class LongTextPreference extends Preference
{
    public LongTextPreference(Context ctx, AttributeSet attrs, int defStyle)
    {
        super(ctx, attrs, defStyle);
    }

    public LongTextPreference(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
    }

    @Override
    protected void onBindView(View view)
    {
        super.onBindView(view);

        TextView summary= (TextView)view.findViewById(android.R.id.title);
        summary.setSingleLine(false);
        summary.setMaxLines(2);
    }
}
