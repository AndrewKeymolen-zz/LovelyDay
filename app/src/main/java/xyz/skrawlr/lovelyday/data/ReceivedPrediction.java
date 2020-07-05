package xyz.skrawlr.lovelyday.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReceivedPrediction {
    @Expose
    @SerializedName("date")
    private String date;
    @Expose
    @SerializedName("horoscope")
    private String horoscope;
    @Expose
    @SerializedName("sunsign")
    private String sunsign;
    @Expose
    @SerializedName("month")
    private String month;
    @Expose
    @SerializedName("week")
    private String week;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHoroscope() {
        return horoscope;
    }

    public void setHoroscope(String horoscope) {
        this.horoscope = horoscope;
    }

    public String getSunsign() {
        return sunsign;
    }

    public void setSunsign(String sunsign) {
        this.sunsign = sunsign;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}
