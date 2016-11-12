package dv606.my222au.assignment2.alarmClock;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import dv606.my222au.assignment2.R;

public class AlarmActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_layout);
        Button button = (Button) findViewById(R.id.button);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this , R.raw.sound);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                Intent  intent = new Intent(getApplicationContext(), AlarmMainActivity.class);
                startActivity(intent);
            }
        });
    }





}
