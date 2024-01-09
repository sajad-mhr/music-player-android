package com.example.music_player;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediaPlayer mp = MediaPlayer.create(this,R.raw.m1);
        mp.setLooping(true);

        Button btnplaypause = (Button)findViewById(R.id.playpause);


        SeekBar timelineSeek = (SeekBar) findViewById(R.id.timelineSeek);
        timelineSeek.setMax(mp.getDuration());

        TextView currentTime = (TextView)findViewById(R.id.currentTime);
        TextView durationTime = (TextView)findViewById(R.id.durationTime);
        durationTime.setText(musicTimer(mp.getDuration()));

        btnplaypause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new Timer();
                if(mp.isPlaying()){
                    mp.pause();
                    btnplaypause.setText(">");
                }else {
                    mp.start();
                    btnplaypause.setText("||");
                    timer.scheduleAtFixedRate(new TimerTask()
                    {
                        public void run() {
                            timelineSeek.setProgress(mp.getCurrentPosition());
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mp.isPlaying()){
                                        currentTime.setText(musicTimer(mp.getCurrentPosition()));
                                    }else {
                                        timer.cancel();
                                    }
                                }
                            });

                        }
                    }, 0, 1000);
                }
            }
        });


        timelineSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mp.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        AudioManager am;
        am = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        int maxVolume = am.getStreamMaxVolume(am.STREAM_MUSIC);
        int currentVolume = am.getStreamVolume(am.STREAM_MUSIC);
        SeekBar volume = (SeekBar) findViewById(R.id.volumeSeek);
        volume.setMax(maxVolume);
        volume.setProgress(currentVolume);

        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                am.setStreamVolume(am.STREAM_MUSIC,progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    public String musicTimer(long milliseconds){
        String time = String.format("%02d : %02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        );
        return time;
    }
}