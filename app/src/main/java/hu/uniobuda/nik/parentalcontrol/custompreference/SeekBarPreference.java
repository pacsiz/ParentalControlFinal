package hu.uniobuda.nik.parentalcontrol.custompreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import hu.uniobuda.nik.parentalcontrol.R;

public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {
    private static final String PREFERENCENS =
            "http://schemas.android.com/apk/res/hu.uniobuda.nik.parentalcontrol.seekbarpreference";
    private static final String ANDROIDNS = "http://schemas.android.com/apk/res/android";

    private static final String DEFAULT_VALUE = "defaultValue";
    private static final String MIN_VALUE = "minValue";
    private static final String MAX_VALUE = "maxValue";



    private final int defaultValue = 82;
    private final int maxValue = 100;
    private final int minValue = 30;

    private int currentValue;

    private SeekBar seekBar;
    private TextView valueText;
    Context context;


    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    @Override
    protected View onCreateDialogView() {
        SharedPreferences predictValue = context.getSharedPreferences(context.getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        currentValue = predictValue.getInt(context.getString(R.string.SHAREDPREFERENCE_PREDICT_VALUE),defaultValue);
        Log.d("SeekBarPreference", "Current value: "+currentValue);

        // Inflate layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.seekbarpreference, null);

        // Setup minimum and maximum text labels
        ((TextView) view.findViewById(R.id.min_value)).setText(Integer.toString(minValue));
        ((TextView) view.findViewById(R.id.max_value)).setText(Integer.toString(maxValue));

        // Setup SeekBar
        seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        seekBar.setMax(maxValue - minValue);
        seekBar.setProgress(currentValue - minValue);
        seekBar.setOnSeekBarChangeListener(this);

        // Setup text label for current value
        valueText = (TextView) view.findViewById(R.id.current_value);
        valueText.setText(Integer.toString(currentValue));

        setPositiveButtonText(R.string.OK);
        setNegativeButtonText(R.string.cancel);
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (!positiveResult) {
            return;
        }
        else
        {
            SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
            SharedPreferences.Editor e = settings.edit();
            e.putInt(context.getString(R.string.SHAREDPREFERENCE_PREDICT_VALUE),currentValue);
            e.commit();
        }
        if (shouldPersist()) {

 //         persistInt(currentValue);
        }
        notifyChanged();
    }

    @Override
    public CharSequence getSummary() {
        String summary = super.getSummary().toString();
        int value = getPersistedInt(defaultValue);
        return String.format(summary, value);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        currentValue = i + minValue;
        valueText.setText(Integer.toString(currentValue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
