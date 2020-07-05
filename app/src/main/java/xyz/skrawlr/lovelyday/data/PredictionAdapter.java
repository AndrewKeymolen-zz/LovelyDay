package xyz.skrawlr.lovelyday.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import xyz.skrawlr.lovelyday.R;
import xyz.skrawlr.lovelyday.views.LikedView;
import xyz.skrawlr.lovelyday.views.MainActivity;

public class PredictionAdapter extends PredictionRecyclerAdapter {

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH);
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;

    public PredictionAdapter(Cursor cursor) {
        super.mCursor = cursor;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    protected void postItemClick(PredictionHolder predictionHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(predictionHolder.itemView, predictionHolder.getAdapterPosition());
        }
    }

    @Override
    public PredictionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_prediction, parent, false);

        return new PredictionHolder(itemView, mContext);
    }

    @Override
    public void onBindViewHolder(final PredictionHolder holder, final int position) {
        holder.dateView.setText(simpleDateFormat.format(getItem(position).date));
        holder.predictionView.setText(getItem(position).prediction);
        holder.likedView.setLiked(getItem(position).liked);
        holder.signView.setSign(getItem(position).sign);
        holder.likedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LikedView) v).setLiked(!((LikedView) v).getLiked());
                DatabaseManager.getInstance(holder.context).likePrediction(getItem(position).id, ((LikedView) v).getLiked());
            }
        });
        holder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) holder.context).confirmDelete(getItem(position).id);
            }
        });
        holder.shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri imageUri = Uri.parse("android.resource://" + "xyz.skrawlr.lovelyday"
                        + "/drawable/" + "logo_transparent_rogne_whitebg");
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, simpleDateFormat.format(getItem(position).date) +
                        " - " + getItem(position).sign.substring(0, 1).toUpperCase() + getItem(position).sign.substring(1) + ": \n\n" +
                        getItem(position).prediction +
                        "\n\n + Get your own daily, weekly and monthly predictions with Lovely Day: https://play.google.com/store/apps/details?id=xyz.skrawlr.lovelyday");
//                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("text/plain");
                ((MainActivity) holder.context).startActivity(Intent.createChooser(shareIntent, "Send"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}
