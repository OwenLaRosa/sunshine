package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment that displays 7 day weather forecast
 */
public class ForecastFragment extends Fragment {

    ArrayAdapter<String> mForecastAdapter;
    ListView forecastListView;
    private String apiKey = "83ca909b4cd38c2adeee23c2b697ca65";

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] forecasts = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7",
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecasts));
        mForecastAdapter = new ArrayAdapter<String>(
                // fragment's current context or parent activity
                getActivity(),
                // list item layout
                R.layout.list_item_forecast,
                // text view within the list item
                R.id.list_item_forecast_textview,
                // array of data to populate the list view
                weekForecast
        );

        forecastListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(mForecastAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("94043, US");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        protected Void doInBackground(String... params) {

            // check if there are no parameters
            if (params.length == 0) {
                return null;
            }

            // class for connecting to the network
            HttpURLConnection urlConnection = null;
            // class for reading the returned data
            BufferedReader reader = null;
            // string to hold the returned data in JSON format
            String forecastJsonStr = null;

            // values for weather parameters
            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {

                // constants for weather parameter names
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String KEY_PARAM = "appid";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .build();

                // url to be used in the GET request
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                // open connection to the URL
                urlConnection = (HttpURLConnection) url.openConnection();
                // specify the type of request
                urlConnection.setRequestMethod("GET");
                // connect to the server
                urlConnection.connect();
                // get the input stream
                InputStream inputStream = urlConnection.getInputStream();
                // read the stream into a string
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                // initialize the previously defined buffer reader
                reader = new BufferedReader(new InputStreamReader(inputStream));
                // each line of returned JSON data
                String line;
                while ((line = reader.readLine()) != null) {
                    // append to the buffer and add a newline for readability
                    buffer.append(line + "\n");
                }
                // check if the stream is empty
                if (buffer.length() == 0) {
                    // if so, then there's no need for parsing
                    return null;
                }
                // convert the buffer's contents to string format
                forecastJsonStr = buffer.toString();
                // log the data to verify that it's correct
                Log.v(LOG_TAG, "Forecast JSON string: " + forecastJsonStr);
            } catch (IOException e) {
                // log the error
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    // disconnect from the server
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream: ", e);
                    }
                }
            }
            return null;
        }
    }
}