package com.doan.thongbaodiemdung;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.doan.thongbaodiemdung.Data.DatabaseHelper;

public class AlarmActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SharedPreferences sharedPreferences;
    private String ringtone;

    private DatabaseHelper dbHelper;
    private Button button;

    private ImageView dimiss_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alarm);

        dbHelper = new DatabaseHelper(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ringtone = sharedPreferences.getString("pref_ringtone", "");

        int resId = this.getResources().getIdentifier(ringtone, "raw", this.getPackageName());
        mediaPlayer = MediaPlayer.create(this, resId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        /*button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAlarm();
                Intent intent = new Intent(AlarmActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });*/


        dimiss_image = (ImageView) findViewById(R.id.dissmiss_image);

        dimiss_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                Intent intent = new Intent(AlarmActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }
}
