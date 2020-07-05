package xyz.skrawlr.lovelyday.views;

import android.content.Context;
import android.util.AttributeSet;

import xyz.skrawlr.lovelyday.R;

public class LikedView extends android.support.v7.widget.AppCompatImageView {

    private boolean mLiked = false;

    public LikedView(Context context) {
        super(context);
    }

    public LikedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LikedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean getLiked() {
        return mLiked;
    }

    public void setLiked(boolean liked) {
        mLiked = liked;
        if (liked) {
            this.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_blue_18dp));
        } else {
            this.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_blue_18dp));
        }
    }
}
