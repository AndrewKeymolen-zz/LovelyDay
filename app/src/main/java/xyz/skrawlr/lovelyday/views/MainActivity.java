package xyz.skrawlr.lovelyday.views;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.skrawlr.lovelyday.R;
import xyz.skrawlr.lovelyday.data.DatabaseContract;
import xyz.skrawlr.lovelyday.data.DatabaseManager;
import xyz.skrawlr.lovelyday.data.PredictionDbHelper;
import xyz.skrawlr.lovelyday.data.PredictionService;
import xyz.skrawlr.lovelyday.data.ReceivedPrediction;
import xyz.skrawlr.lovelyday.data.RetrofitPrediction;
import xyz.skrawlr.lovelyday.reminder.AlarmReceiver;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnClickListener, Spinner.OnItemSelectedListener {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Calendar calToday;
    private Calendar calLastUpdateToday;
    private Calendar calLastUpdateWeek;
    private Calendar calLastUpdateMonth;
    private AlertDialog dialogWelcome = null;
    private String dayOfBirth = "1";
    private String monthOfBirth = "January";
    private AlertDialog alertDialogDate;
    private Toast toast;
//    private InterstitialAd mInterstitialAd;
    private AlarmReceiver alarmReceiver = new AlarmReceiver();
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildrenCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildrenCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(ResourcesCompat.getFont(this, R.font.raleway_medium));
                    ((TextView) tabViewChild).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sortOrder = getResources().getString(R.string.pref_sortBy_date);

                String sortOrderPref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString(getResources().getString(R.string.pref_sortBy_key), getResources().getString(R.string.pref_sortBy_date));

                if (!sortOrderPref.equals(getString(R.string.pref_sortBy_liked))) {
                    sortOrder = getString(R.string.pref_sortBy_liked);
                }

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(getString(R.string.pref_sortBy_key), sortOrder);
                editor.commit();
                refreshFragments();
                showAToast("Sorted based on the " + sortOrder + "!");
            }
        });

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

    private void gestionNotification() {
        Boolean firstLaunch = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getBoolean(getResources().getString(R.string.FIRST_LAUNCH), true);

        if(firstLaunch) {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(getString(R.string.pref_key_notification_activation), true);
            editor.putString(getString(R.string.pref_notification_time), "20:22");
            editor.commit();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_BOOT_COMPLETED);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
        else if(getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getBoolean(getResources().getString(R.string.pref_key_notification_activation), false)){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_BOOT_COMPLETED);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    public void showAToast(String st) { //"Toast toast" is declared in the class
        try {
            toast.getView().isShown();     // true if visible
            toast.setText(st);
        } catch (Exception e) {         // invisible if exception
            toast = Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT);
        }
        toast.show();  //finally display it
    }

    public void loadPredictions() {
        String sign = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString(getResources().getString(R.string.pref_sign_key), getResources().getString(R.string.aries));
        PredictionService service = RetrofitPrediction.getRetrofitInstance().create(PredictionService.class);
        Call<ReceivedPrediction> call;

        PredictionDbHelper predictionDbHelper = new PredictionDbHelper(getApplicationContext());
        SQLiteDatabase db = predictionDbHelper.getWritableDatabase();

        Date today = new Date();
        calToday = Calendar.getInstance();
        calToday.setTime(today);
        calToday.set(Calendar.MILLISECOND, 0);
        calToday.set(Calendar.SECOND, 0);
        calToday.set(Calendar.MINUTE, 0);
        calToday.set(Calendar.HOUR, 0);

        Date lastUpdateToday = new Date();
        final Cursor cursorToday = db.rawQuery("SELECT MAX(date) as date FROM prediction where frequency = 0 and sign = ?;", new String[]{sign});
        if (cursorToday != null) {
            try {
                if (cursorToday.moveToFirst()) {
                    lastUpdateToday = DatabaseContract.getColumnDate(cursorToday, DatabaseContract.PredictionColumns.DATE);
                }
            } finally {
                cursorToday.close();
            }
        }
        calLastUpdateToday = Calendar.getInstance();
        calLastUpdateToday.setTime(lastUpdateToday);
        Date lastUpdateWeek = new Date();
        final Cursor cursorWeek = db.rawQuery("SELECT MAX(date) as date FROM prediction where frequency = 1 and sign = ?;", new String[]{sign});
        if (cursorWeek != null) {
            try {
                if (cursorWeek.moveToFirst()) {
                    lastUpdateWeek = DatabaseContract.getColumnDate(cursorWeek, DatabaseContract.PredictionColumns.DATE);
                }
            } finally {
                cursorWeek.close();
            }
        }
        calLastUpdateWeek = Calendar.getInstance();
        calLastUpdateWeek.setTime(lastUpdateWeek);
        Date lastUpdateMonth = new Date();
        final Cursor cursorMonth = db.rawQuery("SELECT MAX(date) as date FROM prediction where frequency = 2 and sign = ?;", new String[]{sign});
        if (cursorMonth != null) {
            try {
                if (cursorMonth.moveToFirst()) {
                    lastUpdateMonth = DatabaseContract.getColumnDate(cursorMonth, DatabaseContract.PredictionColumns.DATE);
                }
            } finally {
                cursorMonth.close();
            }
        }
        calLastUpdateMonth = Calendar.getInstance();
        calLastUpdateMonth.setTime(lastUpdateMonth);

        if (calToday.get(Calendar.YEAR) >= calLastUpdateToday.get(Calendar.YEAR) && calToday.get(Calendar.DAY_OF_YEAR) > calLastUpdateToday.get(Calendar.DAY_OF_YEAR)) {
            call = service.getPrediction(sign, "today");
            call.enqueue(new Callback<ReceivedPrediction>() {
                @Override
                public void onResponse(@NonNull Call<ReceivedPrediction> call, @NonNull Response<ReceivedPrediction> response) {
                    ContentValues values = new ContentValues();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    Date date = new Date();
                    try {
                        date = formatter.parse(response.body().getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Calendar calThis = Calendar.getInstance();
                    calThis.setTime(date);
                    if (calThis.get(Calendar.YEAR) >= calLastUpdateToday.get(Calendar.YEAR) && calThis.get(Calendar.DAY_OF_YEAR) > calLastUpdateToday.get(Calendar.DAY_OF_YEAR))
                    {

                        values.put(DatabaseContract.PredictionColumns.DATE, date.getTime());
                        values.put(DatabaseContract.PredictionColumns.FREQUENCY, 0);
                        values.put(DatabaseContract.PredictionColumns.PREDICTION, response.body().getHoroscope());
                        values.put(DatabaseContract.PredictionColumns.LIKED, 0);
                        values.put(DatabaseContract.PredictionColumns.SIGN, response.body().getSunsign());
                        PredictionDbHelper predictionDbHelper = new PredictionDbHelper(getApplicationContext());
                        SQLiteDatabase db = predictionDbHelper.getWritableDatabase();
                        db.insert(DatabaseContract.TABLE_PREDICTION, null, values);
//                    if (!(calToday.get(Calendar.YEAR) >= calLastUpdateWeek.get(Calendar.YEAR) && calToday.get(Calendar.WEEK_OF_YEAR) > calLastUpdateWeek.get(Calendar.WEEK_OF_YEAR)) &&
//                            !(calToday.get(Calendar.YEAR) >= calLastUpdateMonth.get(Calendar.YEAR) && calToday.get(Calendar.MONTH) > calLastUpdateMonth.get(Calendar.MONTH))) {
                        refreshFragments();
                        askToRate();
//                    }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ReceivedPrediction> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (calToday.get(Calendar.YEAR) >= calLastUpdateWeek.get(Calendar.YEAR) && calToday.get(Calendar.WEEK_OF_YEAR) > calLastUpdateWeek.get(Calendar.WEEK_OF_YEAR)) {
            call = service.getPrediction(sign, "week");
            call.enqueue(new Callback<ReceivedPrediction>() {
                @Override
                public void onResponse(@NonNull Call<ReceivedPrediction> call, @NonNull Response<ReceivedPrediction> response) {
                    ContentValues values = new ContentValues();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                    Date date = new Date();
                    try {
                        date = formatter.parse(response.body().getWeek().substring(0, 10));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Calendar calThis = Calendar.getInstance();
                    calThis.setTime(date);
                    if (calThis.get(Calendar.YEAR) >= calLastUpdateWeek.get(Calendar.YEAR) && calThis.get(Calendar.DAY_OF_YEAR) > calLastUpdateWeek.get(Calendar.DAY_OF_YEAR)) {
                        values.put(DatabaseContract.PredictionColumns.DATE, date.getTime());
                        values.put(DatabaseContract.PredictionColumns.FREQUENCY, 1);
                        values.put(DatabaseContract.PredictionColumns.PREDICTION, response.body().getHoroscope());
                        values.put(DatabaseContract.PredictionColumns.LIKED, 0);
                        values.put(DatabaseContract.PredictionColumns.SIGN, response.body().getSunsign());
                        PredictionDbHelper predictionDbHelper = new PredictionDbHelper(getApplicationContext());
                        SQLiteDatabase db = predictionDbHelper.getWritableDatabase();
                        db.insert(DatabaseContract.TABLE_PREDICTION, null, values);

//                    if(!(calToday.get(Calendar.YEAR) >= calLastUpdateMonth.get(Calendar.YEAR) && calToday.get(Calendar.MONTH) > calLastUpdateMonth.get(Calendar.MONTH))){
                        refreshFragments();
//                    }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ReceivedPrediction> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (calToday.get(Calendar.YEAR) > calLastUpdateMonth.get(Calendar.YEAR) || (calToday.get(Calendar.YEAR) == calLastUpdateMonth.get(Calendar.YEAR) && calToday.get(Calendar.MONTH) > calLastUpdateMonth.get(Calendar.MONTH))) {
            call = service.getPrediction(sign, "month");
            call.enqueue(new Callback<ReceivedPrediction>() {
                @Override
                public void onResponse(@NonNull Call<ReceivedPrediction> call, @NonNull Response<ReceivedPrediction> response) {
                    ContentValues values = new ContentValues();
                    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");

                    Date date = new Date();
                    try {
                        date = formatter.parse(response.body().getMonth());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Calendar calThis = Calendar.getInstance();
                    calThis.setTime(date);
                    if (calThis.get(Calendar.YEAR) > calLastUpdateMonth.get(Calendar.YEAR) || (calThis.get(Calendar.YEAR) == calLastUpdateMonth.get(Calendar.YEAR) && calThis.get(Calendar.MONTH) > calLastUpdateMonth.get(Calendar.MONTH))) {
                        values.put(DatabaseContract.PredictionColumns.DATE, date.getTime());
                        values.put(DatabaseContract.PredictionColumns.FREQUENCY, 2);
                        values.put(DatabaseContract.PredictionColumns.PREDICTION, response.body().getHoroscope());
                        values.put(DatabaseContract.PredictionColumns.LIKED, 0);
                        values.put(DatabaseContract.PredictionColumns.SIGN, response.body().getSunsign());
                        PredictionDbHelper predictionDbHelper = new PredictionDbHelper(getApplicationContext());
                        SQLiteDatabase db = predictionDbHelper.getWritableDatabase();
                        db.insert(DatabaseContract.TABLE_PREDICTION, null, values);

                        refreshFragments();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ReceivedPrediction> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        refreshFragments();
    }

    private void askToRate() {
        RateItDialogFragment.show(this, getSupportFragmentManager());
        Boolean ratingAsked = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getBoolean(getResources().getString(R.string.RATING_ASKED), false);
        Boolean firstLaunch = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getBoolean(getResources().getString(R.string.FIRST_LAUNCH), true);

        if (!ratingAsked && !firstLaunch) {
        } else {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(getString(R.string.RATING_ASKED), false);
            editor.commit();
        }

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(getString(R.string.FIRST_LAUNCH), false);
        editor.commit();

    }

    public void refreshFragments() {
        Fragment page;
        for (int i = 0; i <= 2; i++) {
            page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + i);
            if (page != null) {
                ((MainFragment) page).restartLoader();
            }
        }
    }

    public void confirmDelete(final int cursorId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog_delete, null));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                DatabaseManager.getInstance(getApplicationContext()).deletePredictionById(cursorId);
                refreshFragments();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(R.id.deleteTextViewMessage);
        textView.setText(R.string.message_delete);
        dialog.getWindow().setBackgroundDrawableResource(R.color.backGround);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                return false;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return false;

        }
        return false;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        displayDateSpinnerAlertDialog();
    }

    private void displayDateSpinnerAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setCancelable(false);
        builder.setView(inflater.inflate(R.layout.custom_dialog_date, null));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                saveBirthDay();
            }
        });
        alertDialogDate = builder.create();
        alertDialogDate.show();
        ((TextView) alertDialogDate.findViewById(R.id.dateTextViewTitle)).setText(getResources().getString(R.string.welcome));
        ((TextView) alertDialogDate.findViewById(R.id.dateTextViewMessage)).setText(getResources().getString(R.string.welcome_to_lovely_day_a_minimalistic_and_easy_to_use_astrology_app_please_select_your_birthdate));

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner daysSpinner = (Spinner) alertDialogDate.findViewById(R.id.days_spinner);
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
        //Nothing
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
        loadPredictions();
    }

    private void showWelcomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog_welcome, null));
        builder.setPositiveButton("Ok", this);
        builder.setCancelable(false);
        dialogWelcome = builder.create();
        dialogWelcome.show();
        TextView textView = (TextView) dialogWelcome.findViewById(R.id.welcomeTextViewMessage);
        textView.setText(R.string.welcome_message);
        dialogWelcome.getWindow().setBackgroundDrawableResource(R.color.backGround);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(listener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        LocalBroadcastManager.getInstance(this).registerReceiver(alarmReceiver,
                intentFilter);
        gestionNotification();
        String birthDate = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString(getResources().getString(R.string.pref_birth_date), "");
        if ((alertDialogDate == null) && birthDate.equals("")) {
            displayDateSpinnerAlertDialog();
//            showWelcomeDialog();
        } else if ((alertDialogDate != null) && alertDialogDate.isShowing()) {
        } else {
            loadPredictions();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(listener);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(alarmReceiver);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return MainFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


}
