package xyz.skrawlr.lovelyday.views;

import android.content.Context;
import android.util.AttributeSet;

import xyz.skrawlr.lovelyday.R;

public class SignView extends android.support.v7.widget.AppCompatImageView {

    private String mSign = "aries";

    public SignView(Context context) {
        super(context);
    }

    public SignView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SignView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getSign() {
        return mSign;
    }

    public void setSign(String sign) {
        mSign = sign;
        switch (sign) {
            case "aquarius":
                this.setImageDrawable(getResources().getDrawable(R.drawable.aquariuspd));
                break;
            case "aries":
                this.setImageDrawable(getResources().getDrawable(R.drawable.ariespd));
                break;
            case "cancer":
                this.setImageDrawable(getResources().getDrawable(R.drawable.cancerpd));
                break;
            case "capricorn":
                this.setImageDrawable(getResources().getDrawable(R.drawable.capricornpd));
                break;
            case "gemini":
                this.setImageDrawable(getResources().getDrawable(R.drawable.geminipd));
                break;
            case "leo":
                this.setImageDrawable(getResources().getDrawable(R.drawable.leopd));
                break;
            case "libra":
                this.setImageDrawable(getResources().getDrawable(R.drawable.librapd));
                break;
            case "pisces":
                this.setImageDrawable(getResources().getDrawable(R.drawable.piscespd));
                break;
            case "sagittarus":
                this.setImageDrawable(getResources().getDrawable(R.drawable.sagittaruspd));
                break;
            case "scorpio":
                this.setImageDrawable(getResources().getDrawable(R.drawable.scorpiopd));
                break;
            case "taurus":
                this.setImageDrawable(getResources().getDrawable(R.drawable.tauruspd));
                break;
            case "virgo":
                this.setImageDrawable(getResources().getDrawable(R.drawable.virgopd));
                break;
            default:
                this.setImageDrawable(getResources().getDrawable(R.drawable.ariespd));
                break;
        }
    }
}
