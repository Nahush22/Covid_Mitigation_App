package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class VideoPlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String TAG = "VideoPlayerActivity";
    YouTubePlayerView youTubePlayerView;
    TextView title, date, channel, desc;

    TextView playTxt;
    Button playBtn;

    String videoId = "W4hTJybfU7s", name = null, dt = null, chn = null, dsc = null;

    YouTubePlayer.OnInitializedListener onInitializedListener;

    YouTubePlayer.OnFullscreenListener onFullscreenListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        youTubePlayerView = findViewById(R.id.youtubePlayerView);

//        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                Log.d(TAG, "Player initialisation success!");
//
//                youTubePlayer.loadVideo(videoId);
//
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//                Log.d(TAG, "Player initialisation failure!");
//
//            }
//        };

        title = findViewById(R.id.videoTitle);
        date = findViewById(R.id.videoDate);
        channel = findViewById(R.id.videoChannel);
        desc = findViewById(R.id.videoDescription);

        playTxt = findViewById(R.id.playTxt);
        playBtn = findViewById(R.id.playBtn);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            videoId= null;
        } else {
            videoId = extras.getString("Id");
            name = extras.getString("title");
            dt = extras.getString("date");
            chn = extras.getString("channel");
            dsc = extras.getString("desc");
        }



        if(name == null && date == null && channel == null && desc == null)
        {
            Log.d(TAG, "Video Details Empty");
        }
        else
        {
            title.setText(name);
            date.setText(dt);
            channel.setText(chn);
            desc.setText(dsc);
        }

        youTubePlayerView.initialize("AIzaSyCqiZ0Ssb7S5HE_VZVIu6e3AVJ8PKM7ggo", this);

//        playBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                youTubePlayerView.initialize(getString(R.string.map_key), onInitializedListener);
//                youTubePlayerView.initialize("AIzaSyCqiZ0Ssb7S5HE_VZVIu6e3AVJ8PKM7ggo", onInitializedListener);
//            }
//        });
//
//        playBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                youTubePlayerView.initialize("AIzaSyCqiZ0Ssb7S5HE_VZVIu6e3AVJ8PKM7ggo", this);
//            }
//        });


    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(videoId);

        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);

        youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean b) {
//                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);

//                if(!b)
//                    youTubePlayer.loadVideo(videoId);
            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
