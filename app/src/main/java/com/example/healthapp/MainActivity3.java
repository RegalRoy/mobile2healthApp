package com.example.healthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.healthapp.ui.item.VolleySingleten;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MainActivity3 extends AppCompatActivity {
    VideoView videoView;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        requestQueue = VolleySingleten.getInstance(this).getRequestQueue();
        videoView = findViewById(R.id.videoView);
        Intent intent = getIntent();
        String exercise = intent.getStringExtra("exercise");

        getExerciseApi(exercise + " " + "exercise");

        String videoPath = "https://player.vimeo.com/external/456110386.hd.mp4?s=6212aac3e7481ce5ae521ce01f1f8928de0f2004&profile_id=170&oauth2_token_id=57447761";
        String path = "https://www.pexels.com/video/video-of-forest-1448735/";



//        MediaController mediaController = new MediaController(this);
//        videoView.setMediaController(mediaController);
//        mediaController.setAnchorView(videoView);

//        videoView.setMediaController(null); // Hide the media controller for full-screen mode
//        videoView.setOnPreparedListener(mp -> {
//            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
//        });

        // Start the video playback
        videoView.start();

    }

    private void getExerciseApi(String exercise) {
        String url = String.format("https://api.pexels.com/videos/search?query=%s&size=large", exercise);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("videos");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONArray jsonArray1 = jsonObject.getJSONArray("video_files");
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                    String urlVid = jsonObject1.getString("link");
                    MediaController mediaController = new MediaController(getApplicationContext());
                    mediaController.setAnchorView(videoView);
                    videoView.setVideoURI(Uri.parse(urlVid));

                    videoView.start();
                } catch (JSONException e) {
                    Toast.makeText(MainActivity3.this, "eror", Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);

                }
            }
        }, null){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "q6i0TgzMXmXn2g9oyA5y7zZcbC2GYb0LYNtC9FehP9gd9pTQzd0GVjFe");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}