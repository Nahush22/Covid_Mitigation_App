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

public class VideoListActivity extends AppCompatActivity {

    String nxtPgToken = null;

    private static final String TAG = "VideoListActivity";
    String playListId;
    ArrayList<PlaylistModel> videoModels = new ArrayList<>();

    RecyclerView recyclerView;
    PlaylistAdapter videoListAdapter;

    ProgressBar progressBar;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    int itemCount = 0;

    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        progressBar = findViewById(R.id.videoListProgressBar);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            playListId= null;
        } else {
            playListId = extras.getString("Id");
        }

        if(playListId != null)
        {
//            title.setText(course);
        }

        recyclerView = findViewById(R.id.videoList);

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

        String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=10&playlistId=" + playListId + "&key=" + key;

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

//                        Toast.makeText(VideoListActivity.this, "Getting items", Toast.LENGTH_SHORT).show();

                        int length = jsonArray.length();

                        for(int i = 0; i < length; i++)
                        {
                            JSONObject jObj = jsonArray.getJSONObject(i);

                            String videoId = jObj.getJSONObject("snippet").getJSONObject("resourceId").getString("videoId");

//                            Toast.makeText(VideoListActivity.this, "Video Id:" + videoId, Toast.LENGTH_SHORT).show();

                            String date = jObj.getJSONObject("snippet").getString("publishedAt");
                            String title = jObj.getJSONObject("snippet").getString("title");
                            String description = jObj.getJSONObject("snippet").getString("description");
                            String thumbnail = jObj.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url");
                            String channelTitle = jObj.getJSONObject("snippet").getString("channelTitle");

                            PlaylistModel videoModel = new PlaylistModel(videoId, date, title, description, thumbnail, channelTitle);

                            Log.d(TAG, videoModel.toString());

                            videoModels.add(videoModel);

                        }

                        // THIS PART DEALS WITH STOPPING RECYCLER VIEW FROM SCROLLING UP WHEN NEW ITEMS ARE ADDED

                        //https://stackoverflow.com/questions/28658579/refreshing-data-in-recyclerview-and-keeping-its-scroll-position - 2nd answer WORKED!!!!!! (Method 1)
                        Parcelable recyclerViewState;
                        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

                        videoListAdapter.notifyDataSetChanged();
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
                Toast.makeText(VideoListActivity.this, "Unable to get lab data", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    private void showRecyclerView() {

        videoListAdapter = new PlaylistAdapter(this, videoModels);
        recyclerView.setAdapter(videoListAdapter);
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

        }
    };

}
