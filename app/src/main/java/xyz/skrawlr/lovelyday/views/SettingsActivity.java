package xyz.skrawlr.lovelyday.views;

import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import xyz.skrawlr.lovelyday.R;
import xyz.skrawlr.lovelyday.reminder.AlarmReceiver;

import static xyz.skrawlr.lovelyday.views.MainActivity.MY_PREFS_NAME;

public class SettingsActivity extends AppCompatPreferenceActivity implements TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private AlarmReceiver alarmReceiver = new AlarmReceiver();
    private String dayOfBirth = "1";
    private String monthOfBirth = "January";
    private int day = 0;
    private int month = 0;
    private AlertDialog alertDialogDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.xml.pref_general);

        Preference btnDateFilter = (Preference) findPreference("btnDateFilter");
        btnDateFilter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                showDateDialog();
                return false;
            }
        });
        Preference btnTimeFilter = (Preference) findPreference("btnTimeFilter");
        btnTimeFilter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                showTimeDialog();
                return false;
            }
        });
        Preference btnAboutUs = (Preference) findPreference("btnAboutUs");
        btnAboutUs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                showAboutDialog();
                return false;
            }
        });
        Preference btnPrivacy = (Preference) findPreference("btnPrivacy");
        btnPrivacy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                showPrivacyDialog();
                return false;
            }
        });
        Preference btnRate = (Preference) findPreference("btnRate");
        btnRate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit().putBoolean("DISABLED", true).commit();
                return false;
            }
        });
//        Preference btnBuy = (Preference) findPreference("btnBuy");
//        btnBuy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference preference) {
//                return false;
//            }
//        });
        getListView().setBackgroundColor(getResources().getColor(R.color.backGround));
        //Feature Task 11
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (Objects.equals(key, getResources().getString(R.string.pref_key_notification_activation))) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_BOOT_COMPLETED);

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
            }
        };
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog_about, null));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                       }
        });
        builder.create();
        builder.show();
    }

    private void showPrivacyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog_privacy, null));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create();
        builder.show();
    }

    private void displayDateSpinnerAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog_date, null));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                saveBirthDay();
            }
        });
        alertDialogDate = builder.create();
        alertDialogDate.show();
        ((TextView) alertDialogDate.findViewById(R.id.dateTextViewMessage)).setText(getResources().getString(R.string.please_select_your_birthday));

        Spinner monthesSpinner = (Spinner) alertDialogDate.findViewById(R.id.monthes_spinner);

        ArrayList<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.monthes_array)));
        ArrayAdapter<String> monthesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        monthesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthesSpinner.setAdapter(monthesArrayAdapter);
        monthesSpinner.setOnItemSelectedListener(this);

        Spinner daysSpinner = (Spinner) alertDialogDate.findViewById(R.id.days_spinner);

        ArrayList<String> dayslist = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.days_array31)));
        ArrayAdapter<String> daysArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dayslist);
        daysArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(daysArrayAdapter);
        daysSpinner.setOnItemSelectedListener(this);

        daysSpinner.setSelection(day - 1);
        monthesSpinner.setSelection(month);

        alertDialogDate.getWindow().setBackgroundDrawableResource(R.color.backGround);
    }

    private void saveBirthDay() {
        int mm = 0;
        int dd = Integer.parseInt(dayOfBirth);

        switch (monthOfBirth) {
            case "January":
                mm = 0;
                break;
            case "February":
                mm = 1;
                break;
            case "March":
                mm = 2;
                break;
            case "April":
                mm = 3;
                break;
            case "May":
                mm = 4;
                break;
            case "June":
                mm = 5;
                break;
            case "July":
                mm = 6;
                break;
            case "August":
                mm = 7;
                break;
            case "September":
                mm = 8;
                break;
            case "October":
                mm = 9;
                break;
            case "November":
                mm = 10;
                break;
            case "December":
                mm = 11;
                break;
        }

        dateToSign(mm, dd);

    }

    public void dateToSign(int month, int dayOfMonth) {
        String sign = "aries";
        String dis = "Aries";

        month += 1;
        Integer mmdd = (month) * 100 + (dayOfMonth);
        if (mmdd <= 119) {
            sign = "capricorn";
            dis = "Capricorn";
        } else if (120 <= mmdd && mmdd <= 218) {
            sign = "aquarius";
            dis = "Aquarius";
        } else if (219 <= mmdd && mmdd <= 320) {
            sign = "pisces";
            dis = "Pisces";
        } else if (321 <= mmdd && mmdd <= 419) {
            sign = "aries";
            dis = "Aries";
        } else if (420 <= mmdd && mmdd <= 520) {
            sign = "taurus";
            dis = "Taurus";
        } else if (521 <= mmdd && mmdd <= 620) {
            sign = "gemini";
            dis = "Gemini";
        } else if (621 <= mmdd && mmdd <= 722) {
            sign = "cancer";
            dis = "Cancer";
        } else if (723 <= mmdd && mmdd <= 822) {
            sign = "leo";
            dis = "Leo";
        } else if (823 <= mmdd && mmdd <= 922) {
            sign = "virgo";
            dis = "Virgo";
        } else if (923 <= mmdd && mmdd <= 1022) {
            sign = "libra";
            dis = "Libra";
        } else if (1023 <= mmdd && mmdd <= 1121) {
            sign = "scorpio";
            dis = "Scorpio";
        } else if (1122 <= mmdd && mmdd <= 1221) {
            sign = "sagittarius";
            dis = "Sagittarius";
        } else if (1222 <= mmdd) {
            sign = "capricorn";
            dis = "Capricorn";
        }
        month -= 1;

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        String birthdate = "";
        if (month < 10) {
            birthdate = birthdate + "0" + Integer.toString(month);
        } else {
            birthdate = birthdate + Integer.toString(month);
        }
        if (dayOfMonth < 10) {
            birthdate = birthdate + "0" + Integer.toString(dayOfMonth);
        } else {
            birthdate = birthdate + Integer.toString(dayOfMonth);
        }

        editor.putString(getString(R.string.pref_birth_date), birthdate);
        editor.commit();
        editor.putString(getString(R.string.pref_sign_key), sign);
        editor.commit();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog_sign, null));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        SignView signView = (SignView) dialog.findViewById(R.id.signViewDiag);
        signView.setSign(sign);
        TextView signTextView = (TextView) dialog.findViewById(R.id.signTextView);
        signTextView.setText(String.format("%s%s", getString(R.string.your_sign_is), " " + dis));
        dialog.getWindow().setBackgroundDrawableResource(R.color.backGround);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(listener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        LocalBroadcastManager.getInstance(this).registerReceiver(alarmReceiver,
                intentFilter);
    }

    //Feature Task 11
    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(listener);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(alarmReceiver);
    }

    private void showDateDialog() {
        // Use the current date or the registered date as the default date in the picker
        String birthDate = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString(getResources().getString(R.string.pref_birth_date), "");
        if (!birthDate.equals("")) {
            month = Integer.parseInt(birthDate.substring(0, 2));
            day = Integer.parseInt(birthDate.substring(2, 4));
        }
        displayDateSpinnerAlertDialog();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        String hourOfDayS;
        String minuteS;
        if (hourOfDay < 10) {
            hourOfDayS = "0" + Integer.toString(hourOfDay);
        } else {
            hourOfDayS = Integer.toString(hourOfDay);
        }
        if (minute < 10) {
            minuteS = "0" + Integer.toString(minute);
        } else {
            minuteS = Integer.toString(minute);
        }
        editor.putString(getString(R.string.pref_notification_time), hourOfDayS + ":" + minuteS);
        editor.commit();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_BOOT_COMPLETED);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Toast.makeText(getApplicationContext(), "Notification set everyday at " + hourOfDayS + ":" + minuteS + "!", Toast.LENGTH_SHORT).show();
    }

    private void showTimeDialog() {
        // Use the current time or the registered time as the default date in the picker
        String notificationTime = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString(getResources().getString(R.string.pref_notification_time), "");
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        if (!notificationTime.equals("")) {
            hour = Integer.parseInt(notificationTime.substring(0, 2));
            minute = Integer.parseInt(notificationTime.substring(3, 5));
        }
        new TimePickerDialog(this, this, hour, minute, true).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner daysSpinner = (Spinner) alertDialogDate.findViewById(R.id.days_spinner);
        Spinner monthesSpinner = (Spinner) alertDialogDate.findViewById(R.id.monthes_spinner);
        ArrayAdapter<CharSequence> daysArrayAdapter = (ArrayAdapter<CharSequence>) daysSpinner.getAdapter();

        if (parent.getItemAtPosition(position).equals("January") ||
                parent.getItemAtPosition(position).equals("March") ||
                parent.getItemAtPosition(position).equals("May") ||
                parent.getItemAtPosition(position).equals("July") ||
                parent.getItemAtPosition(position).equals("August") ||
                parent.getItemAtPosition(position).equals("October") ||
                parent.getItemAtPosition(position).equals("December")) {
            daysArrayAdapter.clear();
            daysArrayAdapter.addAll(getResources().getStringArray(R.array.days_array31));
            monthOfBirth = (String) parent.getItemAtPosition(position);
        } else if (parent.getItemAtPosition(position).equals("February")) {
            daysArrayAdapter.clear();
            daysArrayAdapter.addAll(getResources().getStringArray(R.array.days_array29));
            monthOfBirth = (String) parent.getItemAtPosition(position);
        } else if (parent.getItemAtPosition(position).equals("April") ||
                parent.getItemAtPosition(position).equals("June") ||
                parent.getItemAtPosition(position).equals("September") ||
                parent.getItemAtPosition(position).equals("November")) {
            daysArrayAdapter.clear();
            daysArrayAdapter.addAll(getResources().getStringArray(R.array.days_array30));
            monthOfBirth = (String) parent.getItemAtPosition(position);
        } else {
            dayOfBirth = (String) parent.getItemAtPosition(position);
        }

        if (!dayOfBirth.equals("") && !monthOfBirth.equals("")) {
            alertDialogDate.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        } else {
            alertDialogDate.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
