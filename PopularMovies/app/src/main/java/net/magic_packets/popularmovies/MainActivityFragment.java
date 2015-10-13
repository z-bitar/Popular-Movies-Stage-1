package net.magic_packets.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivityFragment extends Fragment {
    GridView grid_main;

    public static List<String> IMAGE_URLS = new ArrayList<String>();
    public static List<String> MOVIE_TITLE = new ArrayList<String>();
    public static List<String> MOVIE_USER_RATING = new ArrayList<String>();
    public static List<String> MOVIE_RELEASE_DATE = new ArrayList<String>();
    public static List<String> MOVIE_OVERVIEW = new ArrayList<String>();
    private ImageGridAdapter grid_adapter;


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateMovie();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        grid_main = (GridView) rootView.findViewById(R.id.grid_main);

        grid_adapter = new ImageGridAdapter(getActivity(), IMAGE_URLS);

        grid_main.setAdapter(grid_adapter);


        grid_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("title", MOVIE_TITLE.get(position));
                intent.putExtra("poster", IMAGE_URLS.get(position));
                intent.putExtra("rating", MOVIE_USER_RATING.get(position));
                intent.putExtra("date", MOVIE_RELEASE_DATE.get(position));
                intent.putExtra("overview", MOVIE_OVERVIEW.get(position));

                startActivity(intent);

            }
        });


        return rootView;
    }

    private String[] getMoviePosterFromJson(String theMovieDbJasonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String POSTER = "poster_path";
        final String TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVERAGE = "vote_average";


        JSONObject forecastJson = new JSONObject(theMovieDbJasonStr);
        //Get the results node
        JSONArray movieArray = forecastJson.getJSONArray(RESULTS);

        int results_array_size = movieArray.length();

        String[] posterPath = new String[results_array_size];
        String[] title = new String[results_array_size];
        String[] overview = new String[results_array_size];
        String[] date = new String[results_array_size];
        String[] rating = new String[results_array_size];
        for (int i = 0; i < movieArray.length(); i++) {


            // Get the JSON object representing the movie
            JSONObject movieObject = movieArray.getJSONObject(i);
            //Get required params
            String posterObject = movieObject.getString(POSTER);
            String titleObject = movieObject.getString(TITLE);
            String overviewObject = movieObject.getString(OVERVIEW);
            String releaseDateObject = movieObject.getString(RELEASE_DATE);
            String ratingObject = movieObject.getString(VOTE_AVERAGE);


            posterPath[i] = "http://image.tmdb.org/t/p/w500" + posterObject;
            title[i] = titleObject;
            overview[i] = overviewObject;
            date[i] = releaseDateObject;
            rating[i] = ratingObject;
            MOVIE_TITLE.add(i, title[i]);
            MOVIE_USER_RATING.add(i, rating[i]);
            MOVIE_RELEASE_DATE.add(i, date[i]);
            MOVIE_OVERVIEW.add(i, overview[i]);

        }

        return posterPath;
    }

    public class FetchDataTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchDataTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String theMovieDbJasonStr = null;

            try {
                // Construct the URL for the themoviedb query
                // Possible parameters are avaiable themoviedb API page, at
                // http://docs.themoviedb.apiary.io/#
                final String SORT = "sort_by";
                final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?api_key=MY_APP_KEY";
                Uri buildUri = Uri.parse(BASE_URL).buildUpon().appendQueryParameter(SORT, params[0]).build();
                /*required URLs
                *https://api.themoviedb.org/3/discover/movie?api_key=MYAPIKEY&sort_by=popularity.desc
                *https://api.themoviedb.org/3/discover/movie?api_key=MYAPIKEY&sort_by=vote_average.desc
                *for image poster example: http://image.tmdb.org/t/p/w500/hTKME3PUzdS3ezqK5BZcytXLCUl.jpg
                */
                URL url = new URL(buildUri.toString());
                //Log.v(LOG_TAG, "BUILD URI" + buildUri.toString());

                // Create the request to themoviewdb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                theMovieDbJasonStr = buffer.toString();
                //log json data Log.v(LOG_TAG, "TheMocieDb JASON STRING" + theMovieDbJasonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the Movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                return getMoviePosterFromJson(theMovieDbJasonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {

            if (result != null) {

                grid_adapter.clear();
                for (String MOVIE_POSTER : result) {
                    grid_adapter.add(MOVIE_POSTER);
                }
                // New data is back from the server.
            }

        }

    }


    private void updateMovie() {
        FetchDataTask dataTask = new FetchDataTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.sort_by),
                getString(R.string.sort_by_default));
        dataTask.execute(sort);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }
}
