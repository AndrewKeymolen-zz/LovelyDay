package xyz.skrawlr.lovelyday.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.skrawlr.lovelyday.R;

import static android.content.Context.MODE_PRIVATE;
import static xyz.skrawlr.lovelyday.views.MainActivity.MY_PREFS_NAME;

public class PredictionDbHelper extends SQLiteOpenHelper {
    private static final String TAG = PredictionDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "prediction.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_TASKS = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s TEXT, %s INTEGER, %s TEXT)",
            DatabaseContract.TABLE_PREDICTION,
            DatabaseContract.PredictionColumns._ID,
            DatabaseContract.PredictionColumns.DATE,
            DatabaseContract.PredictionColumns.FREQUENCY,
            DatabaseContract.PredictionColumns.PREDICTION,
            DatabaseContract.PredictionColumns.LIKED,
            DatabaseContract.PredictionColumns.SIGN
    );

    //Used to read data from res/ and assets/
    private Resources mResources;
    private Context mContext;
    private SQLiteDatabase db;
    private PredictionService service;
    private SharedPreferences sharedPref;
    private String sign;

    public PredictionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mResources = context.getResources();
        sharedPref = mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_TASKS);
        //try {
        this.db = db;
        //getPredictionInit();
        //readPredictionFromResources(db);
        //} catch (IOException | JSONException e) {
        //    e.printStackTrace();
        //}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_PREDICTION);
        onCreate(db);
    }

    private void readPredictionFromResources(SQLiteDatabase db) throws IOException, JSONException {
        StringBuilder builder = new StringBuilder();
        InputStream in = mResources.openRawResource(R.raw.prediction);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        //Parse resource into key/values
        final String rawJson = builder.toString();

        JSONObject mJSONObject = new JSONObject(rawJson);
        JSONArray predictions = mJSONObject.getJSONArray("prediction");
        ContentValues values;
        JSONObject prediction;

        for (int i = 0; i < predictions.length(); i++) {
            prediction = predictions.getJSONObject(i);
            values = new ContentValues();

            values.put(DatabaseContract.PredictionColumns.DATE, prediction.getLong("date"));
            values.put(DatabaseContract.PredictionColumns.FREQUENCY, prediction.getInt("frequency"));
            values.put(DatabaseContract.PredictionColumns.PREDICTION, prediction.getString("prediction"));
            values.put(DatabaseContract.PredictionColumns.LIKED, prediction.getInt("liked"));

            db.insertOrThrow(DatabaseContract.TABLE_PREDICTION, null, values);
        }
    }

    private void getPredictionInit() {
        sign = sharedPref.getString(mResources.getString(R.string.pref_sign_key), mResources.getString(R.string.aries));
        service = RetrofitPrediction.getRetrofitInstance().create(PredictionService.class);

        Call<ReceivedPrediction> call = service.getPrediction(sign, "today");
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
                values.put(DatabaseContract.PredictionColumns.DATE, date.getTime());
                values.put(DatabaseContract.PredictionColumns.FREQUENCY, 0);
                values.put(DatabaseContract.PredictionColumns.PREDICTION, response.body().getHoroscope());
                values.put(DatabaseContract.PredictionColumns.LIKED, 0);
                values.put(DatabaseContract.PredictionColumns.SIGN, response.body().getSunsign());

                db.insertOrThrow(DatabaseContract.TABLE_PREDICTION, null, values);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(mResources.getString(R.string.pref_lastUpdate_key), response.body().getDate());
                editor.commit();

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
                        values.put(DatabaseContract.PredictionColumns.DATE, date.getTime());
                        values.put(DatabaseContract.PredictionColumns.FREQUENCY, 1);
                        values.put(DatabaseContract.PredictionColumns.PREDICTION, response.body().getHoroscope());
                        values.put(DatabaseContract.PredictionColumns.LIKED, 0);
                        values.put(DatabaseContract.PredictionColumns.SIGN, response.body().getSunsign());

                        db.insertOrThrow(DatabaseContract.TABLE_PREDICTION, null, values);

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
                                values.put(DatabaseContract.PredictionColumns.DATE, date.getTime());
                                values.put(DatabaseContract.PredictionColumns.FREQUENCY, 2);
                                values.put(DatabaseContract.PredictionColumns.PREDICTION, response.body().getHoroscope());
                                values.put(DatabaseContract.PredictionColumns.LIKED, 0);
                                values.put(DatabaseContract.PredictionColumns.SIGN, response.body().getSunsign());

                                db.insertOrThrow(DatabaseContract.TABLE_PREDICTION, null, values);

                                //Si y'a un crash, c'est sûrement ici
                                //((MainActivity)mContext).refreshFragments();
                            }

                            @Override
                            public void onFailure(@NonNull Call<ReceivedPrediction> call, @NonNull Throwable t) {
                                Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<ReceivedPrediction> call, @NonNull Throwable t) {
                        Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ReceivedPrediction> call, @NonNull Throwable t) {
                Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
