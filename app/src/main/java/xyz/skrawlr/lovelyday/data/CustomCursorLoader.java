package xyz.skrawlr.lovelyday.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import xyz.skrawlr.lovelyday.R;

import static android.content.Context.MODE_PRIVATE;
import static xyz.skrawlr.lovelyday.views.MainActivity.MY_PREFS_NAME;

public class CustomCursorLoader extends CursorLoader {
    private final ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private final int frequency;

    public CustomCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, int frequency) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        this.frequency = frequency;
    }

    @Override
    public Cursor loadInBackground() {
        String sortOrder = "";
        SharedPreferences prefs = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String sortOrderPref = prefs.getString(getContext().getResources().getString(R.string.pref_sortBy_key), getContext().getResources().getString(R.string.pref_sortBy_date));
        if (sortOrderPref.equals(getContext().getString(R.string.pref_sortBy_liked))) {
            sortOrder = DatabaseContract.LIKED_SORT;
        }

        Cursor cursor;

        switch (frequency) {
            case 1:
                cursor = DatabaseManager.getInstance(getContext()).queryAllDailyPrediction(sortOrder);
                break;
            case 2:
                cursor = DatabaseManager.getInstance(getContext()).queryAllWeeklyPrediction(sortOrder);
                break;
            case 3:
                cursor = DatabaseManager.getInstance(getContext()).queryAllMonthlyPrediction(sortOrder);
                break;
            default:
                cursor = DatabaseManager.getInstance(getContext()).queryAllDailyPrediction(sortOrder);
        }

        if (cursor != null) {
            // Ensure the cursor window is filled
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }

        return cursor;
    }
}
