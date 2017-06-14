package com.filiereticsa.arc.augmentepf.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import com.filiereticsa.arc.augmentepf.R;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
//        VideoView videoView = (VideoView) findViewById(R.id.tuto_view);
//        String path = "android.resource://" + getPackageName() + "/" + R.raw.test_lamba;
//        videoView.setVideoURI(Uri.parse(path));
//        videoView.start();
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                Intent intent = new Intent(TutorialActivity.this, HomePageActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
