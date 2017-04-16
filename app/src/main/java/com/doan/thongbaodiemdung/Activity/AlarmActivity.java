package com.doan.thongbaodiemdung.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doan.thongbaodiemdung.Other.BackgroundService;
import com.doan.thongbaodiemdung.R;

public class AlarmActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private String ringtone;

    private ImageView dimiss_image;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alarm);

        ringtone = getIntent().getStringExtra("ringtone");

        try {
            int resId = this.getResources().getIdentifier(ringtone, "raw", this.getPackageName());
            mediaPlayer = MediaPlayer.create(this, resId);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        String info = getIntent().getStringExtra("info");
        textView = (TextView) findViewById(R.id.info_textview);
        textView.setText(info);

        dimiss_image = (ImageView) findViewById(R.id.dissmiss_image);

        dimiss_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                BackgroundService.IS_ALARMING = false;
                Intent intent = new Intent(AlarmActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        BackgroundService.IS_ALARMING = false;
        super.onDestroy();
    }
}
