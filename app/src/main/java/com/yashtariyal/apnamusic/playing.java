package com.harshsharma.apnamusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import android.widget.SeekBar;

public class playing extends AppCompatActivity {
    TextView textview, starttime, endtime;
    ImageView previous, pause, next, icon;
    MediaPlayer mediaPlayer;
    ArrayList<File> songs;
    String textcontent;
    int position;
    Thread updateSeek;
    SeekBar seekbar;
    Animation rotateAnimation;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        textview = findViewById(R.id.textView);
        icon = findViewById(R.id.imageView);
        previous = findViewById(R.id.previous);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        seekbar = findViewById(R.id.seekBar);
//        starttime = findViewById(R.id.textView4);
//        endtime = findViewById(R.id.textView5);

        rotateAnimation();

        int time = 0;


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        songs = (ArrayList)bundle.getParcelableArrayList("songList");
        textcontent = intent.getStringExtra("currentSong");
        textview.setText(textcontent);
        textview.setSelected(true);
        position = intent.getIntExtra("position", 0);
        Uri uri =Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();

//        endtime.setText(mediaPlayer.getDuration());
        seekbar.setMax(mediaPlayer.getDuration());

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekbar.getProgress());
//                starttime.setText(position);
            }
        });

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPos = 0;
                try{
                    while(currentPos < mediaPlayer.getDuration()){
                        currentPos = mediaPlayer.getCurrentPosition();
                        seekbar.setProgress(currentPos);

                        sleep(800);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    pause.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    pause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position != 0){
                    position = position - 1;
                }
                else{
                    position = songs.size() +- 1;
                }
                Uri uri =Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                pause.setImageResource(R.drawable.pause);
                textview.setText(songs.get(position).getName().toString());
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position != songs.size()-1){
                    position += 1;
                }
                else{
                    position = 0;
                }
                Uri uri =Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                pause.setImageResource(R.drawable.pause);
                textview.setText(songs.get(position).getName().toString());
            }
        });
    }

    private void rotateAnimation() {
        rotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        icon.startAnimation(rotateAnimation);
    }
}