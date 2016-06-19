package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        ArrayAdapter<String> mForecastAdapter;
        ListView forecastListView;
        private String apiKey = "83ca909b4cd38c2adeee23c2b697ca65";

        public PlaceholderFragment() {
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

            // class for connecting to the network
            HttpURLConnection urlConnection = null;
            // class for reading the returned data
            BufferedReader reader = null;
            // string to hold the returned data in JSON format
            String forecastJsonStr = null;

            try {
                // url to be used in the GET request
                URL url = new URL("https://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
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
                    forecastJsonStr = null;
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
                    forecastJsonStr = null;
                }
                // convert the buffer's contents to string format
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                // log the error
                Log.e("PlaceholderFragment", "Error ", e);
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    // disconnect from the server
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream: ", e);
                    }
                }
            }




            return rootView;
        }
    }
}
