package xyz.skrawlr.lovelyday.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

import xyz.skrawlr.lovelyday.R;

import static android.content.Context.MODE_PRIVATE;
import static xyz.skrawlr.lovelyday.views.MainActivity.MY_PREFS_NAME;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();

    /* Schedule the alarm based on user preferences */
    public static void scheduleAlarm(Context context) {
        AlarmManager manager = AlarmManagerProvider.getAlarmManager(context);

        String notificationsEnabled = context.getString(R.string.pref_key_notification_activation);
        String notificationTime = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString(context.getResources().getString(R.string.pref_notification_time), "10:00");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean enabled = preferences.getBoolean(notificationsEnabled, true);

        //Intent to trigger
        Intent intent = new Intent(context, ReminderService.class);
        PendingIntent operation = PendingIntent
                .getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (enabled) {
            //Gather the time preference
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(notificationTime.substring(0, 2)));
            startTime.set(Calendar.MINUTE, Integer.parseInt(notificationTime.substring(3, 5)));
            startTime.set(Calendar.SECOND, 0);

            //Start at the preferred time
            //If that time has passed today, set for tomorrow
            if (Calendar.getInstance().after(startTime)) {
                startTime.add(Calendar.DATE, 1);
            }

            manager.setRepeating(AlarmManager.RTC, startTime.getTimeInMillis(), 86400000, operation);
        } else {
            manager.cancel(operation);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Schedule alarm on BOOT_COMPLETED
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            scheduleAlarm(context);
        }
    }

}
