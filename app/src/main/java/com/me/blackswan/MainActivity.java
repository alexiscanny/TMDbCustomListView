package com.me.blackswan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.me.blackswan.adapter.CustomListAdapter;
import com.me.blackswan.app.AppController;
import com.me.blackswan.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Movies json url
    private static final String url = "http://api.themoviedb.org/3/movie/popular?api_key=0a08e38b874d0aa2d426ffc04357069d";
    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<Movie>();
    private ListView listView;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, movieList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // changing action bar color
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#1b1b1b")));

        // Creating volley request obj
        JsonObjectRequest movieReq = new JsonObjectRequest(JsonRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response2) {
                Log.d(TAG, response2.toString());
                hidePDialog();
                try {
                    //here u need to use jsonArray but at the same time need to understand how make the array from
                    //this because the error is that can't convert into array
						/*JSONObject	response2 = response3.getJSONArray("results");
						JSONObject	response1 = response2.getJSONObject("results");

							Iterator x = response1.keys();
							JSONArray response = new JSONArray();

							while (x.hasNext()){
								String key = (String) x.next();
								response.put(response1.get(key));
							}*/

                    JSONArray response=response2.getJSONArray("results");
                    // Parsing json
                    for (int i = 0; i < response.length(); i++) {
//							try {

                        JSONObject obj = response.getJSONObject(i);
                        Movie movie = new Movie();
                        movie.setTitle(obj.getString("title"));
                        movie.setThumbnailUrl("http://image.tmdb.org/t/p/w500" + obj.getString("poster_path"));
                        movie.setRating(((Number) obj.get("vote_average"))
                                .doubleValue());
                        movie.setOverview(obj.getString("overview"));
                        movie.setYear(obj.getString("release_date"));

                        // Genre is json array
                        JSONArray genreArry = obj.getJSONArray("genre_ids");
                        ArrayList<String> genre = new ArrayList<String>();
                        for (int j = 0; j < genreArry.length(); j++) {
                            genre.add((String) genreArry.get(j).toString());
                        }
                        movie.setGenre(genre);

                        // adding movie to movies array
                        movieList.add(movie);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }





                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                adapter.notifyDataSetChanged();

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

   /* @Override
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
    }*/

}
