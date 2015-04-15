package hu.uniobuda.nik.parentalcontrol;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


public class DeviceAccessPersonSettingsActivity extends Activity {

    TextView personName;
    CheckBox isAccessEnabled;
    TextView from;
    TextView to;
    Button selectDays;
    Button save;
    ArrayList<String> selectedDays = new ArrayList<String>();
    int hour;
    int minute;
    String orderedwd[] = new String[7];

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


        //Log.d("ordwdutolsó", orderedwd[6]);
        for (int i = 0; i < weekdays.length-2; i++)
        {
            orderedwd[i] = weekdays[i+2].toLowerCase();
            Log.d("ordwd",orderedwd[i]+"<->"+weekdays[i+2]);
        }
        orderedwd[6] = weekdays[1].toLowerCase();

        String name = getIntent().getStringExtra(getString(R.string.EXTRA_PERSON_NAME)).toLowerCase();
        personName.setText(name.substring(0,1).toUpperCase()+name.substring(1));

        SharedPreferences sh = getSharedPreferences(name, Context.MODE_PRIVATE);
        final Editor e = sh.edit();
        String personDays = sh.getString(getString(R.string.SHAREDPREFERENCE_SELECTED_DAYS),"");
        String[] daysArray = personDays.split(":");
        for (String day : daysArray)
        {
            selectedDays.add(day);
            Log.d("nap", day);
        }

        from.setText(sh.getString(getString(R.string.SHAREDPREFERENCE_TIME_FROM),""));
        to.setText(sh.getString(getString(R.string.SHAREDPREFERENCE_TIME_TO), ""));
        isAccessEnabled.setChecked(sh.getBoolean(getString(R.string.SHAREDPREFERENCE_ACCESS_CONTROL_FOR_PERSON), false));

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


                ListView list = new ListView(DeviceAccessPersonSettingsActivity.this);

                final ArrayList<String> foreignDays = new ArrayList<String>();
                foreignDays.add(getString(R.string.monday));
                foreignDays.add(getString(R.string.tuesday));
                foreignDays.add(getString(R.string.wednesday));
                foreignDays.add(getString(R.string.thursday));
                foreignDays.add(getString(R.string.friday));
                foreignDays.add(getString(R.string.saturday));
                foreignDays.add(getString(R.string.sunday));
                DeviceAccessPersonSettingsListAdapter adapter = new DeviceAccessPersonSettingsListAdapter
                        (DeviceAccessPersonSettingsActivity.this, R.layout.urllist_layout, orderedwd, foreignDays, selectedDays);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.urlCheckBox);
                        checkBox.performClick();
                        if(checkBox.isChecked())
                        {
                            selectedDays.add(orderedwd[position]);
                        }
                        else
                        {
                            selectedDays.remove(orderedwd[position]);
                        }
                    }
                });

                builder.setView(list);
                builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            String days ="";
                            StringBuilder sb = new StringBuilder();
                            for (String day : selectedDays)
                            {
                                sb.append(day+":");
                            }
                            Log.d("mentett napok", sb.toString());

                            e.putString(getString(R.string.SHAREDPREFERENCE_SELECTED_DAYS),sb.toString());
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
