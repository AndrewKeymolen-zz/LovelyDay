package xyz.skrawlr.lovelyday.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import xyz.skrawlr.lovelyday.data.DatabaseContract.PredictionColumns;

import static xyz.skrawlr.lovelyday.data.DatabaseContract.getColumnBoolean;
import static xyz.skrawlr.lovelyday.data.DatabaseContract.getColumnDate;
import static xyz.skrawlr.lovelyday.data.DatabaseContract.getColumnFrequency;
import static xyz.skrawlr.lovelyday.data.DatabaseContract.getColumnInt;
import static xyz.skrawlr.lovelyday.data.DatabaseContract.getColumnString;

public class Prediction implements Parcelable {
    public static final int NO_ID = -1;
    public static final Creator<Prediction> CREATOR = new Creator<Prediction>() {
        @Override
        public Prediction createFromParcel(Parcel source) {
            return new Prediction(source);
        }

        @Override
        public Prediction[] newArray(int size) {
            return new Prediction[size];
        }
    };
    public final Frequency frequency;
    public final Date date;
    public final String prediction;
    public final Boolean liked;
    public final String sign;
    public int id;
    //Discrete values
    public Prediction(Frequency frequency, Date date, String prediction, Boolean liked, String sign) {
        this.frequency = frequency;
        this.date = date;
        this.prediction = prediction;
        this.liked = liked;
        this.sign = sign;
    }


    //DB Cursor
    public Prediction(Cursor cursor) {
        this.id = getColumnInt(cursor, PredictionColumns._ID);
        this.frequency = getColumnFrequency(cursor, PredictionColumns.FREQUENCY);
        this.date = getColumnDate(cursor, PredictionColumns.DATE);
        this.prediction = getColumnString(cursor, PredictionColumns.PREDICTION);
        this.liked = getColumnBoolean(cursor, PredictionColumns.LIKED);
        this.sign = getColumnString(cursor, PredictionColumns.SIGN);
    }

    //Data Parcel
    private Prediction(Parcel in) {
        this.id = in.readInt();
        this.frequency = (Frequency) in.readSerializable();
        this.date = (Date) in.readSerializable();
        this.prediction = in.readString();
        this.liked = in.readByte() != 0;
        this.sign = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeSerializable(frequency);
        dest.writeSerializable(date);
        dest.writeString(prediction);
        dest.writeByte((byte) (liked ? 1 : 0));
        dest.writeString(sign);
    }

    public enum Frequency {
        Daily,
        Weekly,
        Monthly;
    }
}
