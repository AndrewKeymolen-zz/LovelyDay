package xyz.skrawlr.lovelyday.views;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import xyz.skrawlr.lovelyday.R;
import xyz.skrawlr.lovelyday.data.CustomCursorLoader;
import xyz.skrawlr.lovelyday.data.DatabaseContract;
import xyz.skrawlr.lovelyday.data.PredictionAdapter;
import xyz.skrawlr.lovelyday.data.PredictionDbHelper;

public class MainFragment extends Fragment implements
        View.OnClickListener, PredictionAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.TABLE_PREDICTION);
    private static final String ARG_SECTION_NUMBER = "section_number";
    PredictionDbHelper mDbHelper;
    private PredictionAdapter mAdapter;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new PredictionAdapter(null);
        mAdapter.setOnItemClickListener(this);

        //For the creation of the database if it's not yet done
        mDbHelper = new PredictionDbHelper(getContext());
        mDbHelper.getWritableDatabase();

        mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                ((MainActivity) getActivity()).refreshFragments();
            }
        };
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperrefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        restartLoader();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                swipeRefreshLayout.setRefreshing(true);
                ((MainActivity) getActivity()).loadPredictions();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CustomCursorLoader(getContext(), CONTENT_URI,
                null, null, null, null, this.getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity().getApplicationContext(), "Your predictions are up to date!", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(View v, int position) {

    }

    @Override
    public void onRefresh() {
        ((MainActivity) getActivity()).loadPredictions();
    }
}
