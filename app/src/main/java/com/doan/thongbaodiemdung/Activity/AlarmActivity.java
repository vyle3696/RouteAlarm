package com.doan.thongbaodiemdung.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doan.thongbaodiemdung.R;
import com.doan.thongbaodiemdung.Service.BackgroundService;

import java.io.IOException;

public class AlarmActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private String ringtone;
    private String ringtonePath;

    private ImageView dimiss_image;
    private TextView textView;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //lệnh rung màn hình
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 200, 500 };
        vibrator.vibrate(pattern, 0);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alarm);
        ringtonePath = getIntent().getStringExtra("ringtonePath");


        // phat nhac bao thuc
        //neu đường dãn nhạc tồn tại
        if (!ringtonePath.equals("")) {

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(ringtonePath);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (IOException e) {
                e.printStackTrace();
                ringtone = "ringtone";

                try {
                    int resId = this.getResources().getIdentifier(ringtone, "raw", this.getPackageName());
                    mediaPlayer = MediaPlayer.create(this, resId);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                } catch (Exception ex) {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        } else {
            ringtone = getIntent().getStringExtra("ringtone");

            try {
                if(ringtone.equals("")) {
                    ringtone = "ringtone";
                }
                int resId = this.getResources().getIdentifier(ringtone, "raw", this.getPackageName());
                mediaPlayer = MediaPlayer.create(this, resId);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } catch (Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        String info = getIntent().getStringExtra("info");
        textView = (TextView) findViewById(R.id.info_textview);
        textView.setText(info);

        dimiss_image = (ImageView) findViewById(R.id.dissmiss_image);

        //Tạo hiệu ứng cho imageView
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
        AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
        dimiss_image.startAnimation(fadeIn);
        dimiss_image.startAnimation(fadeOut);
        fadeIn.setDuration(500);
        fadeOut.setDuration(500);
        fadeIn.setStartOffset(500+fadeOut.getStartOffset()+500);
        //fadeOut.setStartOffset(500+fadeIn.getStartOffset()+500);
        fadeOut.setRepeatCount(Animation.INFINITE);

        dimiss_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                vibrator.cancel();
                BackgroundService.IS_ALARMING = false;
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        vibrator.cancel();
        mediaPlayer.stop();
        BackgroundService.IS_ALARMING = false;
        super.onDestroy();
    }
}
