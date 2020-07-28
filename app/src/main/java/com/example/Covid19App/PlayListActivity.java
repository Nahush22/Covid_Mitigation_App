package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlayListActivity extends AppCompatActivity {

    String nxtPgToken = null;

    private static final String TAG = "PlayListActivity";
    String course;
    ArrayList<PlaylistModel> playlistModels = new ArrayList<>();

    RecyclerView recyclerView;
    PlaylistAdapter playlistAdapter;

    ProgressBar progressBar;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        progressBar = findViewById(R.id.lastProgBar);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            course= null;
        } else {
            course = extras.getString("Skill");
        }

        if(course != null)
        {
//            title.setText(course);
        }

        recyclerView = findViewById(R.id.playList);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        //https://stackoverflow.com/questions/26543131/how-to-implement-endless-list-with-recyclerview
        recyclerView.addOnScrollListener(onScrollListener);


        getData();
        showRecyclerView();
    }

    private void getData() {

        String key = getString(R.string.map_key);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&order=viewCount&q=" + course + "&type=playlist&key=" + key;

        if(nxtPgToken != null)
        {
            url += "&pageToken=" + nxtPgToken;
            nxtPgToken = null;

            Log.d(TAG, url);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                if(response != null)
                {
                    Log.d(TAG, response);

                    try {
//                        https://stackoverflow.com/questions/32624166/how-to-get-json-array-within-json-object/32624372

                        JSONObject jsonObject = new JSONObject(response);
                        Log.d(TAG, jsonObject.toString());

//                        if(nxtPgToken == null)
//                            playlistModels.clear();

                        if(jsonObject.has("nextPageToken"))
                            nxtPgToken = jsonObject.getString("nextPageToken");

                        JSONArray jsonArray = jsonObject.getJSONArray("items");

                        int length = jsonArray.length();

                        for(int i = 0; i < length; i++)
                        {
                            JSONObject jObj = jsonArray.getJSONObject(i);

                            String playListId = jObj.getJSONObject("id").getString("playlistId");
                            String date = jObj.getJSONObject("snippet").getString("publishedAt");
                            String title = jObj.getJSONObject("snippet").getString("title");
                            String description = jObj.getJSONObject("snippet").getString("description");
                            String thumbnail = jObj.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url");
                            String channelTitle = jObj.getJSONObject("snippet").getString("channelTitle");

                            PlaylistModel playlistModel = new PlaylistModel(playListId, date, title, description, thumbnail, channelTitle);
                            playlistModels.add(playlistModel);

                        }

                        // THIS PART DEALS WITH STOPPING RECYCLER VIEW FROM SCROLLING UP WHEN NEW ITEMS ARE ADDED

                        //https://stackoverflow.com/questions/28658579/refreshing-data-in-recyclerview-and-keeping-its-scroll-position - 2nd answer WORKED!!!!!! (Method 1)
                        Parcelable recyclerViewState;
                        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

                        playlistAdapter.notifyDataSetChanged();
//                        recyclerView.stopScroll(); //https://stackoverflow.com/questions/28993640/recyclerview-notifydatasetchanged-scrolls-to-top-position - 5th answer WORKED!!!!! (Method 2)

//                        showRecyclerView();

                        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                Log.d(TAG, error.toString());
                Toast.makeText(PlayListActivity.this, "Unable to get lab data", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);

    }

    private void showRecyclerView() {
        playlistAdapter = new PlaylistAdapter(this, playlistModels);
        recyclerView.setAdapter(playlistAdapter);

    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = linearLayoutManager.getItemCount();
            firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                Log.i("Yaeye!", "end called");

                // Do something

                if(nxtPgToken != null)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    getData();
                }

                loading = true;
            }

//            if(isLastItemDisplaying(recyclerView)){
//
//                if(nxtPgToken != null)
//                {
//                    progressBar.setVisibility(View.VISIBLE);
//                    getData();
//                }
//
//                Log.d(TAG, "LoadMore");
//            }



        }
    };

//    private Boolean isLastItemDisplaying(RecyclerView recyclerView){
//        //Check if the adapter count item is greater than 0
//        if(recyclerView.getAdapter().getItemCount() != 0){
//            //Get the last visible item on screen using the layoutmanager
//            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
//            //Apply some logic here
//            if(lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
//            {
//                return true;
//            }
//        }
//        return false;
//    }
}
