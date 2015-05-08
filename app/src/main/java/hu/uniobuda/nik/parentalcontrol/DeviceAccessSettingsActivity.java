package hu.uniobuda.nik.parentalcontrol;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import hu.uniobuda.nik.parentalcontrol.adapters.DeviceAccessSettingsListAdapter;


public class DeviceAccessSettingsActivity extends Activity {

    TextView personName;
    CheckBox isAccessEnabled;
    TextView from;
    TextView to;
    Button selectDays;
    Button save;
    ArrayList<String> selectedDays = new ArrayList<String>();
    int hour;
    int minute;
    String orderedWeekdays[] = new String[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_access_person_settings);

        personName = (TextView) findViewById(R.id.personName);
        isAccessEnabled = (CheckBox) findViewById(R.id.checkBoxAccessControlIsEnabled);
        from = (TextView) findViewById(R.id.editTextFrom);
        to = (TextView) findViewById(R.id.editTextTo);
        selectDays = (Button) findViewById(R.id.btnSelectDays);
        save = (Button) findViewById(R.id.btnSaveDeviceAccessSettings);

        Calendar currentTime = new GregorianCalendar();
        hour = currentTime.get(Calendar.HOUR_OF_DAY);
        minute = currentTime.get(Calendar.MINUTE);
        String weekdays[] = new DateFormatSymbols(Locale.ENGLISH).getWeekdays();

        //Order weekdays
        for (int i = 0; i < weekdays.length - 2; i++) {
            orderedWeekdays[i] = weekdays[i + 2].toLowerCase();
        }
        orderedWeekdays[6] = weekdays[1].toLowerCase();

        String name = getIntent().getStringExtra(getString(R.string.EXTRA_PERSON_NAME)).toLowerCase();
        personName.setText(name.substring(0, 1).toUpperCase() + name.substring(1));

        SharedPreferences persons = getSharedPreferences(name, Context.MODE_PRIVATE);
        final Editor e = persons.edit();

        final String[] fromArr = persons.getString(getString(R.string.SHAREDPREFERENCE_TIME_FROM), "").split(":");
        final String[] toArr = persons.getString(getString(R.string.SHAREDPREFERENCE_TIME_TO), "").split(":");
        String[] days = persons.getString(getString(R.string.SHAREDPREFERENCE_SELECTED_DAYS), "").split(":");

        for (String day : days) {
            selectedDays.add(day);
        }

        if (fromArr.length == 2) {
            from.setText(fromArr[0] + ":" + (Integer.parseInt(fromArr[1]) < 10 ? "0" + fromArr[1] : fromArr[1]));
        }
        if (toArr.length == 2) {
            to.setText(toArr[0] + ":" + (Integer.parseInt(toArr[1]) < 10 ? "0" + toArr[1] : toArr[1]));
        }

        isAccessEnabled.setChecked(persons.getBoolean(getString(R.string.SHAREDPREFERENCE_ACCESS_CONTROL_FOR_PERSON), false));
        isAccessEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.putBoolean(getString(R.string.SHAREDPREFERENCE_ACCESS_CONTROL_FOR_PERSON),
                        isAccessEnabled.isChecked());
            }
        });

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog tpd = new TimePickerDialog(DeviceAccessSettingsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                from.setText(hourOfDay + ":" + (minute < 10 ? "0" + minute : minute));
                                e.putString(getString(R.string.SHAREDPREFERENCE_TIME_FROM),
                                        hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);
                tpd.setTitle(R.string.selectFromTime);
                if (fromArr.length>1) {
                    tpd.updateTime(Integer.parseInt(fromArr[0]), Integer.parseInt(fromArr[1]));
                }
                tpd.show();
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(DeviceAccessSettingsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                to.setText(hourOfDay + ":" + (minute < 10 ? "0" + minute : minute));
                                e.putString(getString(R.string.SHAREDPREFERENCE_TIME_TO),
                                        hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);
                tpd.setTitle(R.string.selectToTime);
                if (toArr.length>1) {
                    tpd.updateTime(Integer.parseInt(toArr[0]), Integer.parseInt(toArr[1]));
                }
                tpd.show();
            }
        });

        selectDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceAccessSettingsActivity.this);
                builder.setTitle(R.string.selectDays);

                ListView list = new ListView(DeviceAccessSettingsActivity.this);

                final ArrayList<String> foreignDays = new ArrayList<String>();
                foreignDays.add(getString(R.string.monday));
                foreignDays.add(getString(R.string.tuesday));
                foreignDays.add(getString(R.string.wednesday));
                foreignDays.add(getString(R.string.thursday));
                foreignDays.add(getString(R.string.friday));
                foreignDays.add(getString(R.string.saturday));
                foreignDays.add(getString(R.string.sunday));

                DeviceAccessSettingsListAdapter adapter = new DeviceAccessSettingsListAdapter
                        (DeviceAccessSettingsActivity.this, R.layout.checkboxlist_layout, orderedWeekdays, foreignDays, selectedDays);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.urlCheckBox);
                        checkBox.performClick();
                        if (checkBox.isChecked()) {
                            selectedDays.add(orderedWeekdays[position]);
                        } else {
                            selectedDays.remove(orderedWeekdays[position]);
                        }
                    }
                });

                builder.setView(list);
                builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder sb = new StringBuilder();

                        for (String day : selectedDays) {
                            sb.append(day + ":");
                        }
                        //Log.d("DeviceAccessSettingsActivity", "Saved days: "+sb.toString());

                        e.putString(getString(R.string.SHAREDPREFERENCE_SELECTED_DAYS), sb.toString());
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.commit();
                finish();
            }
        });
    }
}
