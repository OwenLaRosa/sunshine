package com.example.android.sunshine.app;

/**
 * Created by Owen LaRosa on 7/14/16.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A Detail fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_PRESSURE = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    private String mForecastString;

    private ShareActionProvider mShareActionProvider;

    TextView mDayTextView;
    TextView mDateTextView;
    TextView mHighTextView;
    TextView mLowTextView;
    ImageView mIconImageView;
    TextView mHumidityTextView;
    TextView mWindTextView;
    TextView mPressureTextView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDayTextView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDateTextView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mHighTextView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTextView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mIconImageView = (ImageView) rootView.findViewById(R.id.detail_icon_imageview);
        mHumidityTextView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindTextView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureTextView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastString + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            return new CursorLoader(getActivity(), intent.getData(), FORECAST_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) return;

        Context context = getActivity();

        long date = data.getLong(COL_WEATHER_DATE);
        String description = data.getString(COL_WEATHER_DESC);
        boolean isMetric = Utility.isMetric(getActivity());
        double high = data.getDouble(COL_WEATHER_MAX_TEMP);
        double low = data.getDouble(COL_WEATHER_MIN_TEMP);
        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        int windSpeed = data.getInt(COL_WEATHER_WIND_SPEED);
        int windDegrees = data.getInt(COL_WEATHER_DEGREES);
        float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        int condition = data.getInt(COL_WEATHER_CONDITION_ID);

        TextView dayTextView = (TextView) getView().findViewById(R.id.detail_day_textview);
        dayTextView.setText(Utility.getDayName(context, date));

        TextView dateTextView = (TextView) getView().findViewById(R.id.detail_date_textview);
        dateTextView.setText(Utility.getFormattedMonthDay(context, date));

        TextView highTextView = (TextView) getView().findViewById(R.id.detail_high_textview);
        highTextView.setText(Utility.formatTemperature(context, high, isMetric));
        TextView lowTextView = (TextView) getView().findViewById(R.id.detail_low_textview);
        lowTextView.setText(Utility.formatTemperature(context, low, isMetric));

        ImageView imageView = (ImageView) getView().findViewById(R.id.detail_icon_imageview);
        imageView.setImageResource(Utility.getArtResourceForWeatherCondition(condition));

        TextView descriptionTextView = (TextView) getView().findViewById(R.id.detail_description_textview);
        descriptionTextView.setText(description);
        TextView humidityTextView = (TextView) getView().findViewById(R.id.detail_humidity_textview);
        humidityTextView.setText(getActivity().getString(R.string.format_humidity, humidity));
        TextView windTextView = (TextView) getView().findViewById(R.id.detail_wind_textview);
        windTextView.setText(Utility.getFormattedWind(context, windSpeed, windDegrees));
        TextView pressureTextView = (TextView) getView().findViewById(R.id.detail_pressure_textview);
        pressureTextView.setText(getActivity().getString(R.string.format_pressure, pressure));

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
