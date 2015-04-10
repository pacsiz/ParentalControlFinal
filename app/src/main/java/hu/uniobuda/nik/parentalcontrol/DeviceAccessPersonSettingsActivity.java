package hu.uniobuda.nik.parentalcontrol;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class DeviceAccessPersonSettingsActivity extends Activity {

    TextView personName;
    CheckBox isAccessEnabled;
    EditText from;
    EditText to;
    Button selectDays;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_access_person_settings);
        personName = (TextView) findViewById(R.id.personName);
        isAccessEnabled = (CheckBox) findViewById(R.id.checkBoxAccessControlIsEnabled);
        from = (EditText) findViewById(R.id.editTextFrom);
        to = (EditText) findViewById(R.id.editTextTo);
        selectDays = (Button) findViewById(R.id.btnSelectDays);
        save = (Button) findViewById(R.id.btnSaveDeviceAccessSettings);

        String name = getIntent().getStringExtra(getString(R.string.EXTRA_PERSON_NAME)).toLowerCase();
        personName.setText(name);

        SharedPreferences sh = getSharedPreferences(name, Context.MODE_PRIVATE);
        final Editor e = sh.edit();

        isAccessEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.putBoolean(getString(R.string.SHAREDPREFERENCE_DEVICE_ACCESS_ISENABLED),
                        isAccessEnabled.isChecked());
            }
        });

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog tpd = new TimePickerDialog(DeviceAccessPersonSettingsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                from.setText(hourOfDay + ":" + minute);
                                e.putString(getString(R.string.SHAREDPREFERENCE_TIME_FROM),
                                        hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);

                tpd.setTitle(R.string.selectFromTime);
                tpd.show();
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog tpd = new TimePickerDialog(DeviceAccessPersonSettingsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                to.setText(hourOfDay + ":" + minute);
                                e.putString(getString(R.string.SHAREDPREFERENCE_TIME_TO),
                                        hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);

                tpd.setTitle(R.string.selectToTime);
                tpd.show();
            }
        });

        selectDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(DeviceAccessPersonSettingsActivity.this);
                builder.setTitle(R.string.selectDays);

                final ArrayList<String> selectedDays = new ArrayList<String>();
                ListView list = new ListView(DeviceAccessPersonSettingsActivity.this);

                final ArrayList<String> days = new ArrayList<String>();
                days.add(getString(R.string.monday));
                days.add(getString(R.string.tuesday));
                days.add(getString(R.string.wednesday));
                days.add(getString(R.string.thursday));
                days.add(getString(R.string.saturday));
                days.add(getString(R.string.sunday));
                URLLIstAdapter adapter = new URLLIstAdapter(DeviceAccessPersonSettingsActivity.this, R.layout.urllist_layout, days);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.urlCheckBox);
                        checkBox.performClick();
                        if(checkBox.isChecked())
                        {
                            selectedDays.add(days.get(position));
                        }
                        else
                        {
                            selectedDays.remove(days.get(position));
                        }
                    }
                });

            }
        });


    }

}
