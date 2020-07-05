package xyz.skrawlr.lovelyday.reminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import xyz.skrawlr.lovelyday.R;
import xyz.skrawlr.lovelyday.views.MainActivity;

public class ReminderService extends IntentService {

    private static final String TAG = ReminderService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 42;

    public ReminderService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Present a notification to the user
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Create action intent
        Intent action = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, action, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001");
//        int icon = selectSign();
        mBuilder.setSmallIcon(R.drawable.ic_stat_name);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
//        mBuilder.setContentTitle(getString(R.string.notification_title));
        mBuilder.setContentText(getString(R.string.notification_text));
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001", "Channel human readable title - New prediction available", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        manager.notify(NOTIFICATION_ID, mBuilder.build());
    }

//    private int selectSign() {
//        int signInt = R.drawable.ariesp;
//        String sign = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString(getApplicationContext().getResources().getString(R.string.pref_sign_key), "aries");
//
//        switch (sign) {
//            case "aquarius":  signInt = R.drawable.aquariuspd;
//                break;
//            case "aries":  signInt = R.drawable.ariespd;
//                break;
//            case "cancer":  signInt = R.drawable.cancerpd;
//                break;
//            case "capricorn":  signInt = R.drawable.capricornpd;
//                break;
//            case "gemini":  signInt = R.drawable.geminipd;
//                break;
//            case "leo":  signInt = R.drawable.leopd;
//                break;
//            case "libra":  signInt = R.drawable.librapd;
//                break;
//            case "pisces":  signInt = R.drawable.piscespd;
//                break;
//            case "sagittarus":  signInt = R.drawable.sagittaruspd;
//                break;
//            case "scorpio": signInt = R.drawable.scorpiopd;
//                break;
//            case "taurus": signInt = R.drawable.tauruspd;
//                break;
//            case "virgo": signInt = R.drawable.virgopd;
//                break;
//            default: signInt = R.drawable.ariespd;
//                break;
//        }
//        return signInt;
//    }
}

