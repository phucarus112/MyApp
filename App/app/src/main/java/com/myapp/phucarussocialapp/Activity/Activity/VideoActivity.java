package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.myapp.phucarussocialapp.R;

public class VideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    YouTubePlayerView youTubePlayerView;
    String id ="";
    final int REQUEST_CODE_VIDEO = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        youTubePlayerView = (YouTubePlayerView)findViewById(R.id.ylv);
        Intent intent = getIntent();
        id = intent.getStringExtra("idVid");
        youTubePlayerView.initialize(PlayistActivity.API_KEY,VideoActivity.this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(id);
        youTubePlayer.setFullscreen(true);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if(youTubeInitializationResult.isUserRecoverableError()){
            youTubeInitializationResult.getErrorDialog(VideoActivity.this,REQUEST_CODE_VIDEO);
        }else Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_VIDEO){
            youTubePlayerView.initialize(PlayistActivity.API_KEY, VideoActivity.this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
