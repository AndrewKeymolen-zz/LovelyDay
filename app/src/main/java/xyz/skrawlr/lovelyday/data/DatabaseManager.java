package xyz.skrawlr.lovelyday.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;

public class DatabaseManager {
    private static DatabaseManager sInstance;
    private PredictionDbHelper mPredictionDbHelper;

    private DatabaseManager(Context context) {
        mPredictionDbHelper = new PredictionDbHelper(context);
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseManager(context.getApplicationContext());
        }

        return sInstance;
    }

    public Cursor queryAllDailyPrediction(String sortOrder) {
        SQLiteDatabase db = mPredictionDbHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DatabaseContract.TABLE_PREDICTION);
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = DatabaseContract.DATE_SORT;
        }
        String[] whereArgs = {"0"};
        Cursor cursor =
                builder.query(
                        db,
                        null,
                        DatabaseContract.PredictionColumns.FREQUENCY + "=?",
                        whereArgs,
                        null,
                        null,
                        sortOrder);
        return cursor;
    }

    public Cursor queryAllWeeklyPrediction(String sortOrder) {
        SQLiteDatabase db = mPredictionDbHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DatabaseContract.TABLE_PREDICTION);
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = DatabaseContract.DATE_SORT;
        }
        String[] whereArgs = {"1"};
        Cursor cursor =
                builder.query(
                        db,
                        null,
                        DatabaseContract.PredictionColumns.FREQUENCY + "=?",
                        whereArgs,
                        null,
                        null,
                        sortOrder);
        return cursor;
    }

    public Cursor queryAllMonthlyPrediction(String sortOrder) {
        SQLiteDatabase db = mPredictionDbHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DatabaseContract.TABLE_PREDICTION);
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = DatabaseContract.DATE_SORT;
        }
        String[] whereArgs = {"2"};
        Cursor cursor =
                builder.query(
                        db,
                        null,
                        DatabaseContract.PredictionColumns.FREQUENCY + "=?",
                        whereArgs,
                        null,
                        null,
                        sortOrder);
        return cursor;
    }

    public Cursor queryPredictionById(int id) {
        SQLiteDatabase db = mPredictionDbHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DatabaseContract.TABLE_PREDICTION);
        builder.appendWhere(DatabaseContract.PredictionColumns._ID + " = " +
                id);
        Cursor cursor =
                builder.query(
                        db,
                        null,
                        null,
                        null,
                        null,
                        null,
                        DatabaseContract.DATE_SORT);
        return cursor;
    }

    public void likePrediction(int id, boolean liked) {
        ContentValues values = new ContentValues(1);
        values.put(DatabaseContract.PredictionColumns.LIKED, (liked ? 1 : 0));
        SQLiteDatabase db = mPredictionDbHelper.getWritableDatabase();
        db.update(DatabaseContract.TABLE_PREDICTION, values, DatabaseContract.PredictionColumns._ID + " = " + id, null);
    }

    public void deletePredictionById(int id) {
        SQLiteDatabase db = mPredictionDbHelper.getWritableDatabase();
        db.delete(DatabaseContract.TABLE_PREDICTION, DatabaseContract.PredictionColumns._ID + " = " + id, null);
    }
}
