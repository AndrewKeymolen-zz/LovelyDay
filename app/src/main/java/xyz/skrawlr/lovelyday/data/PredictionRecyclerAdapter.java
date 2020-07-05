package xyz.skrawlr.lovelyday.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.skrawlr.lovelyday.R;
import xyz.skrawlr.lovelyday.views.LikedView;
import xyz.skrawlr.lovelyday.views.SignView;

public class PredictionRecyclerAdapter extends
        RecyclerView.Adapter<PredictionRecyclerAdapter.PredictionHolder> {

    protected Cursor mCursor;

    protected void postItemClick(PredictionHolder predictionHolder) {

    }

    @Override
    public PredictionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(PredictionHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * Return the {@link Prediction} represented by this item in the adapter.
     *
     * @param position Adapter item position.
     * @return A new {@link Prediction} filled with this position's attributes
     * @throws IllegalArgumentException if position is out of the adapter's bounds.
     */
    public Prediction getItem(int position) {
        if (position < 0 || position >= getItemCount()) {
            throw new IllegalArgumentException("Item position is out of adapter's range");
        } else if (mCursor.moveToPosition(position)) {
            return new Prediction(mCursor);
        }
        return null;
    }

    /* ViewHolder for each prediction item */
    public class PredictionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView dateView;
        public TextView predictionView;
        public LikedView likedView;
        public SignView signView;
        public ImageView shareView;
        public ImageView deleteView;
        public Context context;

        public PredictionHolder(View itemView, final Context context) {
            super(itemView);

            dateView = (TextView) itemView.findViewById(R.id.date);
            predictionView = (TextView) itemView.findViewById(R.id.prediction);
            likedView = (LikedView) itemView.findViewById(R.id.liked);
            signView = (SignView) itemView.findViewById(R.id.signView);
            shareView = (ImageView) itemView.findViewById(R.id.shareView);
            deleteView = (ImageView) itemView.findViewById(R.id.deleteView);
            this.context = context;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            postItemClick(this);
        }
    }
}
