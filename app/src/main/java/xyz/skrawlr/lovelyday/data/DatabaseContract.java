package xyz.skrawlr.lovelyday.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Date;

public class DatabaseContract {
    //Database schema information
    public static final String TABLE_PREDICTION = "prediction";
    //Unique authority string
    public static final String CONTENT_AUTHORITY = "xyz.skrawlr.lovelyday";
    /* Sort order constants */
    public static final String DATE_SORT = String.format("%s DESC",
            PredictionColumns.DATE);
    public static final String LIKED_SORT = String.format("%s DESC, %s DESC",
            PredictionColumns.LIKED, PredictionColumns.DATE);
    //Base content Uri
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_PREDICTION)
            .build();

    /* Helpers to retrieve column values */
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public static Prediction.Frequency getColumnFrequency(Cursor cursor, String columnName) {
        return Prediction.Frequency.values()[cursor.getInt(cursor.getColumnIndex(columnName))];
    }

    public static Date getColumnDate(Cursor cursor, String columnName) {
        return new Date(cursor.getLong(cursor.getColumnIndex(columnName)));
    }

    public static boolean getColumnBoolean(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName)) != 0;
    }

    public static final class PredictionColumns implements BaseColumns {
        public static final String FREQUENCY = "frequency";
        public static final String DATE = "date";
        public static final String PREDICTION = "prediction";
        public static final String LIKED = "liked";
        public static final String SIGN = "sign";
    }


}
